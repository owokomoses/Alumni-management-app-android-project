package com.example.alumnimanagementsystemapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.Screen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// ViewModel to handle fetching users from Firestore
class UsersViewModel : ViewModel() {

    val users = liveData {
        val usersList = fetchUsers()
        emit(usersList)
    }

    // Function to fetch users from Firestore
    private suspend fun fetchUsers(): List<User> {
        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("users")
        val usersSnapshot = usersCollection.get().await()
        return usersSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
    }
}

data class User(
    val name: String = "",
    val email: String = ""
)

@Composable
fun Users(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var usersList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Fetch users from Firestore
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                usersList = result.documents.map { it.data ?: emptyMap() }
            }
    }

    Screen(
        navController = navController,
        authViewModel = authViewModel
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(usersList) { user ->
                Text(text = "User: ${user["name"]} (${user["email"]})", modifier = Modifier.padding(16.dp))
            }
        }
    }
}