package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BloodRequest
import com.example.ui.theme.*

@Composable
fun BloodDonationTab(
    bloodRequests: List<BloodRequest>,
    selectedRequest: BloodRequest?,
    onSelectRequest: (BloodRequest?) -> Unit,
    bloodType: String,
    locationName: String,
    urgency: String,
    isSubmitting: Boolean,
    completedAnimatingRequests: Set<Int>,
    onBloodTypeChange: (String) -> Unit,
    onLocationNameChange: (String) -> Unit,
    onUrgencyChange: (String) -> Unit,
    onSubmitRequest: () -> Unit,
    onRequestStatusUpdate: (Int, String) -> Unit
) {
    // Submit Button float up spring animation
    val submitOffset by animateDpAsState(
        targetValue = if (isSubmitting) (-24).dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "btn_float"
    )

    val bloodTypesList = listOf("O+", "A+", "B+", "O-", "A-", "B-", "AB+", "AB-")
    val urgencyLevels = listOf("Low", "Medium", "Urgent")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("blood_donation_tab")
            .background(Color.Transparent),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Title Summary
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Request Blood Match",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate900,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Submit a request to broadcast across the hovering air corridors.",
                    fontSize = 13.sp,
                    color = SleekSlate500
                )
            }
        }

        // 2. Request Blood Form Card (Floating styling)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(16.dp, RoundedCornerShape(32.dp), ambientColor = HoverShadow)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White.copy(alpha = 0.8f))
                    .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(32.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = "Request Blood Capsule",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate900,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Input A: Blood Type Chips List
                Text(
                    text = "Required Blood Type",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate500,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp)
                ) {
                    bloodTypesList.forEach { type ->
                        val isSelected = bloodType == type
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .shadow(if (isSelected) 2.dp else 0.dp, CircleShape)
                                .clip(CircleShape)
                                .background(if (isSelected) PrimaryBlue else SleekSlate100)
                                .border(1.dp, if (isSelected) Color.Transparent else SleekSlate200, CircleShape)
                                .clickable { onBloodTypeChange(type) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("blood_chip_$type")
                        ) {
                            Text(
                                text = type,
                                color = if (isSelected) Color.White else SleekSlate800,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // Input B: Location Input (Floating Card field)
                Text(
                    text = "Delivery Medical Center Location",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate500,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = locationName,
                    onValueChange = onLocationNameChange,
                    placeholder = { Text("e.g. Saint Mary Hospital") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = SleekSlate200,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedPlaceholderColor = SleekSlate400,
                        unfocusedPlaceholderColor = SleekSlate400
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("blood_location_input"),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.LocalHospital, contentDescription = "Hospital", tint = PrimaryBlue)
                    }
                )

                // Input C: Urgency Selection Chips
                Text(
                    text = "Urgency Level",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate500,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    urgencyLevels.forEach { level ->
                        val isSelected = urgency == level
                        val activeColor = when (level) {
                            "Urgent" -> StatusUrgent
                            "Medium" -> StatusMedium
                            else -> StatusLow
                        }
                        val containerColor = if (isSelected) activeColor else SleekSlate100
                        val textColor = if (isSelected) {
                            if (level == "Medium") TextDarkPrimary else Color.White
                        } else SleekSlate700

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(containerColor)
                                .border(1.dp, if (isSelected) Color.Transparent else SleekSlate200, RoundedCornerShape(12.dp))
                                .clickable { onUrgencyChange(level) }
                                .padding(vertical = 10.dp)
                                .testTag("urgency_chip_$level")
                        ) {
                            Text(
                                text = level,
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Submit Button floats upwards when submitting
                Button(
                    onClick = onSubmitRequest,
                    enabled = !isSubmitting && locationName.isNotBlank(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        disabledContainerColor = SleekSlate200
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp,
                        hoveredElevation = 10.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .offset(y = submitOffset) // Upward float spring translation
                        .testTag("submit_request_button")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Broadcast", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Broadcast Floating Request", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // 3. Donor Matching Interactive Map Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Live Donation Air Map",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate900,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "Tapping markers expands detail cards; markers bob gently in space.",
                    fontSize = 12.sp,
                    color = SleekSlate500,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Native Map Canvas Container - styled like Tailwind: bg-slate-200/40 rounded-[40px]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .shadow(12.dp, RoundedCornerShape(32.dp), ambientColor = HoverShadow)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SleekSlate200.copy(alpha = 0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(32.dp))
                ) {
                    MapWidget(
                        bloodRequests = bloodRequests.filter { it.status == "Pending" || it.status == "Matched" },
                        selectedRequest = selectedRequest,
                        onSelectRequest = { onSelectRequest(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // 4. Smooth Rising Info Sheet for Map Selection
        item {
            AnimatedVisibility(
                visible = selectedRequest != null,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
                ) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                selectedRequest?.let { req ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(24.dp, RoundedCornerShape(28.dp), ambientColor = HoverShadow)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(28.dp))
                            .padding(20.dp)
                            .testTag("map_detail_card")
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.1f))
                                ) {
                                    Icon(imageVector = Icons.Default.Bloodtype, contentDescription = "blood", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Blood Type Match: ${req.bloodType}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekSlate900,
                                    letterSpacing = (-0.3).sp
                                )
                            }

                            // Urgency level tag
                            val badgeColor = when (req.urgency) {
                                "Urgent" -> StatusUrgent
                                "Medium" -> StatusMedium
                                else -> StatusLow
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = req.urgency,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (req.urgency == "Medium") SleekSlate900 else badgeColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.PinDrop, contentDescription = "Pin", tint = SleekSlate400, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = req.locationName, fontSize = 13.sp, color = SleekSlate700)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { onSelectRequest(null) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekSlate700),
                                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Dismiss", color = SleekSlate700)
                            }

                            Button(
                                onClick = { onRequestStatusUpdate(req.id, "Matched") },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = StatusMatched),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("help_now_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Help", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text("Help Now", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Status Display List Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Request Broadcast Stream",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekSlate900,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "Active broadcasting match records. Completed items slide up and float away.",
                    fontSize = 12.sp,
                    color = SleekSlate500
                )
            }
        }

        // List Item details (custom items)
        if (bloodRequests.isEmpty()) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = HoverShadow)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Air, contentDescription = "Empty", tint = SleekSlate400, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No active requests broadcasting.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SleekSlate900)
                        Text("Use the form above to deploy one in space.", fontSize = 12.sp, color = SleekSlate500, textAlign = TextAlign.Center)
                    }
                }
            }
        } else {
            items(bloodRequests, key = { it.id }) { req ->
                val isCompletedAnimating = completedAnimatingRequests.contains(req.id)

                // Float upward fade animation spec
                val itemAlpha by animateFloatAsState(
                    targetValue = if (isCompletedAnimating) 0f else 1f,
                    animationSpec = tween(900, easing = EaseOutCubic),
                    label = "item_fade"
                )

                val itemOffsetY by animateDpAsState(
                    targetValue = if (isCompletedAnimating) (-120).dp else 0.dp,
                    animationSpec = tween(900, easing = EaseOutCubic),
                    label = "item_y"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .offset(y = itemOffsetY)
                        .alpha(itemAlpha)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp), ambientColor = HoverShadow)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.8f))
                            .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                            .clickable { onSelectRequest(req) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.1f))
                                ) {
                                    Text(text = req.bloodType, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                                }

                                Text(
                                    text = req.locationName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekSlate900,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Urgency tag
                                Text(
                                    text = "Urgency: ${req.urgency}",
                                    fontSize = 11.sp,
                                    color = SleekSlate700
                                )

                                Text(
                                    text = "•",
                                    fontSize = 11.sp,
                                    color = SleekSlate400
                                )

                                Text(
                                    text = "Posted just now",
                                    fontSize = 11.sp,
                                    color = SleekSlate400
                                )
                            }
                        }

                        // Rightside badges & controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            val badgeColor = when (req.status) {
                                "Matched" -> StatusMatched
                                "Completed" -> StatusCompleted
                                else -> StatusPending
                            }

                            // Status Tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badgeColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = req.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }

                            // Completing float transition action
                            if (req.status != "Completed") {
                                IconButton(
                                    onClick = { onRequestStatusUpdate(req.id, "Completed") },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SleekSlate100)
                                        .testTag("complete_button_${req.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Complete Request",
                                        tint = StatusMatched,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
