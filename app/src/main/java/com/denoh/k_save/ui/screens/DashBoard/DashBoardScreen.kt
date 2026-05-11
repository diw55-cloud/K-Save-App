package com.denoh.k_save.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.denoh.k_save.models.RideOption
import com.denoh.k_save.network.MpesaUtils
import com.denoh.k_save.ui.screens.DashBoard.DashboardViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    destination: String = "",
    initialCategory: String? = null,
    goHome: () -> Unit = {},
    onTripConfirmed: (Int, String) -> Unit = { _, _ -> },
    savings: Int = 0,
    viewModel: DashboardViewModel = viewModel()
) {
    val context = LocalContext.current
    val rides by viewModel.rides.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var currentScreen by remember { mutableStateOf(if (initialCategory != null) "rides" else "main") }
    var selectedRide by remember { mutableStateOf<RideOption?>(null) }
    var showPayment by remember { mutableStateOf(false) }
    var showReceipt by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("254") }
    var paidFare by remember { mutableStateOf(0) }

    LaunchedEffect(destination, initialCategory) {
        viewModel.setDestination(destination)
        if (initialCategory != null) {
            viewModel.loadRidesByCategory(initialCategory)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { ProfessionalTopBar(savings) },
        bottomBar = { ProfessionalBottomBar(goHome, { currentScreen = "main" }) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF4A148C), Color(0xFF000000))
                    )
                )
                .padding(padding)
        ) {

            when (currentScreen) {
                "main" -> {
                    LazyColumn(Modifier.padding(16.dp)) {
                        item {
                            val title = if (destination.isNotEmpty()) "Rides to $destination" else "Select Service"
                            Text(
                                title,
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        items(categories) { category ->
                            CategoryCard(category) {
                                viewModel.loadRidesByCategory(category)
                                currentScreen = "rides"
                            }
                        }
                    }
                }

                "rides" -> {
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.Cyan)
                        }
                    } else {
                        RideListScreen(rides) { ride ->
                            selectedRide = ride
                            showPayment = true
                        }
                    }
                }
            }

            if (showPayment && selectedRide != null) {
                val fare = if (selectedRide!!.rainChance > 50) selectedRide!!.baseFare + 40 else selectedRide!!.baseFare
                PaymentScreen(
                    item = selectedRide!!.name,
                    amount = fare,
                    phoneNumber = phoneNumber,
                    onPhoneChange = { phoneNumber = it },
                    onCancel = { showPayment = false },
                    onPay = {
                        MpesaUtils.performSTKPush(phoneNumber, fare) { result ->
                            Toast.makeText(context, result, Toast.LENGTH_LONG).show()
                            if (result.contains("Success", ignoreCase = true)) {
                                paidFare = fare
                                showPayment = false
                                showReceipt = true
                            }
                        }
                    }
                )
            }

            if (showReceipt && selectedRide != null) {
                ReceiptScreen(selectedRide!!.name) {
                    showReceipt = false
                    onTripConfirmed(paidFare, phoneNumber)
                }
            }
        }
    }
}

@Composable
fun CategoryCard(title: String, onClick: () -> Unit) {
    val imageUrl = when(title.lowercase()) {
        "cars" -> "https://www.uber-assets.com/image/upload/f_auto,q_auto:eco,c_fill,w_150,h_150/v1649231046/assets/84/ad05b2-3e28-4442-990d-2a3b043324c4/original/Uber_Auto_312x312.png"
        "bikes" -> "https://www.uber-assets.com/image/upload/f_auto,q_auto:eco,c_fill,w_150,h_150/v1648135216/assets/c0/9b973a-fca6-4786-82ee-01774319fb0a/original/Uber_Moto_Kenya_312x312.png"
        "matatus" -> "https://www.uber-assets.com/image/upload/f_auto,q_auto:eco,c_fill,w_150,h_150/v1542350172/assets/80/e78345-d85c-4573-a5bc-4389659b829d/original/Uber_X_312x312.png"
        "rentals" -> "https://mobile-content.uber.com/launch-experience/rentals.png"
        "package" -> "https://mobile-content.uber.com/launch-experience/package.png"
        "planes" -> "https://mobile-content.uber.com/launch-experience/reserve.png"
        else -> "https://www.uber-assets.com/image/upload/f_auto,q_auto:eco,c_fill,w_150,h_150/v1542350172/assets/80/e78345-d85c-4573-a5bc-4389659b829d/original/Uber_X_312x312.png"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(20.dp))
            Text(
                title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Cyan
            )
        }
    }
}

@Composable
fun ProfessionalTopBar(savings: Int = 0) {
    var time by remember { mutableStateOf("") }
    val date = remember {
        SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(Date())
    }

    LaunchedEffect(true) {
        while (true) {
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            delay(1000)
        }
    }

    Surface(color = Color.Black.copy(alpha = 0.5f)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(time, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(date, color = Color.LightGray, fontSize = 12.sp)
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Savings", color = Color.LightGray, fontSize = 10.sp)
                    Text("Ksh $savings", color = Color.Green, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ProfessionalBottomBar(goHome: () -> Unit, goDash: () -> Unit) {
    Surface(color = Color.Black.copy(alpha = 0.8f)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomItem("🏠", "Home", goHome)
            BottomItem("🚗", "Dashboard", goDash)
            BottomItem("💰", "Savings") {}
        }
    }
}

@Composable
fun BottomItem(icon: String, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)) {
        Text(icon, style = MaterialTheme.typography.titleLarge)
        Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun RideListScreen(items: List<RideOption>, onSelect: (RideOption) -> Unit) {
    LazyColumn(Modifier.padding(16.dp)) {
        items(items) { item ->
            val fare = if (item.rainChance > 50) item.baseFare + 40 else item.baseFare
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onSelect(item) }
            ) {
                Row(Modifier.padding(12.dp)) {
                    AsyncImage(
                        model = if(item.imageUrl.isEmpty()) "https://www.uber-assets.com/image/upload/f_auto,q_auto:eco,c_fill,w_150,h_150/v1542350172/assets/80/e78345-d85c-4573-a5bc-4389659b829d/original/Uber_X_312x312.png" else item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(Modifier.width(16.dp))
                    
                    Column(Modifier.weight(1f)) {
                        Text(item.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Route: ${item.route}", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Badge(containerColor = Color.Black.copy(alpha = 0.5f)) { Text("🚦 ${item.traffic}", color = Color.Yellow, fontSize = 10.sp) }
                            Badge(containerColor = Color.Black.copy(alpha = 0.5f)) { Text("🌧 ${item.rainChance}%", color = Color.Cyan, fontSize = 10.sp) }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Ksh $fare", color = Color.Green, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentScreen(
    item: String,
    amount: Int,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    onCancel: () -> Unit,
    onPay: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(24.dp).fillMaxWidth()
        ) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pay Ksh $amount to K-Save", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Paying for: $item", color = Color.LightGray)
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneChange,
                    label = { Text("M-Pesa Number") },
                    placeholder = { Text("2547XXXXXXXX") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Green,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onPay,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("LIPA NA M-PESA", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onCancel, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Cancel Transaction", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ReceiptScreen(item: String, onDone: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✅", style = MaterialTheme.typography.displayLarge)
            Spacer(Modifier.height(16.dp))
            Text(
                "Payment Successful",
                color = Color.Green,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("Your $item ride is confirmed", color = Color.White)
            Text("Driver: James (KDJ 123X)", color = Color.Gray)
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onDone,
                modifier = Modifier.width(200.dp).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("Go to Trip")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    DashboardScreen()
}
