import SwiftUI
import kmmsdk

struct ContentView: View {
    @StateObject private var viewModel = MainViewModel(beaconScanner: kmmsdk.IOSBeaconScanner())

    var body: some View {
        VStack {
            Text("Hello, World!")
            Text(viewModel.result)
            Button("Start") {
                viewModel.startScanning()
            }
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
