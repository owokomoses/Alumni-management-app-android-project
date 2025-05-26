package com.example.alumnimanagementsystemapp

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userProfileState = MutableStateFlow(UserProfile())
    val userProfileState: StateFlow<UserProfile> get() = _userProfileState
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference
    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser



    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
        // Add listener for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null && user.isEmailVerified) {
                _authState.value = AuthState.Authenticated
                // Fetch profile data whenever auth state changes
                fetchProfileFromFirestore(user.uid)
            } else {
                _authState.value = AuthState.Unauthenticated
                // Clear profile data when user logs out
                _userProfileState.value = UserProfile()
            }
        }
    }

    private fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null || !currentUser.isEmailVerified) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }


    fun login(email: String, password: String) {
        if (email.isEmpty()) {
            _authState.value = AuthState.Error("Email can't be empty")
            return
        }
        
        // If password is empty, this is an automatic login after verification
        if (password.isEmpty()) {
            val currentUser = auth.currentUser
            if (currentUser?.isEmailVerified == true) {
                _authState.value = AuthState.Authenticated
                // Fetch profile data immediately
                fetchProfileFromFirestore(currentUser.uid)
                return
            }
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        // Save user info to Firestore
                        saveUserToFirestore(user)
                        // Fetch profile data immediately
                        fetchProfileFromFirestore(user.uid)
                        _authState.value = AuthState.Authenticated
                    } else {
                        // Email not verified
                        _authState.value =
                            AuthState.Error("Email not verified. Please verify to activate account.")
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }


    fun signup(email: String, password: String, displayName: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Set the display name for the user
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileUpdateTask ->
                            if (profileUpdateTask.isSuccessful) {
                                // Save user info to Firestore
                                saveUserToFirestore(user)
                                // Send verification email
                                user?.sendEmailVerification()
                                    ?.addOnCompleteListener { verificationTask ->
                                        if (verificationTask.isSuccessful) {
                                            _authState.value = AuthState.VerificationEmailSent
                                        } else {
                                            _authState.value = AuthState.Error("Failed to send verification email")
                                        }
                                    }
                            } else {
                                _authState.value = AuthState.Error("Failed to update display name")
                            }
                        }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }


    fun sendVerificationEmail() {
        val user = auth.currentUser
        if (user != null) {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.value = AuthState.VerificationEmailSent
                    } else {
                        _authState.value = AuthState.Error("Failed to send verification email")
                    }
                }
        } else {
            _authState.value = AuthState.Error("No user logged in")
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                val auth = Firebase.auth
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.postValue(AuthState.ResetPasswordSent)
                    } else {
                        _authState.postValue(
                            AuthState.Error(
                                task.exception?.message ?: "An error occurred"
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                _authState.postValue(AuthState.Error(e.message ?: "An error occurred"))
            }
        }
    }

    private fun saveUserToFirestore(user: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        val userInfo = hashMapOf(
            "uid" to user.uid,
            "name" to user.displayName,
            "email" to user.email
        )

        db.collection("users").document(user.uid)
            .set(userInfo)
            .addOnSuccessListener {
                // Success
            }
            .addOnFailureListener {
                // Handle failure
            }
    }

    // Fetch user profile from Firestore
    fun fetchProfileFromFirestore(userId: String) {
        if (userId.isEmpty()) return
        
        db.collection("profiles").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error listening for profile updates: ", error)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    if (profile != null) {
                        _userProfileState.value = profile
                    }
                } else {
                    // If no profile exists, create one with basic user info
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val newProfile = UserProfile(
                            name = currentUser.displayName ?: "",
                            email = currentUser.email ?: "",
                            about = "",
                            profileImageUrl = null
                        )
                        _userProfileState.value = newProfile
                        // Save the new profile to Firestore
                        saveProfileToFirestore(userId, newProfile.name, newProfile.email, newProfile.about)
                    }
                }
            }
    }

    // Update saveProfileToFirestore to be more robust
    fun saveProfileToFirestore(userId: String, name: String, about: String, email: String, profileImageUri: Uri?) {
        if (userId.isEmpty()) return

        val profileData = hashMapOf(
            "name" to name,
            "about" to about,
            "email" to email,
            "profileImageUrl" to profileImageUri?.toString()
        )

        db.collection("profiles").document(userId)
            .set(profileData)
            .addOnSuccessListener {
                Log.d(TAG, "Profile successfully written!")
                // Update the local state immediately
                _userProfileState.value = UserProfile(
                    name = name,
                    email = email,
                    about = about,
                    profileImageUrl = profileImageUri?.toString()
                )
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }



    // Save profile to Firestore including image upload
    fun saveProfileToFirestore(userId: String, name: String, email: String, about: String) {
        val profileData = hashMapOf(
            "name" to name,
            "email" to email,
            "about" to about
        )

        // Save profile to 'profiles/{userId}' document
        db.collection("profiles").document(userId).set(profileData)
            .addOnSuccessListener {
                Log.d("Firestore", "Profile successfully created/updated!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing profile", e)
            }
    }







    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object VerificationEmailSent : AuthState()
    object ResetPasswordSent : AuthState()
    data class Error(val message: String) : AuthState()
}

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val about: String = "",
    val profileImageUrl: String? = null
)