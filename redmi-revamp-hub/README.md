# Redmi Revamp Hub 🚀

**Redmi Revamp Hub** is a native, modern Material 3 Android utility crafted specifically for the **Redmi Note 12 5G** (`sunstone` / `22111317I`) running MIUI / HyperOS. 

It provides an on-device UI interface to manage, toggle, and automate all system debloating, hardware performance tuning, and battery optimizations directly on the phone via **Shizuku (Wireless ADB)** or root — **with zero tethering to a PC** and **zero risk to critical services**.

---

## 🌟 Key Features & Capabilities

### 1. ⚡ 1-Tap Full System Optimization Suite
Performs the complete performance overhaul in seconds:
- **Comprehensive Debloat**: Clears data and uninstalls/freezes 25+ MIUI adware services, analytics engines, analytics daemons, GetApps, and Joyose thermal throttling limiters.
- **Display Overhaul**: Permanently locks display refresh rate to **120Hz** (`min_refresh_rate=120`, `peak_refresh_rate=120`, `user_refresh_rate=120`) eliminating MIUI dynamic 60Hz drops.
- **Snappy UI Motion**: Sets window, transition, and animator duration scales to **0.5x** for immediate touch responsiveness.
- **System-Wide Ad-Blocking**: Enables secure encrypted AdGuard Private DNS (`dns.adguard-dns.com`) at OS level, blocking ads in apps and browsers without draining battery through local VPNs.
- **Fast Deep Sleep (Aggressive Doze)**: Tunes kernel Doze parameters to enter deep sleep within **2 minutes** of screen-off (default Android takes 30-60 minutes).
- **5G Multi-Carrier Aggregation**: Removes MIUI carrier aggregation restrictions for unthrottled 5G speeds and lower latency.
- **Storage & RAM Maintenance**: Executes hardware `sm fstrim` across UFS flash storage and trims app cache up to 2GB.

### 2. 🛡️ Non-Negotiable Critical Safeguards
Ensures everyday family and communication services are never killed or delayed by battery saver:
- **NoBrokerHood**: Placed in `EXEMPTED` standby bucket (Bucket 5), Doze battery whitelist, `USE_FULL_SCREEN_INTENT`, and MIUI Autostart (`MIUIOP 10008`) granted so gate approval calls ring instantly.
- **WhatsApp**: Placed in `EXEMPTED` standby bucket, battery whitelisted, `PICTURE_IN_PICTURE` and Autostart enabled, and background fine location guaranteed for flawless video calls and live location sharing.
- **Google Maps Family Live Location**: Placed in `EXEMPTED` bucket, battery whitelisted, background fine location tracking (`MONITOR_LOCATION`, `RUN_ANY_IN_BACKGROUND`) enabled for continuous screen-off location sharing.
- **AirDroid Kids**: Battery whitelisted with autostart enabled for family tracking.

### 3. 🎛️ Modular Quick Toggles
Individual real-time controls with persistent settings:
- **120Hz Display Lock** (Toggle between 120Hz Locked and Dynamic)
- **0.5x Animation Speed** (Toggle between 0.5x Snappy and 1.0x Stock)
- **AdGuard Private DNS** (Toggle between Ad-Blocked and Default)
- **Fast Deep Sleep (2-min Doze)** (Toggle between 2-minute Doze and Stock Android 30m Doze)
- **Modem Sleep on Wi-Fi** (Disables continuous mobile data radio when Wi-Fi is connected, saving battery)
- **5G Multi-Carrier Boost** (Removes 5G throttling limits)

### 4. 🛒 Background App Standby Optimizer
Automatically scans third-party e-commerce, delivery, and carrier apps (Amazon, Flipkart, Myntra, Meesho, Nykaa, BigBasket, Blinkit, Swiggy, Zomato, MyAirtel, MyJio, CRED, Snapdeal, Tata Neu, etc.):
- Puts them into Android's `RESTRICTED` standby bucket.
- Denies `RUN_ANY_IN_BACKGROUND` wake locks, stopping background battery drain, background tracking, and promotional push notifications.

### 5. ⏰ Automated Daily 24h Background Maintenance
Integrated with Android `WorkManager`:
- Automatically triggers once every 24 hours while the phone is connected to a charger.
- Runs `sm fstrim` to maintain UFS flash storage write speeds and prevent lag over months of use.
- Re-enforces critical safeguards (NoBrokerHood, WhatsApp, Maps) in case a system update reverted them.
- Purges junk runtime caches and limits `logcat` buffer to 256KB to keep RAM free.
- Detects newly installed shopping apps and restricts them automatically.
- Sends a silent completion notification upon finishing.

---

## 📱 How It Works (Shizuku Architecture)

Redmi Revamp Hub communicates with Android's system server using **Shizuku**:
1. No Root is required.
2. Shizuku uses Android's built-in **Wireless Debugging** interface (`adb`).
3. Once Shizuku is started, Redmi Revamp Hub executes high-privilege commands (`pm`, `am`, `settings`, `cmd appops`, `dumpsys`, `sm fstrim`) securely directly on the phone.

---

## 🚀 Quick Start / Installation

1. **Install Shizuku**:
   - Download and install **Shizuku** from Google Play Store or GitHub.
   - Open Shizuku -> Start via **Wireless Debugging** (Settings -> Developer options -> Wireless debugging -> Pair with Shizuku).
2. **Install Redmi Revamp Hub**:
   - Install `RedmiRevampHub-v1.0.0.apk` from the `apk/` directory or build from source using Android Studio.
3. **Open Redmi Revamp Hub**:
   - Grant Shizuku permission when prompted.
   - Tap **"Run Full System Optimization"** for an immediate 1-tap overhaul.
   - Leave **"Daily Background Maintenance"** enabled for automatic 24-hour maintenance.

---

## 🛠️ Building from Source

```bash
git clone https://github.com/amolvbharambe/redminote12-5g-public-revamp.git
cd redminote12-5g-public-revamp/redmi-revamp-hub
./gradlew assembleDebug
```
The output APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.

---

## 📄 License
Open source and released under the MIT License.
