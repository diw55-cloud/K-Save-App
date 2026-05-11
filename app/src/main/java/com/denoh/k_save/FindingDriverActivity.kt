package com.denoh.k_save

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun FindingDriverScreen(destination: String) {

    var dots by remember { mutableStateOf("") }
    var found by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {

        repeat(6) {
            dots = when (dots) {
                "" -> "."
                "." -> ".."
                else -> ""
            }
            delay(400)
        }

        found = true
    }

    if (!found) {

        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0D0D0D)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Finding driver$dots", color = Color.White)
            Text(destination, color = Color.Gray)
        }

    } else {

        Column(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0D0D0D)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Driver Found 🚗", color = Color.White)
            Text("John - Toyota Axio", color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DriverPreview() {
    FindingDriverScreen(destination = "")
}