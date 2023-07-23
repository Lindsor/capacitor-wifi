import { WebPlugin } from '@capacitor/core';

import type {
  GetCurrentWifiResult,
  ScanWifiResult,
  WifiPlugin,
} from './definitions';

export class WifiWeb extends WebPlugin implements WifiPlugin {
  async scanWifi(): Promise<ScanWifiResult> {
    return {
      wifis: [],
    };
  }

  async getCurrentWifi(): Promise<GetCurrentWifiResult> {
    return {
      currentWifi: undefined,
    };
  }
}
