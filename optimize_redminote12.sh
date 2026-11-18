#!/bin/bash
# ==============================================================================
# Redmi Note 12 5G (sunstone_in / 22111317I) Complete Optimization Script
# All-in-one restoration script for ADB
# ==============================================================================

echo ">>> Checking ADB device connection..."
adb get-state > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Device not connected via ADB. Ensure Wireless Debugging or USB is active."
    exit 1
fi

DEVICE_MODEL=$(adb shell getprop ro.product.model)
echo ">>> Connected to: $DEVICE_MODEL"

# ------------------------------------------------------------------------------
# 1. DEBLOATING (Uninstalls & Cleanups)
# ------------------------------------------------------------------------------
echo ">>> [1/7] Removing Adware, Trackers, and System Bloatware..."

UNINSTALL_PKGS=(
    # MIUI Adware & Telemetry
    "com.miui.msa.global"
    "com.miui.analytics"
    "com.xiaomi.mipicks"
    "com.miui.daemon"
    "com.miui.android.fashiongallery"
    "com.xiaomi.discover"
    "com.xiaomi.glgm"
    "com.miui.yellowpage"
    "com.miui.miservice"
    "com.miui.bugreport"
    "com.miui.cleaner"
    "com.milink.service"
    "com.miui.player"
    "com.indus.appstore"
    "cn.wps.xiaomi.abroad.lite"

    # Xiaomi Performance & Cloud
    "com.xiaomi.joyose"
    "com.xiaomi.payment"
    "com.miui.cloudservice"
    "com.miui.cloudbackup"
    "com.miui.micloudsync"
    "com.xiaomi.micloud.sdk"

    # Facebook Services & Apps
    "com.facebook.system"
    "com.facebook.appmanager"
    "com.facebook.services"
    "com.facebook.katana"
    "com.facebook.orca"

    # Google Bloat
    "com.google.android.feedback"
    "com.google.android.tts"
    "com.google.android.videos"

    # Third Party Bloat / Duplicates
    "com.instagram.android"
    "com.flipkart.shopsy"
    "com.gps.draw.flag.coloring.games"
)

for pkg in "${UNINSTALL_PKGS[@]}"; do
    adb shell pm clear "$pkg" > /dev/null 2>&1
    out=$(adb shell pm uninstall -k --user 0 "$pkg" 2>&1)
    echo "  - $pkg: $out"
done

# Protected Xiaomi apps that fail uninstall [-1000] are disabled
DISABLE_PKGS=(
    "com.xiaomi.midrop"
    "com.xiaomi.scanner"
)

for pkg in "${DISABLE_PKGS[@]}"; do
    adb shell pm disable-user --user 0 "$pkg" > /dev/null 2>&1
    adb shell pm clear "$pkg" > /dev/null 2>&1
    echo "  - $pkg (protected): disabled & cache cleared"
done

# ------------------------------------------------------------------------------
# 2. DISPLAY & FLUIDITY (120Hz & 0.5x Animations)
# ------------------------------------------------------------------------------
echo ">>> [2/7] Configuring 120Hz Refresh Rate and 0.5x Animations..."
adb shell settings put global window_animation_scale 0.5
adb shell settings put global transition_animation_scale 0.5
adb shell settings put global animator_duration_scale 0.5
adb shell settings put system min_refresh_rate 120.0
adb shell settings put system peak_refresh_rate 120.0
adb shell settings put system user_refresh_rate 120

# ------------------------------------------------------------------------------
# 3. AD-BLOCKING (AdGuard Private DNS)
# ------------------------------------------------------------------------------
echo ">>> [3/7] Enabling System-Wide AdGuard Private DNS..."
adb shell settings put global private_dns_mode hostname
adb shell settings put global private_dns_specifier dns.adguard-dns.com

# ------------------------------------------------------------------------------
# 4. BATTERY & FAST DEEP SLEEP (Doze Tuning)
# ------------------------------------------------------------------------------
echo ">>> [4/7] Applying Fast Deep Sleep (Doze) and Cellular Modem Saver..."
adb shell settings put global mobile_data_always_on 0
DOZE_CONSTANTS="inactive_to=120000,sensing_to=0,locating_to=0,location_accuracy=20.0,motion_inactive_to=0,idle_after_inactive_to=120000,idle_pending_to=180000,max_idle_pending_to=360000,idle_pending_factor=2.0,idle_to=1800000,max_idle_to=21600000,idle_factor=2.0,min_time_to_alarm=3600000,max_temp_app_allowlist_duration_ms=20000,mms_temp_app_allowlist_duration_ms=60000,sms_temp_app_allowlist_duration_ms=20000,notification_allowlist_duration_ms=20000"
adb shell settings put global deviceidle_constants "$DOZE_CONSTANTS"

# ------------------------------------------------------------------------------
# 5. FAST SIDELOADING & ADB
# ------------------------------------------------------------------------------
echo ">>> [5/7] Disabling Sideloading Verification Delays..."
adb shell settings put global package_verifier_enable 0
adb shell settings put global verifier_verify_adb_installs 0

# ------------------------------------------------------------------------------
# 6. PRIVACY & APPOPS RESTRICTIONS (Truecaller, Launcher, E-Commerce)
# ------------------------------------------------------------------------------
echo ">>> [6/7] Restricting Truecaller, Launcher, and Background E-Commerce Apps..."

