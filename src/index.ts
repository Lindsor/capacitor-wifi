import { registerPlugin } from '@capacitor/core';

import type { WifiPlugin } from './definitions';

const Wifi = registerPlugin<WifiPlugin>('Wifi', {
  web: () => import('./web').then(m => new m.WifiWeb()),
});

export * from './definitions';
export { Wifi };
