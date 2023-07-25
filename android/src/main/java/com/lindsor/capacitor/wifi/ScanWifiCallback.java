package com.lindsor.capacitor.wifi;

import androidx.annotation.Nullable;
import java.util.ArrayList;

public abstract class ScanWifiCallback {

    public abstract void onSuccess(@Nullable ArrayList<WifiEntry> wifis);

    public abstract void onError(WifiError error);
}
