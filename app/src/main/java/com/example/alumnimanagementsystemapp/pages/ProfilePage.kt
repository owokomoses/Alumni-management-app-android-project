package com.example.alumnimanagementsystemapp.pages


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.alumnimanagementsystemapp.AuthViewModel
import androidx.compose.runtime.collectAsState


@OptIn(androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi::class)
@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    val currentUser = authViewModel.currentUser
    val userId = currentUser?.uid ?: ""

    // Observe the user profile state from the AuthViewModel
    val userProfile by authViewModel.userProfileState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(userProfile.name ?: "") }
    var email by remember { mutableStateOf(userProfile.email ?: "") }
    var about by remember { mutableStateOf(userProfile.about ?: "") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    // Fetch profile data from Firestore on login
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            authViewModel.fetchProfileFromFirestore(userId) // Trigger the Firestore fetch from AuthViewModel
        }
    }


    // Set up the image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        profileImageUri = uri // Save the selected image URI
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile image section with camera icon
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            // Display the saved image URL or the selected image
            if (profileImageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUrl),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                profileImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } ?: Image(
                    painter = painterResource(id = com.example.alumnimanagementsystemapp.R.drawable.profile),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            // Camera icon with rounded background
            Box(
                modifier = Modifier
                    .size(40.dp) // Adjust this size for the background
                    .clip(CircleShape) // Makes the background rounded
                    .background(Color.Gray) // Sets the background color to grey
                    .clickable { launcher.launch("image/*") }, // Clickable action on the box
                contentAlignment = Alignment.Center // Center the icon within the box
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt, // Camera icon
                    contentDescription = "Change Profile Image",
                    modifier = Modifier.size(24.dp), // Size of the icon
                    tint = Color.Green // Icon color
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name section
        EditableTextField(
            label = "Name",
            value = name,
            onEditClick = { showEditDialog = true },
            icon = Icons.Default.Person // Add appropriate icon for name
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email section (moved after the name)
        EditableTextField(
            label = "Email",
            value = email, // Display the email
            onEditClick = { showEditDialog = true },
            icon = Icons.Default.Email // Use appropriate icon for email
        )

        Spacer(modifier = Modifier.height(16.dp))

        // About section
        EditableTextField(
            label = "About",
            value = about,
            onEditClick = { showEditDialog = true },
            icon = Icons.Default.Info// Add appropriate icon for about
        )

    }

    // Edit dialog
    if (showEditDialog) {
        EditBottomSheet(
            initialName = userProfile.name,
            initialAbout = userProfile.about,
            initialEmail = userProfile.email,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newAbout, newEmail ->
                authViewModel.saveProfileToFirestore(userId, newName, newAbout, newEmail, profileImageUri)
                // Update the UI state with the new values after saving
                name = newName
                about = newAbout
                email = newEmail
                showEditDialog = false
            }
        )
    }
}


@Composable
fun EditableTextField(label: String, value: String, onEditClick: () -> Unit, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon, // icon before text
                contentDescription = "$label Icon",
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(label, fontSize = 14.sp, color = Color.Gray)
                Text(
                    if (value.isEmpty()) "Not provided" else value, // Show "Not provided" if empty
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Edit, // Ensure that R.drawable.icon exists
            contentDescription = "Edit $label",
            modifier = Modifier
                .size(24.dp)
                .clickable { onEditClick() },
            tint = Color.Green
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBottomSheet(
    initialName: String,
    initialAbout: String,
    initialEmail: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var newName by remember { mutableStateOf(initialName) }
    var newAbout by remember { mutableStateOf(initialAbout) }
    var newEmail by remember { mutableStateOf(initialEmail) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Edit Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Person,
                        contentDescription = "Name Icon"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = "Email Icon"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newAbout,
                onValueChange = { newAbout = it },
                label = { Text("About") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = "About Icon"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Red,
                    focusedLabelColor = Color.Red,
                    cursorColor = Color.Red,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSave(newName.ifEmpty { "Not provided" }, newAbout.ifEmpty { "Not provided" }, newEmail.ifEmpty { "Not provided" }) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}








