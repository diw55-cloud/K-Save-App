package com.denoh.k_save.models

data class Ride(
    val name: String,
    val type: String,
    val route: String,
    val price: Int,
    val isLuxury: Boolean = false,
    val etaMinutes: Int = 5
)