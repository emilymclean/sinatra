import SwiftUI
import ui

@main
struct iOSApp: App {
    init() {
        InitKt.doInit()
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
