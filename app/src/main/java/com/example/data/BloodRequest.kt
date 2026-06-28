package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_requests")
data class BloodRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bloodType: String,
    val locationName: String,
    val latitude: Float,
    val longitude: Float,
    val urgency: String, // "Low", "Medium", "Urgent"
    val status: String = "Pending", // "Pending", "Matched", "Completed"
    val timestamp: Long = System.currentTimeMillis()
)
