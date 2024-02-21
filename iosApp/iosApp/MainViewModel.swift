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
    private let lokateSDK: LokateSDK
    

    @Published var result: String = "Not Running"

    init(lokateSDK: LokateSDK) {
        self.lokateSDK = lokateSDK
    }

    func startScanning() {
        self.lokateSDK.startScanning()
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
