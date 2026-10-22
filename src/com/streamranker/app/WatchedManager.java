package com.streamranker.app;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class WatchedManager {
    private static final String PREF_NAME = "stream_ranker_prefs";
    private static final String KEY_WATCHED_IDS = "watched_movie_ids";
    private static final String KEY_WATCHED_TITLES = "watched_movie_titles";
    private static final String KEY_EXCLUDE_WATCHED = "exclude_watched_setting";

    private static WatchedManager instance;
    private final Set<String> watchedIds = new HashSet<>();
    private final Set<String> watchedTitles = new HashSet<>();
    private boolean excludeWatched = false;

    public static synchronized WatchedManager getInstance() {
        if (instance == null) {
            instance = new WatchedManager();
        }
        return instance;
    }

    private WatchedManager() {}

    public synchronized void init(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        excludeWatched = prefs.getBoolean(KEY_EXCLUDE_WATCHED, false);
        watchedIds.clear();
        watchedTitles.clear();

        Set<String> savedIds = prefs.getStringSet(KEY_WATCHED_IDS, null);
        if (savedIds != null) {
            watchedIds.addAll(savedIds);
        }

        Set<String> saved = prefs.getStringSet(KEY_WATCHED_TITLES, null);
        if (saved != null) {
            for (String item : saved) {
                if (item.startsWith("tt") || item.startsWith("tmdb_")) {
                    watchedIds.add(item);
                } else {
                    watchedTitles.add(item);
                }
            }
        }
    }

    public synchronized boolean isWatched(String id, String title) {
        if (id != null && watchedIds.contains(id.trim())) return true;
        if (title != null && watchedTitles.contains(title.trim().toLowerCase())) return true;
        return false;
    }

    public synchronized boolean toggleWatched(Context context, String id, String title) {
        String cleanId = (id != null && !id.trim().isEmpty()) ? id.trim() : "";
        String cleanTitle = (title != null && !title.trim().isEmpty()) ? title.trim().toLowerCase() : "";

        boolean currentlyWatched = (!cleanId.isEmpty() && watchedIds.contains(cleanId)) ||
                                   (!cleanTitle.isEmpty() && watchedTitles.contains(cleanTitle));

        boolean nowWatched;
        if (currentlyWatched) {
            if (!cleanId.isEmpty()) watchedIds.remove(cleanId);
            if (!cleanTitle.isEmpty()) watchedTitles.remove(cleanTitle);
            nowWatched = false;
        } else {
            if (!cleanId.isEmpty()) watchedIds.add(cleanId);
            if (!cleanTitle.isEmpty()) watchedTitles.add(cleanTitle);
            nowWatched = true;
        }

        save(context);
        return nowWatched;
    }

    public synchronized boolean isExcludeWatched() {
        return excludeWatched;
    }

    public synchronized void setExcludeWatched(Context context, boolean exclude) {
        this.excludeWatched = exclude;
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_EXCLUDE_WATCHED, exclude)
            .apply();
    }

    public synchronized int getCount() {
        // Return true count of unique watched movies without duplicates
        if (!watchedIds.isEmpty()) {
            return watchedIds.size();
        }
        return watchedTitles.size();
    }

    public synchronized Set<String> getWatchedKeys() {
        Set<String> all = new HashSet<>(watchedIds);
        all.addAll(watchedTitles);
        return all;
    }

    private void save(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_WATCHED_IDS, new HashSet<>(watchedIds))
            .putStringSet(KEY_WATCHED_TITLES, new HashSet<>(watchedTitles))
            .apply();
    }
}