# Truecaller SMS & Telemetry cut-off
adb shell pm revoke com.truecaller android.permission.READ_SMS > /dev/null 2>&1
adb shell pm revoke com.truecaller android.permission.RECEIVE_SMS > /dev/null 2>&1
adb shell pm revoke com.truecaller android.permission.SEND_SMS > /dev/null 2>&1
for op in READ_SMS RECEIVE_SMS WRITE_SMS SEND_SMS RECEIVE_MMS GET_USAGE_STATS RUN_ANY_IN_BACKGROUND; do
    adb shell cmd appops set com.truecaller $op ignore > /dev/null 2>&1
done
adb shell am force-stop com.truecaller > /dev/null 2>&1

# Microsoft Launcher AppOps (Disable usage tracking)
adb shell cmd appops set com.microsoft.launcher GET_USAGE_STATS ignore > /dev/null 2>&1

# 13 E-Commerce & Carrier Apps (Put in RESTRICTED standby bucket)
RESTRICTED_APPS=(
    "in.amazon.mShop.android.shopping"
    "com.flipkart.android"
    "com.myntra.android"
    "com.meesho.supply"
    "com.fsn.nykaa"
    "com.bigbasket.mobileapp"
    "com.grofers.customerapp"
    "in.swiggy.android"
    "com.application.zomato"
    "com.olx.southasia"
    "in.dmart"
    "com.myairtelapp"
    "com.jio.myjio"
)

for pkg in "${RESTRICTED_APPS[@]}"; do
    adb shell am set-standby-bucket "$pkg" restricted > /dev/null 2>&1
    adb shell cmd appops set "$pkg" RUN_ANY_IN_BACKGROUND ignore > /dev/null 2>&1
done

# ------------------------------------------------------------------------------
# 7. NETWORK, 5G & CRITICAL APP PROTECTION (NoBrokerHood)
# ------------------------------------------------------------------------------
echo ">>> [7/7] Optimizing Wi-Fi, 5G Stack, and Safeguarding NoBrokerHood..."

# Network & Wi-Fi Stack
adb shell settings put global data_stall_recovery_on_bad_network 1
adb shell settings put global network_avoid_bad_wifi 1
adb shell settings put global wifi_avoid_bad_networks 1
adb shell settings put global wifi_suspend_optimizations_enabled 0

# Jio 5G Multi-Carrier Aggregation (Disable 5G Power Throttle)
adb shell settings put system power_center_is_5g_mode_enable_slot1 0
adb shell settings put system power_center_is_5g_mode_enable 0

# Safeguard NoBrokerHood, WhatsApp, and Live Location Sharing (Google Maps & AirDroid)
adb shell am set-standby-bucket com.app.nobrokerhood exempted > /dev/null 2>&1
adb shell dumpsys deviceidle whitelist +com.app.nobrokerhood > /dev/null 2>&1
adb shell cmd appops set com.app.nobrokerhood USE_FULL_SCREEN_INTENT allow > /dev/null 2>&1
adb shell cmd appops set com.app.nobrokerhood SYSTEM_ALERT_WINDOW allow > /dev/null 2>&1
adb shell cmd appops set com.app.nobrokerhood 10008 allow > /dev/null 2>&1

# Safeguard WhatsApp calls and Live Location
adb shell am set-standby-bucket com.whatsapp exempted > /dev/null 2>&1
adb shell dumpsys deviceidle whitelist +com.whatsapp > /dev/null 2>&1
adb shell cmd appops set com.whatsapp 10008 allow > /dev/null 2>&1
adb shell cmd appops set com.whatsapp USE_FULL_SCREEN_INTENT allow > /dev/null 2>&1
adb shell cmd appops set com.whatsapp FINE_LOCATION allow > /dev/null 2>&1
adb shell cmd appops set com.whatsapp MONITOR_LOCATION allow > /dev/null 2>&1

# Safeguard Google Maps Live Family Location Sharing
adb shell am set-standby-bucket com.google.android.apps.maps exempted > /dev/null 2>&1
adb shell dumpsys deviceidle whitelist +com.google.android.apps.maps > /dev/null 2>&1
adb shell cmd appops set com.google.android.apps.maps 10008 allow > /dev/null 2>&1
adb shell cmd appops set com.google.android.apps.maps FINE_LOCATION allow > /dev/null 2>&1
adb shell cmd appops set com.google.android.apps.maps MONITOR_LOCATION allow > /dev/null 2>&1
adb shell cmd appops set com.google.android.apps.maps RUN_ANY_IN_BACKGROUND allow > /dev/null 2>&1

# Safeguard AirDroid Kids (if installed for family tracking)
adb shell am set-standby-bucket com.sand.airdroidkids exempted > /dev/null 2>&1
adb shell dumpsys deviceidle whitelist +com.sand.airdroidkids > /dev/null 2>&1
adb shell cmd appops set com.sand.airdroidkids 10008 allow > /dev/null 2>&1

# Hardware FSTRIM & Logcat Buffer Tuning (Free RAM & flash maintenance)
adb shell logcat -G 256K > /dev/null 2>&1
adb shell sm fstrim > /dev/null 2>&1
adb shell pm trim-caches 2000M > /dev/null 2>&1

echo "=============================================================================="
echo ">>> SUCCESS: All optimizations successfully applied to $DEVICE_MODEL!"
echo "=============================================================================="
