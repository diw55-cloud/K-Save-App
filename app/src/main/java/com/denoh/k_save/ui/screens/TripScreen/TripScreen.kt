package com.denoh.k_save.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denoh.k_save.network.MpesaUtils
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripScreen(
    destination: String = "Westlands",
    phoneNumber: String = "254",
    farePaid: Int = 0,
    goHome: () -> Unit = {}
) {
    val context = LocalContext.current

    // Dynamic Location setup
    val destinationLatLng = remember(destination) {
        when {
            destination.contains("Mombasa", true) -> LatLng(-4.0435, 39.6682)
            destination.contains("Airport", true) -> LatLng(-1.3190, 36.9250)
            else -> LatLng(-1.286389, 36.817223)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destinationLatLng, 14f)
    }

    var progress by remember { mutableFloatStateOf(0f) }
    var timeRemaining by remember { mutableIntStateOf(12) }

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(3000)
            progress += 0.05f
            if (timeRemaining > 1) timeRemaining -= 1
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = LinearEasing), label = "progress"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Trip in Progress 🚗", fontWeight = FontWeight.Black, color = Color.White, fontSize = 18.sp)
                            Text(if (progress >= 1f) "Arrived" else "En route to $destination", color = Color(0xFF00D4FF), fontSize = 11.sp)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Black.copy(alpha = 0.8f))
                )
            },
            bottomBar = {
                Surface(
                    color = Color(0xFF121212),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(androidx.compose.foundation.shape.CircleShape),
                            color = Color(0xFF00FF88),
                            trackColor = Color.White.copy(0.1f),
                        )

                        Spacer(Modifier.height(24.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1541899481282-d53bffe3c35d?auto=format&fit=crop&w=800&q=80",
                                contentDescription = null,
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(10.dp))
                            )
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Premium Ride", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Driver: David", color = Color.Gray, fontSize = 12.sp)
                            }
                            Spacer(Modifier.weight(1f))
                            Text("Ksh $farePaid", color = Color(0xFF00FF88), fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(Modifier.padding(vertical = 20.dp), color = Color.White.copy(0.1f))

                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("Destination", color = Color.Gray, fontSize = 11.sp)
                                Text(destination, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("ETA", color = Color.Gray, fontSize = 11.sp)
                                Text("$timeRemaining mins", color = Color(0xFF00D4FF), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (farePaid > 0) {
                                    MpesaUtils.initiateRefund(phoneNumber, farePaid) { result ->
                                        Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                                        goHome()
                                    }
                                } else { goHome() }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancel & Instant Refund", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = false),
                    properties = MapProperties(mapType = MapType.NORMAL)
                ) {
                    val markerState = rememberMarkerState(position = destinationLatLng)
                    Marker(
                        state = markerState,
                        title = "Your Position",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripPreview() {
    TripScreen(farePaid = 250)
}
