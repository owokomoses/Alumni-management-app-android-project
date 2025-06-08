package com.example.alumnimanagementsystemapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.alumnimanagementsystemapp.AuthViewModel
import com.example.alumnimanagementsystemapp.Screen
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsPage(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var applications by remember { mutableStateOf<List<JobApplication>>(emptyList()) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedApplication by remember { mutableStateOf<JobApplication?>(null) }
    var selectedStatus by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Fetch applications
    LaunchedEffect(Unit) {
        db.collection("jobApplications")
            .orderBy("appliedDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                if (snapshot != null) {
                    applications = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data ?: return@mapNotNull null
                            JobApplication(
                                id = doc.id,
                                jobId = data["jobId"] as? String ?: "",
                                applicantId = data["applicantId"] as? String ?: "",
                                applicantName = data["applicantName"] as? String ?: "",
                                applicantEmail = data["applicantEmail"] as? String ?: "",
                                coverLetter = data["coverLetter"] as? String ?: "",
                                resumeUrl = data["resumeUrl"] as? String ?: "",
                                status = data["status"] as? String ?: "Pending",
                                appliedDate = DateConverter.toDate(data["appliedDate"] as? com.google.firebase.Timestamp) ?: Date()
                            )
                        } catch (e: Exception) {
                            null
                        }
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
                text = "Job Applications",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )

            // Applications List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(applications) { application ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Applicant Info
                            Text(
                                text = application.applicantName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Text(
                                text = application.applicantEmail,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Cover Letter Preview
                            Text(
                                text = "Cover Letter",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Text(
                                text = application.coverLetter.take(100) + if (application.coverLetter.length > 100) "..." else "",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Resume Link
                            Text(
                                text = "Resume",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                            Text(
                                text = application.resumeUrl,
                                fontSize = 14.sp,
                                color = Color.Blue
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Application Date and Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Applied: ${dateFormat.format(application.appliedDate)}",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = application.status,
                                    fontSize = 14.sp,
                                    color = when (application.status) {
                                        "Accepted" -> Color.Green
                                        "Declined" -> Color.Red
                                        else -> Color.Gray
                                    },
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Delete Button
                                Button(
                                    onClick = {
                                        selectedApplication = application
                                        showDeleteDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Delete")
                                }

                                // Status Update Buttons
                                if (application.status == "Pending") {
                                    Row {
                                        Button(
                                            onClick = {
                                                selectedApplication = application
                                                selectedStatus = "Accepted"
                                                showStatusDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Green
                                            )
                                        ) {
                                            Text("Accept")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = {
                                                selectedApplication = application
                                                selectedStatus = "Declined"
                                                showStatusDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red
                                            )
                                        ) {
                                            Text("Decline")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Status Update Dialog
    if (showStatusDialog && selectedApplication != null) {
        AlertDialog(
            onDismissRequest = {
                showStatusDialog = false
                selectedApplication = null
            },
            title = {
                Text(
                    "Update Application Status",
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to ${selectedStatus.lowercase()} this application?",
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedApplication?.let { application ->
                            db.collection("jobApplications")
                                .document(application.id)
                                .update("status", selectedStatus)
                                .addOnSuccessListener {
                                    showStatusDialog = false
                                    selectedApplication = null
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedStatus == "Accepted") Color.Green else Color.Red
                    )
                ) {
                    Text(selectedStatus)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showStatusDialog = false
                        selectedApplication = null
                    }
                ) {
                    Text("Cancel", color = Color.Red)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Red,
            textContentColor = Color.Gray
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedApplication != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedApplication = null
            },
            title = {
                Text(
                    "Delete Application",
                    color = Color.Red,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete this application?",
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedApplication?.let { application ->
                            db.collection("jobApplications")
                                .document(application.id)
                                .delete()
                                .addOnSuccessListener {
                                    showDeleteDialog = false
                                    selectedApplication = null
                                }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedApplication = null
                    }
                ) {
                    Text("Cancel", color = Color.Red)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Red,
            textContentColor = Color.Gray
        )
    }
} 