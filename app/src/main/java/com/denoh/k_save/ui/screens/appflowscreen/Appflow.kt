package com.denoh.k_save.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppFlowScreen(onEnterApp: () -> Unit = {}) {
    var stage by remember { mutableStateOf(0) }
    var tapped by remember { mutableStateOf(false) }
    val scale = remember { Animatable(0.85f) }
    val fade = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Interactive Ripple State - Making every touch count
    var ripplePos by remember { mutableStateOf(Offset.Zero) }
    val rippleAlpha = remember { Animatable(0f) }
    val rippleScale = remember { Animatable(0f) }

    val infinite = rememberInfiniteTransition(label = "pulse")
    
    // Background Glow Animation
    val orbAnimate by infinite.animateFloat(
        initialValue = -40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(tween(6000), RepeatMode.Reverse),
        label = "orb"
    )

    val feed = listOf(
        "Initializing neural mobility engine...",
        "Syncing with Nairobi traffic grids...",
        "Drivers standing by...",
        "Systems Nominal"
    )
    var index by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        fade.animateTo(1f, tween(1500))
        scale.animateTo(1f, tween(1500, easing = EaseOutBack))
        
        // Progress through stages
        delay(1000)
        stage = 1
        index = 1
        delay(1500)
        stage = 2
        index = 3
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030303))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Visual & Haptic feedback for every touch
                    ripplePos = offset
                    scope.launch {
                        rippleAlpha.snapTo(0.6f)
                        rippleScale.snapTo(0f)
                        launch { rippleAlpha.animateTo(0f, tween(800)) }
                        launch { rippleScale.animateTo(2.5f, tween(800, easing = EaseOutExpo)) }
                    }
                    
                    if (stage == 2 && !tapped) {
                        tapped = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        scope.launch {
                            // Dramatic "Warp" transition
                            launch { scale.animateTo(4f, tween(1000, easing = EaseInExpo)) }
                            launch { fade.animateTo(0f, tween(800)) }
                            delay(900)
                            onEnterApp()
                        }
                    } else {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                }
            }
    ) {
        // 🌌 DYNAMIC BACKGROUND
        Canvas(modifier = Modifier.fillMaxSize().blur(100.dp)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00D4FF).copy(0.3f), Color.Transparent),
                    center = Offset(size.width * 0.2f + orbAnimate, size.height * 0.3f),
                    radius = 800f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFF3D71).copy(0.2f), Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.7f + orbAnimate),
                    radius = 900f
                )
            )
        }

        // Touch Feedback Ripple
        Box(
            modifier = Modifier
                .offset(x = (ripplePos.x / 2.7f).dp - 50.dp, y = (ripplePos.y / 2.7f).dp - 50.dp)
                .size(100.dp)
                .scale(rippleScale.value)
                .alpha(rippleAlpha.value)
                .background(
                    Brush.radialGradient(listOf(Color.White, Color.Transparent)),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
                .alpha(fade.value)
                .scale(scale.value),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Identity Logo
            Box(contentAlignment = Alignment.Center) {
                val rotation by infinite.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing)),
                    label = "ring"
                )
                
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .graphicsLayer { rotationZ = rotation }
                        .background(
                            Brush.sweepGradient(listOf(Color(0xFF00D4FF), Color.Transparent, Color(0xFFFF3D71), Color.Transparent)),
                            CircleShape
                        ).alpha(0.25f)
                )

                Text(
                    "K",
                    fontSize = 110.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.graphicsLayer { shadowElevation = 40f }
                )
            }

            Spacer(Modifier.height(48.dp))

            Text(
                "K-SAVE",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 16.sp
            )
            
            Text(
                "PREMIUM MOBILITY SOLUTION",
                fontSize = 10.sp,
                color = Color(0xFF00D4FF),
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp
            )

            Spacer(Modifier.height(100.dp))

            // Interaction Center
            Surface(
                color = Color.White.copy(0.04f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(120.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (stage < 2) {
                        CircularProgressIndicator(
                            color = Color(0xFF00D4FF),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(feed[index], color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Light)
                    } else {
                        ReadyPrompt(tapped)
                    }
                }
            }
        }
    }
}

@Composable
fun ReadyPrompt(tapped: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_text")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "alpha"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "ENGINE OPTIMIZED",
            color = Color(0xFF00FF88),
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 4.sp,
            modifier = Modifier.alpha(if (tapped) 1f else alpha)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            if (tapped) "INITIATING JUMP..." else "TOUCH TO BEGIN JOURNEY",
            color = Color.White.copy(0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppFlowPreview() {
    AppFlowScreen()
}
