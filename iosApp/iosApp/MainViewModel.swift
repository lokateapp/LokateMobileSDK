//
//  MainViewModel.swift
//  iosApp
//
//  Created by Umut BAŞER on 6.12.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import kmmsdk
import Combine
import SwiftUI

class MainViewModel
: ObservableObject {
    private let beaconScanner: IOSBeaconScanner
    private var cancellables:[Ktor_ioCloseable] = []

    @Published var result: String = "Not Running"

    init(beaconScanner: IOSBeaconScanner) {
        self.beaconScanner = beaconScanner
        self.beaconScanner.setScanPeriod(scanPeriodMillis: 500)
    }

    func startScanning() {
        self.beaconScanner.start()
        self.observeRegion()
    }
    
    func stopScanning(){
        self.cancellables.forEach(){job in
            job.close()
        }
        self.cancellables.removeAll()
        self.beaconScanner.stop()
        self.result = "Not Running"
    }

    private func observeRegion() {
        // Assuming that `observeRegion()` returns `CFlow<BeaconScanResult>`
        self.cancellables.append(
        beaconScanner.observeRegion().watch(){result in
            self.result = "Beacon: \(String(describing: result?.beacon.uuid)):\(String(describing: result?.beacon.major)):\(String(describing: result?.beacon.minor)), RSSI: \(String(describing: result?.rssi)), Distance: \(String(describing: result?.proximity))"        }
        )
    }
}
