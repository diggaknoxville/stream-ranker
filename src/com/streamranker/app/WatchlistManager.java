package com.streamranker.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class WatchlistManager {
    private static final String PREF_NAME = "stream_ranker_prefs";
    private static final String KEY_WATCHLIST_IDS = "watchlist_imdb_ids";
    private static final String KEY_USER_IMDB_ID = "user_imdb_id";

    private static WatchlistManager instance;
    private final Set<String> watchlistIds = new HashSet<>();
    private String userImdbId = "";

    public static synchronized WatchlistManager getInstance() {
        if (instance == null) {
            instance = new WatchlistManager();
        }
        return instance;
    }

    private WatchlistManager() {}

    public synchronized void init(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userImdbId = prefs.getString(KEY_USER_IMDB_ID, "");
        Set<String> saved = prefs.getStringSet(KEY_WATCHLIST_IDS, null);
        watchlistIds.clear();
        if (saved != null) {
            watchlistIds.addAll(saved);
        } else {
            // Seed default favorite sample watchlist IDs (e.g. Inception, Interstellar, Pulp Fiction, Parasite)
            watchlistIds.add("tt0816692"); // Interstellar
            watchlistIds.add("tt1375666"); // Inception
            watchlistIds.add("tt6751668"); // Parasite
            watchlistIds.add("tt0110912"); // Pulp Fiction
            watchlistIds.add("tt0137523"); // Fight Club
            watchlistIds.add("tt2582802"); // Whiplash
            save(context);
        }
    }

    public synchronized boolean isWatchlisted(String imdbId) {
        return imdbId != null && watchlistIds.contains(imdbId.trim());
    }

    public synchronized void toggle(Context context, String imdbId) {
        if (imdbId == null) return;
        imdbId = imdbId.trim();
        if (watchlistIds.contains(imdbId)) {
            watchlistIds.remove(imdbId);
        } else {
            watchlistIds.add(imdbId);
        }
        save(context);
    }

    public synchronized boolean toggleWatchlist(Context context, String imdbId, String title) {
        String key = (imdbId != null && !imdbId.isEmpty()) ? imdbId.trim() : (title != null ? title.trim() : "");
        if (key.isEmpty()) return false;
        boolean nowWatchlisted;
        if (watchlistIds.contains(key)) {
            watchlistIds.remove(key);
            nowWatchlisted = false;
        } else {
            watchlistIds.add(key);
            nowWatchlisted = true;
        }
        save(context);
        return nowWatchlisted;
    }

    public synchronized void setWatchlist(Context context, Set<String> ids) {
        watchlistIds.clear();
        for (String id : ids) {
            if (id != null && !id.trim().isEmpty()) {
                watchlistIds.add(id.trim());
            }
        }
        save(context);
    }

    public synchronized int getCount() {
        return watchlistIds.size();
    }

    public synchronized String getUserImdbId() {
        return userImdbId;
    }

    public synchronized void setUserImdbId(Context context, String id) {
        this.userImdbId = id != null ? id.trim() : "";
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_IMDB_ID, this.userImdbId)
            .apply();
    }

    public synchronized boolean isImdbAppInstalled(Context context) {
        if (context == null) return false;
        try {
            context.getPackageManager().getPackageInfo("com.imdb.mobile", 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized void openImdbWatchlist(Context context) {
        if (context == null) return;
        try {
            if (isImdbAppInstalled(context)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("imdb:///watchlist"));
                intent.setPackage("com.imdb.mobile");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, "IMDb Watchlist geöffnet • Tippe dort auf 'Teilen' für StreamRanker!", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception ignored) {}

        // Fallback to Web
        try {
            String url = "https://www.imdb.com/user/watchlist";
            if (userImdbId != null && !userImdbId.isEmpty()) {
                url = "https://www.imdb.com/user/" + userImdbId + "/watchlist";
            }
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(webIntent);
        } catch (Exception ignored) {}
    }

    public synchronized Set<String> importFromStream(Context context, InputStream is) {
        Set<String> added = new HashSet<>();
        if (is == null) return added;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line, added);
            }
            watchlistIds.addAll(added);
            save(context);
        } catch (Exception ignored) {}
        return added;
    }

    public synchronized Set<String> importCsvWithIds(Context context, String text) {
        Set<String> added = new HashSet<>();
        if (text == null) return added;
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            parseLine(line, added);
        }
        watchlistIds.addAll(added);
        save(context);
        return added;
    }

    private void parseLine(String line, Set<String> target) {
        if (line == null) return;
        line = line.trim();
        if (line.isEmpty() || line.startsWith("Position") || line.startsWith("Const")) return;

        int idx = 0;
        while ((idx = line.indexOf("tt", idx)) != -1) {
            int end = idx + 2;
            while (end < line.length() && Character.isDigit(line.charAt(end))) {
                end++;
            }
            String id = line.substring(idx, end);
            if (id.length() >= 7) {
                target.add(id);
            }
            idx = end;
        }
    }

    public synchronized int importCsv(Context context, String text) {
        return importCsvWithIds(context, text).size();
    }

    private void save(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_WATCHLIST_IDS, new HashSet<>(watchlistIds))
            .apply();
    }
}

