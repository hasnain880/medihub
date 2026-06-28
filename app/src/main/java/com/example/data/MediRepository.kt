package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

class MediRepository(
    private val context: Context,
    private val bloodRequestDao: BloodRequestDao,
    private val ambulanceDao: AmbulanceDao
) {
    private var firestore: FirebaseFirestore? = null
    private var auth: FirebaseAuth? = null
    var isFirebaseAvailable: Boolean = false
        private set

    init {
        try {
            // Check if Firebase is configured in the app
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firestore = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                isFirebaseAvailable = true
                Log.d("MediHub", "Firebase initialized successfully.")
            } else {
                Log.w("MediHub", "Firebase is not configured. Running in Local Sandbox Mode.")
            }
        } catch (e: Exception) {
            Log.e("MediHub", "Firebase initialization failed: ${e.message}. Running in Local Sandbox Mode.")
        }
    }

    // Requests
    val allRequests: Flow<List<BloodRequest>> = bloodRequestDao.getAllRequests()

    suspend fun insertRequest(request: BloodRequest): Long {
        val id = bloodRequestDao.insertRequest(request)
        val requestWithId = request.copy(id = id.toInt())
        
        // Sync to Firestore if available
        if (isFirebaseAvailable) {
            try {
                firestore?.collection("blood_requests")
                    ?.document(id.toString())
                    ?.set(requestWithId)
            } catch (e: Exception) {
                Log.e("MediHub", "Firestore insert failed: ${e.message}")
            }
        }
        return id
    }

    suspend fun updateRequestStatus(id: Int, status: String) {
        bloodRequestDao.updateRequestStatus(id, status)
        if (isFirebaseAvailable) {
            try {
                firestore?.collection("blood_requests")
                    ?.document(id.toString())
                    ?.update("status", status)
            } catch (e: Exception) {
                Log.e("MediHub", "Firestore update status failed: ${e.message}")
            }
        }
    }

    suspend fun deleteRequest(id: Int) {
        bloodRequestDao.deleteRequestById(id)
        if (isFirebaseAvailable) {
            try {
                firestore?.collection("blood_requests")
                    ?.document(id.toString())
                    ?.delete()
            } catch (e: Exception) {
                Log.e("MediHub", "Firestore delete failed: ${e.message}")
            }
        }
    }

    // Ambulances
    val allAmbulances: Flow<List<Ambulance>> = ambulanceDao.getAllAmbulances()

    suspend fun insertAmbulances(ambulances: List<Ambulance>) {
        ambulanceDao.insertAmbulances(ambulances)
    }

    suspend fun updateAmbulanceDispatchState(id: Int, isAvailable: Boolean, isDispatched: Boolean, targetLat: Float, targetLng: Float) {
        ambulanceDao.updateAmbulanceDispatchState(id, isAvailable, isDispatched, targetLat, targetLng)
    }

    suspend fun updateAmbulanceLocation(id: Int, lat: Float, lng: Float, eta: Int, dist: Float) {
        ambulanceDao.updateAmbulanceLocation(id, lat, lng, eta, dist)
    }

    suspend fun seedDefaultAmbulances() {
        val defaults = listOf(
            Ambulance(id = 1, codeName = "AMB-01", driverName = "Dr. Stephen Strange", distance = 1.2f, etaMinutes = 4, latitude = 0.25f, longitude = 0.35f),
            Ambulance(id = 2, codeName = "AMB-08", driverName = "Tony Stark", distance = 3.5f, etaMinutes = 11, latitude = 0.65f, longitude = 0.25f),
            Ambulance(id = 3, codeName = "AMB-15", driverName = "Natasha Romanoff", distance = 0.8f, etaMinutes = 3, latitude = 0.45f, longitude = 0.75f),
            Ambulance(id = 4, codeName = "AMB-24", driverName = "Bruce Banner", distance = 5.2f, etaMinutes = 16, latitude = 0.15f, longitude = 0.55f)
        )
        ambulanceDao.insertAmbulances(defaults)
    }
}
