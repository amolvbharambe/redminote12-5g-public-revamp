package com.redmi.revamphub;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OptimizationEngine {

    public static final String[] BLOATWARE_PACKAGES = {
        // MIUI Adware & Telemetry
        "com.miui.msa.global",
        "com.miui.analytics",
        "com.xiaomi.mipicks",
        "com.miui.daemon",
        "com.miui.android.fashiongallery",
        "com.xiaomi.discover",
        "com.xiaomi.glgm",
        "com.miui.yellowpage",
        "com.miui.miservice",
        "com.miui.bugreport",
        "com.miui.cleaner",
        "com.milink.service",
        "com.miui.player",
        "com.indus.appstore",
        "cn.wps.xiaomi.abroad.lite",

        // Xiaomi Cloud & Performance Limiter
        "com.xiaomi.joyose",
        "com.xiaomi.payment",
        "com.miui.cloudservice",
        "com.miui.cloudbackup",
        "com.miui.micloudsync",
        "com.xiaomi.micloud.sdk",

        // Facebook Services & Unused Apps
        "com.facebook.system",
        "com.facebook.appmanager",
        "com.facebook.services",
        "com.facebook.katana",
        "com.facebook.orca",

        // Google Bloat
        "com.google.android.feedback",
        "com.google.android.tts",
        "com.google.android.videos",

        // Pre-installed Third-Party Bloat
        "com.instagram.android",
        "com.flipkart.shopsy",
        "com.gps.draw.flag.coloring.games"
    };

    public static final String[] PROTECTED_PACKAGES = {
        "com.xiaomi.midrop",
        "com.xiaomi.scanner"
    };

    public static final Set<String> HIGH_WAKEUP_CANDIDATES = new HashSet<>(Arrays.asList(
        "in.amazon.mShop.android.shopping",
        "com.flipkart.android",
        "com.myntra.android",
        "com.meesho.supply",
        "com.fsn.nykaa",
        "com.bigbasket.mobileapp",
        "com.grofers.customerapp",
        "in.swiggy.android",
        "com.application.zomato",
        "com.olx.southasia",
        "in.dmart",
        "com.myairtelapp",
        "com.jio.myjio",
        "com.dreamplug.androidapp",
        "com.snapdeal.main",
        "com.tatadigital.tcp"
    ));

    public static List<String> getFullOptimizationCommands() {
        List<String> list = new ArrayList<>();

        // 1. Debloat
        for (String pkg : BLOATWARE_PACKAGES) {
            list.add("pm clear " + pkg + " 2>/dev/null; pm uninstall -k --user 0 " + pkg);
        }
        for (String pkg : PROTECTED_PACKAGES) {
            list.add("pm disable-user --user 0 " + pkg + "; pm clear " + pkg + " 2>/dev/null");
        }

        // 2. Display & Animations
        list.add("settings put system min_refresh_rate 120.0");
        list.add("settings put system peak_refresh_rate 120.0");
        list.add("settings put system user_refresh_rate 120");
        list.add("settings put global window_animation_scale 0.5");
        list.add("settings put global transition_animation_scale 0.5");
        list.add("settings put global animator_duration_scale 0.5");

        // 3. Ad-Blocking Private DNS
        list.add("settings put global private_dns_mode hostname");
        list.add("settings put global private_dns_specifier dns.adguard-dns.com");

        // 4. Battery & Fast Deep Sleep
        list.add("settings put global mobile_data_always_on 0");
        String dozeConstants = "inactive_to=120000,sensing_to=0,locating_to=0,location_accuracy=20.0,motion_inactive_to=0,idle_after_inactive_to=120000,idle_pending_to=180000,max_idle_pending_to=360000,idle_pending_factor=2.0,idle_to=1800000,max_idle_to=21600000,idle_factor=2.0,min_time_to_alarm=3600000,max_temp_app_allowlist_duration_ms=20000,mms_temp_app_allowlist_duration_ms=60000,sms_temp_app_allowlist_duration_ms=20000,notification_allowlist_duration_ms=20000";
        list.add("settings put global deviceidle_constants " + dozeConstants);

        // 5. Fast Sideloading
        list.add("settings put global package_verifier_enable 0");
        list.add("settings put global verifier_verify_adb_installs 0");

        // 6. Network & 5G Performance
        list.add("settings put global data_stall_recovery_on_bad_network 1");
        list.add("settings put global network_avoid_bad_wifi 1");
        list.add("settings put global wifi_avoid_bad_networks 1");
        list.add("settings put global wifi_suspend_optimizations_enabled 0");
        list.add("settings put system power_center_is_5g_mode_enable_slot1 0");
        list.add("settings put system power_center_is_5g_mode_enable 0");

        // 7. Critical Safeguards
        list.addAll(getSafeguardCommands());

        // 8. Hardware Maintenance
        list.add("logcat -G 256K");
        list.add("sm fstrim");
        list.add("pm trim-caches 2000M");

        return list;
    }

    public static List<String> getSafeguardCommands() {
        List<String> list = new ArrayList<>();
        // NoBrokerHood (Gate entry approval protection)
        list.add("am set-standby-bucket com.app.nobrokerhood exempted");
        list.add("dumpsys deviceidle whitelist +com.app.nobrokerhood");
        list.add("cmd appops set com.app.nobrokerhood USE_FULL_SCREEN_INTENT allow");
        list.add("cmd appops set com.app.nobrokerhood SYSTEM_ALERT_WINDOW allow");
        list.add("cmd appops set com.app.nobrokerhood 10008 allow");

        // WhatsApp Video Calls & Live Location
        list.add("am set-standby-bucket com.whatsapp exempted");
        list.add("dumpsys deviceidle whitelist +com.whatsapp");
        list.add("cmd appops set com.whatsapp 10008 allow");
        list.add("cmd appops set com.whatsapp USE_FULL_SCREEN_INTENT allow");
        list.add("cmd appops set com.whatsapp FINE_LOCATION allow");
        list.add("cmd appops set com.whatsapp MONITOR_LOCATION allow");

        // Google Maps Live Family Location Sharing
        list.add("am set-standby-bucket com.google.android.apps.maps exempted");
        list.add("dumpsys deviceidle whitelist +com.google.android.apps.maps");
        list.add("cmd appops set com.google.android.apps.maps 10008 allow");
        list.add("cmd appops set com.google.android.apps.maps FINE_LOCATION allow");
        list.add("cmd appops set com.google.android.apps.maps MONITOR_LOCATION allow");
        list.add("cmd appops set com.google.android.apps.maps RUN_ANY_IN_BACKGROUND allow");

        // AirDroid Kids Family Tracking
        list.add("am set-standby-bucket com.sand.airdroidkids exempted");
        list.add("dumpsys deviceidle whitelist +com.sand.airdroidkids");
        list.add("cmd appops set com.sand.airdroidkids 10008 allow");

        return list;
    }

    public static List<String> getAppStandbyCommands(Context context) {
        List<String> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : packages) {
            String pkg = appInfo.packageName;
            if (HIGH_WAKEUP_CANDIDATES.contains(pkg)) {
                list.add("am set-standby-bucket " + pkg + " restricted");
                list.add("cmd appops set " + pkg + " RUN_ANY_IN_BACKGROUND ignore");
            }
        }
        return list;
    }

    public static List<String> getDailyMaintenanceCommands(Context context) {
        List<String> list = new ArrayList<>();
        // Storage TRIM & RAM cache cleanup
        list.add("sm fstrim");
        list.add("pm trim-caches 2000M");
        list.add("logcat -G 256K");

        // Auto-restrict newly installed shopping/carrier apps
        list.addAll(getAppStandbyCommands(context));

        // Re-enforce safeguards in case a MIUI update reverted them
        list.addAll(getSafeguardCommands());

        // Verify 120Hz & Ad-Block DNS
        list.add("settings put system min_refresh_rate 120.0");
        list.add("settings put system peak_refresh_rate 120.0");
        list.add("settings put global private_dns_mode hostname");
        list.add("settings put global private_dns_specifier dns.adguard-dns.com");

        return list;
    }
}
