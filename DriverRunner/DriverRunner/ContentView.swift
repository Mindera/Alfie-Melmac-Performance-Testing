import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            Text("Hello, world!")
            
            // Add a button
            Button(action: {
                print("Button tapped!")
            }) {
                Text("Tap Me")
            }
            .accessibilityIdentifier("ButtonIdentifier") // Set the accessibility identifier
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
