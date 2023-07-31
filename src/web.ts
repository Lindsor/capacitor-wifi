import { WebPlugin } from '@capacitor/core';

import type {
  ConnectToWifiResult,
  GetCurrentWifiResult,
  ScanWifiResult,
  WifiPlugin,
  PermissionStatus,
} from './definitions';
import { WifiErrorCode } from './definitions';

export class WifiWeb extends WebPlugin implements WifiPlugin {
  async scanWifi(): Promise<ScanWifiResult> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }

  async getCurrentWifi(): Promise<GetCurrentWifiResult> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }

  async connectToWifiBySsidAndPassword(): Promise<ConnectToWifiResult> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }

  async connectToWifiBySsidPrefixAndPassword(): Promise<ConnectToWifiResult> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }

  async requestPermissions(): Promise<PermissionStatus> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }

  async checkPermissions(): Promise<PermissionStatus> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }

  async disconnectAndForget(): Promise<void> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }
}
