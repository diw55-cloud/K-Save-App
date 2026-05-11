package com.denoh.k_save.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denoh.k_save.models.RideOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideScreen(
    title: String = "Available Rides",
    rides: List<RideOption>,
    onSelect: (RideOption) -> Unit,
    onBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF004D40), Color(0xFF000000))
                )
            )
    ) {
        Scaffold(

            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(title, color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Text(text = "←", color = Color.White, fontSize = 24.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.6f)
                    )
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(rides) { ride ->
                    val fare = if (ride.rainChance > 50) ride.baseFare + 40 else ride.baseFare
                    val vehicleImage = when(ride.category.lowercase()) {
                        "bikes" -> "https://www.uber-assets.com/image/upload/f_auto,q_auto:eco,c_fill,w_150,h_150/v1648135216/assets/c0/9b973a-fca6-4786-82ee-01774319fb0a/original/Uber_Moto_Kenya_312x312.png"
                        else -> "https://www.uber-assets.com/image/upload/f_auto,q_auto:eco,c_fill,w_150,h_150/v1542350172/assets/80/e78345-d85c-4573-a5bc-4389659b829d/original/Uber_X_312x312.png"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(ride) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = if(ride.imageUrl.isEmpty()) vehicleImage else ride.imageUrl,
                                contentDescription = ride.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                            
                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ride.name,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text("Route: ${ride.route}", color = Color.DarkGray, fontSize = 12.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🚦 ${ride.traffic}", color = Color(0xFFE65100), fontSize = 12.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Text("🌧 ${ride.rainChance}%", color = Color(0xFF0277BD), fontSize = 12.sp)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Ksh $fare",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                                Text("Economy", color = Color.Gray, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RideScreenPreview() {
    val sampleRides = listOf(
        RideOption(1, "UberX 🚗", "CBD → Westlands", "Moderate", 30, 250, "Cars", ""),
        RideOption(2, "Boda 🏍", "CBD → Ngara", "Heavy", 70, 120, "Bikes", ""),
        RideOption(3, "UberXL 🚐", "CBD → Rongai", "Heavy", 60, 150, "Matatus", "")
    )
    RideScreen(
        title = "Available Rides",
        rides = sampleRides,
        onSelect = {}
    )
}
