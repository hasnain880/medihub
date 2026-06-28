package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    onSignIn: (String, String) -> Unit,
    isFirebaseAvailable: Boolean,
    authStatus: String
) {
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    // Floating bubble animation coordinates
    val infiniteTransition = rememberInfiniteTransition(label = "auth_particles")
    val floatAnimY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_y"
    )

    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SpaceSkyLight, SpacePurpleLight, Color.White)
                )
            )
            .padding(24.dp)
            .testTag("auth_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Ambient background circles representing anti-gravity bubbles
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = (-80).dp, y = (-120).dp + floatAnimY.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 100.dp, y = 140.dp - floatAnimY.dp)
                .clip(CircleShape)
                .background(SecondaryPurple.copy(alpha = 0.08f))
        )

        // Main Login Card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = HoverShadow,
                    spotColor = HoverShadow
                )
                .clip(RoundedCornerShape(28.dp))
                .background(GlassWhite)
                .padding(32.dp)
        ) {
            // Elevated Floating Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = floatAnimY.dp * 0.5f)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryBlue, SecondaryPurple)
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Emergency,
                    contentDescription = "MediHub Logo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "MediHub",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkPrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = "Celestial Life Protection Network",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextDarkSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Firebase Connection Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isFirebaseAvailable) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (isFirebaseAvailable) Icons.Default.CloudDone else Icons.Default.CloudOff,
                    contentDescription = "Sync state",
                    tint = if (isFirebaseAvailable) StatusMatched else StatusPending,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isFirebaseAvailable) "Firebase Cloud Connected" else "Offline Sandbox Persisted",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isFirebaseAvailable) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Input Fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Display Name") },
                placeholder = { Text("e.g. John Doe") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0x80F8FAFC)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_name_input"),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Name", tint = TextDarkMuted)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                placeholder = { Text("yourname@gmail.com") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0x80F8FAFC)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_email_input")
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Sign In Action Button (floats up or scales beautifully)
            val btnElevation = if (authStatus == "Authenticating") 2.dp else 8.dp
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        onSignIn(email, name)
                    }
                },
                enabled = authStatus != "Authenticating" && name.isNotBlank() && email.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = Color(0xFFE2E8F0)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = btnElevation,
                    pressedElevation = 2.dp,
                    hoveredElevation = 12.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .offset(y = if (authStatus == "Authenticating") 4.dp else 0.dp)
                    .testTag("google_signin_button")
            ) {
                if (authStatus == "Authenticating") {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Custom Google G-Logo vector simulation for aesthetic perfection
                        Text(
                            text = "G  ",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Sign in with Google Account",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "By signing in, you access secure real-time request broadcast channels.",
                fontSize = 11.sp,
                color = TextDarkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

// Utility to create state flow values inside composables easily
@Composable
fun <T> rememberStateFlowOf(initialValue: T): MutableState<T> {
    return remember { mutableStateOf(initialValue) }
}
