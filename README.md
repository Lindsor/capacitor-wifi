# ln-capacitor-wifi

Connect to Wifi through your capacitor plugin. Good for IoT device connections.

## Install

```bash
npm install ln-capacitor-wifi
npx cap sync
```

## API

<docgen-index>

* [`scanWifi()`](#scanwifi)
* [`getCurrentWifi()`](#getcurrentwifi)
* [`connectToWifiBySsidAndPassword(...)`](#connecttowifibyssidandpassword)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### scanWifi()

```typescript
scanWifi() => Promise<ScanWifiResult>
```

**Returns:** <code>Promise&lt;<a href="#scanwifiresult">ScanWifiResult</a>&gt;</code>

--------------------


### getCurrentWifi()

```typescript
getCurrentWifi() => Promise<GetCurrentWifiResult>
```

**Returns:** <code>Promise&lt;<a href="#getcurrentwifiresult">GetCurrentWifiResult</a>&gt;</code>

--------------------


### connectToWifiBySsidAndPassword(...)

```typescript
connectToWifiBySsidAndPassword(connectToWifiRequest: ConnectToWifiRequest) => Promise<ConnectToWifiResult>
```

| Param                      | Type                                                                  |
| -------------------------- | --------------------------------------------------------------------- |
| **`connectToWifiRequest`** | <code><a href="#connecttowifirequest">ConnectToWifiRequest</a></code> |

**Returns:** <code>Promise&lt;<a href="#connecttowifiresult">ConnectToWifiResult</a>&gt;</code>

--------------------


### Interfaces


#### ScanWifiResult

| Prop        | Type                     |
| ----------- | ------------------------ |
| **`wifis`** | <code>WifiEntry[]</code> |


#### WifiEntry

| Prop                | Type                          |
| ------------------- | ----------------------------- |
| **`bssid`**         | <code>string</code>           |
| **`capabilities`**  | <code>WifiCapability[]</code> |
| **`ssid`**          | <code>string</code>           |
| **`level`**         | <code>number</code>           |
| **`isCurrentWifi`** | <code>boolean</code>          |


#### GetCurrentWifiResult

| Prop              | Type                                            |
| ----------------- | ----------------------------------------------- |
| **`currentWifi`** | <code><a href="#wifientry">WifiEntry</a></code> |


#### ConnectToWifiResult

| Prop             | Type              |
| ---------------- | ----------------- |
| **`wasSuccess`** | <code>true</code> |


#### ConnectToWifiRequest

| Prop           | Type                |
| -------------- | ------------------- |
| **`ssid`**     | <code>string</code> |
| **`password`** | <code>string</code> |


### Enums


#### WifiCapability

| Members                 | Value                            |
| ----------------------- | -------------------------------- |
| **`WPA2_PSK_CCM`**      | <code>'WPA2-PSK-CCM'</code>      |
| **`RSN_PSK_CCMP`**      | <code>'RSN-PSK-CCMP'</code>      |
| **`RSN_SAE_CCM`**       | <code>'RSN-SAE-CCM'</code>       |
| **`WPA2_EAP_SHA1_CCM`** | <code>'WPA2-EAP/SHA1-CCM'</code> |
| **`RSN_EAP_SHA1_CCMP`** | <code>'RSN-EAP/SHA1-CCMP'</code> |
| **`ESS`**               | <code>'ESS'</code>               |
| **`ES`**                | <code>'ES'</code>                |
| **`WP`**                | <code>'WP'</code>                |


#### SpecialSsid

| Members      | Value                        |
| ------------ | ---------------------------- |
| **`HIDDEN`** | <code>'[HIDDEN_SSID]'</code> |

</docgen-api>
