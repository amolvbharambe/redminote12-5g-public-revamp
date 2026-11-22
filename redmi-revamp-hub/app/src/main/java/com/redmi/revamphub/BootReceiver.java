package com.redmi.revamphub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;

public class BootReceiver extends BroadcastReceiver {

    private static final String PREFS_NAME = "RedmiRevampPrefs";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)
                || "android.intent.action.QUICKBOOT_POWERON".equals(action)
                || "com.htc.intent.action.QUICKBOOT_POWERON".equals(action)) {

            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean autoDevMode = prefs.getBoolean("pref_auto_dev_mode", true);
            boolean autoAdbWifi = prefs.getBoolean("pref_auto_adb_wifi", true);

            try {
                // Auto-enable Developer Options on Reboot
                if (autoDevMode) {
                    Settings.Global.putInt(context.getContentResolver(), "development_settings_enabled", 1);
                }

                // Auto-enable Wireless Debugging on Reboot
                if (autoAdbWifi) {
                    Settings.Global.putInt(context.getContentResolver(), "adb_wifi_enabled", 1);
                }

                // Re-apply 0.5x snappy animations (prevents HyperOS from resetting to 1.0x on reboot)
                if (prefs.getBoolean("pref_animations", true)) {
                    Settings.Global.putFloat(context.getContentResolver(), Settings.Global.WINDOW_ANIMATION_SCALE, 0.5f);
                    Settings.Global.putFloat(context.getContentResolver(), Settings.Global.TRANSITION_ANIMATION_SCALE, 0.5f);
                    Settings.Global.putFloat(context.getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 0.5f);
                }

                // Re-enforce 120Hz display refresh rate
                if (prefs.getBoolean("pref_120hz", true)) {
                    Settings.System.putFloat(context.getContentResolver(), "min_refresh_rate", 120.0f);
                    Settings.System.putFloat(context.getContentResolver(), "peak_refresh_rate", 120.0f);
                    Settings.System.putInt(context.getContentResolver(), "user_refresh_rate", 120);
                }
            } catch (SecurityException ignored) {
                // WRITE_SECURE_SETTINGS permission not yet granted; will be applied when Shizuku connects
            }
        }
    }
}
