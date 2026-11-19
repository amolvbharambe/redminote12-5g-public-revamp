# Redmi Note 12 5G Optimization & Debloat Suite

A comprehensive, safe, and rootless optimization and debloating script for the **Redmi Note 12 5G** (`sunstone` / `22111317I`) running MIUI 14 or Xiaomi HyperOS on Android 14.

This tool strips out pre-installed adware and tracking telemetry, eliminates background CPU/RAM bottlenecks, improves battery standby with deep sleep tuning, locks silky-smooth 120Hz display refresh rate, and optimizes network and Wi-Fi performance while safeguarding critical daily apps (incoming calls, visitor alerts, and live location sharing).

---

## Features

### 1. Deep System Debloating (25+ Bloatware Packages)
- **Adware & Tracking**: Removes MIUI System Ads (`msa`), Xiaomi Analytics, GetApps, Daemon, Wallpaper Carousel / Glance, Discover, and Xiaomi Services.
- **Background Bloat**: Removes Xiaomi Cleaner (`micleansdk`), Mi Link, Mi Music, Indus Appstore, and WPS Office Lite.
- **Cloud & Thermal Limits**: Removes Joyose (thermal/FPS throttling) and unused Mi Cloud background sync engines.
- **Social & Media Bloat**: Removes Facebook background services and pre-installed demo apps.
- **Protected Apps**: Safely freezes protected packages (ShareMe, Mi Scanner) to zero RAM/CPU usage.

### 2. Display & Fluidity
- **Locked 120Hz Refresh Rate**: Sets both minimum and peak refresh rates to 120.0 Hz, preventing MIUI from downclocking to 60Hz in YouTube, Maps, or browsers.
- **0.5x System Animations**: Speeds up window and transition animations by 2× for an instant snappier feel.

### 3. Battery & Standby Optimization
- **Fast Deep Sleep (Aggressive Doze)**: Tunes `deviceidle` constants so the device enters deep sleep within 2 minutes of screen-off (instead of 30–60 minutes).
- **Cellular Modem Sleep**: Powers down the 5G/4G cellular modem during active Wi-Fi connection to stop idle battery drain.
- **App Standby Buckets**: Places high-wakeup e-commerce and shopping apps into the `RESTRICTED` standby bucket.

### 4. System-Wide Ad-Blocking
- Sets **AdGuard Private DNS** (`dns.adguard-dns.com`) at the system level. Blocks in-app banner ads, popups, and tracking across all apps and browsers with zero battery impact and no VPN required.

### 5. Network & 5G Performance
- **Instant Data Stall Recovery**: Immediately recovers stalled connections instead of hanging for 30 seconds.
- **Dead Wi-Fi Auto-Failover**: Automatically fails over to 5G when a Wi-Fi router loses internet access.
- **5G Carrier Aggregation**: Disables Xiaomi's 5G power-saver throttling to maintain peak throughput and lowest ping.

### 6. Critical App Protection
- **NoBrokerHood / Gate Security**: Whitelisted from Doze, granted Autostart and full-screen intent permissions to ensure gate visitor alerts and intercom calls arrive instantly.
- **WhatsApp Calls & Location**: Whitelisted from Doze, granted Autostart and Picture-in-Picture permissions so incoming calls never miss a beat.
- **Family Live Location**: Ensures Google Maps and tracking apps broadcast continuous GPS pings even while locked in your pocket.

### 7. Hardware & Storage Maintenance
- **UFS Storage FSTRIM**: Trims flash memory blocks to maintain peak write speeds and extend storage life.
- **Logcat Buffer Reduction**: Trims bloated log buffers to free ~15 MB of physical RAM and eliminate background logging interrupts.

---

## Prerequisites

1. An Android phone with **Developer Options** enabled.
2. **USB Debugging** or **Wireless Debugging** turned on.
3. **Android Platform Tools (ADB)** installed on your computer.

---

## Usage

### 1. Connect via ADB

**Over USB**:
Connect your phone with a USB cable and verify:
```bash
adb devices
```

**Over Wi-Fi (Wireless Debugging)**:
1. Go to **Settings → Additional settings → Developer options → Wireless debugging**.
2. Tap **Pair device with pairing code**.
3. Run on your computer:
   ```bash
   adb pair <IP>:<PORT> <PAIRING_CODE>
   adb connect <IP>:<CONNECT_PORT>
   ```

### 2. Run the Script

Clone the repository and execute the optimization script:
```bash
git clone https://github.com/amolvbharambe/redminote12-5g-public-revamp.git
cd redminote12-5g-public-revamp
chmod +x optimize_redminote12.sh
./optimize_redminote12.sh
```

The script will detect your connected device and run all optimization stages in under 30 seconds.

---

## Reverting Changes

All removals are 100% reversible without root or factory resetting.

- **To restore an uninstalled system app**:
  ```bash
  adb shell cmd package install-existing --user 0 <package_name>
  ```
  *Example*: `adb shell cmd package install-existing --user 0 com.miui.player`

- **To re-enable a disabled app**:
  ```bash
  adb shell pm enable <package_name>
  ```

- **To reset animation scales to default (1.0x)**:
  ```bash
  adb shell settings put global window_animation_scale 1.0
  adb shell settings put global transition_animation_scale 1.0
  adb shell settings put global animator_duration_scale 1.0
  ```

- **To restore default Doze settings**:
  ```bash
  adb shell settings delete global deviceidle_constants
  ```

---

## License

This project is licensed under the MIT License.
