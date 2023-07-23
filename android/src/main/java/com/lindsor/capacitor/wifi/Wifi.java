package com.lindsor.capacitor.wifi;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class Wifi {

    private Context context;
    private WifiManager wifiManager = null;

    public Wifi(Context context) {
        this.context = context;
    }

    public void scanForWifi(Context context, PluginCall call) {
        this.ensureWifiManager();

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
            JSObject result = new JSObject();
            result.put("wasSuccess", false);
            result.put("reason", "No permission");
            call.resolve(result);
            return;
        }
        final List<ScanResult> scanResults = this.wifiManager.getScanResults();

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean wasSuccess = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

                JSObject result = new JSObject();
                result.put("wasSuccess", wasSuccess);
                call.resolve(result);
            }
        };
    }

    private void ensureWifiManager() {
        if (this.wifiManager == null) {
            this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        }
    }
}
