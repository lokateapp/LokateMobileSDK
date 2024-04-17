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
import platform.Foundation.NSLog
import platform.Foundation.timeIntervalSinceNow
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
actual suspend fun getCurrentGeolocation(): Pair<Double, Double> =
    suspendCancellableCoroutine { continuation ->
        NSLog("Requesting current location")
        val manager = SharedCLLocationManager.manager

        fun errorListener(didFailWithError: NSError) {
            NSLog("Failed to get location: ${didFailWithError.localizedDescription}")
            SharedCLLocationManager.requestStopUpdatingLocation()
            SharedCLLocationManager.removeErrorListener(::errorListener)
            SharedCLLocationManager.removeLocationUpdateListener()
            continuation.resumeWithException(RuntimeException("Failed to get location: ${didFailWithError.localizedDescription}"))
        }

        fun locationUpdate(didUpdateLocations: List<*>){
            NSLog("Received location update: $didUpdateLocations")
            val location = didUpdateLocations.firstOrNull() as? CLLocation
            if (location != null) {
                location.coordinate.useContents {
                    SharedCLLocationManager.requestStopUpdatingLocation()
                    SharedCLLocationManager.removeErrorListener(::errorListener)
                    SharedCLLocationManager.removeLocationUpdateListener()
                    continuation.resume(Pair(latitude, longitude))
                }
            } else {
                SharedCLLocationManager.requestStopUpdatingLocation()
                SharedCLLocationManager.removeErrorListener(::errorListener)
                SharedCLLocationManager.removeLocationUpdateListener()
                continuation.resumeWithException(IllegalStateException("Failed to get current location"))
            }
        }

        if (manager.authorizationStatus() == DENIED) {
            NSLog("Location permissions not granted")
            SharedCLLocationManager.requestStopUpdatingLocation()
            SharedCLLocationManager.removeErrorListener(::errorListener)
            SharedCLLocationManager.removeLocationUpdateListener()
            continuation.resumeWithException(UnsupportedOperationException("Location permissions not granted"))
            return@suspendCancellableCoroutine
        }
        // if location is not available or location is more than 30 mins old, request location updates
        if(manager.location == null || (manager.location?.timestamp?.timeIntervalSinceNow
                ?: 0.0) > 60.0 * 30
        ) {
            SharedCLLocationManager.requestStartUpdatingLocation()
            SharedCLLocationManager.addErrorListener(::errorListener)
            SharedCLLocationManager.setLocationUpdateListener(::locationUpdate)
        } else {
            manager.location?.coordinate?.useContents {
                NSLog(manager.location.toString())
                SharedCLLocationManager.requestStopUpdatingLocation()
                SharedCLLocationManager.removeErrorListener(::errorListener)
                SharedCLLocationManager.removeLocationUpdateListener()
                continuation.resume(Pair(latitude, longitude))
            }
        }
    }
