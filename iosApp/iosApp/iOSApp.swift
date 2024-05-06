import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        DIHelperKt.startKoinIBeacon()
        //DIHelperKt.startKoinEstimote()
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
