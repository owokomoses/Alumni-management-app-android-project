package com.example.alumnimanagementsystemapp.models

import java.util.*

data class JobApplication(
    val id: String = "",
    val jobId: String = "",
    val applicantId: String = "",
    val applicantName: String = "",
    val applicantEmail: String = "",
    val coverLetter: String = "",
    val resumeUrl: String = "",
    val status: String = "Pending",
    val appliedDate: Date = Date()
) 