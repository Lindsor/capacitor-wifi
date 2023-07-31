package com.lindsor.capacitor.wifi;

import android.Manifest;
import android.util.Log;
import androidx.annotation.Nullable;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.util.ArrayList;
import org.json.JSONException;

@CapacitorPlugin(
    name = "Wifi",
    permissions = {
        @Permission(alias = "LOCATION", strings = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }),
        @Permission(
            alias = "NETWORK",
            strings = {
                Manifest.permission.INTERNET,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE
            }
        )
    }
)
public class WifiPlugin extends Plugin {

    private Wifi wifi = null;

    @Override
    public void load() {
        this.wifi = new Wifi(getContext());
    }

    @PluginMethod
    @PermissionCallback
    public void connectToWifiBySsidAndPassword(PluginCall call) {
        if (!hasRequiredPermissions()) {
            requestAllPermissions(call, "connectToWifiBySsidAndPassword");
            return;
        }

        String ssid = call.getString("ssid");
        String password = call.getString("password");

        if (ssid == null || "".equals(ssid)) {
            WifiError error = new WifiError(WifiErrorCode.MISSING_SSID_CONNECT_WIFI);
            call.reject(error.code.name(), error.toCapacitorResult());
            return;
        }

        ConnectToWifiCallback callback = new ConnectToWifiCallback() {
            @Override
            public void onConnected(WifiEntry wifiEntry) {
                JSObject result = new JSObject();
                result.put("wasSuccess", true);

                if (wifiEntry != null) {
                    result.put("wifi", wifiEntry.toCapacitorResult());
                }

                call.resolve(result);
            }

            @Override
            public void onError(WifiError error) {
                call.reject(error.toCapacitorRejectCode(), error.toCapacitorResult());
            }
        };

        wifi.connectToWifiBySsid(ssid, password, callback);
    }

    @PluginMethod
    @PermissionCallback
    public void connectToWifiBySsidPrefixAndPassword(PluginCall call) {
        if (!hasRequiredPermissions()) {
            requestAllPermissions(call, "connectToWifiBySsidPrefixAndPassword");
            return;
        }

        String ssidPrefix = call.getString("ssidPrefix");
        String password = call.getString("password");

        if (ssidPrefix == null || "".equals(ssidPrefix)) {
            WifiError error = new WifiError(WifiErrorCode.MISSING_SSID_CONNECT_WIFI);
            call.reject(error.code.name(), error.toCapacitorResult());
            return;
        }

        ConnectToWifiCallback callback = new ConnectToWifiCallback() {
            @Override
            public void onConnected(WifiEntry wifiEntry) {
                JSObject result = new JSObject();
                result.put("wasSuccess", true);

                if (wifiEntry != null) {
                    result.put("wifi", wifiEntry.toCapacitorResult());
                }

                call.resolve(result);
            }

            @Override
            public void onError(WifiError error) {
                call.reject(error.toCapacitorRejectCode(), error.toCapacitorResult());
            }
        };

        wifi.connectToWifiBySsidPrefix(ssidPrefix, password, callback);
    }

    @PluginMethod
    @PermissionCallback
    public void scanWifi(PluginCall call) {
        if (!hasRequiredPermissions()) {
            requestAllPermissions(call, "scanWifi");
            return;
        }

        wifi.scanForWifi(
            new ScanWifiCallback() {
                @Override
                public void onSuccess(@Nullable ArrayList<WifiEntry> wifis) {
                    JSObject result = new JSObject();

                    JSArray wifiList = new JSArray();
                    if (wifis == null) {
                        result.put("wifis", wifiList);
                        call.resolve(result);
                        return;
                    }

                    for (int i = 0; i < wifis.size(); i++) {
                        try {
                            wifiList.put(i, wifis.get(i).toCapacitorResult());
                        } catch (JSONException e) {
                            Log.e("WIFI_LOGGER", e.getMessage());
                        }
                    }

                    result.put("wifis", wifiList);
                    call.resolve(result);
                }

                @Override
                public void onError(WifiError error) {
                    JSObject result = new JSObject();
                    result.put("wifis", null);
                    call.resolve(result);
                }
            }
        );
    }

    @PluginMethod
    @PermissionCallback
    public void getCurrentWifi(PluginCall call) {
        if (!hasRequiredPermissions()) {
            requestAllPermissions(call, "getCurrentWifi");
            return;
        }

        JSObject result = new JSObject();
        WifiEntry wifi = this.wifi.getCurrentWifiCached();

        if (wifi == null) {
            result.put("currentWifi", null);
        } else {
            result.put("currentWifi", wifi.toCapacitorResult());
        }
        call.resolve(result);
    }

    @PluginMethod
    @PermissionCallback
    public void disconnectAndForget(PluginCall call) {
        if (!hasRequiredPermissions()) {
            requestAllPermissions(call, "disconnectAndForget");
            return;
        }

        JSObject result = new JSObject();
        this.wifi.disconnectAndForget();
        result.put("wasSuccess", true);
        call.resolve(result);
    }
}
