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
    private let beaconScanner: IOSBeaconScanner2
    

    @Published var result: String = "Not Running"

    init(beaconScanner: IOSBeaconScanner2) {
        self.beaconScanner = beaconScanner
    }

    func startScanning() {
        self.beaconScanner.setRegions()
        self.beaconScanner.startScanning()
        //self.beaconScanner.scanResultFlow().collect
    }
    
    func stopScanning(){
        /*self.cancellables.forEach(){job in
            job.close()
        }
        self.cancellables.removeAll()*/
        
        //self.beaconScanner.stopScanning()
        self.result = "Not Running"
    }

}
