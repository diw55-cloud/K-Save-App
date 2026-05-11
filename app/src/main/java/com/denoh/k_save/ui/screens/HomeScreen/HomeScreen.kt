package com.denoh.k_save.screens

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denoh.k_save.network.LocationHelper
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLocationSelected: (String) -> Unit = {},
    onServiceSelected: (String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onDiagnosticClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentLocationName by remember { mutableStateOf("Locating...") }
    val nairobi = LatLng(-1.286389, 36.817223)
    var showSheet by remember { mutableStateOf(false) }
    
    var isOnline by remember { mutableStateOf(checkInternet(context)) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(nairobi, 13f)
    }

    val nearbyVehicles = remember {
        listOf(
            LatLng(-1.2840, 36.8200) to "🚗 Bolt Car",
            LatLng(-1.2880, 36.8150) to "🏍️ Boda",
            LatLng(-1.2900, 36.8250) to "🚗 Bolt Car",
            LatLng(-1.2750, 36.8100) to "🚲 Electric Bike",
            LatLng(-1.3200, 36.9300) to "✈️ Plane: KQ 102 (Taking off)",
            LatLng(-1.3320, 36.9250) to "✈️ Plane: Flight 540 (Ready)",
            LatLng(-1.3190, 36.8600) to "🚆 SGR Train (Arriving)"
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            LocationHelper.getCurrentLocation(context) { address ->
                currentLocationName = address
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        isOnline = checkInternet(context)
    }

    val destinations = listOf(
        "Westlands", "CBD", "Kilimani", "Karen", "Lavington", 
        "Kileleshwa", "Langata", "South C", "South B", "Ngong",
        "Rongai", "Kasarani", "Donholm", "Eastleigh", "Parklands",
        "Gigiri", "Runda", "Muthaiga", "Upper Hill", "Thika Town",
        "Juja", "Ruiru", "Githurai 44", "Githurai 45", "Kikuyu Town",
        "Limuru Town", "Banana", "Ruaka Town", "Muchatha Town",
        "Wangige", "Lower Kabete", "Rosslyn", "Garden Estate",
        "Two Rivers Mall", "Village Market", "Garden City Mall", "Sarit Centre", "Junction Mall",
        "The Hub Karen", "Nairobi Hospital", "Aga Khan Hospital", "Kenyatta Hospital",
        "JKIA Airport", "Wilson Airport", "SGR Terminus Syokimau", "Mombasa"
    )

    val services = listOf(
        ServiceItem("Ride", "https://mobile-content.uber.com/launch-experience/ride.png", Color.White),
        ServiceItem("Package", "https://mobile-content.uber.com/launch-experience/package.png", Color.White),
        ServiceItem("Rentals", "https://mobile-content.uber.com/launch-experience/rentals.png", Color.White),
        ServiceItem("Airport", "https://mobile-content.uber.com/launch-experience/reserve.png", Color.White)
    )

    Box(modifier = Modifier
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
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Hello, Denis", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("Premium Member", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = onDiagnosticClick) {
                            Icon(Icons.Default.Build, "Diagnostic", tint = Color.White)
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, "Settings", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)) {
                    
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = true),
                        properties = MapProperties(isMyLocationEnabled = true)
                    ) {
                        nearbyVehicles.forEach { (pos, label) ->
                            Marker(
                                state = MarkerState(position = pos),
                                title = label,
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    when {
                                        label.contains("🚗") -> BitmapDescriptorFactory.HUE_AZURE
                                        label.contains("✈️") -> BitmapDescriptorFactory.HUE_VIOLET
                                        label.contains("🚆") -> BitmapDescriptorFactory.HUE_ORANGE
                                        else -> BitmapDescriptorFactory.HUE_GREEN
                                    }
                                )
                            )
                        }
                    }

                    if (!isOnline) {
                        Surface(
                            color = Color.Black.copy(0.7f),
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Warning, "Offline", tint = Color.Yellow, modifier = Modifier.size(40.dp))
                                Text("Offline Mode", color = Color.White, fontWeight = FontWeight.Bold)
                                Text("Showing cached map and local drivers", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clickable { showSheet = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Row(
                        Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📍", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Where to?",
                                color = Color.Black,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Current: $currentLocationName",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Services Near You",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(services) { service ->
                        ServiceCard(service) {
                            onServiceSelected(service.name)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF212121))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Airport Express ✈️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Planes departing JKIA soon", color = Color.LightGray, fontSize = 12.sp)
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { onServiceSelected("Airport") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4FF)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Book Ticket", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?auto=format&fit=crop&w=800&q=80",
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDiagnosticClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Icon(Icons.Default.Build, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Diagnostic")
                    }
                    Button(
                        onClick = onSettingsClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Icon(Icons.Default.Settings, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Settings")
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }

        if (showSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { showSheet = false }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(600.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            "Select Destination",
                            color = Color.Black,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(destinations) { place ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            showSheet = false
                                            onLocationSelected(place)
                                        },
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = when {
                                                place.contains("Mall") -> "🛍️"
                                                place.contains("Hospital") -> "🏥"
                                                place.contains("Airport") -> "✈️"
                                                else -> "🏢"
                                            },
                                            fontSize = 20.sp
                                        )
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text(text = place, color = Color.Black, fontWeight = FontWeight.Bold)
                                            Text(text = "Rides heading to $place", color = Color.Gray, fontSize = 11.sp)
                                        }
                                        Spacer(Modifier.weight(1f))
                                        Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun checkInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}

data class ServiceItem(val name: String, val imageRes: String, val bgColor: Color)

@Composable
fun ServiceCard(service: ServiceItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp).clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(service.bgColor, RoundedCornerShape(20.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = service.imageRes,
                contentDescription = service.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(service.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HomeScreen()
}
