package com.lindsor.capacitor.wifi;

import androidx.annotation.Nullable;

public abstract class ConnectToWifiCallback {

    public abstract void onConnected(@Nullable WifiEntry wifiEntry);

    public abstract void onError(WifiError error);
}
