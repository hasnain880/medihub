package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.Ambulance
import com.example.ui.theme.*

@Composable
fun AmbulanceDispatchTab(
    ambulances: List<Ambulance>,
    selectedAmbulance: Ambulance?,
    onSelectAmbulance: (Ambulance?) -> Unit,
    userLocation: Pair<Float, Float>?,
    isLocating: Boolean,
    dispatchedAmbulance: Ambulance?,
    arrivalCountdown: Int?,
    isArrived: Boolean,
    onAcquireLocation: () -> Unit,
    onDispatchAmbulance: (Ambulance) -> Unit,
    onResetDispatch: () -> Unit
) {
    // Pulse animation for Get My Location scanner
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_locator")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "locator_pulse"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "locator_alpha"
    )

    // Animated locator capsule floating
    val locatorFloatY by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "locator_float"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ambulance_dispatch_tab")
            .background(Color.Transparent)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Title Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Ambulance Flight Corridor",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SleekSlate900,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Dispatch anti-gravity ambulance shuttles gliding safely above street gridlocks.",
                fontSize = 13.sp,
                color = SleekSlate500
            )
        }

        // 2. Central Locator Panel (If user location is missing)
        if (userLocation == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(180.dp)
                    .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = HoverShadow)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SpaceSkyLight.copy(alpha = 0.8f), SpacePurpleLight.copy(alpha = 0.8f))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "Identify Your Beacon Location",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekSlate900,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Scanner pulse visual
                    Box(contentAlignment = Alignment.Center) {
                        if (isLocating) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .border(2.dp, PrimaryBlue.copy(alpha = pulseAlpha), CircleShape)
                                    .clip(CircleShape)
                            )
                        }

                        // Get Location Action Button
                        Button(
                            onClick = onAcquireLocation,
                            enabled = !isLocating,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            modifier = Modifier
                                .height(56.dp)
                                .offset(y = locatorFloatY.dp)
                                .testTag("get_location_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isLocating) Icons.Default.Sync else Icons.Default.MyLocation,
                                    contentDescription = "location",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (isLocating) "Scanning Corridors..." else "Get My Beacon Location",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // 3. User location is acquired, show Map Container & Live glide animations!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.GpsFixed, contentDescription = "Gps", tint = StatusMatched, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Beacon Anchor Locked (0.5, 0.5)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Text(
                        text = "Reset Beacon",
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onResetDispatch() }
                            .testTag("reset_beacon_trigger")
                    )
                }

                // Interactive map component loaded with data
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .shadow(12.dp, RoundedCornerShape(32.dp), ambientColor = HoverShadow)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SleekSlate200.copy(alpha = 0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(32.dp))
                ) {
                    MapWidget(
                        ambulances = if (dispatchedAmbulance != null) emptyList() else ambulances.filter { it.isAvailable },
                        selectedAmbulance = selectedAmbulance,
                        onSelectAmbulance = { onSelectAmbulance(it) },
                        userLocation = userLocation,
                        activeDispatch = dispatchedAmbulance,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dispatch Flight Status banner overlay (anti-gravity banner)
                    dispatchedAmbulance?.let { amb ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .align(Alignment.TopCenter)
                                .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = HoverShadow)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.85f))
                                .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                                .padding(12.dp)
                                .testTag("dispatch_status_banner")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(SecondaryPurple.copy(alpha = 0.1f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Emergency,
                                            contentDescription = "cross",
                                            tint = SecondaryPurple,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = "Ambulance is on the way",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekSlate900
                                        )
                                        Text(
                                            text = "${String.format("%.1f", amb.distance)} km • Air Corridor Route",
                                            fontSize = 11.sp,
                                            color = SleekSlate500
                                        )
                                    }
                                }

                                // Floating Countdown Badge bobbing
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .shadow(4.dp, RoundedCornerShape(10.dp))
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SecondaryPurple)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${arrivalCountdown ?: amb.etaMinutes}m ETA",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Smooth Rising Ambulance Detail Card
        AnimatedVisibility(
            visible = selectedAmbulance != null && userLocation != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
            ) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            selectedAmbulance?.let { amb ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(24.dp, RoundedCornerShape(28.dp), ambientColor = HoverShadow)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(28.dp))
                        .padding(20.dp)
                        .testTag("ambulance_detail_card")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.1f))
                            ) {
                                Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = "shuttle", tint = PrimaryBlue)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = amb.codeName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekSlate900,
                                    letterSpacing = (-0.3).sp
                                )
                                Text(
                                    text = "Anti-Gravity Hover Shuttle",
                                    fontSize = 11.sp,
                                    color = SleekSlate500
                                )
                            }
                        }

                        // Available tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(StatusMatched.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Hovering Near",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusMatched
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Corridor distance grids info
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SleekSlate100)
                            .border(1.dp, SleekSlate200, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = "PILOT CODENAME", fontSize = 10.sp, color = SleekSlate400, fontWeight = FontWeight.Bold)
                            Text(text = amb.driverName, fontSize = 13.sp, color = SleekSlate800, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "ESTIMATED DISTANCE", fontSize = 10.sp, color = SleekSlate400, fontWeight = FontWeight.Bold)
                            Text(text = "${amb.distance} km", fontSize = 13.sp, color = SleekSlate800, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "ARRIVAL TIME", fontSize = 10.sp, color = SleekSlate400, fontWeight = FontWeight.Bold)
                            Text(text = "${amb.etaMinutes} mins", fontSize = 13.sp, color = SecondaryPurple, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { onSelectAmbulance(null) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekSlate700),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Dismiss", color = SleekSlate700)
                        }

                        Button(
                            onClick = { onDispatchAmbulance(amb) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("dispatch_ambulance_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Launch, contentDescription = "launch", tint = Color.White, modifier = Modifier.size(16.dp))
                                Text("Dispatch Shuttle", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // 5. Full screen floating arrived dialogue overlay
        AnimatedVisibility(
            visible = isArrived,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(24.dp, RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.95f), SleekSlate100.copy(alpha = 0.95f))
                        )
                    )
                    .border(2.dp, SecondaryPurple.copy(alpha = 0.2f), RoundedCornerShape(28.dp))
                    .padding(24.dp)
                    .testTag("arrival_alert_banner")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(StatusMatched)
                    ) {
                        Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ambulance Arrived Safely",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekSlate900
                    )

                    Text(
                        text = "Hovering stabilizers have locked onto your location anchor. Prepare for immediate medical transport boarding.",
                        fontSize = 12.sp,
                        color = SleekSlate500,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onResetDispatch,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.testTag("dismiss_arrival_button")
                    ) {
                        Text("Acknowledge Landing", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
