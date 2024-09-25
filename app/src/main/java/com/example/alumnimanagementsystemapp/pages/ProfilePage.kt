package com.example.alumnimanagementsystemapp.pages


import androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthViewModel

@OptIn(androidx.benchmark.perfetto.ExperimentalPerfettoTraceProcessorApi::class)
@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showImagePicker by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }

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
            Image(
                painter = painterResource(id = com.example.alumnimanagementsystemapp.R.drawable.profile), // replace with actual image
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Icon(
                imageVector = Icons.Rounded.CameraAlt, // camera icon
                contentDescription = "Change Profile Image",
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape) // Makes the background rounded
                    .background(Color.Gray)
                    .clickable { showImagePicker = true },
                tint = Color.Green
            )
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

        // About section
        EditableTextField(
            label = "About",
            value = about,
            onEditClick = { showEditDialog = true },
            icon = Icons.Default.Info// Add appropriate icon for about
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone section (can be static or editable)
        Text(
            text = "+254 773 379546",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }

    // Edit dialog
    if (showEditDialog) {
        EditBottomSheet(
            initialName = name,
            initialAbout = about,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newAbout ->
                name = newName
                about = newAbout
                showEditDialog = false
            }
        )
    }

    // Show image picker (Dialog/BottomSheet for changing profile image)
    if (showImagePicker) {
        ImagePickerDialog(
            onDismiss = { showImagePicker = false },
            onImageSelected = { /* Handle image selection */ }
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
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var newName by remember { mutableStateOf(initialName) }
    var newAbout by remember { mutableStateOf(initialAbout) }

    BottomSheetScaffold(
        sheetContent = {
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
                            imageVector = Icons.Rounded.Person, // Add icon here
                            contentDescription = "Name Icon"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = newAbout,
                    onValueChange = { newAbout = it },
                    label = { Text("About") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Info, // Add icon here
                            contentDescription = "About Icon"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onSave(newName, newAbout) }) {
                    Text("Save")
                }
            }
        },
        scaffoldState = rememberBottomSheetScaffoldState(),
        sheetPeekHeight = 0.dp
    ) {
        // Content behind the bottom sheet
    }
}
@Composable
fun ImagePickerDialog(onDismiss: () -> Unit, onImageSelected: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Pick an image", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Placeholder for image picker functionality
            Button(onClick = {
                onImageSelected("path/to/new/image")
                onDismiss()
            }) {
                Text("Select Image")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    }
}




