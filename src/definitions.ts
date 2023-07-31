import type { PermissionState } from '@capacitor/core';

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
  isCurrentWifi: boolean;
}

export interface ScanWifiResult {
  wifis: WifiEntry[];
}

export interface GetCurrentWifiResult {
  currentWifi?: WifiEntry;
}

export interface ConnectToWifiResult {
  wasSuccess: true;
  wifi?: WifiEntry;
}

export interface ConnectToWifiRequest {
  ssid: string;
  password?: string;
}

export interface ConnectToWifiPrefixRequest {
  ssidPrefix: string;
  password?: string;
}

export enum WifiErrorCode {
  COULD_NOT_ADD_OR_UPDATE_WIFI_SSID_CONFIG = 'COULD_NOT_ADD_OR_UPDATE_WIFI_SSID_CONFIG',
  FAILED_TO_ENABLE_NETWORK = 'FAILED_TO_ENABLE_NETWORK',
  FAILED_TO_RECONNECT_NETWORK = 'FAILED_TO_RECONNECT_NETWORK',
  MISSING_SSID_CONNECT_WIFI = 'MISSING_SSID_CONNECT_WIFI',
  MISSING_PASSWORD_CONNECT_WIFI = 'MISSING_PASSWORD_CONNECT_WIFI',
  METHOD_UNIMPLEMENTED = 'METHOD_UNIMPLEMENTED',
}

export interface WifiError {
  code: WifiErrorCode;
}

export interface PermissionStatus {
  LOCATION: PermissionState;
  NETWORK: PermissionState;
}

export interface WifiPlugin {
  scanWifi(): Promise<ScanWifiResult>;
  getCurrentWifi(): Promise<GetCurrentWifiResult>;
  connectToWifiBySsidAndPassword(
    connectToWifiRequest: ConnectToWifiRequest,
  ): Promise<ConnectToWifiResult>;
  connectToWifiBySsidPrefixAndPassword(
    connectToWifiPrefixRequest: ConnectToWifiPrefixRequest,
  ): Promise<ConnectToWifiResult>;

  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;

  disconnectAndForget(): Promise<void>;
}
