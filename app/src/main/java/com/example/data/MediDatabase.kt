package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodRequestDao {
    @Query("SELECT * FROM blood_requests ORDER BY timestamp DESC")
    fun getAllRequests(): Flow<List<BloodRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BloodRequest): Long

    @Query("UPDATE blood_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: Int, status: String)

    @Query("DELETE FROM blood_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Int)
}

@Dao
interface AmbulanceDao {
    @Query("SELECT * FROM ambulances")
    fun getAllAmbulances(): Flow<List<Ambulance>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmbulances(ambulances: List<Ambulance>)

    @Query("UPDATE ambulances SET isAvailable = :isAvailable, isDispatched = :isDispatched, targetLatitude = :targetLat, targetLongitude = :targetLng WHERE id = :id")
    suspend fun updateAmbulanceDispatchState(id: Int, isAvailable: Boolean, isDispatched: Boolean, targetLat: Float, targetLng: Float)

    @Query("UPDATE ambulances SET latitude = :lat, longitude = :lng, etaMinutes = :eta, distance = :dist WHERE id = :id")
    suspend fun updateAmbulanceLocation(id: Int, lat: Float, lng: Float, eta: Int, dist: Float)

    @Query("DELETE FROM ambulances")
    suspend fun clearAll()
}

@Database(entities = [BloodRequest::class, Ambulance::class], version = 1, exportSchema = false)
abstract class MediDatabase : RoomDatabase() {
    abstract fun bloodRequestDao(): BloodRequestDao
    abstract fun ambulanceDao(): AmbulanceDao
}
