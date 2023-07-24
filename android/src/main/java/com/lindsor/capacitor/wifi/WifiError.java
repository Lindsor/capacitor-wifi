package com.lindsor.capacitor.wifi;

import com.getcapacitor.JSObject;

public class WifiError {

    public WifiErrorCode code;
    public Exception exception;

    public WifiError(WifiErrorCode code) {
        this(code, code.name());
    }

    public WifiError(WifiErrorCode code, String debugMessage) {
        this.code = code;
        this.exception = new Exception(debugMessage);
    }

    public String toCapacitorRejectCode() {
        return this.code.name();
    }

    public JSObject toCapacitorResult() {
        JSObject result = new JSObject();

        result.put("errorCode", this.code.name());

        return result;
    }
}
