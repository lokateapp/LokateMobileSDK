import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

    init() {
        DIHelperKt.startKoinIBeacon()
    }
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
