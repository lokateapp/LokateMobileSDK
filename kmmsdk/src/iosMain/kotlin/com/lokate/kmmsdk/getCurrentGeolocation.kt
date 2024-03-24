package com.lokate.kmmsdk

import com.lokate.kmmsdk.utils.DENIED
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
actual suspend fun getCurrentGeolocation(): Pair<Double, Double> =
    suspendCancellableCoroutine { continuation ->
        val locationManager = CLLocationManager()
        locationManager.delegate =
            object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>,
                ) {
                    val location = didUpdateLocations.firstOrNull() as? CLLocation
                    if (location != null) {
                        location.coordinate.useContents {
                            continuation.resume(Pair(latitude, longitude))
                        }
                    } else {
                        continuation.resumeWithException(IllegalStateException("Failed to get current location"))
                    }
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError,
                ) {
                    continuation.resumeWithException(RuntimeException("Failed to get location: ${didFailWithError.localizedDescription}"))
                }
            }

        if (CLLocationManager.authorizationStatus() == DENIED) {
            continuation.resumeWithException(UnsupportedOperationException("Location permissions not granted"))
            return@suspendCancellableCoroutine
        }

        locationManager.requestWhenInUseAuthorization()
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.startUpdatingLocation()
    }
