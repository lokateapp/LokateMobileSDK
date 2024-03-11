package com.lokate.kmmsdk

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual suspend fun getCurrentGeolocation(): Pair<Double, Double> = suspendCancellableCoroutine { continuation ->
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

    if (ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        continuation.resumeWithException(SecurityException("Location permissions not granted"))
        return@suspendCancellableCoroutine
    }

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                continuation.resume(Pair(location.latitude, location.longitude))
            } else {
                continuation.resumeWithException(IllegalStateException("Last known location is null"))
            }
        }
        .addOnFailureListener { e ->
            continuation.resumeWithException(e)
        }
}
