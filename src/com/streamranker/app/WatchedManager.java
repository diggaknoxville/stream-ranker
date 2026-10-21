package com.streamranker.app;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class WatchedManager {
    private static final String PREF_NAME = "stream_ranker_prefs";
    private static final String KEY_WATCHED_TITLES = "watched_movie_titles";
    private static final String KEY_EXCLUDE_WATCHED = "exclude_watched_setting";

    private static WatchedManager instance;
    private final Set<String> watchedSet = new HashSet<>();
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
        Set<String> saved = prefs.getStringSet(KEY_WATCHED_TITLES, null);
        watchedSet.clear();
        if (saved != null) {
            watchedSet.addAll(saved);
        }
    }

    public synchronized boolean isWatched(String id, String title) {
        if (id != null && watchedSet.contains(id.trim())) return true;
        if (title != null && watchedSet.contains(title.trim().toLowerCase())) return true;
        return false;
    }

    public synchronized boolean toggleWatched(Context context, String id, String title) {
        String key = (id != null && !id.isEmpty()) ? id.trim() : (title != null ? title.trim().toLowerCase() : "");
        if (key.isEmpty()) return false;

        boolean nowWatched;
        if (watchedSet.contains(key) || (title != null && watchedSet.contains(title.trim().toLowerCase()))) {
            watchedSet.remove(key);
            if (title != null) watchedSet.remove(title.trim().toLowerCase());
            nowWatched = false;
        } else {
            watchedSet.add(key);
            if (title != null) watchedSet.add(title.trim().toLowerCase());
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
        return watchedSet.size();
    }

    public synchronized Set<String> getWatchedKeys() {
        return new HashSet<>(watchedSet);
    }

    private void save(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY_WATCHED_TITLES, new HashSet<>(watchedSet))
            .apply();
    }
}

