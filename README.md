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
* [`connectToWifiBySsidPrefixAndPassword(...)`](#connecttowifibyssidprefixandpassword)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`disconnectAndForget()`](#disconnectandforget)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)
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


### connectToWifiBySsidPrefixAndPassword(...)

```typescript
connectToWifiBySsidPrefixAndPassword(connectToWifiPrefixRequest: ConnectToWifiPrefixRequest) => Promise<ConnectToWifiResult>
```

| Param                            | Type                                                                              |
| -------------------------------- | --------------------------------------------------------------------------------- |
| **`connectToWifiPrefixRequest`** | <code><a href="#connecttowifiprefixrequest">ConnectToWifiPrefixRequest</a></code> |

**Returns:** <code>Promise&lt;<a href="#connecttowifiresult">ConnectToWifiResult</a>&gt;</code>

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### requestPermissions()

```typescript
requestPermissions() => Promise<PermissionStatus>
```

**Returns:** <code>Promise&lt;<a href="#permissionstatus">PermissionStatus</a>&gt;</code>

--------------------


### disconnectAndForget()

```typescript
disconnectAndForget() => Promise<void>
```

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

| Prop             | Type                                            |
| ---------------- | ----------------------------------------------- |
| **`wasSuccess`** | <code>true</code>                               |
| **`wifi`**       | <code><a href="#wifientry">WifiEntry</a></code> |


#### ConnectToWifiRequest

| Prop           | Type                |
| -------------- | ------------------- |
| **`ssid`**     | <code>string</code> |
| **`password`** | <code>string</code> |


#### ConnectToWifiPrefixRequest

| Prop             | Type                |
| ---------------- | ------------------- |
| **`ssidPrefix`** | <code>string</code> |
| **`password`**   | <code>string</code> |


#### PermissionStatus

| Prop           | Type                                                        |
| -------------- | ----------------------------------------------------------- |
| **`LOCATION`** | <code><a href="#permissionstate">PermissionState</a></code> |
| **`NETWORK`**  | <code><a href="#permissionstate">PermissionState</a></code> |


### Type Aliases


#### PermissionState

<code>'prompt' | 'prompt-with-rationale' | 'granted' | 'denied'</code>


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
