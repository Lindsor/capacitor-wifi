package com.lindsor.capacitor.wifi;

import android.util.Log;
import androidx.annotation.Nullable;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.ArrayList;
import org.json.JSONException;

@CapacitorPlugin(name = "Wifi")
public class WifiPlugin extends Plugin {

    private Wifi wifi = null;

    @Override
    public void load() {
        this.wifi = new Wifi(getContext());
    }

    @PluginMethod
    public void connectToWifiBySsidAndPassword(PluginCall call) {
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
    public void scanWifi(PluginCall call) {
        wifi.scanForWifi(
            new ScanWifiCallback() {
                @Override
                public void onSuccess(@Nullable ArrayList<WifiEntry> wifis) {
                    JSArray wifiList = new JSArray();
                    for (int i = 0; i < wifis.size(); i++) {
                        try {
                            wifiList.put(i, wifis.get(i).toCapacitorResult());
                        } catch (JSONException e) {
                            Log.e("WIFI_LOGGER", e.getMessage());
                        }
                    }

                    JSObject result = new JSObject();
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
    public void getCurrentWifi(PluginCall call) {
        JSObject result = new JSObject();
        WifiEntry wifi = this.wifi.getCurrentWifiCached();

        if (wifi == null) {
            result.put("currentWifi", null);
        } else {
            result.put("currentWifi", wifi.toCapacitorResult());
        }
        call.resolve(result);
    }
}
