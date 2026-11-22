package com.redmi.revamphub;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

public class DailyMaintenanceWorker extends Worker {

    public static final String WORK_NAME = "RedmiDailyMaintenanceWork";
    private static final String CHANNEL_ID = "redmi_maintenance_channel";

    public DailyMaintenanceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        // Run maintenance commands synchronously
        List<String> commands = OptimizationEngine.getDailyMaintenanceCommands(context);
        for (String cmd : commands) {
            ShellExecutor.executeSync(cmd);
        }

        // Post completion notification
        showNotification(context);

        return Result.success();
    }

    private void showNotification(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Daily Maintenance",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Notifications for daily storage and system optimization");
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Redmi Revamp Hub")
            .setContentText("Daily maintenance complete: UFS storage trimmed & safeguards verified.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true);

        try {
            manager.notify(1001, builder.build());
        } catch (SecurityException ignored) {}
    }
}
