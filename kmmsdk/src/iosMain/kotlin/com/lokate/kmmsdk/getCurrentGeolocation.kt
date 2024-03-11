package com.lokate.kmmsdk

import com.lokate.kmmsdk.utils.DENIED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.CoreLocation.requestWhenInUseAuthorization
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual suspend fun getCurrentGeolocation(): Pair<Double, Double> = suspendCancellableCoroutine { continuation ->
    val locationManager = CLLocationManager()
    locationManager.delegate = object : CLLocationManagerDelegateProtocol {
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            val location = didUpdateLocations.firstOrNull() as? CLLocation
            if (location != null) {
                continuation.resume(Pair(location.coordinate.latitude, location.coordinate.longitude))
            } else {
                continuation.resumeWithException(IllegalStateException("Failed to get current location"))
            }
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            continuation.resumeWithException(RuntimeException("Failed to get location: ${didFailWithError.localizedDescription}"))
        }
    }

    if (CLLocationManager.authorizationStatus() == DENIED) {
        continuation.resumeWithException(SecurityException("Location permissions not granted"))
        return@suspendCancellableCoroutine
    }

    locationManager.requestWhenInUseAuthorization()
    locationManager.desiredAccuracy = kCLLocationAccuracyBest
    locationManager.startUpdatingLocation()
}
