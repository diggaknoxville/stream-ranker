package com.streamranker.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncImageLoader {
    private static final String TAG = "AsyncImageLoader";
    private static AsyncImageLoader instance;

    private static class SimpleCache {
        private final int maxEntries;
        private final LinkedHashMap<String, Bitmap> map = new LinkedHashMap<>();

        public SimpleCache(int maxEntries) {
            this.maxEntries = maxEntries;
        }

        public synchronized Bitmap get(String key) {
            return map.get(key);
        }

        public synchronized void put(String key, Bitmap bitmap) {
            if (key == null || bitmap == null) return;
            map.put(key, bitmap);
            if (map.size() > maxEntries) {
                try {
                    String oldest = map.keySet().iterator().next();
                    map.remove(oldest);
                } catch (Exception ignored) {}
            }
        }
    }

    private final SimpleCache memoryCache = new SimpleCache(150);
    private final ExecutorService executor = Executors.newFixedThreadPool(6);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private File diskCacheDir;

    public static synchronized AsyncImageLoader getInstance(Context context) {
        if (instance == null) {
            instance = new AsyncImageLoader(context.getApplicationContext());
        }
        return instance;
    }

    private AsyncImageLoader(Context context) {
        try {
            diskCacheDir = new File(context.getCacheDir(), "poster_cache");
            if (!diskCacheDir.exists()) {
                diskCacheDir.mkdirs();
            }
        } catch (Exception ignored) {}
    }

    public void load(final ImageView imageView, final String url, final String fallbackTitle) {
        if (imageView == null) return;

        Bitmap placeholder = createPlaceholder(fallbackTitle, 200, 300);
        imageView.setImageBitmap(placeholder);

        if (url == null || url.trim().isEmpty()) {
            return;
        }

        final String cleanUrl = url.trim();

        Bitmap cached = memoryCache.get(cleanUrl);
        if (cached != null) {
            imageView.setImageBitmap(cached);
            return;
        }

        imageView.setTag(cleanUrl);

        executor.execute(() -> {
            Bitmap bitmap = loadFromDisk(cleanUrl);
            if (bitmap == null) {
                bitmap = downloadBitmap(cleanUrl);
                if (bitmap != null) {
                    saveToDisk(cleanUrl, bitmap);
                }
            }

            if (bitmap != null) {
                memoryCache.put(cleanUrl, bitmap);
                final Bitmap finalBitmap = bitmap;
                mainHandler.post(() -> {
                    if (cleanUrl.equals(imageView.getTag())) {
                        imageView.setImageBitmap(finalBitmap);
                    }
                });
            }
        });
    }

    private Bitmap downloadBitmap(String urlStr) {
        try {
            String currentUrl = urlStr;
            for (int redirect = 0; redirect < 3; redirect++) {
                URL url = new URL(currentUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36");
                conn.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
                conn.connect();

                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == 307) {
                    String loc = conn.getHeaderField("Location");
                    if (loc != null && !loc.isEmpty()) {
                        currentUrl = loc;
                        continue;
                    }
                }

                if (code == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    is.close();
                    return bm;
                } else {
                    Log.w(TAG, "HTTP " + code + " for " + currentUrl);
                    return null;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Download error for " + urlStr + ": " + e.getMessage());
        }
        return null;
    }

    private Bitmap loadFromDisk(String url) {
        if (diskCacheDir == null) return null;
        try {
            String filename = "img_" + Math.abs(url.hashCode()) + ".png";
            File file = new File(diskCacheDir, filename);
            if (file.exists() && file.length() > 0) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void saveToDisk(String url, Bitmap bitmap) {
        if (diskCacheDir == null || bitmap == null) return;
        try {
            String filename = "img_" + Math.abs(url.hashCode()) + ".png";
            File file = new File(diskCacheDir, filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.flush();
            fos.close();
        } catch (Exception ignored) {}
    }

    private Bitmap createPlaceholder(String title, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint bgPaint = new Paint();
        bgPaint.setColor(0xFF161B22);
        canvas.drawRect(0, 0, width, height, bgPaint);

        Paint accentPaint = new Paint();
        accentPaint.setColor(0xFF21262D);
        canvas.drawRect(0, 0, width, 50, accentPaint);

        Paint starPaint = new Paint();
        starPaint.setColor(0xFFF5C518);
        starPaint.setTextSize(36);
        starPaint.setAntiAlias(true);
        canvas.drawText("★", width / 2f - 18, 120, starPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(0xFF8B949E);
        textPaint.setTextSize(22);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        String shortTitle = title != null ? title : "Movie";
        if (shortTitle.length() > 14) {
            shortTitle = shortTitle.substring(0, 12) + "...";
        }
        canvas.drawText(shortTitle, width / 2f, 170, textPaint);

        return bitmap;
    }
}
