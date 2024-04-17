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

        fun clearLocationListeners() {
            SharedCLLocationManager.requestStopUpdatingLocation()
            SharedCLLocationManager.removeLocationUpdateListener()
        }

        fun errorListener(didFailWithError: NSError) {
            NSLog("Failed to get location: ${didFailWithError.localizedDescription}")
            clearLocationListeners()
            SharedCLLocationManager.removeErrorListener(::errorListener)
            continuation.resumeWithException(RuntimeException("Failed to get location: ${didFailWithError.localizedDescription}"))
        }

        fun locationUpdate(didUpdateLocations: List<*>) {
            NSLog("Received location update: $didUpdateLocations")
            val location = didUpdateLocations.firstOrNull() as? CLLocation
            if (location != null) {
                location.coordinate.useContents {
                    clearLocationListeners()
                    SharedCLLocationManager.removeErrorListener(::errorListener)
                    continuation.resume(Pair(latitude, longitude))
                }
            } else {
                clearLocationListeners()
                SharedCLLocationManager.removeErrorListener(::errorListener)
                continuation.resumeWithException(IllegalStateException("Failed to get current location"))
            }
        }

        if (manager.authorizationStatus() == DENIED) {
            NSLog("Location permissions not granted")
            clearLocationListeners()
            SharedCLLocationManager.removeErrorListener(::errorListener)
            continuation.resumeWithException(UnsupportedOperationException("Location permissions not granted"))
            return@suspendCancellableCoroutine
        }

        SharedCLLocationManager.requestStartUpdatingLocation()
        SharedCLLocationManager.addErrorListener(::errorListener)
        SharedCLLocationManager.setLocationUpdateListener(::locationUpdate)

    }
