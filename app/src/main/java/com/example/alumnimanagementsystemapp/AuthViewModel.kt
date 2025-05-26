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
                        // Check existing role before saving
                        db.collection("users").document(user.uid)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                if (userDoc.exists()) {
                                    // User exists, fetch their profile
                                    fetchProfileFromFirestore(user.uid)
                                } else {
                                    // New user, save to Firestore
                                    saveUserToFirestore(user)
                                }
                                _authState.value = AuthState.Authenticated
                            }
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
        
        // Check if user already exists
        db.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { userDoc ->
                if (!userDoc.exists()) {
                    // Only set role for new users
                    val role = if (user.email?.lowercase() == "owokomoses@gmail.com") "admin" else "student"
                    
                    val userInfo = hashMapOf(
                        "uid" to user.uid,
                        "name" to user.displayName,
                        "email" to user.email,
                        "role" to role
                    )

                    db.collection("users").document(user.uid)
                        .set(userInfo)
                        .addOnSuccessListener {
                            // Success
                            Log.d(TAG, "New user successfully created in Firestore")
                        }
                        .addOnFailureListener {
                            // Handle failure
                            Log.w(TAG, "Error creating new user in Firestore", it)
                        }
                } else {
                    // User exists, no need to modify their role
                    Log.d(TAG, "User already exists in Firestore")
                }
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
                        // Check if user exists in users collection first
                        db.collection("users").document(userId)
                            .get()
                            .addOnSuccessListener { userDoc ->
                                val role = if (userDoc.exists()) {
                                    // Use existing role from users collection
                                    userDoc.getString("role") ?: "student"
                                } else {
                                    // Default to student for new users
                                    "student"
                                }
                                
                                val newProfile = UserProfile(
                                    name = currentUser.displayName ?: "",
                                    email = currentUser.email ?: "",
                                    about = "",
                                    profileImageUrl = null,
                                    role = role
                                )
                                _userProfileState.value = newProfile
                                // Save the new profile to Firestore
                                saveProfileToFirestore(
                                    userId = userId,
                                    name = currentUser.displayName ?: "",
                                    email = currentUser.email ?: "",
                                    about = "",
                                    profileImageUri = null,
                                    role = role
                                )
                            }
                    }
                }
            }
    }

    // Update saveProfileToFirestore to include role
    fun saveProfileToFirestore(
        userId: String,
        name: String,
        email: String,
        about: String,
        profileImageUri: Uri? = null,
        role: String? = null
    ) {
        if (userId.isEmpty()) return

        // Get the current role from Firestore to ensure persistence
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                val currentRole = userDoc.getString("role") ?: "student"
                val finalRole = if (currentRole == "admin") {
                    // If user is already admin, keep admin role
                    "admin"
                } else if (_userProfileState.value.role == "admin") {
                    // If current user is admin, they can set any role
                    role ?: currentRole
                } else {
                    // If current user is not admin, keep their existing role
                    currentRole
                }

                // Save to profiles collection
                val profileData = hashMapOf(
                    "name" to name,
                    "about" to about,
                    "email" to email,
                    "profileImageUrl" to profileImageUri?.toString(),
                    "role" to finalRole
                )

                // Save to users collection
                val userData = hashMapOf(
                    "uid" to userId,
                    "name" to name,
                    "email" to email,
                    "role" to finalRole
                )

                // Save to both collections
                val batch = db.batch()
                
                // Update profiles collection
                val profileRef = db.collection("profiles").document(userId)
                batch.set(profileRef, profileData)
                
                // Update users collection
                val userRef = db.collection("users").document(userId)
                batch.set(userRef, userData)

                // Commit the batch
                batch.commit()
                    .addOnSuccessListener {
                        Log.d(TAG, "Profile and user data successfully written!")
                        // Only update the local state if we're updating the current user's profile
                        if (userId == currentUser?.uid) {
                            _userProfileState.value = UserProfile(
                                name = name,
                                email = email,
                                about = about,
                                profileImageUrl = profileImageUri?.toString(),
                                role = finalRole
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error writing documents", e)
                    }
            }
    }

    fun signout() {
        // Clear the user profile state immediately
        _userProfileState.value = UserProfile()
        // Then sign out
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
    val profileImageUrl: String? = null,
    val role: String = "student" // Default role is student
)