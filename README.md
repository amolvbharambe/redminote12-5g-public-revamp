# Redmi Note 12 5G Optimization & Debloat Suite 🚀

A comprehensive, safe, and rootless optimization and debloating suite for the **Redmi Note 12 5G** (`sunstone` / `22111317I`) running MIUI 14 or Xiaomi HyperOS on Android 14.

This project offers two convenient ways to optimize your device:
1. 📱 **Redmi Revamp Hub (On-Device Android App)**: A native Material 3 application that runs directly on your phone using Shizuku (Wireless ADB) — providing real-time modular toggles, 1-tap optimization, and automated 24h background maintenance without needing a PC.
2. 💻 **Rootless ADB Shell Script (`optimize_redminote12.sh`)**: A portable 30-second automated script executable from any PC or Mac.

---

## 📱 Redmi Revamp Hub (Android App)

Located in [`redmi-revamp-hub/`](./redmi-revamp-hub), this on-device app puts full control into a clean, modern user interface:

- **1-Tap Full System Optimization**: Removes 25+ bloat/adware services, locks 120Hz display, speeds animations to 0.5x, activates AdGuard Private DNS, enables Fast Deep Sleep, boosts 5G, and runs UFS fstrim.
- **Real-Time Modular Toggles**:
  - 🔄 **120Hz Display Lock** (Eliminates dynamic 60Hz drops)
  - ⚡ **0.5x Snappy Animations**
  - 🛡️ **AdGuard Private DNS** (OS-level ad-blocking without battery-draining VPNs)
  - 🌙 **Fast Deep Sleep (2-min Doze)** (Enters kernel Doze in 2 minutes instead of 30-60m)
  - 📶 **Modem Sleep on Wi-Fi** (Powers down cellular radio while on Wi-Fi)
  - 🚀 **5G Multi-Carrier Aggregation** (Removes MIUI power-saving throttle)
- **App Standby Optimizer**: Scans and restricts 16+ high-drain shopping/carrier apps (`RESTRICTED` standby bucket).
- **Critical App Safeguards**: Guarantees instant gate approvals (**NoBrokerHood**), incoming video/voice calls (**WhatsApp**), and background family GPS tracking (**Google Maps**, **AirDroid Kids**).
- **Automated Daily 24h Background Maintenance**: Uses Android `WorkManager` to run flash storage TRIM, cache purging, safeguard checks, and new app optimization daily while charging.

### 📥 Prebuilt APK
The compiled APK is ready to install directly from [`redmi-revamp-hub/apk/RedmiRevampHub-v1.0.0.apk`](./redmi-revamp-hub/apk/RedmiRevampHub-v1.0.0.apk).

---

## 💻 CLI Optimization Script (`optimize_redminote12.sh`)

If you prefer running via a computer using ADB:

### Prerequisites
1. Android phone with **Developer Options** enabled.
2. **USB Debugging** or **Wireless Debugging** turned on.
3. **Android Platform Tools (ADB)** installed on your computer.

### Execution
```bash
git clone https://github.com/amolvbharambe/redminote12-5g-public-revamp.git
cd redminote12-5g-public-revamp
chmod +x optimize_redminote12.sh
./optimize_redminote12.sh
```

---

## 🛡️ Critical App Safeguards (Non-Negotiables)
Both the app and CLI script strictly preserve and prioritize everyday essentials:
- **NoBrokerHood**: Placed in `EXEMPTED` standby bucket (Bucket 5), Doze battery whitelist, `USE_FULL_SCREEN_INTENT`, and MIUI Autostart (`MIUIOP 10008`) granted so gate visitor approvals ring instantly.
- **WhatsApp**: Placed in `EXEMPTED` standby bucket, battery whitelisted, `PICTURE_IN_PICTURE` and Autostart enabled, background fine location guaranteed.
- **Family Live Location**: Ensures Google Maps and AirDroid Kids broadcast continuous GPS pings with zero screen-off throttling.

---

## 🔄 Reverting Changes

All optimizations are 100% reversible without root or factory resetting:
- **Restore an uninstalled system app**:
  ```bash
  adb shell cmd package install-existing --user 0 <package_name>
  ```
- **Re-enable a disabled app**:
  ```bash
  adb shell pm enable <package_name>
  ```
- **Reset animation scales to default (1.0x)**:
  ```bash
  adb shell settings put global window_animation_scale 1.0
  adb shell settings put global transition_animation_scale 1.0
  adb shell settings put global animator_duration_scale 1.0
  ```
- **Restore default Doze settings**:
  ```bash
  adb shell settings delete global deviceidle_constants
  ```

---

## 📄 License
This project is licensed under the MIT License.
