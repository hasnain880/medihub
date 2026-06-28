package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.DepartureBoard
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MediHeader(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    userName: String?,
    userEmail: String?,
    onSignOut: () -> Unit
) {
    // Transparent, clean container that lets the underlying sleek page gradient shine through
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("app_header")
    ) {
        // Logo & Profile Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Logo group
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryBlue)
                        .shadow(4.dp, RoundedCornerShape(8.dp), spotColor = PrimaryBlue.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "M",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Column {
                    Text(
                        text = "MediHub",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekSlate900,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            // User Info & Sign out group
            userName?.let { name ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, SleekSlate100, RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // Profile Circle
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SleekSlate100)
                    ) {
                        val initials = if (name.length >= 2) name.substring(0, 1).uppercase() else name.take(1).uppercase()
                        Text(
                            text = initials,
                            color = SleekSlate700,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.widthIn(max = 80.dp)) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekSlate800,
                            maxLines = 1
                        )
                    }

                    // Logout trigger
                    IconButton(
                        onClick = onSignOut,
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign Out",
                            tint = SleekSlate400,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Tabs Row - styled like Tailwind: bg-white/60 backdrop-blur-md p-1 rounded-2xl
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.6f))
                .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            TabButton(
                title = "Blood Donation",
                icon = Icons.Default.Bloodtype,
                isSelected = currentTab == "Blood Donation",
                onClick = { onTabSelected("Blood Donation") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_blood_donation")
            )

            TabButton(
                title = "Ambulance",
                icon = Icons.Default.DepartureBoard,
                isSelected = currentTab == "Ambulance Dispatch",
                onClick = { onTabSelected("Ambulance Dispatch") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tab_ambulance_dispatch")
            )
        }
    }
}

@Composable
fun TabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerBg = if (isSelected) Color.White else Color.Transparent
    val contentColor = if (isSelected) PrimaryBlue else SleekSlate500
    val shadowModifier = if (isSelected) {
        Modifier.shadow(2.dp, RoundedCornerShape(12.dp))
    } else {
        Modifier
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .then(shadowModifier)
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg)
            .clickable { onClick() }
            .padding(vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

