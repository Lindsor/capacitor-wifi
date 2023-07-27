import Foundation
import Capacitor
import SystemConfiguration.CaptiveNetwork
import CoreLocation

struct WifiEntry {
    var bssid: String
    var ssid: String = "[HIDDEN_SSID]"
    var level: Int = -1
    var isCurrentWify: Bool = false
    var capabilities: [String] = []
}

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(WifiPlugin)
public class WifiPlugin: CAPPlugin, CLLocationManagerDelegate {

    var _currentCall: CAPPluginCall?
    var _locationManager: CLLocationManager = CLLocationManager()

    private let wifi = Wifi()

    public func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        var locationState = "granted"

        let call: CAPPluginCall = _currentCall! as CAPPluginCall
        _currentCall = nil

        if status != .authorizedAlways && status != .authorizedWhenInUse {
            locationState = "denied"
        } else if status == .restricted {
            locationState = "prompt"
        }

        call.resolve([
            "LOCATION": locationState,
            "NETWORK": "granted"
        ])
    }

    @objc override public func checkPermissions(_ call: CAPPluginCall) {

        var locationState = "granted"

        let locationStatus = CLLocationManager.authorizationStatus()
        if locationStatus != .authorizedAlways && locationStatus != .authorizedWhenInUse {
            locationState = "denied"
        } else if locationStatus == .restricted {
            locationState = "prompt"
        }

        call.resolve([
            "LOCATION": locationState,
            "NETWORK": "granted"
        ])
    }

    @objc override public func requestPermissions(_ call: CAPPluginCall) {

        let locationStatus = CLLocationManager.authorizationStatus()
        if locationStatus != .authorizedAlways && locationStatus != .authorizedWhenInUse {
            _currentCall = call
            _locationManager.delegate = self
            _locationManager.requestWhenInUseAuthorization()
            return
        }

        call.resolve([
            "LOCATION": "granted",
            "NETWORK": "granted"
        ])
    }

    @objc func connectToWifiBySsidAndPassword(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": wifi.echo(value)
        ])
    }

    @objc func scanWifi(_ call: CAPPluginCall) {
        if let interfaces: NSArray = CNCopySupportedInterfaces() {
            var wifis = [WifiEntry]()

            for interface in interfaces {
                let interfaceName = interface as! String

                if let dict = CNCopyCurrentNetworkInfo(interfaceName as CFString) as NSDictionary? {
                    let networkInfo = WifiEntry(
                        bssid: dict[kCNNetworkInfoKeyBSSID as String] as? String ?? "",
                        ssid: dict[kCNNetworkInfoKeySSID as String] as? String ?? "[HIDDEN_SSID]",
                        level: -1,
                        isCurrentWify: false,
                        capabilities: [String]()
                    )
                    wifis.append(networkInfo)
                }
            }

            call.resolve([
                "wifis": wifis
            ] as PluginCallResultData)
            return
        }

        let wifis: [String] = []
        call.resolve(["wifis": wifis])
    }

    @objc func getCurrentWifi(_ call: CAPPluginCall) {
        call.resolve(["currentWifi": ""])
    }
}
