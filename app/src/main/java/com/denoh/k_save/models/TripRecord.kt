package com.denoh.k_save.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_records")
data class TripRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destination: String,
    val fare: Int,
    val date: Long,
    val rating: Float = 0f,
    val driverRating: Float = 0f,
    val status: String = "Completed"
)
