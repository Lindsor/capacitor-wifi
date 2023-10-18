import Foundation
import Capacitor
import SystemConfiguration.CaptiveNetwork
import CoreLocation
import NetworkExtension

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

        if _currentCall == nil {
            return
        }

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

    @objc func connectToWifiBySsidPrefixAndPassword(_ call: CAPPluginCall) {
        let ssidPrefix: String = call.getString("ssidPrefix", "")
        let _: String? = call.getString("password")

        print("CONNECTING", ssidPrefix)

        let hotspotConfig = NEHotspotConfiguration(
            ssidPrefix: ssidPrefix
        )

        hotspotConfig.joinOnce = true

        NEHotspotConfigurationManager.shared.apply(hotspotConfig) { (error) in
            if let error = error {
                print("error = ", error)
                call.reject("MISSING_SSID_CONNECT_WIFI")
                return
            } else {
                print("Success!")
            }

            let currentWifi: WifiEntry? = self.getCurrentWifiInfo()

            call.resolve([
                "wasSuccess": true,
                "wifi": self.wifiEntryToWifiDict(wifiEntry: currentWifi) as Any
            ])
        }
    }

    @objc func connectToWifiBySsidAndPassword(_ call: CAPPluginCall) {
        let hotspotConfig = NEHotspotConfiguration(
            ssid: call.getString("ssid", ""),
            passphrase: call.getString("password", ""),
            isWEP: false
        )

        NEHotspotConfigurationManager.shared.apply(hotspotConfig) { (error) in
            if let error = error {
                print("error = ", error)
            } else {
                print("Success!")
            }

            call.resolve(["wasSuccess": true])
        }
    }

    @objc func scanWifi(_ call: CAPPluginCall) {
        if let _: NSArray = CNCopySupportedInterfaces() {

            let currentWifi: WifiEntry? = getCurrentWifiInfo()

            if currentWifi == nil {
                call.resolve([
                    "wifis": [] as [String]
                ])
                return
            }

            var wifis: [[String: Any]] = []
            let currentWifiDictionary: [String: Any] = wifiEntryToWifiDict(wifiEntry: currentWifi)!
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

        if wifiEntry == nil {
            call.resolve(["currentWifi": ""])
        } else {
            call.resolve([
                "currentWifi": wifiEntryToWifiDict(wifiEntry: wifiEntry) as Any
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

    func wifiEntryToWifiDict(wifiEntry: WifiEntry?) -> [String: Any]? {
        if wifiEntry == nil {
            return nil
        }

        return [
            "bssid": wifiEntry?.bssid ?? "",
            "ssid": wifiEntry?.ssid ?? "[HIDDEN_SSID]",
            "isCurrentWifi": wifiEntry?.isCurrentWify ?? false,
            "level": -1,
            "capabilities": [String]()
        ] as [String: Any]
    }
}
