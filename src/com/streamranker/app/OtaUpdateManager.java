package com.streamranker.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OtaUpdateManager {
    private static final String TAG = "OtaUpdateManager";
    public static final String GITHUB_REPO = "diggaknoxville/stream-ranker";
    public static final String CURRENT_VERSION = "1.6.0";

    private static OtaUpdateManager instance;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static synchronized OtaUpdateManager getInstance() {
        if (instance == null) {
            instance = new OtaUpdateManager();
        }
        return instance;
    }

    private OtaUpdateManager() {}

    public void checkUpdate(final Activity activity, final boolean userInitiated) {
        if (activity == null || activity.isFinishing()) return;

        executor.execute(() -> {
            try {
                String apiUrl = "https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest";
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("User-Agent", "StreamRanker-Android/" + CURRENT_VERSION);
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                    // No releases published yet on the repo
                    if (userInitiated) {
                        mainHandler.post(() -> Toast.makeText(activity, "Keine Releases auf GitHub gefunden (Repo aktuell)", Toast.LENGTH_SHORT).show());
                    }
                    return;
                }
                if (code != HttpURLConnection.HTTP_OK) {
                    if (userInitiated) {
                        mainHandler.post(() -> Toast.makeText(activity, "Update-Prüfung fehlgeschlagen (HTTP " + code + ")", Toast.LENGTH_SHORT).show());
                    }
                    return;
                }

                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                JSONObject release = new JSONObject(sb.toString());
                String tagName = release.optString("tag_name", "");
                String releaseName = release.optString("name", tagName);
                String body = release.optString("body", "Neue Verbesserungen & Bugfixes.");

                // Find APK asset
                String apkDownloadUrl = null;
                JSONArray assets = release.optJSONArray("assets");
                if (assets != null) {
                    for (int i = 0; i < assets.length(); i++) {
                        JSONObject asset = assets.optJSONObject(i);
                        if (asset == null) continue;
                        String name = asset.optString("name", "");
                        if (name.endsWith(".apk")) {
                            apkDownloadUrl = asset.optString("browser_download_url", null);
                            break;
                        }
                    }
                }

                final boolean newer = isNewer(tagName, CURRENT_VERSION);
                final String finalApkUrl = apkDownloadUrl;

                mainHandler.post(() -> {
                    if (activity.isFinishing()) return;

                    if (newer && finalApkUrl != null) {
                        showUpdateDialog(activity, tagName, releaseName, body, finalApkUrl);
                    } else if (userInitiated) {
                        Toast.makeText(activity, "StreamRanker ist auf dem neuesten Stand (v" + CURRENT_VERSION + ")", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.w(TAG, "Update check failed: " + e.getMessage());
                if (userInitiated) {
                    mainHandler.post(() -> Toast.makeText(activity, "Konnte nicht nach Updates suchen: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void showUpdateDialog(final Activity activity, final String tagName, final String releaseName,
                                  final String changelog, final String downloadUrl) {
        new AlertDialog.Builder(activity)
            .setTitle("🚀 Neues Update: " + tagName)
            .setMessage((releaseName.isEmpty() ? "" : releaseName + "\n\n") + changelog + "\n\nMöchtest du das Update jetzt installieren?")
            .setPositiveButton("Jetzt installieren", (d, which) -> downloadAndInstall(activity, downloadUrl, tagName))
            .setNegativeButton("Später", null)
            .show();
    }

    private void downloadAndInstall(final Activity activity, final String downloadUrl, final String tagName) {
        final ProgressDialog progress = new ProgressDialog(activity);
        progress.setTitle("StreamRanker Update");
        progress.setMessage("Lade " + tagName + " herunter...");
        progress.setIndeterminate(false);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setMax(100);
        progress.setCancelable(false);
        progress.show();

        executor.execute(() -> {
            File outFile = null;
            try {
                File dir = activity.getExternalCacheDir();
                if (dir == null) dir = activity.getCacheDir();
                outFile = new File(dir, "stream-ranker-update.apk");
                if (outFile.exists()) outFile.delete();

                URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent", "StreamRanker-Android/" + CURRENT_VERSION);
                conn.connect();

                // Handle redirects if any
                int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == 307) {
                    String newUrl = conn.getHeaderField("Location");
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setRequestProperty("User-Agent", "StreamRanker-Android/" + CURRENT_VERSION);
                    conn.connect();
                }

                int fileLength = conn.getContentLength();
                InputStream input = conn.getInputStream();
                FileOutputStream output = new FileOutputStream(outFile);

                byte[] data = new byte[8192];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        final int percent = (int) (total * 100 / fileLength);
                        mainHandler.post(() -> progress.setProgress(percent));
                    }
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                final File finalFile = outFile;
                mainHandler.post(() -> {
                    progress.dismiss();
                    installApk(activity, finalFile);
                });

            } catch (Exception e) {
                Log.e(TAG, "Download failed: " + e.getMessage(), e);
                mainHandler.post(() -> {
                    progress.dismiss();
                    Toast.makeText(activity, "Download fehlgeschlagen: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void installApk(Activity activity, File apkFile) {
        try {
            Uri apkUri = AppFileProvider.getUriForFile(activity, apkFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Install failed: " + e.getMessage(), e);
            Toast.makeText(activity, "Installation konnte nicht gestartet werden: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isNewer(String remoteTag, String currentVersion) {
        if (remoteTag == null || remoteTag.isEmpty()) return false;
        String cleanRemote = remoteTag.replaceAll("[^0-9.]", "").trim();
        String cleanCurrent = currentVersion.replaceAll("[^0-9.]", "").trim();

        String[] rParts = cleanRemote.split("\\.");
        String[] cParts = cleanCurrent.split("\\.");

        int length = Math.max(rParts.length, cParts.length);
        for (int i = 0; i < length; i++) {
            int rVal = i < rParts.length ? parseSafeInt(rParts[i]) : 0;
            int cVal = i < cParts.length ? parseSafeInt(cParts[i]) : 0;
            if (rVal > cVal) return true;
            if (rVal < cVal) return false;
        }
        return false;
    }

    private static int parseSafeInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
}
