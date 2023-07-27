#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(WifiPlugin, "Wifi",
           CAP_PLUGIN_METHOD(checkPermissions, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(requestPermissions, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(connectToWifiBySsidAndPassword, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(connectToWifiBySsidPrefixAndPassword, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(scanWifi, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(getCurrentWifi, CAPPluginReturnPromise);
)
