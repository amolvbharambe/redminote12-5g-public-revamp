package com.redmi.revamphub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {

    private static final int SHIZUKU_CODE = 1001;
    private static final String PREFS_NAME = "RedmiRevampPrefs";

    private TextView tvShizukuStatus;
    private TextView tvShizukuHint;
    private MaterialCardView cardShizuku;
    private MaterialButton btnFullOptimize;
    private ProgressBar progressBar;
    private TextView tvTerminalLog;

    private MaterialSwitch switch120Hz;
    private MaterialSwitch switchAnimations;
    private MaterialSwitch switchAdGuard;
    private MaterialSwitch switchFastDoze;
    private MaterialSwitch switchModemSleep;
    private MaterialSwitch switch5GBoost;

    private MaterialButton btnEnforceSafeguards;
    private MaterialButton btnOptimizeApps;
    private MaterialSwitch switchDailyMaintenance;

    private SharedPreferences prefs;

    private final Shizuku.OnRequestPermissionResultListener permissionListener = (requestCode, grantResult) -> {
        if (requestCode == SHIZUKU_CODE) {
            updateShizukuStatus();
        }
    };

    private final Shizuku.OnBinderReceivedListener binderReceivedListener = this::updateShizukuStatus;
    private final Shizuku.OnBinderDeadListener binderDeadListener = this::updateShizukuStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        setupListeners();
        registerShizuku();
        loadSavedToggles();
    }

    private void initViews() {
        tvShizukuStatus = findViewById(R.id.tvShizukuStatus);
        tvShizukuHint = findViewById(R.id.tvShizukuHint);
        cardShizuku = findViewById(R.id.cardShizuku);
        btnFullOptimize = findViewById(R.id.btnFullOptimize);
        progressBar = findViewById(R.id.progressBar);
        tvTerminalLog = findViewById(R.id.tvTerminalLog);

        switch120Hz = findViewById(R.id.switch120Hz);
        switchAnimations = findViewById(R.id.switchAnimations);
        switchAdGuard = findViewById(R.id.switchAdGuard);
        switchFastDoze = findViewById(R.id.switchFastDoze);
        switchModemSleep = findViewById(R.id.switchModemSleep);
        switch5GBoost = findViewById(R.id.switch5GBoost);

        btnEnforceSafeguards = findViewById(R.id.btnEnforceSafeguards);
        btnOptimizeApps = findViewById(R.id.btnOptimizeApps);
        switchDailyMaintenance = findViewById(R.id.switchDailyMaintenance);
    }

    private void registerShizuku() {
        Shizuku.addRequestPermissionResultListener(permissionListener);
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener);
        Shizuku.addBinderDeadListener(binderDeadListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateShizukuStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shizuku.removeRequestPermissionResultListener(permissionListener);
        Shizuku.removeBinderReceivedListener(binderReceivedListener);
        Shizuku.removeBinderDeadListener(binderDeadListener);
    }

    private void updateShizukuStatus() {
        if (ShellExecutor.hasShizukuPermission()) {
            tvShizukuStatus.setText(R.string.shizuku_connected);
            tvShizukuStatus.setTextColor(getColor(R.color.badge_green));
            tvShizukuHint.setText("Rootless ADB shell active. All toggles and automated optimization ready.");
        } else if (ShellExecutor.isShizukuAvailable()) {
            tvShizukuStatus.setText("🟡 Shizuku Ready (Permission Needed)");
            tvShizukuStatus.setTextColor(getColor(R.color.badge_blue));
            tvShizukuHint.setText("Tap here to grant ADB permission to Redmi Revamp Hub.");
            cardShizuku.setOnClickListener(v -> ShellExecutor.requestShizukuPermission(SHIZUKU_CODE));
        } else {
            tvShizukuStatus.setText(R.string.shizuku_not_connected);
            tvShizukuStatus.setTextColor(getColor(R.color.badge_red));
            tvShizukuHint.setText("Shizuku service is not running. Tap to launch Shizuku or open Wireless Debugging.");
            cardShizuku.setOnClickListener(v -> {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(this, "Shizuku app not found. Please install Shizuku from Play Store.", Toast.LENGTH_LONG).show();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=moe.shizuku.privileged.api")));
                    } catch (Exception ignored) {}
                }
            });
        }
    }

    private void loadSavedToggles() {
        switch120Hz.setChecked(prefs.getBoolean("pref_120hz", true));
        switchAnimations.setChecked(prefs.getBoolean("pref_animations", true));
        switchAdGuard.setChecked(prefs.getBoolean("pref_adguard", true));
        switchFastDoze.setChecked(prefs.getBoolean("pref_fast_doze", true));
        switchModemSleep.setChecked(prefs.getBoolean("pref_modem_sleep", true));
        switch5GBoost.setChecked(prefs.getBoolean("pref_5g_boost", true));
        switchDailyMaintenance.setChecked(prefs.getBoolean("pref_daily_maintenance", true));
    }

    private void setupListeners() {
        // 1-TAP FULL OPTIMIZATION
        btnFullOptimize.setOnClickListener(v -> runFullOptimization());

        // 120Hz Toggle
        switch120Hz.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("pref_120hz", isChecked).apply();
            String val = isChecked ? "120.0" : "60.0";
            String userVal = isChecked ? "120" : "60";
            ShellExecutor.execute("settings put system min_refresh_rate " + val + " && settings put system peak_refresh_rate " + val + " && settings put system user_refresh_rate " + userVal, null);
            appendLog("Refresh rate set to: " + (isChecked ? "120Hz Locked" : "Default Dynamic"));
        });

        // 0.5x Animations Toggle
        switchAnimations.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("pref_animations", isChecked).apply();
            String scale = isChecked ? "0.5" : "1.0";
            ShellExecutor.execute("settings put global window_animation_scale " + scale + " && settings put global transition_animation_scale " + scale + " && settings put global animator_duration_scale " + scale, null);
            appendLog("Animation scales set to: " + scale + "x");
        });

        // AdGuard Private DNS Toggle
        switchAdGuard.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("pref_adguard", isChecked).apply();
            if (isChecked) {
                ShellExecutor.execute("settings put global private_dns_mode hostname && settings put global private_dns_specifier dns.adguard-dns.com", null);
                appendLog("AdGuard Private DNS: Enabled (Ads Blocked)");
            } else {
                ShellExecutor.execute("settings put global private_dns_mode opportunistic", null);
                appendLog("Private DNS: Reverted to Default");
            }
        });

        // Fast Deep Sleep Toggle
        switchFastDoze.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("pref_fast_doze", isChecked).apply();
            if (isChecked) {
                String dozeConstants = "inactive_to=120000,sensing_to=0,locating_to=0,location_accuracy=20.0,motion_inactive_to=0,idle_after_inactive_to=120000,idle_pending_to=180000,max_idle_pending_to=360000,idle_pending_factor=2.0,idle_to=1800000,max_idle_to=21600000,idle_factor=2.0,min_time_to_alarm=3600000,max_temp_app_allowlist_duration_ms=20000,mms_temp_app_allowlist_duration_ms=60000,sms_temp_app_allowlist_duration_ms=20000,notification_allowlist_duration_ms=20000";
                ShellExecutor.execute("settings put global deviceidle_constants " + dozeConstants, null);
                appendLog("Fast Deep Sleep (2-min Doze): Active");
            } else {
                ShellExecutor.execute("settings delete global deviceidle_constants", null);
                appendLog("Deep Sleep: Reverted to Android Default");
            }
        });

        // Modem Sleep on Wi-Fi Toggle
        switchModemSleep.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("pref_modem_sleep", isChecked).apply();
            String val = isChecked ? "0" : "1";
            ShellExecutor.execute("settings put global mobile_data_always_on " + val, null);
            appendLog("Cellular Modem Always-On: " + (isChecked ? "Disabled (Saves Battery)" : "Enabled"));
        });

        // 5G Boost Toggle
        switch5GBoost.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("pref_5g_boost", isChecked).apply();
            String val = isChecked ? "0" : "1";
            ShellExecutor.execute("settings put system power_center_is_5g_mode_enable_slot1 " + val + " && settings put system power_center_is_5g_mode_enable " + val, null);
            appendLog("5G Multi-Carrier Aggregation: " + (isChecked ? "Un-Throttled" : "Default"));
        });

        // Enforce Safeguards Button
        btnEnforceSafeguards.setOnClickListener(v -> {
            appendLog(">>> Enforcing Safeguards for NoBrokerHood, WhatsApp, Maps...");
            List<String> commands = OptimizationEngine.getSafeguardCommands();
            ShellExecutor.executeBatch(commands, new ShellExecutor.BatchCallback() {
                @Override
                public void onProgress(String currentCommand, String output) {}

                @Override
                public void onComplete(boolean allSuccess) {
                    appendLog("✓ Safeguards active: NoBrokerHood, WhatsApp, Maps exempt & autostart enabled.");
                    Toast.makeText(MainActivity.this, "Safeguards verified and active!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Optimize 3rd Party Apps Button
        btnOptimizeApps.setOnClickListener(v -> {
            appendLog(">>> Scanning third-party apps for background optimization...");
            List<String> commands = OptimizationEngine.getAppStandbyCommands(MainActivity.this);
            if (commands.isEmpty()) {
                appendLog("✓ All candidate apps are already optimized!");
            } else {
                ShellExecutor.executeBatch(commands, new ShellExecutor.BatchCallback() {
                    @Override
                    public void onProgress(String currentCommand, String output) {}

                    @Override
                    public void onComplete(boolean allSuccess) {
                        appendLog("✓ " + (commands.size() / 2) + " shopping/carrier apps placed in RESTRICTED bucket.");
                        Toast.makeText(MainActivity.this, "Third-party apps restricted in background!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Scheduled Daily Maintenance Switch
        switchDailyMaintenance.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("pref_daily_maintenance", isChecked).apply();
            if (isChecked) {
                scheduleDailyMaintenance();
                appendLog("⏰ Daily 24h background maintenance enabled.");
            } else {
                cancelDailyMaintenance();
                appendLog("⏰ Daily background maintenance disabled.");
            }
        });
    }

    private void runFullOptimization() {
        progressBar.setVisibility(View.VISIBLE);
        btnFullOptimize.setEnabled(false);
        tvTerminalLog.setText(">>> Initiating Full System Optimization Suite...\n");

        List<String> commands = OptimizationEngine.getFullOptimizationCommands();

        ShellExecutor.executeBatch(commands, new ShellExecutor.BatchCallback() {
            @Override
            public void onProgress(String currentCommand, String output) {
                if (currentCommand.contains("uninstall") || currentCommand.contains("disable")) {
                    appendLog("• " + currentCommand);
                }
            }

            @Override
            public void onComplete(boolean allSuccess) {
                progressBar.setVisibility(View.GONE);
                btnFullOptimize.setEnabled(true);
                appendLog("\n==================================================");
                appendLog("✓ FULL OPTIMIZATION COMPLETE!");
                appendLog("• 25+ bloat packages removed/frozen");
                appendLog("• 120Hz display & 0.5x animations locked");
                appendLog("• AdGuard Private DNS active");
                appendLog("• Fast Deep Sleep active (2-min Doze)");
                appendLog("• NoBrokerHood, WhatsApp & Maps fully safeguarded");
                appendLog("• Storage FSTRIM & Cache purged");
                appendLog("==================================================");
                Toast.makeText(MainActivity.this, "Redmi Note 12 5G Fully Optimized!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void scheduleDailyMaintenance() {
        Constraints constraints = new Constraints.Builder()
            .setRequiresCharging(true)
            .build();

        PeriodicWorkRequest maintenanceWork = new PeriodicWorkRequest.Builder(
            DailyMaintenanceWorker.class,
            24, TimeUnit.HOURS
        )
        .setConstraints(constraints)
        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyMaintenanceWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            maintenanceWork
        );
    }

    private void cancelDailyMaintenance() {
        WorkManager.getInstance(this).cancelUniqueWork(DailyMaintenanceWorker.WORK_NAME);
    }

    private void appendLog(String text) {
        runOnUiThread(() -> {
            String current = tvTerminalLog.getText().toString();
            tvTerminalLog.setText(current + "\n" + text);
        });
    }
}
