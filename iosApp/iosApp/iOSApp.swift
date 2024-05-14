import SwiftUI
import ComposeApp
import EstimoteUWB



@main
struct iOSApp: App {

    var c: EstimoteUWBScanner
    init() {
        c = EstimoteUWBScanner()
        c.setupDeviceMapping([
            "f3613d5155850d268974c849baa5db14":("5D72CC30-5C61-4C09-889F-9AE750FA84EC", 1, 3), // coconut to white
            "13d27a2ae64fdf4148dd8a54e316c830":("5D72CC30-5C61-4C09-889F-9AE750FA84EC", 1, 4), // lemon to yellow
            "aa1b7a0afd00308e5f8f27ef86039c3e":("5D72CC30-5C61-4C09-889F-9AE750FA84EC", 1, 2) // caramel to red
        ])
        //DIHelperKt.scannerInjection(scanner: UWBScanner())
        DIHelperKt.scannerInjection(scanner: c)
        
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}

class EstimoteUWBScanner: LSDKBeaconScanner{
    let a = SpecialBeaconScannerType()
    var b: EstimoteUWBBeaconScannerManager?
    var deviceMapping: [String: (uuid: String, major: Int, minor: Int)] = [:]
    
    init(){
        b = EstimoteUWBBeaconScannerManager(scanner: self)
    }
    
    func scanResultFlow() -> any Kotlinx_coroutines_coreFlow {
        return a.scanResultFlow()
    }
    
    func onBeaconPositionUpdate(device: EstimoteUWBDevice){
        if let mapping = deviceMapping[device.publicIdentifier] {
            print(mapping)
            print(device.distance)
            print(Double(device.distance))
            a.emitScanResult(result: LSDKBeaconScanResult(beaconUUID: mapping.uuid, major: Int32(mapping.major), minor: Int32(mapping.minor), rssi: 0.0, txPower: 0, accuracy: Double(device.distance), seen: Int64(NSDate().timeIntervalSince1970)*1000))
                }
    }
    
    func setupDeviceMapping(_ mapping: [String: (String, Int, Int)]) {
            deviceMapping = mapping
        }
    
    func setRegions(regions: [LSDKLokateBeacon]) {
        
    }
    
    func startScanning() {
        b?.startScanning()
    }
    
    func stopScanning() {
        b?.stopScanning()
    }
    
    
}

class EstimoteUWBBeaconScannerManager: NSObject, ObservableObject {
    private var uwbManager: EstimoteUWBManager?
    private weak var scanner: EstimoteUWBScanner?
    
    init(scanner: EstimoteUWBScanner) {
        super.init()
        self.scanner = scanner
        setupUWB()
    }

    private func setupUWB() {
        uwbManager = EstimoteUWBManager(delegate: self,
                                        options: EstimoteUWBOptions(shouldHandleConnectivity: true,
                                                                    isCameraAssisted: false))
    }
    
    func startScanning(){
        uwbManager?.startScanning()
    }
    
    func stopScanning(){
        uwbManager?.stopScanning()
    }
}

extension EstimoteUWBBeaconScannerManager: EstimoteUWBManagerDelegate {
    func didUpdatePosition(for device: EstimoteUWBDevice) {
        print("Position updated for device: \(device)")
        scanner?.onBeaconPositionUpdate(device: device)
    }
    
    func didDiscover(device: UWBIdentifiable, with rssi: NSNumber, from manager: EstimoteUWBManager) {
        print("Discovered device: \(device.publicIdentifier) rssi: \(rssi)")
    }
    
    func didConnect(to device: UWBIdentifiable) {
        print("Successfully connected to: \(device.publicIdentifier)")
    }
    
    func didDisconnect(from device: UWBIdentifiable, error: Error?) {
        print("Disconnected from device: \(device.publicIdentifier)- error: \(String(describing: error))")
    }
    
    func didFailToConnect(to device: UWBIdentifiable, error: Error?) {
        print("Failed to conenct to: \(device.publicIdentifier) - error: \(String(describing: error))")
    }
}
