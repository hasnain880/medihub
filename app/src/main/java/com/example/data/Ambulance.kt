package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ambulances")
data class Ambulance(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val codeName: String,
    val driverName: String,
    val distance: Float, // in km
    val etaMinutes: Int, // in minutes
    val latitude: Float,
    val longitude: Float,
    val isAvailable: Boolean = true,
    val isDispatched: Boolean = false,
    val targetLatitude: Float = 0f,
    val targetLongitude: Float = 0f
)
