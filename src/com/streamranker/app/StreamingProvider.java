package com.streamranker.app;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StreamingProvider {
    private static final String TAG = "StreamingProvider";

    public static final StreamingProvider NETFLIX = new StreamingProvider(
        "netflix", "Netflix", "com.netflix.mediaclient", 0xFFE50914,
        "https://www.netflix.com/title/", "https://www.netflix.com/search?q="
    );

    public static final StreamingProvider PRIME_VIDEO = new StreamingProvider(
        "prime", "Prime Video", "com.amazon.avod.thirdpartyclient", 0xFF00A8E1,
        "https://app.primevideo.com/detail?gti=", "https://app.primevideo.com/search?phrase="
    );

    public static final StreamingProvider DISNEY_PLUS = new StreamingProvider(
        "disney", "Disney+", "com.disney.disneyplus", 0xFF113CCF,
        "https://www.disneyplus.com/video/", "https://www.disneyplus.com/search?q="
    );

    public static final StreamingProvider WOW = new StreamingProvider(
        "wow", "WOW", "de.sky.online", 0xFF008272,
        "https://www.wowtv.de/", "https://www.wowtv.de/search?q="
    );

    public static final StreamingProvider YOUTUBE = new StreamingProvider(
        "youtube", "YouTube (Kostenlos)", "com.google.android.youtube", 0xFFFF0000,
        "https://www.youtube.com/watch?v=", "https://www.youtube.com/results?search_query="
    );

    public static final StreamingProvider ARD_MEDIATHEK = new StreamingProvider(
        "ard", "ARD Mediathek", "de.swr.ardmediathek", 0xFF003366,
        "https://www.ardmediathek.de/", "https://www.ardmediathek.de/suche/"
    );

    public static final StreamingProvider ZDF_MEDIATHEK = new StreamingProvider(
        "zdf", "ZDF Mediathek", "com.zdf.zdfmediathek", 0xFFFA7D00,
        "https://www.zdf.de/", "https://www.zdf.de/suche?q="
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
        NETFLIX, PRIME_VIDEO, DISNEY_PLUS, WOW, YOUTUBE, ARD_MEDIATHEK, ZDF_MEDIATHEK, PARAMOUNT, APPLE_TV
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
        if (context == null) return false;
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            if (id.equals("wow")) {
                try {
                    context.getPackageManager().getPackageInfo("de.sky.skygo", 0);
                    return true;
                } catch (Exception ignored) {}
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Launches the title using deep link candidates with fallbacks to app search,
     * app launch with clipboard copy, or JustWatch options.
     */
    public boolean launchTitle(Context context, String deepLinkIdOrUrl, String movieTitle) {
        if (context == null || movieTitle == null || movieTitle.trim().isEmpty()) return false;

        List<Uri> deepLinks = buildCandidateUris(deepLinkIdOrUrl);
        for (Uri uri : deepLinks) {
            // 1. Try with explicit package
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage(packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            } catch (Exception ignored) {}

            // 2. Try generic view intent
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            } catch (Exception ignored) {}
        }

        // Fall back to in-app search and native launch
        return launchSearch(context, movieTitle);
    }

    private List<Uri> buildCandidateUris(String deepLinkIdOrUrl) {
        List<Uri> list = new ArrayList<>();
        if (deepLinkIdOrUrl == null || deepLinkIdOrUrl.trim().isEmpty()) return list;

        String raw = deepLinkIdOrUrl.trim();

        if (id.equals("netflix")) {
            // Extract numeric ID if present
            String netflixId = "";
            if (raw.matches("\\d+")) {
                netflixId = raw;
            } else if (raw.contains("/title/")) {
                String after = raw.substring(raw.indexOf("/title/") + 7);
                int slash = after.indexOf('/');
                int q = after.indexOf('?');
                int end = after.length();
                if (slash != -1) end = Math.min(end, slash);
                if (q != -1) end = Math.min(end, q);
                netflixId = after.substring(0, end).replaceAll("\\D", "");
            }
            if (!netflixId.isEmpty()) {
                list.add(Uri.parse("nflx://www.netflix.com/title/" + netflixId));
                list.add(Uri.parse("https://www.netflix.com/title/" + netflixId));
                list.add(Uri.parse("http://www.netflix.com/watch/" + netflixId));
            }
        } else if (id.equals("prime")) {
            String gti = "";
            if (raw.contains("gti=")) {
                gti = raw.substring(raw.indexOf("gti=") + 4);
                int amp = gti.indexOf('&');
                if (amp != -1) gti = gti.substring(0, amp);
            } else if (raw.startsWith("amzn1.dv.gti.")) {
                gti = raw;
            }
            if (!gti.isEmpty()) {
                list.add(Uri.parse("primevideo://detail?gti=" + gti));
                list.add(Uri.parse("https://app.primevideo.com/detail?gti=" + gti));
                list.add(Uri.parse("https://www.amazon.de/gp/video/detail/" + gti));
                list.add(Uri.parse("https://www.primevideo.com/detail/" + gti));
            }
        } else if (id.equals("disney")) {
            String entityId = "";
            if (raw.contains("entity-")) {
                entityId = raw.substring(raw.indexOf("entity-") + 7);
                int slash = entityId.indexOf('/');
                int q = entityId.indexOf('?');
                int end = entityId.length();
                if (slash != -1) end = Math.min(end, slash);
                if (q != -1) end = Math.min(end, q);
                entityId = entityId.substring(0, end);
            } else if (raw.contains("/video/")) {
                entityId = raw.substring(raw.indexOf("/video/") + 7);
            }
            if (!entityId.isEmpty()) {
                list.add(Uri.parse("disneyplus://browse/entity-" + entityId));
                list.add(Uri.parse("disneyplus://video/" + entityId));
                list.add(Uri.parse("https://www.disneyplus.com/browse/entity-" + entityId));
                list.add(Uri.parse("https://www.disneyplus.com/video/" + entityId));
            }
        } else if (id.equals("wow")) {
            list.add(Uri.parse("wowtv://"));
            list.add(Uri.parse("skyticket://"));
            list.add(Uri.parse("https://www.wowtv.de/"));
        } else if (id.equals("youtube")) {
            if (raw.contains("v=")) {
                String vid = raw.substring(raw.indexOf("v=") + 2);
                int amp = vid.indexOf('&');
                if (amp != -1) vid = vid.substring(0, amp);
                list.add(Uri.parse("vnd.youtube:" + vid));
                list.add(Uri.parse("https://www.youtube.com/watch?v=" + vid));
            } else if (raw.matches("[a-zA-Z0-9_-]{11}")) {
                list.add(Uri.parse("vnd.youtube:" + raw));
                list.add(Uri.parse("https://www.youtube.com/watch?v=" + raw));
            }
        }

        if (raw.startsWith("http://") || raw.startsWith("https://")) {
            list.add(Uri.parse(raw));
        }

        return list;
    }

    /**
     * Executes in-app search via URI schemes, Android Media Search, or falls back
     * to launching the native app with the movie title copied to the clipboard.
     */
    public boolean launchSearch(Context context, String movieTitle) {
        if (context == null || movieTitle == null || movieTitle.trim().isEmpty()) return false;
        String cleanTitle = movieTitle.trim();

        try {
            String encoded = URLEncoder.encode(cleanTitle, StandardCharsets.UTF_8.name());

            // 1. Provider-specific native schemes
            List<Uri> searchUris = new ArrayList<>();
            if (id.equals("netflix")) {
                searchUris.add(Uri.parse("nflx://www.netflix.com/browse?q=" + encoded));
                searchUris.add(Uri.parse("nflx://www.netflix.com/search?q=" + encoded));
            } else if (id.equals("prime")) {
                searchUris.add(Uri.parse("primevideo://search?phrase=" + encoded));
                searchUris.add(Uri.parse("primevideo://search?q=" + encoded));
            } else if (id.equals("disney")) {
                searchUris.add(Uri.parse("disneyplus://search?q=" + encoded));
            } else if (id.equals("youtube")) {
                String ytQuery = URLEncoder.encode(cleanTitle + " ganzer film deutsch", StandardCharsets.UTF_8.name());
                searchUris.add(Uri.parse("vnd.youtube://results?search_query=" + ytQuery));
                searchUris.add(Uri.parse("https://www.youtube.com/results?search_query=" + ytQuery));
            } else if (id.equals("wow")) {
                searchUris.add(Uri.parse("wowtv://search?q=" + encoded));
                searchUris.add(Uri.parse("skyticket://search?q=" + encoded));
                searchUris.add(Uri.parse("https://www.wowtv.de/search?q=" + encoded));
            } else if (id.equals("ard")) {
                searchUris.add(Uri.parse("https://www.ardmediathek.de/suche/" + encoded));
            } else if (id.equals("zdf")) {
                searchUris.add(Uri.parse("https://www.zdf.de/suche?q=" + encoded + "&synth=true"));
            }

            for (Uri sUri : searchUris) {
                try {
                    Intent sIntent = new Intent(Intent.ACTION_VIEW, sUri);
                    sIntent.setPackage(packageName);
                    sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(sIntent);
                    return true;
                } catch (Exception ignored) {}
            }

            // 2. Android Standard Media Search Intent (Supported by major streaming apps)
            if (isInstalled(context)) {
                try {
                    Intent mediaIntent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                    mediaIntent.setPackage(packageName);
                    mediaIntent.putExtra(SearchManager.QUERY, cleanTitle);
                    mediaIntent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*");
                    mediaIntent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, cleanTitle);
                    mediaIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mediaIntent);
                    return true;
                } catch (Exception ignored) {}

                // 3. Android Generic Search Intent
                try {
                    Intent genSearch = new Intent(Intent.ACTION_SEARCH);
                    genSearch.setPackage(packageName);
                    genSearch.putExtra(SearchManager.QUERY, cleanTitle);
                    genSearch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(genSearch);
                    return true;
                } catch (Exception ignored) {}

                // 4. Fallback: Launch app directly and copy movie title to clipboard
                try {
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    if (launchIntent == null && id.equals("wow")) {
                        launchIntent = context.getPackageManager().getLaunchIntentForPackage("de.sky.skygo");
                    }
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        copyTitleToClipboard(context, cleanTitle);
                        context.startActivity(launchIntent);
                        Toast.makeText(context, displayName + " geöffnet • \"" + cleanTitle + "\" in Zwischenablage kopiert", Toast.LENGTH_LONG).show();
                        return true;
                    }
                } catch (Exception ignored) {}
            }

            // 5. Fallback: JustWatch search in Germany (Guaranteed to show exact stream links)
            openJustWatch(context, cleanTitle);
            return true;

        } catch (Exception ex) {
            Log.e(TAG, "Search launch failed: " + ex.getMessage());
            openJustWatch(context, cleanTitle);
            return true;
        }
    }

    public static void openJustWatch(Context context, String movieTitle) {
        if (context == null || movieTitle == null) return;
        try {
            String encoded = URLEncoder.encode(movieTitle.trim(), StandardCharsets.UTF_8.name());
            String jwUrl = "https://www.justwatch.com/de/suche?q=" + encoded;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jwUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {}
    }

    private static void copyTitleToClipboard(Context context, String text) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                ClipData clip = ClipData.newPlainText("Film", text);
                clipboard.setPrimaryClip(clip);
            }
        } catch (Exception ignored) {}
    }

    public static StreamingProvider fromId(String id) {
        if (id == null) return NETFLIX;
        String lower = id.toLowerCase().trim();
        if (lower.equals("sky") || lower.equals("wow")) return WOW;
        if (lower.equals("yt") || lower.equals("youtube") || lower.equals("youtube free")) return YOUTUBE;
        if (lower.equals("ard") || lower.equals("ard mediathek") || lower.equals("ard_mediathek")) return ARD_MEDIATHEK;
        if (lower.equals("zdf") || lower.equals("zdf mediathek") || lower.equals("zdf_mediathek")) return ZDF_MEDIATHEK;
        if (lower.equals("disney") || lower.equals("disneyplus") || lower.equals("disney+")) return DISNEY_PLUS;
        if (lower.equals("prime") || lower.equals("amazon") || lower.equals("prime_video")) return PRIME_VIDEO;
        for (StreamingProvider p : ALL) {
            if (p.id.equalsIgnoreCase(lower)) return p;
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
