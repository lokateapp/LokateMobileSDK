// import SwiftUI
// import kmmsdk
//
// struct ContentView: View {
//     @StateObject private var viewModel = MainViewModel(lokateSDK: kmmsdk.LokateSDK())
//
//     var body: some View {
//         VStack {
//             Text("Hello, Region!")
//             Text(viewModel.result)
//         }.onAppear(){
//             viewModel.startScanning()
//         }
//         .onDisappear(){
//             viewModel.stopScanning()
//         }
//     }
// }
//
// struct ContentView_Previews: PreviewProvider {
//     static var previews: some View {
//         ContentView()
//     }
// }

import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



