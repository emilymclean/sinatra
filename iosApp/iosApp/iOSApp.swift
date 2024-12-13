import SwiftUI
import ui
import FirebaseCore

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FirebaseApp.configure()
        return true
    }
}

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
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
