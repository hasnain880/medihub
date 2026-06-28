package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Ambulance
import com.example.data.BloodRequest
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun MapWidget(
    modifier: Modifier = Modifier,
    bloodRequests: List<BloodRequest> = emptyList(),
    selectedRequest: BloodRequest? = null,
    onSelectRequest: (BloodRequest) -> Unit = {},
    ambulances: List<Ambulance> = emptyList(),
    selectedAmbulance: Ambulance? = null,
    onSelectAmbulance: (Ambulance) -> Unit = {},
    userLocation: Pair<Float, Float>? = null,
    activeDispatch: Ambulance? = null,
) {
    // Beautiful endless anti-gravity hovering/bobbing animation
    val infiniteTransition = rememberInfiniteTransition(label = "bobbing")
    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "marker_bob"
    )

    // Pulsing circle animation for radar beacon effect
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse"
    )

    // Glowing background grids drift animation
    val gridDriftX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid_drift"
    )

    Box(
        modifier = modifier
            .testTag("interactive_map")
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFF1F7FC), Color(0xFFE3F2FD), Color(0xFFECEFF1)),
                    center = Offset.Unspecified,
                    radius = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(bloodRequests, ambulances, userLocation) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height
                        val tapRadius = 35.dp.toPx() // responsive tap buffer

                        // 1. Check tap on blood request markers
                        bloodRequests.forEach { req ->
                            val rx = req.latitude * w  // we mapped latitude to x-coord
                            val ry = req.longitude * h // longitude to y-coord
                            if (hypot(tapOffset.x - rx, tapOffset.y - ry) <= tapRadius) {
                                onSelectRequest(req)
                                return@detectTapGestures
                            }
                        }

                        // 2. Check tap on ambulance markers
                        ambulances.forEach { amb ->
                            val ax = amb.latitude * w
                            val ay = amb.longitude * h
                            if (hypot(tapOffset.x - ax, tapOffset.y - ay) <= tapRadius) {
                                onSelectAmbulance(amb)
                                return@detectTapGestures
                            }
                        }
                    }
                }
        ) {
            val w = size.width.toFloat()
            val h = size.height.toFloat()

            // Draw a subtle drifting celestial grid mapping streets
            val gridSize = 60f
            val startX = (gridDriftX % gridSize)
            var x = startX
            while (x < w) {
                drawLine(
                    color = Color(0x1B2196F3),
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 1.dp.toPx()
                )
                x += gridSize
            }
            var y = 0f
            while (y < h) {
                drawLine(
                    color = Color(0x1B2196F3),
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1.dp.toPx()
                )
                y += gridSize
            }

            // Draw floating air currents (curved waves in background representing light wind)
            val currentPath = androidx.compose.ui.graphics.Path()
            currentPath.moveTo(0f, h * 0.3f)
            currentPath.cubicTo(w * 0.25f, h * 0.15f, w * 0.5f, h * 0.45f, w, h * 0.25f)
            drawPath(
                path = currentPath,
                color = Color(0x0C673AB7),
                style = Stroke(width = 3.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f)))
            )

            // Draw User Location (Radar target beacon)
            userLocation?.let { loc ->
                val ux = loc.first * w
                val uy = loc.second * h

                // Outer pulsing ripples
                drawCircle(
                    color = PrimaryBlue.copy(alpha = (1f - pulseProgress) * 0.25f),
                    radius = 45.dp.toPx() * pulseProgress,
                    center = Offset(ux, uy)
                )
                drawCircle(
                    color = PrimaryBlue.copy(alpha = (1f - pulseProgress) * 0.15f),
                    radius = 80.dp.toPx() * pulseProgress,
                    center = Offset(ux, uy)
                )

                // Glowing anchor shadow
                drawCircle(
                    color = Color(0x15000000),
                    radius = 12.dp.toPx(),
                    center = Offset(ux, uy + 10f)
                )

                // Solid center ring
                drawCircle(
                    color = Color.White,
                    radius = 10.dp.toPx(),
                    center = Offset(ux, uy)
                )
                drawCircle(
                    color = PrimaryBlue,
                    radius = 6.dp.toPx(),
                    center = Offset(ux, uy)
                )
            }

            // Draw active ambulance glide path
            activeDispatch?.let { amb ->
                userLocation?.let { usr ->
                    val sx = amb.latitude * w
                    val sy = amb.longitude * h
                    val tx = usr.first * w
                    val ty = usr.second * h

                    // Draw flight route path
                    drawLine(
                        color = SecondaryPurple.copy(alpha = 0.4f),
                        start = Offset(sx, sy),
                        end = Offset(tx, ty),
                        strokeWidth = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), phase = pulseProgress * 30f)
                    )

                    // Draw arrival rings around destination
                    drawCircle(
                        color = SecondaryPurple.copy(alpha = (1f - pulseProgress) * 0.3f),
                        radius = 30.dp.toPx() * pulseProgress,
                        center = Offset(tx, ty),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // Draw Blood Request Markers
            bloodRequests.forEach { req ->
                val rx = req.latitude * w
                val ry = req.longitude * h + bobbingOffset // Anti-gravity floating offset

                val isSelected = selectedRequest?.id == req.id
                val colorAccent = when (req.urgency) {
                    "Urgent" -> StatusUrgent
                    "Medium" -> StatusMedium
                    else -> StatusLow
                }

                // 1. Hover Shadow underneath
                drawCircle(
                    color = Color(0x1A000000),
                    radius = if (isSelected) 14.dp.toPx() else 8.dp.toPx(),
                    center = Offset(rx, ry + 16f - (bobbingOffset * 0.5f))
                )

                // 2. Pulse radar circle if Urgent
                if (req.urgency == "Urgent") {
                    drawCircle(
                        color = StatusUrgent.copy(alpha = (1f - pulseProgress) * 0.3f),
                        radius = 28.dp.toPx() * pulseProgress,
                        center = Offset(rx, ry)
                    )
                }

                // 3. Selection border outer ring
                if (isSelected) {
                    drawCircle(
                        color = SecondaryPurple.copy(alpha = 0.35f),
                        radius = 20.dp.toPx(),
                        center = Offset(rx, ry),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                // 4. Solid hovering base beacon
                drawCircle(
                    color = Color.White,
                    radius = 15.dp.toPx(),
                    center = Offset(rx, ry)
                )

                // Inner status core
                drawCircle(
                    color = colorAccent,
                    radius = 10.dp.toPx(),
                    center = Offset(rx, ry)
                )

                // White text letter represent blood type
                // (Using a simple cross or drop representation dynamically in paint)
                // Draw a small cross symbol inside
                val crossSize = 4.dp.toPx()
                drawLine(
                    color = Color.White,
                    start = Offset(rx - crossSize, ry),
                    end = Offset(rx + crossSize, ry),
                    strokeWidth = 2.dp.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(rx, ry - crossSize),
                    end = Offset(rx, ry + crossSize),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Draw Ambulance Markers
            ambulances.forEach { amb ->
                // Skip if this is the active dispatched ambulance and drawing in path
                val ax = amb.latitude * w
                val ay = amb.longitude * h + bobbingOffset

                val isSelected = selectedAmbulance?.id == amb.id
                val isDispatched = amb.isDispatched

                // 1. Hover Shadow underneath
                drawCircle(
                    color = Color(0x1F000000),
                    radius = if (isSelected) 16.dp.toPx() else 10.dp.toPx(),
                    center = Offset(ax, ay + 20f - (bobbingOffset * 0.5f))
                )

                // 2. Pulsing green circle if available
                if (amb.isAvailable && !isDispatched) {
                    drawCircle(
                        color = StatusMatched.copy(alpha = (1f - pulseProgress) * 0.25f),
                        radius = 24.dp.toPx() * pulseProgress,
                        center = Offset(ax, ay)
                    )
                }

                // 3. Dispatch flight capsule rings
                if (isDispatched) {
                    drawCircle(
                        color = SecondaryPurple.copy(alpha = (1f - pulseProgress) * 0.4f),
                        radius = 30.dp.toPx() * pulseProgress,
                        center = Offset(ax, ay)
                    )
                }

                // 4. Selection Border
                if (isSelected) {
                    drawCircle(
                        color = PrimaryBlue,
                        radius = 22.dp.toPx(),
                        center = Offset(ax, ay),
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                // 5. White hovering capsule
                drawCircle(
                    color = Color.White,
                    radius = 16.dp.toPx(),
                    center = Offset(ax, ay)
                )

                // 6. Medical Blue inner core
                drawCircle(
                    color = if (isDispatched) SecondaryPurple else PrimaryBlue,
                    radius = 11.dp.toPx(),
                    center = Offset(ax, ay)
                )

                // Draw a tiny H (for Hover Ambulance) or medical cross
                // H symbol lines
                val hSize = 4.dp.toPx()
                drawLine(
                    color = Color.White,
                    start = Offset(ax - hSize, ay - hSize),
                    end = Offset(ax - hSize, ay + hSize),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(ax + hSize, ay - hSize),
                    end = Offset(ax + hSize, ay + hSize),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(ax - hSize, ay),
                    end = Offset(ax + hSize, ay),
                    strokeWidth = 1.5.dp.toPx()
                )
            }
        }

        // Canvas Map overlay labels (anti-gravity grid layout markers)
        Text(
            text = "MediHub Air Corridor Space",
            color = TextDarkMuted.copy(alpha = 0.6f),
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        Text(
            text = "Grid Scale: 100m • Altitude: 20m Hover",
            color = TextDarkMuted.copy(alpha = 0.6f),
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        )
    }
}
