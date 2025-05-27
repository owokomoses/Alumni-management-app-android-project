package com.example.alumnimanagementsystemapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.Screen
import com.google.firebase.firestore.FirebaseFirestore

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "student"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Users(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val currentUser = authViewModel.currentUser
    val currentUserRole = authViewModel.userProfileState.collectAsState().value.role
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var showDeleteDialog by remember { mutableStateOf<User?>(null) }
    var showRoleDialog by remember { mutableStateOf<User?>(null) }
    val db = FirebaseFirestore.getInstance()

    // Fetch users
    LaunchedEffect(Unit) {
        db.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    users = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(User::class.java)?.copy(uid = doc.id)
                    }
                }
            }
    }

    Screen(navController = navController, authViewModel = authViewModel) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            // Header
            Text(
                text = "Users",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )

            // Users List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    UserCard(
                        user = user,
                        isAdmin = currentUserRole == "admin",
                        onDelete = { showDeleteDialog = user },
                        onRoleChange = { showRoleDialog = user }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete ${user.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        db.collection("users").document(user.uid).delete()
                        db.collection("profiles").document(user.uid).delete()
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Role Selection Dialog
    showRoleDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showRoleDialog = null },
            title = { Text("Change Role") },
            text = {
                Column {
                    Text("Select role for ${user.name}:")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                updateUserRole(user.uid, "admin")
                                showRoleDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.role == "admin") Color.Red else Color.Gray
                            )
                        ) {
                            Text("Admin")
                        }
                        Button(
                            onClick = {
                                updateUserRole(user.uid, "student")
                                showRoleDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.role == "student") Color.Red else Color.Gray
                            )
                        ) {
                            Text("Student")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoleDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserCard(
    user: User,
    isAdmin: Boolean,
    onDelete: () -> Unit,
    onRoleChange: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Name and Delete Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (isAdmin) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete User",
                            tint = Color.Red
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Email,
                    contentDescription = "Email",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = user.email,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Role
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Role",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (user.role == "admin") Color.Red.copy(alpha = 0.1f)
                            else Color.Gray.copy(alpha = 0.1f)
                        )
                        .then(
                            if (isAdmin) {
                                Modifier.clickable { onRoleChange() }
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.role.uppercase(),
                            fontSize = 14.sp,
                            color = if (user.role == "admin") Color.Red else Color.Gray
                        )
                        if (isAdmin) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Change Role",
                                tint = if (user.role == "admin") Color.Red else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun updateUserRole(userId: String, newRole: String) {
    val db = FirebaseFirestore.getInstance()
    
    // Only update in users collection
    val userRef = db.collection("users").document(userId)
    userRef.update("role", newRole)
}