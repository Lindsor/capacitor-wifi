package com.lindsor.capacitor.wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class Wifi {

    public static final String IS_WRAPPED_IN_QUOTES_PATTERN = "^\".*\"$";

    private Context context;
    private WifiManager wifiManager = null;

    public Wifi(Context context) {
        this.context = context;
    }

    public void scanForWifi(Context context, PluginCall call) {
        this.ensureWifiManager();

        final JSObject result = new JSObject();

        if (
            ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            result.put("wasSuccess", false);
            result.put("reason", "No permission");
            call.resolve(result);
            return;
        }

        final List<ScanResult> scanResults = this.wifiManager.getScanResults();

        final JSArray wifis = new JSArray();
        for (int i = 0; i < scanResults.size(); i++) {
            final ScanResult scanResult = scanResults.get(i);
            WifiEntry wifiObject = new WifiEntry();

            wifiObject.bssid = scanResult.BSSID;
            wifiObject.level = scanResult.level;
            wifiObject.ssid = this.getScanResultSsid(scanResult);
            wifiObject.capabilities = this.getScanResultCapabilities(scanResult);

            try {
                wifis.put(i, wifiObject.toCapacitorResult());
            } catch (JSONException e) {}
        }

        result.put("wifis", wifis);

        call.resolve(result);
    }

    private void ensureWifiManager() {
        if (this.wifiManager == null) {
            this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        }
    }

    private String getScanResultSsid(ScanResult scanResult) {

        String ssid = null;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ssid = scanResult.SSID;
        } else {
            ssid = scanResult.getWifiSsid().toString();

            // Unwrap the SSID as new Android spec always returns it wrapped
            // ie: SSID = Hello World, returns as SSID = "Hello World"
            if (Boolean.TRUE.equals(ssid.matches(IS_WRAPPED_IN_QUOTES_PATTERN))) {
                ssid = ssid.substring(1, ssid.length() - 2);
            }
        }

        return ssid;
    }

    private ArrayList<String> getScanResultCapabilities(ScanResult scanResult) {

        final ArrayList<String> capabilities = new ArrayList();

        if (scanResult.capabilities == null) {
            return capabilities;
        }

        final String[] capabilitiesStrings = scanResult.capabilities.split("]\\[");

        for (int i = 0; i < capabilitiesStrings.length; i++) {
            String capabilityString = capabilitiesStrings[i];

            if (capabilityString.startsWith("[")) {
                capabilityString = capabilityString.substring(1, capabilityString.length() - 1);
            }

            if (capabilityString.endsWith("]")) {
                capabilityString = capabilityString.substring(0, capabilityString.length() - 2);
            }

            capabilities.add(capabilityString);
        }

        return capabilities;
    }
}
