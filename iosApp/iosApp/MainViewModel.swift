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
    private var cancellables = Set<AnyCancellable>()

    @Published var result: String = "a"

    init(beaconScanner: IOSBeaconScanner) {
        self.beaconScanner = beaconScanner
    }

    func startScanning() {
        beaconScanner.start()
        observeRegion()
    }

    private func observeRegion() {
        // Assuming that `observeRegion()` returns `CFlow<BeaconScanResult>`
        beaconScanner.observeRegion().watch(){result in
            self.result = "Beacon: \(result?.beacon.uuid):\(result?.beacon.major):\(result?.beacon.minor), RSSI: \(result?.rssi), Distance: \(result?.proximity)"        }
    }
}
