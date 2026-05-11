package com.denoh.k_save

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denoh.k_save.network.MpesaUtils

class BookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val destination = intent.getStringExtra("destination") ?: "Unknown"
        setContent {
            BookingScreen(
                destination = destination,
                onPaymentRequested = { phoneNumber, amount ->
                    // Trigger STK Push
                    MpesaUtils.performSTKPush(phoneNumber, amount) { result ->
                        runOnUiThread {
                            Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun BookingScreen(
    destination: String = "Westlands",
    onPaymentRequested: (String, Int) -> Unit = { _, _ -> }
) {
    var selectedMethod by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("254") } // Default country code
    val fare = 1 // Testing with 1 Ksh

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF00695C), Color(0xFF004D40), Color(0xFF000000))
                )
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Confirm Your Ride",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(Modifier.height(24.dp))

            // Booking Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = "https://www.uber-assets.com/image/upload/f_auto,q_auto:eco,c_fill,w_150,h_150/v1542350172/assets/80/e78345-d85c-4573-a5bc-4389659b829d/original/Uber_X_312x312.png",
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Destination", color = Color.Gray, fontSize = 12.sp)
                        Text(destination, color = Color.Black, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Estimated Fare", color = Color.Gray, fontSize = 12.sp)
                        Text("Ksh $fare", color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text("Select Payment Method", color = Color.White, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(16.dp))

            listOf("M-Pesa", "Cash").forEach { method ->
                val isSelected = selectedMethod == method
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { selectedMethod = method },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF00C853) else Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (method == "M-Pesa") "📱" else "💵",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            method,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.weight(1f))
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = Color.White, unselectedColor = Color.Gray)
                        )
                    }
                }
            }

            if (selectedMethod == "M-Pesa") {
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("M-Pesa Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00C853),
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color(0xFF00C853),
                        unfocusedLabelColor = Color.Gray
                    )
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (selectedMethod == "M-Pesa") {
                        onPaymentRequested(phoneNumber, fare)
                    } else {
                        // Handle cash
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                enabled = selectedMethod.isNotEmpty() && (selectedMethod != "M-Pesa" || phoneNumber.length >= 10),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Pay Ksh $fare", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    BookingScreen()
}
