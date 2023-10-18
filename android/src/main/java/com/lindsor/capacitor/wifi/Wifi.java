package com.lindsor.capacitor.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Wifi {

    private final Context context;
    private WifiManager wifiManager = null;

    private ConnectivityManager connectivityManager = null;
    private ConnectivityManager.NetworkCallback connectivityCallback = null;

    public Wifi(Context context) {
        this.context = context;
    }

    public void connectToWifiBySsid(String ssid, @Nullable String password, ConnectToWifiCallback connectedCallback) {
        this.ensureWifiManager();

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            this.connectToWifiBySsidAndPasswordLegacy(ssid, password, connectedCallback);
            return;
        }

        WifiNetworkSpecifier.Builder wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder().setIsHiddenSsid(false).setSsid(ssid);

        if (password != null && !"".equals(password)) {
            wifiNetworkSpecifier.setWpa2Passphrase(password);
        }

        NetworkRequest networkRequest = new NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiNetworkSpecifier.build())
            .build();

        if (this.connectivityCallback != null) {
            this.connectivityManager.unregisterNetworkCallback(this.connectivityCallback);
        }
        this.connectivityCallback =
            new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    // To make sure that requests don't go over mobile data
                    connectivityManager.bindProcessToNetwork(network);

                    getWifiBySsid(
                        ssid,
                        new GetWifiCallback() {
                            @Override
                            public void onSuccess(@Nullable WifiEntry wifiEntry) {
                                connectedCallback.onConnected(wifiEntry);
                            }

                            @Override
                            public void onError(WifiError error) {
                                connectedCallback.onConnected(null);
                            }
                        }
                    );
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();

                    WifiError error = new WifiError(WifiErrorCode.FAILED_TO_ENABLE_NETWORK);
                    connectedCallback.onError(error);
                }
            };

        this.connectivityManager.requestNetwork(networkRequest, this.connectivityCallback);
    }

    public void disconnectAndForget() {
        this.ensureWifiManager();

        if (this.connectivityCallback != null) {
            this.connectivityManager.unregisterNetworkCallback(this.connectivityCallback);
            this.connectivityCallback = null;
        }

        if (this.connectivityManager != null) {
            this.connectivityManager.bindProcessToNetwork(null);
        }
    }

    public void connectToWifiBySsidPrefix(String ssidPrefix, @Nullable String password, ConnectToWifiCallback connectedCallback) {
        this.ensureWifiManager();

        this.scanForWifi(
                new ScanWifiCallback() {
                    @Override
                    public void onSuccess(@Nullable ArrayList<WifiEntry> wifis) {
                        if (wifis == null) {
                            WifiError error = new WifiError(WifiErrorCode.FAILED_TO_ENABLE_NETWORK);
                            connectedCallback.onError(error);
                            return;
                        }

                        for (int i = 0; i < wifis.size(); i++) {
                            WifiEntry wifi = wifis.get(i);
                            if (wifi.ssid.startsWith(ssidPrefix)) {
                                connectToWifiBySsid(wifi.ssid, password, connectedCallback);
                                return;
                            }
                        }

                        WifiError error = new WifiError(WifiErrorCode.FAILED_TO_ENABLE_NETWORK);
                        connectedCallback.onError(error);
                    }

                    @Override
                    public void onError(WifiError error) {
                        connectedCallback.onError(error);
                    }
                }
            );
    }

    /**
     * TODO: Implement scan only 4 times every minute as per android 9 docs:
     * <a href="https://developer.android.com/guide/topics/connectivity/wifi-scan">Restrictions</a>
     */
    public void scanForWifi(ScanWifiCallback callback) {
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
            return;
        }

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isSuccess = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

                if (!isSuccess) {
                    callback.onSuccess(getWifiScanCachedResults());
                    return;
                }

                ArrayList<WifiEntry> wifis = getWifiScanCachedResults();

                callback.onSuccess(wifis);
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.context.registerReceiver(wifiScanReceiver, intentFilter);

        if (wifiManager.startScan()) {
            return;
        }

        callback.onSuccess(this.getWifiScanCachedResults());
    }

    public void getCurrentWifi(GetWifiCallback callback) {
        this.ensureWifiManager();

        this.scanForWifi(
                new ScanWifiCallback() {
                    @Override
                    public void onSuccess(@Nullable ArrayList<WifiEntry> wifis) {
                        if (wifis == null) {
                            callback.onSuccess(null);
                            return;
                        }

                        for (int i = 0; i < wifis.size(); i++) {
                            WifiEntry wifi = wifis.get(i);
                            if (wifi.isCurrentWifi) {
                                callback.onSuccess(wifi);
                                return;
                            }
                        }

                        callback.onSuccess(null);
                    }

                    @Override
                    public void onError(WifiError error) {
                        callback.onSuccess(null);
                    }
                }
            );
    }

    public WifiEntry getCurrentWifiCached() {
        ArrayList<WifiEntry> wifis = this.getWifiScanCachedResults();

        for (int i = 0; i < wifis.size(); i++) {
            WifiEntry wifi = wifis.get(i);
            if (wifi.isCurrentWifi) {
                return wifi;
            }
        }

        return null;
    }

    public void getWifiBySsid(String ssid, GetWifiCallback callback) {
        this.ensureWifiManager();
        this.scanForWifi(
                new ScanWifiCallback() {
                    @Override
                    public void onSuccess(@Nullable ArrayList<WifiEntry> wifis) {
                        if (wifis == null) {
                            callback.onSuccess(null);
                            return;
                        }

                        for (int i = 0; i < wifis.size(); i++) {
                            WifiEntry wifi = wifis.get(i);
                            if (wifi.ssid.equals(ssid)) {
                                callback.onSuccess(wifi);
                                return;
                            }
                        }

                        callback.onSuccess(null);
                    }

                    @Override
                    public void onError(WifiError error) {
                        callback.onSuccess(null);
                    }
                }
            );
    }

    public ArrayList<WifiEntry> getWifiScanCachedResults() {
        this.ensureWifiManager();

        final WifiInfo currentWifiInfo = this.wifiManager.getConnectionInfo();
        String currentWifiBssid = currentWifiInfo == null ? null : currentWifiInfo.getBSSID();

        // Permission requested above.
        @SuppressLint("MissingPermission")
        final List<ScanResult> scanResults = this.wifiManager.getScanResults();

        final ArrayList<WifiEntry> wifis = new ArrayList<>();
        for (int i = 0; i < scanResults.size(); i++) {
            final ScanResult scanResult = scanResults.get(i);
            WifiEntry wifiObject = new WifiEntry();

            wifiObject.bssid = scanResult.BSSID;
            wifiObject.level = scanResult.level;
            wifiObject.ssid = getScanResultSsid(scanResult);
            wifiObject.capabilities = getScanResultCapabilities(scanResult);
            wifiObject.isCurrentWifi = wifiObject.bssid.equals(currentWifiBssid);

            wifis.add(wifiObject);
        }

        return wifis;
    }

    private void ensureWifiManager() {
        if (this.wifiManager == null) {
            this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        }

        if (this.connectivityManager == null) {
            this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }

    private String getScanResultSsid(ScanResult scanResult) {
        String ssid;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ssid = scanResult.SSID;
        } else {
            ssid = Objects.requireNonNull(scanResult.getWifiSsid()).toString();

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
    private void connectToWifiBySsidAndPasswordLegacy(String ssid, String password, ConnectToWifiCallback connectedCallback) {
        this.ensureWifiManager();

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
            connectedCallback.onError(error);
            return;
        }

        if (!this.wifiManager.enableNetwork(netId, true)) {
            WifiError error = new WifiError(WifiErrorCode.FAILED_TO_ENABLE_NETWORK);
            connectedCallback.onError(error);
            return;
        }
        if (!this.wifiManager.reconnect()) {
            WifiError error = new WifiError(WifiErrorCode.FAILED_TO_RECONNECT_NETWORK);
            connectedCallback.onError(error);
            return;
        }

        connectedCallback.onConnected(new WifiEntry());
    }
}
