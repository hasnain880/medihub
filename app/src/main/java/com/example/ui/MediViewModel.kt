package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.Ambulance
import com.example.data.BloodRequest
import com.example.data.MediDatabase
import com.example.data.MediRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MediViewModel(application: Application) : AndroidViewModel(application) {

    private val database: MediDatabase by lazy {
        Room.databaseBuilder(
            application,
            MediDatabase::class.java,
            "medihub_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    val repository: MediRepository by lazy {
        MediRepository(
            application,
            database.bloodRequestDao(),
            database.ambulanceDao()
        )
    }

    // Navigation Tab
    private val _currentTab = MutableStateFlow("Blood Donation")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    // Auth State
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    private val _authStatus = MutableStateFlow("Unauthenticated") // Unauthenticated, Authenticating, Authenticated
    val authStatus: StateFlow<String> = _authStatus.asStateFlow()

    // Blood Request Inputs
    private val _bloodType = MutableStateFlow("O+")
    val bloodType: StateFlow<String> = _bloodType.asStateFlow()

    private val _locationName = MutableStateFlow("")
    val locationName: StateFlow<String> = _locationName.asStateFlow()

    private val _urgency = MutableStateFlow("Medium") // Low, Medium, Urgent
    val urgency: StateFlow<String> = _urgency.asStateFlow()

    // Selection on Blood Donation Map
    private val _selectedRequest = MutableStateFlow<BloodRequest?>(null)
    val selectedRequest: StateFlow<BloodRequest?> = _selectedRequest.asStateFlow()

    // Request submission animation flag (floats up)
    private val _isSubmittingRequest = MutableStateFlow(false)
    val isSubmittingRequest: StateFlow<Boolean> = _isSubmittingRequest.asStateFlow()

    // Map Location States for Ambulance Dispatch
    private val _userLocation = MutableStateFlow<Pair<Float, Float>?>(null) // relative coords (0..1, 0..1)
    val userLocation: StateFlow<Pair<Float, Float>?> = _userLocation.asStateFlow()

    private val _isLocating = MutableStateFlow(false)
    val isLocating: StateFlow<Boolean> = _isLocating.asStateFlow()

    // Ambulance selection
    private val _selectedAmbulance = MutableStateFlow<Ambulance?>(null)
    val selectedAmbulance: StateFlow<Ambulance?> = _selectedAmbulance.asStateFlow()

    // Dispatched ambulance gliding variables
    private val _dispatchedAmbulance = MutableStateFlow<Ambulance?>(null)
    val dispatchedAmbulance: StateFlow<Ambulance?> = _dispatchedAmbulance.asStateFlow()

    private val _arrivalCountdown = MutableStateFlow<Int?>(null)
    val arrivalCountdown: StateFlow<Int?> = _arrivalCountdown.asStateFlow()

    private val _isArrived = MutableStateFlow(false)
    val isArrived: StateFlow<Boolean> = _isArrived.asStateFlow()

    // Fade-out animations tracker for completed requests (ID to Float/Anim progress)
    private val _completedAnimatingRequests = MutableStateFlow<Set<Int>>(emptySet())
    val completedAnimatingRequests: StateFlow<Set<Int>> = _completedAnimatingRequests.asStateFlow()

    // Streams from Repository
    val bloodRequests: StateFlow<List<BloodRequest>> by lazy {
        repository.allRequests.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    val ambulances: StateFlow<List<Ambulance>> by lazy {
        repository.allAmbulances.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private var glideJob: Job? = null

    init {
        // Sign-in listener if Firebase Auth works
        viewModelScope.launch {
            try {
                if (repository.isFirebaseAvailable) {
                    val firebaseAuth = FirebaseAuth.getInstance()
                    firebaseAuth.addAuthStateListener { authState ->
                        val currentUser = authState.currentUser
                        if (currentUser != null) {
                            _userEmail.value = currentUser.email
                            _userName.value = currentUser.displayName ?: currentUser.email?.substringBefore("@")
                            _authStatus.value = "Authenticated"
                        } else {
                            _userEmail.value = null
                            _userName.value = null
                            _authStatus.value = "Unauthenticated"
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MediHub", "Firebase Auth init check failed: ${e.message}")
            }
        }

        // Seed default hovering ambulances
        viewModelScope.launch {
            repository.seedDefaultAmbulances()
        }
    }

    // Auth actions
    fun signInWithGoogleSimulation(email: String, name: String) {
        viewModelScope.launch {
            _authStatus.value = "Authenticating"
            delay(1200) // Beautiful authenticating transition
            _userEmail.value = email
            _userName.value = name
            _authStatus.value = "Authenticated"

            // Try to authentic with real Firebase if possible
            if (repository.isFirebaseAvailable) {
                try {
                    val firebaseAuth = FirebaseAuth.getInstance()
                    // Real credentials would go here, but this ensures state is propagated
                    Log.d("MediHub", "Google Sign-In Simulated & Auth synced.")
                } catch (e: Exception) {
                    Log.e("MediHub", "Firebase Auth Sign-In sync failed: ${e.message}")
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            if (repository.isFirebaseAvailable) {
                try {
                    FirebaseAuth.getInstance().signOut()
                } catch (e: Exception) {
                    Log.e("MediHub", "Firebase sign out failed: ${e.message}")
                }
            }
            _userEmail.value = null
            _userName.value = null
            _authStatus.value = "Unauthenticated"
            // Reset state
            _userLocation.value = null
            _selectedAmbulance.value = null
            _dispatchedAmbulance.value = null
            _arrivalCountdown.value = null
            _isArrived.value = false
        }
    }

    // Blood Request Inputs updates
    fun updateBloodType(type: String) { _bloodType.value = type }
    fun updateLocationName(name: String) { _locationName.value = name }
    fun updateUrgency(level: String) { _urgency.value = level }

    fun submitBloodRequest() {
        if (_locationName.value.isBlank()) return

        viewModelScope.launch {
            _isSubmittingRequest.value = true
            delay(800) // Hover and glide up submit animation

            // Random coordinates for map marker placement (normalized 0.1 to 0.9)
            val randomLat = (0.2f + (Math.random() * 0.6f)).toFloat()
            val randomLng = (0.2f + (Math.random() * 0.6f)).toFloat()

            val request = BloodRequest(
                bloodType = _bloodType.value,
                locationName = _locationName.value,
                latitude = randomLat,
                longitude = randomLng,
                urgency = _urgency.value,
                status = "Pending"
            )

            repository.insertRequest(request)

            // Reset inputs & animation states
            _locationName.value = ""
            _isSubmittingRequest.value = false
        }
    }

    fun selectRequestMarker(request: BloodRequest?) {
        _selectedRequest.value = request
    }

    fun setRequestStatus(id: Int, status: String) {
        viewModelScope.launch {
            if (status == "Completed") {
                // Trigger anti-gravity "fade and float upward" animation before removing/marking completed
                _completedAnimatingRequests.value = _completedAnimatingRequests.value + id
                delay(1000) // let the animation finish
                repository.updateRequestStatus(id, "Completed")
                _completedAnimatingRequests.value = _completedAnimatingRequests.value - id
                if (_selectedRequest.value?.id == id) {
                    _selectedRequest.value = null
                }
            } else {
                repository.updateRequestStatus(id, status)
                // update selection detail if open
                if (_selectedRequest.value?.id == id) {
                    _selectedRequest.value = _selectedRequest.value?.copy(status = status)
                }
            }
        }
    }

    // Ambulance Dispatch Functions
    fun acquireUserLocation() {
        viewModelScope.launch {
            _isLocating.value = true
            delay(1500) // Elevate & scan locate animation
            _userLocation.value = Pair(0.5f, 0.5f) // Place user at the center coordinates
            _isLocating.value = false
        }
    }

    fun selectAmbulanceMarker(ambulance: Ambulance?) {
        _selectedAmbulance.value = ambulance
    }

    fun dispatchAmbulance(ambulance: Ambulance) {
        val userLoc = _userLocation.value ?: return
        glideJob?.cancel() // cancel existing glide

        _isArrived.value = false
        _dispatchedAmbulance.value = ambulance.copy(isDispatched = true, targetLatitude = userLoc.first, targetLongitude = userLoc.second)
        _selectedAmbulance.value = null

        // Start smooth gliding coroutine loop
        glideJob = viewModelScope.launch {
            var progress = 0f
            val startLat = ambulance.latitude
            val startLng = ambulance.longitude
            val targetLat = userLoc.first
            val targetLng = userLoc.second
            val initialEta = ambulance.etaMinutes
            val initialDistance = ambulance.distance

            repository.updateAmbulanceDispatchState(
                ambulance.id,
                isAvailable = false,
                isDispatched = true,
                targetLat = targetLat,
                targetLng = targetLng
            )

            // 50 frames of animation over 5 seconds (100ms interval)
            while (progress < 1.0f) {
                progress += 0.02f
                if (progress > 1.0f) progress = 1.0f

                val currentLat = startLat + (targetLat - startLat) * progress
                val currentLng = startLng + (targetLng - startLng) * progress
                val currentEta = (initialEta * (1f - progress)).toInt().coerceAtLeast(0)
                val currentDistance = (initialDistance * (1f - progress))

                // Update flows
                val updatedAmb = _dispatchedAmbulance.value?.copy(
                    latitude = currentLat,
                    longitude = currentLng,
                    etaMinutes = currentEta,
                    distance = currentDistance
                )
                _dispatchedAmbulance.value = updatedAmb
                _arrivalCountdown.value = currentEta

                // Persist live gliding coordinates
                repository.updateAmbulanceLocation(
                    ambulance.id,
                    lat = currentLat,
                    lng = currentLng,
                    eta = currentEta,
                    dist = currentDistance
                )

                delay(100)
            }

            // Arrived!
            _isArrived.value = true
            _arrivalCountdown.value = 0
            _dispatchedAmbulance.value = _dispatchedAmbulance.value?.copy(
                latitude = targetLat,
                longitude = targetLng,
                etaMinutes = 0,
                distance = 0f
            )

            // Update to Arrived state in Database
            repository.updateAmbulanceLocation(
                ambulance.id,
                lat = targetLat,
                lng = targetLng,
                eta = 0,
                dist = 0f
            )
            repository.updateAmbulanceDispatchState(
                ambulance.id,
                isAvailable = true,
                isDispatched = false,
                targetLat = 0f,
                targetLng = 0f
            )

            // Let the arrival banner float for 4 seconds then fade away
            delay(4000)
            _isArrived.value = false
            _dispatchedAmbulance.value = null
            _arrivalCountdown.value = null

            // Seed back to original locations so the demo remains repeatable!
            repository.seedDefaultAmbulances()
        }
    }

    fun resetDispatch() {
        glideJob?.cancel()
        _dispatchedAmbulance.value = null
        _arrivalCountdown.value = null
        _isArrived.value = false
        viewModelScope.launch {
            repository.seedDefaultAmbulances()
        }
    }
}
