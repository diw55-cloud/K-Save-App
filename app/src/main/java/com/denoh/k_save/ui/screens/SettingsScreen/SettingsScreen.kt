package com.denoh.k_save.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.denoh.k_save.network.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onLogout: () -> Unit = {}) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

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
                    title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text("Account Settings", color = Color.White, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                
                ListItem(
                    headlineContent = { Text("Profile", color = Color.White) },
                    supportingContent = { Text("Edit your profile information", color = Color.Gray) },
                    leadingContent = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Cyan) },
                    modifier = Modifier.clickable { /* TODO: Profile Action */ },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                
                ListItem(
                    headlineContent = { Text("Notifications", color = Color.White) },
                    supportingContent = { Text("Manage your alerts", color = Color.Gray) },
                    leadingContent = { Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Cyan) },
                    modifier = Modifier.clickable { /* TODO: Notifications Action */ },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        sessionManager.logout()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("LOGOUT", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
