import SwiftUI
import ui
import FirebaseCore
import FirebaseRemoteConfig

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        
        return true
    }
}

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    init() {
        FirebaseApp.configure()
        InitKt.doInit(
            remoteConfig: RemoteConfigWrapper(),
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

class RemoteConfigWrapper: RemoteConfigProtocol {
    
    var config = RemoteConfig.remoteConfig()
    
    func fetch(forced: Bool, callback: @escaping (KotlinBoolean) -> Void) {
        config.fetch(withExpirationDuration: forced ? 0.0 : 43200.0) { status, error in
            if status == .failure {
                callback(KotlinBoolean(bool: false))
            } else {
                self.config.activate(completion: { changed, error in
                    callback(KotlinBoolean(bool: true))
                })
            }
        }
    }
    
    func string(key: String) -> String? {
        do {
            return try config.configValue(forKey: key).stringValue
        } catch let error {
            print("\(error)")
            return nil
        }
    }
    
    // WTF
    func number(key_: String) -> NSNumber? {
        do {
            return try config.configValue(forKey: key_).numberValue
        } catch let error {
            print("\(error)")
            return nil
        }
    }
    
    func boolean(key_: String) -> KotlinBoolean? {
        do {
            return try KotlinBoolean(bool: config.configValue(forKey: key_).boolValue)
        } catch let error {
            print("\(error)")
            return nil
        }
    }
    
    func exists(key: String) -> Bool {
        do {
            return try config.keys(withPrefix: "").contains(key)
        } catch let error {
            print("\(error)")
            return false
        }
    }
    
}
