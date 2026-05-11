package com.denoh.k_save.models

data class RideOption(
    val id: Int,
    val name: String,
    val route: String,
    val traffic: String,
    val rainChance: Int,
    val baseFare: Int,
    val category: String,
    val imageUrl: String // Added for images feature
)
