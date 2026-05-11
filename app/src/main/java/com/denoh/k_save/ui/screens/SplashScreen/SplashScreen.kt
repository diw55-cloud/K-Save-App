package com.denoh.k_save.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinish: () -> Unit = {}) {

    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.7f) }

    val infinite = rememberInfiniteTransition(label = "bg")

    val shift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shift"
    )

    val messages = listOf(
        "Connecting mobility network",
        "Finding nearby rides",
        "Optimizing fastest route",
        "Preparing your journey"
    )

    var index by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {

        alpha.animateTo(1f, tween(900))
        scale.animateTo(1f, tween(900))

        repeat(3) {
            delay(800)
            index = (index + 1) % messages.size
        }

        delay(1200)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1B1B1B),
                        Color(0xFF0A0A0A)
                    ),
                    radius = 1200f
                )
            )
    ) {

        // 🌌 MOVING GLOW LAYER (Bolt-style energy feel)
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = shift.dp)
                .blur(80.dp)
                .background(Color(0xFF00D4FF), CircleShape)
                .align(Alignment.TopStart)
        )

        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 120.dp, y = (shift / 2).dp)
                .blur(100.dp)
                .background(Color(0xFFFF3D71), CircleShape)
                .align(Alignment.BottomEnd)
        )

        // 🚀 MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    this.alpha = alpha.value
                    scaleX = scale.value
                    scaleY = scale.value
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // LOGO
            Text(
                text = "K-SAVE",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Ride. Move. Save.",
                fontSize = 14.sp,
                color = Color(0xFFBDBDBD)
            )

            Spacer(Modifier.height(28.dp))

            // LIVE MESSAGE
            Text(
                text = messages[index],
                fontSize = 13.sp,
                color = Color(0xFF8A8A8A)
            )

            Spacer(Modifier.height(16.dp))

            // DOT LOADER
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) {
                    val dotAnim = remember { Animatable(0.3f) }

                    LaunchedEffect(Unit) {
                        while (true) {
                            dotAnim.animateTo(1f, tween(300))
                            dotAnim.animateTo(0.3f, tween(300))
                            delay(100)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    SplashScreen()
}