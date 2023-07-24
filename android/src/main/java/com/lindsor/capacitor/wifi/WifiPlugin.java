package com.lindsor.capacitor.wifi;

import android.util.Log;

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
    public void scanWifi(PluginCall call) {
        ArrayList<WifiEntry> wifis = wifi.scanForWifi();

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

    @PluginMethod
    public void getCurrentWifi(PluginCall call) {
        JSObject result = new JSObject();
        WifiEntry currentWifi = this.wifi.getCurrentWifi();

        if (currentWifi == null) {
            result.put("currentWifi", null);
        } else {
            result.put("currentWifi", currentWifi.toCapacitorResult());
        }

        call.resolve(result);
    }
}
