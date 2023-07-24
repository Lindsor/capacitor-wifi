import { WebPlugin } from '@capacitor/core';

import type {
  ConnectToWifiRequest,
  ConnectToWifiResult,
  GetCurrentWifiResult,
  ScanWifiResult,
  WifiPlugin,
} from './definitions';
import { WifiErrorCode } from './definitions';

export class WifiWeb extends WebPlugin implements WifiPlugin {
  async scanWifi(): Promise<ScanWifiResult> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }

  async getCurrentWifi(): Promise<GetCurrentWifiResult> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }

  async connectToWifiBySsidAndPassword(
    _request: ConnectToWifiRequest,
  ): Promise<ConnectToWifiResult> {
    throw this.unavailable(WifiErrorCode.METHOD_UNIMPLEMENTED);
  }
}
