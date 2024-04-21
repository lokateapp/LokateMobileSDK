package com.lokate.kmmsdk

import platform.CoreLocation.CLBeaconRegion
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.Foundation.NSLog
import platform.darwin.NSObject

object SharedCLLocationManager {
    val manager: CLLocationManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        CLLocationManager().apply {
            this.delegate = LocationManagerDelegate()
            // Beacon
            this.requestAlwaysAuthorization()
            this.allowsBackgroundLocationUpdates = true
            // Geolocation
            this.desiredAccuracy = kCLLocationAccuracyBest
        }
    }

    // BeaconScanner
    // allow injecting a listener to be called when the location manager authorization status changes
    fun setAuthorizationStatusListener(listener: (Int) -> Unit) {
        (manager.delegate as LocationManagerDelegate).apply {
            authorizationListener =
                if (authorizationListener == null) {
                    listener
                } else
                    {
                        NSLog("Authorization listener already set, will allow re-setting but possible memory leak")
                        listener
                        // throw IllegalStateException("Authorization listener already set")
                    }
        }
    }

    // allow injecting a listener to be called when the location manager authorization status changes
    fun setBeaconRangeListener(listener: (List<*>) -> Unit) {
        (manager.delegate as LocationManagerDelegate).apply {
            beaconRangeListener =
                if (beaconRangeListener == null) {
                    listener
                } else
                    {
                        NSLog("Beacon range listener already set, will allow re-setting but possible memory leak")
                        listener
                        // throw IllegalStateException("Beacon range listener already set")
                    }
        }
    }

    // allow injecting a listener to be called when the location manager authorization status changes
    fun addErrorListener(listener: (NSError) -> Unit) {
        (manager.delegate as LocationManagerDelegate).errorListenerList += listener
    }

    // Geolocation
    // allow injecting a listener to be called when location updates are received
    fun setLocationUpdateListener(listener: (List<*>) -> Unit) {
        (manager.delegate as LocationManagerDelegate).apply {
            locationUpdateListener =
                if (locationUpdateListener == null) {
                    listener
                } else {
                    NSLog("Location update listener already set, will allow re-setting but possible memory leak")
                    listener
                    // throw IllegalStateException("Location update listener already set")
                }
        }
    }

    fun removeLocationUpdateListener() {
        (manager.delegate as LocationManagerDelegate).apply {
            locationUpdateListener = null
            manager.stopUpdatingLocation()
        }
    }

    fun removeErrorListener(listener: (NSError) -> Unit) {
        (manager.delegate as LocationManagerDelegate).errorListenerList -= listener
    }

    fun requestStartUpdatingLocation() {
        manager.startUpdatingLocation()
    }

    fun requestStopUpdatingLocation() {
        manager.stopUpdatingLocation()
    }
}

class LocationManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    var errorListenerList: List<(NSError) -> Unit> = emptyList()
    var authorizationListener: ((Int) -> Unit)? = null
    var beaconRangeListener: ((List<*>) -> Unit)? = null
    var locationUpdateListener: ((List<*>) -> Unit)? = null

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        NSLog("locationManagerDidChangeAuthorization: ${manager.authorizationStatus}")
        authorizationListener?.invoke(manager.authorizationStatus)
    }

    // BeaconScanner
    override fun locationManager(
        manager: CLLocationManager,
        didChangeAuthorizationStatus: Int,
    ) {
        NSLog("locationManager didChangeAuthorizationStatus: Status - $didChangeAuthorizationStatus")
        authorizationListener?.invoke(didChangeAuthorizationStatus)
    }

    override fun locationManager(
        manager: CLLocationManager,
        didRangeBeacons: List<*>,
        inRegion: CLBeaconRegion,
    ) {
        NSLog("locationManager didRangeBeacons: $didRangeBeacons")
        beaconRangeListener?.invoke(didRangeBeacons)
    }

    // Geolocation
    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<*>,
    ) {
        NSLog("locationManager didUpdateLocations: $didUpdateLocations")
        locationUpdateListener?.invoke(didUpdateLocations)
    }

    override fun locationManager(
        manager: CLLocationManager,
        didFailWithError: NSError,
    ) {
        NSLog("locationManager didFailWithError: ${didFailWithError.localizedDescription}")
        errorListenerList.forEach { it(didFailWithError) }
    }
}
