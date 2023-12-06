import SwiftUI
import kmmsdk

struct ContentView: View {
    @StateObject private var viewModel = MainViewModel(beaconScanner: kmmsdk.IOSBeaconScanner())

    var body: some View {
        VStack {
            Text("Hello, Region!")
            Text(viewModel.result)
        }.onAppear(){
            viewModel.startScanning()
        }
        .onDisappear(){
            viewModel.stopScanning()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
