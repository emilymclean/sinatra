import SwiftUI
import ui

@main
struct iOSApp: App {
    init() {
        InitKt.doInit(
            versionName: Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as! String,
            versionCode:  Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as! String
        )
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
