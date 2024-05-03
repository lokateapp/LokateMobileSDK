package com.lokate.kmmsdk

import com.lokate.kmmsdk.di.SDKKoinComponent
import com.lokate.kmmsdk.utils.DENIED
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.component.get
import platform.CoreLocation.CLLocation
import platform.Foundation.NSError
import platform.Foundation.NSLog
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GeolocationHelper : SDKKoinComponent() {
    val sharedCLLocationManager: SharedCLLocationManager = get()
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun getCurrentGeolocation(): Pair<Double, Double> =
    suspendCancellableCoroutine { continuation ->
        NSLog("Requesting current location")
        val sharedCLLocationManager = GeolocationHelper.sharedCLLocationManager
        val manager = sharedCLLocationManager.manager

        fun clearLocationListeners() {
            sharedCLLocationManager.requestStopUpdatingLocation()
            sharedCLLocationManager.removeLocationUpdateListener()
        }

        fun errorListener(didFailWithError: NSError) {
            NSLog("Failed to get location: ${didFailWithError.localizedDescription}")
            clearLocationListeners()
            sharedCLLocationManager.removeErrorListener(::errorListener)
            continuation.resumeWithException(RuntimeException("Failed to get location: ${didFailWithError.localizedDescription}"))
        }

        fun locationUpdate(didUpdateLocations: List<*>) {
            NSLog("Received location update: $didUpdateLocations")
            val location = didUpdateLocations.firstOrNull() as? CLLocation
            if (location != null) {
                location.coordinate.useContents {
                    clearLocationListeners()
                    sharedCLLocationManager.removeErrorListener(::errorListener)
                    continuation.resume(Pair(latitude, longitude))
                }
            } else {
                clearLocationListeners()
                sharedCLLocationManager.removeErrorListener(::errorListener)
                continuation.resumeWithException(IllegalStateException("Failed to get current location"))
            }
        }

        if (manager.authorizationStatus() == DENIED) {
            NSLog("Location permissions not granted")
            clearLocationListeners()
            sharedCLLocationManager.removeErrorListener(::errorListener)
            continuation.resumeWithException(UnsupportedOperationException("Location permissions not granted"))
            return@suspendCancellableCoroutine
        }

        sharedCLLocationManager.requestStartUpdatingLocation()
        sharedCLLocationManager.addErrorListener(::errorListener)
        sharedCLLocationManager.setLocationUpdateListener(::locationUpdate)
    }
