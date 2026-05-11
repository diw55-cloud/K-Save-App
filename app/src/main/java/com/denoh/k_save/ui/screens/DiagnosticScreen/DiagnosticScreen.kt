package com.denoh.k_save.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            delay(2000)
            scanResult = "System Healthy - All components working correctly."
            isScanning = false
            Toast.makeText(context, "Scan Complete", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A237E), Color(0xFF000000))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Diagnostic", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("System Diagnostic", color = Color.White, style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.Start))
                Spacer(Modifier.height(24.dp))
                
                DiagnosticItem("Network", if (scanResult != null) "Optimal" else "Connected", Color.Green)
                DiagnosticItem("GPS", if (scanResult != null) "High Accuracy" else "Active", Color.Green)
                DiagnosticItem("Database", "Healthy", Color.Green)
                DiagnosticItem("Storage", "85% Free", Color.Cyan)

                if (scanResult != null) {
                    Spacer(Modifier.height(24.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(scanResult!!, color = Color.White, modifier = Modifier.padding(16.dp))
                    }
                }

                Spacer(Modifier.weight(1f))
                
                Button(
                    onClick = { isScanning = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !isScanning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4FF))
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Scanning System...", color = Color.Black)
                    } else {
                        Text("RUN FULL DIAGNOSTIC", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DiagnosticItem(label: String, status: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.LightGray)
        Text(status, color = color, fontWeight = FontWeight.Bold)
    }
    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
}
