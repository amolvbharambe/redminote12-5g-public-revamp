package com.redmi.revamphub;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rikka.shizuku.Shizuku;

public class ShellExecutor {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onOutput(String line);
        void onComplete(boolean success);
    }

    public interface BatchCallback {
        void onProgress(String currentCommand, String output);
        void onComplete(boolean allSuccess);
    }

    public static boolean isShizukuAvailable() {
        try {
            return Shizuku.pingBinder();
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean hasShizukuPermission() {
        try {
            if (!isShizukuAvailable()) return false;
            return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable t) {
            return false;
        }
    }

    public static void requestShizukuPermission(int requestCode) {
        try {
            if (isShizukuAvailable() && !hasShizukuPermission()) {
                Shizuku.requestPermission(requestCode);
            }
        } catch (Throwable ignored) {}
    }

    private static Process createProcess(String command) throws Exception {
        if (hasShizukuPermission()) {
            try {
                Method method = Shizuku.class.getDeclaredMethod("newProcess", String[].class, String[].class, String.class);
                method.setAccessible(true);
                return (Process) method.invoke(null, new String[]{"sh", "-c", command}, null, null);
            } catch (Throwable ignored) {
                // Fallback to local shell if reflection fails
            }
        }
        try {
            return Runtime.getRuntime().exec(new String[]{"su", "-c", command});
        } catch (Exception e) {
            return Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
        }
    }

    public static void execute(String command, Callback callback) {
        executor.execute(() -> {
            boolean success = false;
            try {
                Process process = createProcess(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final String out = line;
                    mainHandler.post(() -> {
                        if (callback != null) callback.onOutput(out);
                    });
                }
                process.waitFor();
                success = (process.exitValue() == 0);
            } catch (Exception e) {
                final String err = "Error: " + e.getMessage();
                mainHandler.post(() -> {
                    if (callback != null) callback.onOutput(err);
                });
            }

            final boolean finalSuccess = success;
            mainHandler.post(() -> {
                if (callback != null) callback.onComplete(finalSuccess);
            });
        });
    }

    public static void executeBatch(List<String> commands, BatchCallback callback) {
        executor.execute(() -> {
            boolean allSuccess = true;
            for (String cmd : commands) {
                final String currentCmd = cmd;
                StringBuilder outputBuilder = new StringBuilder();
                try {
                    Process process = createProcess(cmd);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        outputBuilder.append(line).append("\n");
                    }
                    process.waitFor();
                    if (process.exitValue() != 0) {
                        allSuccess = false;
                    }
                } catch (Exception e) {
                    outputBuilder.append("Error: ").append(e.getMessage());
                    allSuccess = false;
                }

                final String out = outputBuilder.toString().trim();
                mainHandler.post(() -> {
                    if (callback != null) callback.onProgress(currentCmd, out);
                });
            }

            final boolean finalSuccess = allSuccess;
            mainHandler.post(() -> {
                if (callback != null) callback.onComplete(finalSuccess);
            });
        });
    }

    public static String executeSync(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = createProcess(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            output.append("Error: ").append(e.getMessage());
        }
        return output.toString().trim();
    }
}
