package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MediViewModel
import com.example.ui.components.AmbulanceDispatchTab
import com.example.ui.components.AuthScreen
import com.example.ui.components.BloodDonationTab
import com.example.ui.components.MediHeader
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MediViewModel = viewModel()

                val authStatus by viewModel.authStatus.collectAsStateWithLifecycle()
                val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
                val userName by viewModel.userName.collectAsStateWithLifecycle()

                val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SpaceSkyLight.copy(alpha = 0.5f),
                                    Color.White,
                                    SpacePurpleLight.copy(alpha = 0.5f)
                                )
                            )
                        ),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    if (authStatus != "Authenticated") {
                        AuthScreen(
                            onSignIn = { email, name -> viewModel.signInWithGoogleSimulation(email, name) },
                            isFirebaseAvailable = viewModel.repository.isFirebaseAvailable,
                            authStatus = authStatus
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding()) // Respect edge-to-edge layout offsets
                        ) {
                            MediHeader(
                                currentTab = currentTab,
                                onTabSelected = { viewModel.selectTab(it) },
                                userName = userName,
                                userEmail = userEmail,
                                onSignOut = { viewModel.signOut() }
                            )

                            when (currentTab) {
                                "Blood Donation" -> {
                                    val requests by viewModel.bloodRequests.collectAsStateWithLifecycle()
                                    val selectedRequest by viewModel.selectedRequest.collectAsStateWithLifecycle()
                                    val bloodType by viewModel.bloodType.collectAsStateWithLifecycle()
                                    val locationName by viewModel.locationName.collectAsStateWithLifecycle()
                                    val urgency by viewModel.urgency.collectAsStateWithLifecycle()
                                    val isSubmitting by viewModel.isSubmittingRequest.collectAsStateWithLifecycle()
                                    val completedAnimatingRequests by viewModel.completedAnimatingRequests.collectAsStateWithLifecycle()

                                    BloodDonationTab(
                                        bloodRequests = requests,
                                        selectedRequest = selectedRequest,
                                        onSelectRequest = { viewModel.selectRequestMarker(it) },
                                        bloodType = bloodType,
                                        locationName = locationName,
                                        urgency = urgency,
                                        isSubmitting = isSubmitting,
                                        completedAnimatingRequests = completedAnimatingRequests,
                                        onBloodTypeChange = { viewModel.updateBloodType(it) },
                                        onLocationNameChange = { viewModel.updateLocationName(it) },
                                        onUrgencyChange = { viewModel.updateUrgency(it) },
                                        onSubmitRequest = { viewModel.submitBloodRequest() },
                                        onRequestStatusUpdate = { id, status -> viewModel.setRequestStatus(id, status) }
                                    )
                                }
                                "Ambulance Dispatch" -> {
                                    val ambulances by viewModel.ambulances.collectAsStateWithLifecycle()
                                    val selectedAmbulance by viewModel.selectedAmbulance.collectAsStateWithLifecycle()
                                    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
                                    val isLocating by viewModel.isLocating.collectAsStateWithLifecycle()
                                    val dispatchedAmbulance by viewModel.dispatchedAmbulance.collectAsStateWithLifecycle()
                                    val arrivalCountdown by viewModel.arrivalCountdown.collectAsStateWithLifecycle()
                                    val isArrived by viewModel.isArrived.collectAsStateWithLifecycle()

                                    AmbulanceDispatchTab(
                                        ambulances = ambulances,
                                        selectedAmbulance = selectedAmbulance,
                                        onSelectAmbulance = { viewModel.selectAmbulanceMarker(it) },
                                        userLocation = userLocation,
                                        isLocating = isLocating,
                                        dispatchedAmbulance = dispatchedAmbulance,
                                        arrivalCountdown = arrivalCountdown,
                                        isArrived = isArrived,
                                        onAcquireLocation = { viewModel.acquireUserLocation() },
                                        onDispatchAmbulance = { viewModel.dispatchAmbulance(it) },
                                        onResetDispatch = { viewModel.resetDispatch() }
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
