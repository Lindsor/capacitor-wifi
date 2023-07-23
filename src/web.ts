import { WebPlugin } from '@capacitor/core';

import type { WifiPlugin } from './definitions';

export class WifiWeb extends WebPlugin implements WifiPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
