import { WebPlugin } from '@capacitor/core';

import type { ScanWifiResult, WifiPlugin } from './definitions';

export class WifiWeb extends WebPlugin implements WifiPlugin {
  async scanWifi(): Promise<ScanWifiResult> {
    return {
      wifis: [],
    };
  }
}
