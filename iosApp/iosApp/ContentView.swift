import SwiftUI
import shared
import kmmsdk

struct ContentView: View {
    let greet = shared.Greeting().greet()

    let beaconScanner = kmmsdk.IOSBeaconScanner()

    @State private var result: String = "" // Assuming start() returns String, adjust as needed

    var body: some View {
        VStack {
            Text(greet)
            Text(result)
            Button("Start"){
                self.beaconScanner.start()
            }

        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
