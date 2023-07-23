export enum WifiCapability {
  WPA2_PSK_CCM = 'WPA2-PSK-CCM',
  RSN_PSK_CCMP = 'RSN-PSK-CCMP',
  RSN_SAE_CCM = 'RSN-SAE-CCM',
  WPA2_EAP_SHA1_CCM = 'WPA2-EAP/SHA1-CCM',
  RSN_EAP_SHA1_CCMP = 'RSN-EAP/SHA1-CCMP',
  ESS = 'ESS',
  ES = 'ES',
  WP = 'WP',
}

export enum SpecialSsid {
  HIDDEN = '[HIDDEN_SSID]',
}

export interface WifiEntry {
  bssid: string;
  capabilities: WifiCapability[];
  ssid: string | SpecialSsid;
  level: number;
}

export interface ScanWifiResult {
  wifis: WifiEntry[];
}

export interface GetCurrentWifiResult {
  currentWifi?: WifiEntry;
}

export interface WifiPlugin {
  scanWifi(): Promise<ScanWifiResult>;
  getCurrentWifi(): Promise<GetCurrentWifiResult>;
}
