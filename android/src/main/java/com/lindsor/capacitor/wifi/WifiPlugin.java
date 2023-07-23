package com.lindsor.capacitor.wifi;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Wifi")
public class WifiPlugin extends Plugin {

    private Wifi wifi = new Wifi(getContext());

    @PluginMethod
    public void echo(PluginCall call) {
        wifi.scanForWifi(getContext(), call);
    }
}
