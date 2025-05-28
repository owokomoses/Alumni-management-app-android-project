package com.example.alumnimanagementsystemapp.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import java.text.SimpleDateFormat
import java.util.*

data class JobPost(
    val id: String = "",
    val title: String = "",
    val company: String = "",
    val location: String = "",
    val description: String = "",
    val requirements: List<String> = emptyList(),
    val salary: String = "",
    val type: String = "", // Full-time, Part-time, Contract, etc.
    val postedBy: String = "",
    val postedDate: Date = Date(),
    val deadline: Date? = null
)

// Custom converter for Date type
class DateConverter {
    companion object {
        fun toDate(timestamp: com.google.firebase.Timestamp?): Date? {
            return timestamp?.toDate()
        }

        fun toTimestamp(date: Date?): com.google.firebase.Timestamp? {
            return date?.let { com.google.firebase.Timestamp(it) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Posts(modifier: Modifier, navController: NavController, authViewModel: AuthViewModel) {
    var showNewPostDialog by remember { mutableStateOf(false) }
    var showEditPostDialog by remember { mutableStateOf(false) }
    var selectedJobPost by remember { mutableStateOf<JobPost?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var jobPosts by remember { mutableStateOf<List<JobPost>>(emptyList()) }
    val currentUser = authViewModel.currentUser
    val db = FirebaseFirestore.getInstance()

    // Fetch job posts
    LaunchedEffect(Unit) {
        db.collection("jobPosts")
            .orderBy("postedDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                
                if (snapshot != null) {
                    jobPosts = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data ?: return@mapNotNull null
                            JobPost(
                                id = doc.id,
                                title = data["title"] as? String ?: "",
                                company = data["company"] as? String ?: "",
                                location = data["location"] as? String ?: "",
                                description = data["description"] as? String ?: "",
                                requirements = (data["requirements"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                                salary = data["salary"] as? String ?: "",
                                type = data["type"] as? String ?: "",
                                postedBy = data["postedBy"] as? String ?: "",
                                postedDate = DateConverter.toDate(data["postedDate"] as? com.google.firebase.Timestamp) ?: Date(),
                                deadline = DateConverter.toDate(data["deadline"] as? com.google.firebase.Timestamp)
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
            // Header with New Post button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Job Posts",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                
                FloatingActionButton(
                    onClick = { showNewPostDialog = true },
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Post")
                }
            }

            // Job Posts List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(jobPosts) { jobPost ->
                    JobPostCard(
                        jobPost = jobPost,
                        onEdit = { post ->
                            selectedJobPost = post
                            showEditPostDialog = true
                        },
                        onDelete = { post ->
                            selectedJobPost = post
                            showDeleteConfirmation = true
                        },
                        currentUserEmail = currentUser?.email
                    )
                }
            }
        }
    }

    // New Post Dialog
    if (showNewPostDialog) {
        NewJobPostDialog(
            onDismiss = { showNewPostDialog = false },
            onPost = { title, company, location, description, requirements, salary, type, deadline ->
                val newPost = hashMapOf(
                    "title" to title,
                    "company" to company,
                    "location" to location,
                    "description" to description,
                    "requirements" to requirements.split(",").map { it.trim() },
                    "salary" to salary,
                    "type" to type,
                    "postedBy" to (authViewModel.currentUser?.email ?: ""),
                    "postedDate" to DateConverter.toTimestamp(Date()),
                    "deadline" to DateConverter.toTimestamp(deadline)
                )
                
                db.collection("jobPosts").add(newPost)
                showNewPostDialog = false
            }
        )
    }

    // Edit Post Dialog
    if (showEditPostDialog && selectedJobPost != null) {
        NewJobPostDialog(
            onDismiss = { 
                showEditPostDialog = false
                selectedJobPost = null
            },
            onPost = { title, company, location, description, requirements, salary, type, deadline ->
                val jobPost = hashMapOf(
                    "title" to title,
                    "company" to company,
                    "location" to location,
                    "description" to description,
                    "requirements" to requirements.split(",").map { it.trim() },
                    "salary" to salary,
                    "type" to type,
                    "deadline" to DateConverter.toTimestamp(deadline)
                )

                // Use the document ID directly
                db.collection("jobPosts")
                    .document(selectedJobPost?.id ?: "")
                    .update(jobPost)
                    .addOnSuccessListener {
                        showEditPostDialog = false
                        selectedJobPost = null
                    }
                    .addOnFailureListener { e ->
                        // Handle error
                    }
            },
            initialTitle = selectedJobPost?.title ?: "",
            initialCompany = selectedJobPost?.company ?: "",
            initialLocation = selectedJobPost?.location ?: "",
            initialDescription = selectedJobPost?.description ?: "",
            initialRequirements = selectedJobPost?.requirements?.joinToString(", ") ?: "",
            initialSalary = selectedJobPost?.salary ?: "",
            initialType = selectedJobPost?.type ?: "",
            initialDeadline = selectedJobPost?.deadline
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation && selectedJobPost != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                selectedJobPost = null
            },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this job post?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Use the document ID directly
                        db.collection("jobPosts")
                            .document(selectedJobPost?.id ?: "")
                            .delete()
                            .addOnSuccessListener {
                                showDeleteConfirmation = false
                                selectedJobPost = null
                            }
                            .addOnFailureListener { e ->
                                // Handle error
                            }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        selectedJobPost = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun JobPostCard(
    jobPost: JobPost,
    modifier: Modifier = Modifier,
    onEdit: (JobPost) -> Unit,
    onDelete: (JobPost) -> Unit,
    currentUserEmail: String?
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    text = jobPost.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                
                // Only show edit/delete buttons if the current user is the post creator
                if (currentUserEmail == jobPost.postedBy) {
                    Row {
                        IconButton(onClick = { onEdit(jobPost) }) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = "Edit Post",
                                tint = Color.Red
                            )
                        }
                        IconButton(onClick = { onDelete(jobPost) }) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete Post",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = jobPost.company,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = jobPost.location,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = jobPost.type,
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = jobPost.description,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Requirements:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = jobPost.requirements.joinToString(", "),
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Salary: ${jobPost.salary}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Posted: ${dateFormat.format(jobPost.postedDate)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            if (jobPost.deadline != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Application Deadline: ${dateFormat.format(jobPost.deadline)}",
                    fontSize = 14.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJobPostDialog(
    onDismiss: () -> Unit,
    onPost: (
        title: String,
        company: String,
        location: String,
        description: String,
        requirements: String,
        salary: String,
        type: String,
        deadline: Date?
    ) -> Unit,
    initialTitle: String = "",
    initialCompany: String = "",
    initialLocation: String = "",
    initialDescription: String = "",
    initialRequirements: String = "",
    initialSalary: String = "",
    initialType: String = "Full-time",
    initialDeadline: Date? = null
) {
    var title by remember { mutableStateOf(initialTitle) }
    var company by remember { mutableStateOf(initialCompany) }
    var location by remember { mutableStateOf(initialLocation) }
    var description by remember { mutableStateOf(initialDescription) }
    var requirements by remember { mutableStateOf(initialRequirements) }
    var salary by remember { mutableStateOf(initialSalary) }
    var type by remember { mutableStateOf(initialType) }
    var deadline by remember { mutableStateOf(initialDeadline) }
    var expanded by remember { mutableStateOf(false) }
    
    val jobTypes = listOf("Full-time", "Part-time", "Contract", "Internship", "Remote")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTitle.isEmpty()) "Create Job Post" else "Edit Job Post", color = Color.Red) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Job Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Company") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                
                OutlinedTextField(
                    value = requirements,
                    onValueChange = { requirements = it },
                    label = { Text("Requirements (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                
                OutlinedTextField(
                    value = salary,
                    onValueChange = { salary = it },
                    label = { Text("Salary") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = type,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Job Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        jobTypes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    type = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && company.isNotBlank() && location.isNotBlank() && 
                        description.isNotBlank() && requirements.isNotBlank() && salary.isNotBlank()) {
                        onPost(title, company, location, description, requirements, salary, type, deadline)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text(if (initialTitle.isEmpty()) "Post" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Red)
            }
        }
    )
} 