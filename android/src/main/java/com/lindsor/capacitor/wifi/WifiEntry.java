package com.lindsor.capacitor.wifi;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import java.util.ArrayList;
import org.json.JSONException;

public class WifiEntry {

    public static final String HIDDEN_SSID = "[HIDDEN_SSID]";

    public String bssid = null;
    public ArrayList<String> capabilities = null;
    public String ssid = null;

    public int level = -1;

    public boolean isCurrentWifi = false;

    public JSObject toCapacitorResult() {
        JSObject result = new JSObject();
        result.put("bssid", this.bssid);
        result.put("level", this.level);
        result.put("isCurrentWifi", this.isCurrentWifi);

        if ("".equals(this.ssid)) {
            result.put("ssid", HIDDEN_SSID);
        } else {
            result.put("ssid", this.ssid);
        }

        JSArray capabilitiesArray = new JSArray();

        if (this.capabilities != null) {
            for (int i = 0; i < this.capabilities.size(); i++) {
                try {
                    capabilitiesArray.put(i, this.capabilities.get(i));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        result.put("capabilities", capabilitiesArray);

        return result;
    }
}
