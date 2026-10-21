package com.streamranker.app;

import android.content.Context;
import android.content.SharedPreferences;
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

    public synchronized int importCsv(Context context, String text) {
        int count = 0;
        if (text == null) return 0;
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            // Check for IMDb IDs like tt1234567 or CSV columns
            line = line.trim();
            if (line.isEmpty() || line.startsWith("Position") || line.startsWith("Const")) continue;
            
            // Look for ttXXXXXXX pattern
            int idx = line.indexOf("tt");
            if (idx != -1) {
                int end = idx + 2;
                while (end < line.length() && Character.isDigit(line.charAt(end))) {
                    end++;
                }
                String id = line.substring(idx, end);
                if (id.length() >= 7) {
                    watchlistIds.add(id);
                    count++;
                }
            }
        }
        save(context);
        return count;
    }

    private void save(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_WATCHLIST_IDS, new HashSet<>(watchlistIds))
            .apply();
    }
}

