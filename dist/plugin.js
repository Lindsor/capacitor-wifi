var capacitorWifi = (function (exports, core) {
    'use strict';

    exports.WifiCapability = void 0;
    (function (WifiCapability) {
        WifiCapability["WPA2_PSK_CCM"] = "WPA2-PSK-CCM";
        WifiCapability["RSN_PSK_CCMP"] = "RSN-PSK-CCMP";
        WifiCapability["RSN_SAE_CCM"] = "RSN-SAE-CCM";
        WifiCapability["WPA2_EAP_SHA1_CCM"] = "WPA2-EAP/SHA1-CCM";
        WifiCapability["RSN_EAP_SHA1_CCMP"] = "RSN-EAP/SHA1-CCMP";
        WifiCapability["ESS"] = "ESS";
        WifiCapability["ES"] = "ES";
        WifiCapability["WP"] = "WP";
    })(exports.WifiCapability || (exports.WifiCapability = {}));
    exports.SpecialSsid = void 0;
    (function (SpecialSsid) {
        SpecialSsid["HIDDEN"] = "[HIDDEN_SSID]";
    })(exports.SpecialSsid || (exports.SpecialSsid = {}));
    exports.WifiErrorCode = void 0;
    (function (WifiErrorCode) {
        WifiErrorCode["COULD_NOT_ADD_OR_UPDATE_WIFI_SSID_CONFIG"] = "COULD_NOT_ADD_OR_UPDATE_WIFI_SSID_CONFIG";
        WifiErrorCode["FAILED_TO_ENABLE_NETWORK"] = "FAILED_TO_ENABLE_NETWORK";
        WifiErrorCode["FAILED_TO_RECONNECT_NETWORK"] = "FAILED_TO_RECONNECT_NETWORK";
        WifiErrorCode["MISSING_SSID_CONNECT_WIFI"] = "MISSING_SSID_CONNECT_WIFI";
        WifiErrorCode["MISSING_PASSWORD_CONNECT_WIFI"] = "MISSING_PASSWORD_CONNECT_WIFI";
        WifiErrorCode["METHOD_UNIMPLEMENTED"] = "METHOD_UNIMPLEMENTED";
    })(exports.WifiErrorCode || (exports.WifiErrorCode = {}));

    const Wifi = core.registerPlugin('Wifi', {
        web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.WifiWeb()),
    });

    class WifiWeb extends core.WebPlugin {
        async scanWifi() {
            throw this.unavailable(exports.WifiErrorCode.METHOD_UNIMPLEMENTED);
        }
        async getCurrentWifi() {
            throw this.unavailable(exports.WifiErrorCode.METHOD_UNIMPLEMENTED);
        }
        async connectToWifiBySsidAndPassword() {
            throw this.unavailable(exports.WifiErrorCode.METHOD_UNIMPLEMENTED);
        }
        async connectToWifiBySsidPrefixAndPassword() {
            throw this.unavailable(exports.WifiErrorCode.METHOD_UNIMPLEMENTED);
        }
        async requestPermissions() {
            throw this.unavailable(exports.WifiErrorCode.METHOD_UNIMPLEMENTED);
        }
        async checkPermissions() {
            throw this.unavailable(exports.WifiErrorCode.METHOD_UNIMPLEMENTED);
        }
        async disconnectAndForget() {
            throw this.unavailable(exports.WifiErrorCode.METHOD_UNIMPLEMENTED);
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        WifiWeb: WifiWeb
    });

    exports.Wifi = Wifi;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
