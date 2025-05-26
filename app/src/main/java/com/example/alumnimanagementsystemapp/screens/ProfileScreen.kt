package com.example.alumnimanagementsystemapp.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.R
import com.example.alumnimanagementsystemapp.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val currentUser = authViewModel.currentUser
    val userId = currentUser?.uid ?: ""
    val userProfile by authViewModel.userProfileState.collectAsState()
    val context = LocalContext.current

    var showEditDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    // Only fetch if we don't have the data yet
    LaunchedEffect(userId) {
        if (userId.isNotEmpty() && userProfile.name.isEmpty() && userProfile.email.isEmpty()) {
            authViewModel.fetchProfileFromFirestore(userId)
        }
    }

    // Set up the image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImageUri = uri
    }

    Screen(
        navController = navController,
        authViewModel = authViewModel
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image with Camera Icon
                    Box(
                        modifier = Modifier
                            .size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Main profile image/letter circle
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (userProfile.profileImageUrl != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(userProfile.profileImageUrl),
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = userProfile.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                            }
                        }

                        // Camera button at bottom right
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .clickable { launcher.launch("image/*") }
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CameraAlt,
                                contentDescription = "Change Profile Image",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name
                    Text(
                        text = userProfile.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )

                    // Role Badge
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (userProfile.role == "admin") Color.Red.copy(alpha = 0.1f)
                                else Color.Gray.copy(alpha = 0.1f)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .then(
                                if (userProfile.role == "admin") {
                                    Modifier.clickable { showRoleDialog = true }
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = userProfile.role.uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (userProfile.role == "admin") Color.Red else Color.Gray
                            )
                            if (userProfile.role == "admin") {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = "Edit Role",
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Profile Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // About Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "About",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            IconButton(
                                onClick = { showEditDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit About",
                                    tint = Color.Red
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = userProfile.about.ifEmpty { "Add something about yourself..." },
                            fontSize = 16.sp,
                            color = Color.Gray,
                            lineHeight = 24.sp
                        )
                    }
                }

                // Contact Information
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Contact Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Email,
                                contentDescription = "Email",
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = currentUser?.email ?: "",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    // Edit Dialog - Only for About section
    if (showEditDialog) {
        EditAboutDialog(
            initialAbout = userProfile.about,
            onDismiss = { showEditDialog = false },
            onSave = { newAbout ->
                authViewModel.saveProfileToFirestore(
                    userId = userId,
                    name = currentUser?.displayName ?: userProfile.name,
                    about = newAbout,
                    email = currentUser?.email ?: userProfile.email,
                    profileImageUri = profileImageUri
                )
                showEditDialog = false
            }
        )
    }

    // Role Selection Dialog - Only show for admin users
    if (showRoleDialog && userProfile.role == "admin") {
        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    "Select Role",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Admin Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                authViewModel.saveProfileToFirestore(
                                    userId = userId,
                                    name = userProfile.name,
                                    email = userProfile.email,
                                    about = userProfile.about,
                                    profileImageUri = profileImageUri,
                                    role = "admin"
                                )
                                showRoleDialog = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = userProfile.role == "admin",
                            onClick = {
                                authViewModel.saveProfileToFirestore(
                                    userId = userId,
                                    name = userProfile.name,
                                    email = userProfile.email,
                                    about = userProfile.about,
                                    profileImageUri = profileImageUri,
                                    role = "admin"
                                )
                                showRoleDialog = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Red,
                                unselectedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin",
                            fontSize = 16.sp,
                            color = if (userProfile.role == "admin") Color.Red else Color.Gray
                        )
                    }

                    Divider(
                        color = Color.Gray.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Student Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                authViewModel.saveProfileToFirestore(
                                    userId = userId,
                                    name = userProfile.name,
                                    email = userProfile.email,
                                    about = userProfile.about,
                                    profileImageUri = profileImageUri,
                                    role = "student"
                                )
                                showRoleDialog = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = userProfile.role == "student",
                            onClick = {
                                authViewModel.saveProfileToFirestore(
                                    userId = userId,
                                    name = userProfile.name,
                                    email = userProfile.email,
                                    about = userProfile.about,
                                    profileImageUri = profileImageUri,
                                    role = "student"
                                )
                                showRoleDialog = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Red,
                                unselectedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Student",
                            fontSize = 16.sp,
                            color = if (userProfile.role == "student") Color.Red else Color.Gray
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showRoleDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        "Cancel",
                        fontSize = 16.sp
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAboutDialog(
    initialAbout: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var newAbout by remember { mutableStateOf(initialAbout) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit About",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = newAbout,
                    onValueChange = { newAbout = it },
                    label = { Text("About") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = "About Icon",
                            tint = Color.Red
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(newAbout.ifEmpty { "Not provided" })
                }
            ) {
                Text(
                    "Save",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = Color.Gray
                )
            }
        }
    )
} 