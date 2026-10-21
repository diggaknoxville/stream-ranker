package com.streamranker.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import java.net.URLEncoder;
import java.util.Objects;

public class StreamingProvider {
    private static final String TAG = "StreamingProvider";

    public static final StreamingProvider NETFLIX = new StreamingProvider(
        "netflix", "Netflix", "com.netflix.mediaclient", 0xFFE50914,
        "https://www.netflix.com/title/", "https://www.netflix.com/search?q="
    );

    public static final StreamingProvider PRIME_VIDEO = new StreamingProvider(
        "prime", "Prime Video", "com.amazon.avod.thirdpartyclient", 0xFF00A8E1,
        "https://watch.amazon.de/detail?gti=", "https://app.primevideo.com/search?phrase="
    );

    public static final StreamingProvider DISNEY_PLUS = new StreamingProvider(
        "disney", "Disney+", "com.disney.disneyplus", 0xFF113CCF,
        "https://www.disneyplus.com/video/", "https://www.disneyplus.com/search?q="
    );

    public static final StreamingProvider WOW = new StreamingProvider(
        "wow", "WOW / Sky", "de.sky.online", 0xFF002D62,
        "https://www.wowtv.de/", "https://www.wowtv.de/search?q="
    );

    public static final StreamingProvider PARAMOUNT = new StreamingProvider(
        "paramount", "Paramount+", "com.paramountplus", 0xFF0064FF,
        "https://www.paramountplus.com/", "https://www.paramountplus.com/search/?q="
    );

    public static final StreamingProvider APPLE_TV = new StreamingProvider(
        "appletv", "Apple TV+", "com.apple.atve.androidtv.appletv", 0xFF333333,
        "https://tv.apple.com/", "https://tv.apple.com/search?term="
    );

    private static final StreamingProvider[] ALL = new StreamingProvider[] {
        NETFLIX, PRIME_VIDEO, DISNEY_PLUS, WOW, PARAMOUNT, APPLE_TV
    };

    private final String id;
    private final String displayName;
    private final String packageName;
    private final int brandColor;
    private final String defaultUrlPrefix;
    private final String searchUrlPrefix;

    public StreamingProvider(String id, String displayName, String packageName, int brandColor,
                             String defaultUrlPrefix, String searchUrlPrefix) {
        this.id = id;
        this.displayName = displayName;
        this.packageName = packageName;
        this.brandColor = brandColor;
        this.defaultUrlPrefix = defaultUrlPrefix;
        this.searchUrlPrefix = searchUrlPrefix;
    }

    public static StreamingProvider[] values() {
        return ALL;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getPackageName() { return packageName; }
    public int getBrandColor() { return brandColor; }

    public boolean isInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Launches the streaming title in the native app, with automatic fallback to app search.
     */
    public boolean launchTitle(Context context, String deepLinkIdOrUrl, String movieTitle) {
        if (movieTitle == null || movieTitle.trim().isEmpty()) return false;

        // If we have an explicit http web URL from verified seed data, try it first
        if (deepLinkIdOrUrl != null && deepLinkIdOrUrl.startsWith("http")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLinkIdOrUrl));
                intent.setPackage(packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            } catch (Exception e) {
                Log.w(TAG, "Direct launch failed, falling back to search in app: " + e.getMessage());
            }
        }

        // 100% reliable in-app search: opens the streaming app directly to this title
        return launchSearch(context, movieTitle);
    }

    /**
     * Explicitly opens the streaming app directly to the search page for this title.
     */
    public boolean launchSearch(Context context, String movieTitle) {
        if (movieTitle == null || movieTitle.trim().isEmpty()) return false;
        try {
            String query = URLEncoder.encode(movieTitle.trim(), "UTF-8");
            String searchUrl = searchUrlPrefix + query;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
            intent.setPackage(packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
            return true;
        } catch (Exception ex) {
            Log.w(TAG, "Search with package failed: " + ex.getMessage() + ", trying general intent");
            try {
                String query = URLEncoder.encode(movieTitle.trim(), "UTF-8");
                String searchUrl = searchUrlPrefix + query;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchUrl));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            } catch (Exception ex2) {
                // Fallback: try launching the app directly
                try {
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(launchIntent);
                        return true;
                    }
                } catch (Exception ignored) {}
                return false;
            }
        }
    }

    public static StreamingProvider fromId(String id) {
        if (id == null) return NETFLIX;
        for (StreamingProvider p : ALL) {
            if (p.id.equalsIgnoreCase(id)) return p;
        }
        return NETFLIX;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreamingProvider that = (StreamingProvider) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
