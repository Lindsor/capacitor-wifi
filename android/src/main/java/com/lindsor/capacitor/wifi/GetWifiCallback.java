package com.lindsor.capacitor.wifi;

import androidx.annotation.Nullable;

public abstract class GetWifiCallback {

    public abstract void onSuccess(@Nullable WifiEntry wifiEntry);

    public abstract void onError(WifiError error);
}
