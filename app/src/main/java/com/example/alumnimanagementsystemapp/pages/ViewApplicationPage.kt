package com.example.alumnimanagementsystemapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
fun ViewApplicationPage(
    navController: NavController,
    applicationId: String,
    authViewModel: AuthViewModel
) {
    var application by remember { mutableStateOf<JobApplication?>(null) }
    val db = FirebaseFirestore.getInstance()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    // Fetch application details
    LaunchedEffect(applicationId) {
        db.collection("jobApplications")
            .document(applicationId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data
                    if (data != null) {
                        application = JobApplication(
                            id = document.id,
                            jobId = data["jobId"] as? String ?: "",
                            applicantId = data["applicantId"] as? String ?: "",
                            applicantName = data["applicantName"] as? String ?: "",
                            applicantEmail = data["applicantEmail"] as? String ?: "",
                            coverLetter = data["coverLetter"] as? String ?: "",
                            resumeUrl = data["resumeUrl"] as? String ?: "",
                            status = data["status"] as? String ?: "Pending",
                            appliedDate = DateConverter.toDate(data["appliedDate"] as? com.google.firebase.Timestamp) ?: Date()
                        )
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
                .verticalScroll(rememberScrollState())
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Red
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Application Details",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }

            // Application Content
            application?.let { app ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                            text = "Applicant Information",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Name: ${app.applicantName}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Email: ${app.applicantEmail}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Cover Letter
                        Text(
                            text = "Cover Letter",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = app.coverLetter,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Resume
                        Text(
                            text = "Resume",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = app.resumeUrl,
                            fontSize = 16.sp,
                            color = Color.Blue
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Application Status and Date
                        Text(
                            text = "Application Status",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Status: ${app.status}",
                            fontSize = 16.sp,
                            color = when (app.status) {
                                "Accepted" -> Color.Green
                                "Declined" -> Color.Red
                                else -> Color.Gray
                            },
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Applied on: ${dateFormat.format(app.appliedDate)}",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
} 