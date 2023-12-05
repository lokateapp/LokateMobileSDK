import SwiftUI
import kmmsdk


@main
struct iOSApp: App {
    var kmm = IOSBeaconScanner()
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
