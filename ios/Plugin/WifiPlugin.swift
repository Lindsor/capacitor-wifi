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
        if _: NSArray = CNCopySupportedInterfaces() {
            
            let currentWifi: WifiEntry? = getCurrentWifiInfo()
            
            if (currentWifi == nil) {
                call.resolve([
                    "wifis": [] as Array<String>
                ])
                return;
            }

            var wifis: Array<Dictionary<String, Any>> = []
            let currentWifiDictionary: Dictionary<String, Any> = [
                "bssid": currentWifi?.bssid ?? "",
                "ssid": currentWifi?.ssid ?? "[HIDDEN_SSID]",
                "isCurrentWifi": currentWifi?.isCurrentWify ?? false,
                "level": -1,
                "capabilities": [String](),
            ]
            wifis.append(currentWifiDictionary)
            call.resolve([
                "wifis": wifis
            ] as PluginCallResultData)
            return
        }

        let wifis: [String] = []
        call.resolve(["wifis": wifis])
    }

    @objc func getCurrentWifi(_ call: CAPPluginCall) {
        let wifiEntry: WifiEntry? = getCurrentWifiInfo()
        
        if (wifiEntry == nil) {
            call.resolve(["currentWifi": ""])
        } else {
            call.resolve([
                "currentWifi": [
                    "bssid": wifiEntry?.bssid ?? "",
                    "ssid": wifiEntry?.ssid ?? "[HIDDEN_SSID]",
                    "isCurrentWifi": wifiEntry?.isCurrentWify ?? false,
                    "level": -1,
                    "capabilities": [String](),
                ] as [String : Any]
            ])
        }
        
    }
    
    func getCurrentWifiInfo() -> WifiEntry? {
        if let interfaces = CNCopySupportedInterfaces() as NSArray? {
            for interface in interfaces {
                if let interfaceInfo = CNCopyCurrentNetworkInfo(interface as! CFString) as NSDictionary? {
                    let wifiEntry: WifiEntry = WifiEntry(
                        bssid: interfaceInfo[kCNNetworkInfoKeyBSSID as String] as? String ?? "",
                        ssid: interfaceInfo[kCNNetworkInfoKeySSID as String] as? String ?? "[HIDDEN_SSID]",
                        isCurrentWify: true
                    )
                    return wifiEntry
                }
            }
        }
        return nil
    }
}
