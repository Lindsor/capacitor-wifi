package com.lindsor.capacitor.wifi;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import java.util.ArrayList;
import java.util.List;

public class Wifi {

    private final Context context;
    private WifiManager wifiManager = null;

    public Wifi(Context context) {
        this.context = context;
    }

    public void connectToWifiBySsidAndPassword(PluginCall call, String ssid, String password) {
        this.ensureWifiManager();

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            this.connectToWifiBySsidAndPasswordLegacy(call, ssid, password);
            return;
        }

        WifiNetworkSpecifier.Builder wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
            .setIsHiddenSsid(false)
            .setSsid(ssid)
            .setWpa2Passphrase(password);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier.build())
            .build();

        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);

                // To make sure that requests don't go over mobile data
                connectivityManager.bindProcessToNetwork(network);

                JSObject result = new JSObject();
                result.put("wasSuccess", true);
                call.resolve(result);
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();

                WifiError error = new WifiError(WifiErrorCode.FAILED_TO_ENABLE_NETWORK);
                call.reject(error.toCapacitorRejectCode(), error.toCapacitorResult());
            }
        };

        connectivityManager.requestNetwork(networkRequest, callback);
    }

    public ArrayList<WifiEntry> scanForWifi() {
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
            // TODO: Throw RequiresPermission error
            return new ArrayList<>();
        }

        final WifiInfo currentWifiInfo = this.wifiManager.getConnectionInfo();
        String currentWifiBssid = currentWifiInfo == null ? null : currentWifiInfo.getBSSID();

        final List<ScanResult> scanResults = this.wifiManager.getScanResults();

        final ArrayList<WifiEntry> wifis = new ArrayList<>();
        for (int i = 0; i < scanResults.size(); i++) {
            final ScanResult scanResult = scanResults.get(i);
            WifiEntry wifiObject = new WifiEntry();

            wifiObject.bssid = scanResult.BSSID;
            wifiObject.level = scanResult.level;
            wifiObject.ssid = this.getScanResultSsid(scanResult);
            wifiObject.capabilities = this.getScanResultCapabilities(scanResult);
            wifiObject.isCurrentWifi = wifiObject.bssid.equals(currentWifiBssid);

            wifis.add(wifiObject);
        }

        return wifis;
    }

    public WifiEntry getCurrentWifi() {
        this.ensureWifiManager();

        ArrayList<WifiEntry> wifis = this.scanForWifi();

        for (int i = 0; i < wifis.size(); i++) {
            WifiEntry wifi = wifis.get(i);
            if (wifi.isCurrentWifi) {
                return wifi;
            }
        }

        return null;
    }

    private void ensureWifiManager() {
        if (this.wifiManager == null) {
            this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        }
    }

    private String getScanResultSsid(ScanResult scanResult) {
        String ssid;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ssid = scanResult.SSID;
        } else {
            ssid = scanResult.getWifiSsid().toString();

            // Unwrap the SSID as new Android spec always returns it wrapped
            // ie: SSID = Hello World, returns as SSID = "Hello World"
            if (Boolean.TRUE.equals(ssid.startsWith("\"")) && Boolean.TRUE.equals(ssid.endsWith("\""))) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
        }

        return ssid;
    }

    private ArrayList<String> getScanResultCapabilities(ScanResult scanResult) {
        final ArrayList<String> capabilities = new ArrayList<>();

        if (scanResult.capabilities == null) {
            return capabilities;
        }

        final String[] capabilitiesStrings = scanResult.capabilities.split("]\\[");

        for (String capabilitiesString : capabilitiesStrings) {
            String capabilityString = capabilitiesString;

            if (capabilityString.startsWith("[")) {
                capabilityString = capabilityString.substring(1);
            }

            if (capabilityString.endsWith("]")) {
                capabilityString = capabilityString.substring(0, capabilityString.length() - 1);
            }

            capabilities.add(capabilityString);
        }

        return capabilities;
    }

    // TODO: Remove once no longer needed
    @SuppressWarnings("deprecation")
    private void connectToWifiBySsidAndPasswordLegacy(PluginCall call, String ssid, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = password;

        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        wifiConfig.status = WifiConfiguration.Status.ENABLED;

        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

        int netId = this.wifiManager.addNetwork(wifiConfig);

        if (netId == -1) {
            WifiError error = new WifiError(WifiErrorCode.COULD_NOT_ADD_OR_UPDATE_WIFI_SSID_CONFIG);
            call.reject(error.toCapacitorRejectCode(), error.toCapacitorResult());
            return;
        }

        if (!this.wifiManager.enableNetwork(netId, true)) {
            WifiError error = new WifiError(WifiErrorCode.FAILED_TO_ENABLE_NETWORK);
            call.reject(error.toCapacitorRejectCode(), error.toCapacitorResult());
            return;
        }
        if (!this.wifiManager.reconnect()) {
            WifiError error = new WifiError(WifiErrorCode.FAILED_TO_RECONNECT_NETWORK);
            call.reject(error.toCapacitorRejectCode(), error.toCapacitorResult());
            return;
        }

        JSObject result = new JSObject();
        result.put("wasSuccess", true);
        call.resolve(result);
    }
}
