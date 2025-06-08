package com.example.alumnimanagementsystemapp.utils

import com.google.firebase.Timestamp
import java.util.*

class DateConverter {
    companion object {
        fun toDate(timestamp: Timestamp?): Date? {
            return timestamp?.toDate()
        }

        fun toTimestamp(date: Date?): Timestamp? {
            return date?.let { Timestamp(it) }
        }
    }
} 