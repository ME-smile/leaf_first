import Flutter
import UIKit

public class SwiftLeafFirstPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "leaf_first", binaryMessenger: registrar.messenger())
    let instance = SwiftLeafFirstPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    let versionNumber = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as! String
            let buildNumber = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as! String
            print(versionNumber)
            print(buildNumber)
    result("iOS " + UIDevice.current.systemVersion)
  }
}
