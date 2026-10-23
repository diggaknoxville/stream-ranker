package com.streamranker.app;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovieDatabase {
    public static final int SORT_IMDB = 0;
    public static final int SORT_ROTTEN_TOMATOES = 1;
    public static final int SORT_PERLE = 2;
    public static final int SORT_YEAR = 3;

    private static MovieDatabase instance;
    private final List<Movie> masterList = new ArrayList<>();
    private Context appContext;
    private final ExecutorService cacheExecutor = Executors.newSingleThreadExecutor();

    public static synchronized MovieDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new MovieDatabase(context != null ? context.getApplicationContext() : null);
        } else if (context != null && instance.masterList.isEmpty()) {
            instance.loadCatalogFromResources(context);
        }
        return instance;
    }

    public static synchronized MovieDatabase getInstance() {
        if (instance == null) {
            instance = new MovieDatabase(null);
        }
        return instance;
    }

    private MovieDatabase() {
        this(null);
    }

    private MovieDatabase(Context context) {
        if (context != null) {
            this.appContext = context.getApplicationContext();
            loadCatalogFromResources(context);
        } else {
            loadCatalogFromClasspathFallback();
        }
    }

    public synchronized void loadCatalogFromResources(Context context) {
        if (context == null) return;
        this.appContext = context.getApplicationContext();

        // 1. Load bundled seed catalog
        try (InputStream is = context.getResources().openRawResource(R.raw.seed_catalog)) {
            loadCatalogFromStream(is);
        } catch (Exception e) {
            android.util.Log.e("MovieDatabase", "Error loading seed_catalog.json", e);
        }

        // 2. Load disk cache of dynamically discovered movies
        File cachedFile = new File(context.getFilesDir(), "cached_catalog.json");
        if (cachedFile.exists() && cachedFile.length() > 0) {
            try (InputStream fis = new FileInputStream(cachedFile)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int read;
                while ((read = fis.read(buf)) != -1) {
                    baos.write(buf, 0, read);
                }
                String jsonStr = baos.toString(StandardCharsets.UTF_8.name());
                JSONArray arr = new JSONArray(jsonStr);
                List<Movie> diskMovies = new ArrayList<>(arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    Movie m = Movie.fromJson(obj);
                    if (m != null && !"youtube".equalsIgnoreCase(m.getProvider().getId())) {
                        diskMovies.add(m);
                    }
                }
                if (!diskMovies.isEmpty()) {
                    addMoviesInternal(diskMovies, false);
                }
            } catch (Exception e) {
                android.util.Log.w("MovieDatabase", "Failed to read cached_catalog.json: " + e.getMessage());
            }
        }
    }

    private void loadCatalogFromClasspathFallback() {
        try (InputStream is = MovieDatabase.class.getResourceAsStream("/res/raw/seed_catalog.json")) {
            if (is != null) {
                loadCatalogFromStream(is);
            }
        } catch (Exception ignored) {}
    }

    public synchronized void loadCatalogFromStream(InputStream is) {
        if (is == null) return;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int read;
            while ((read = is.read(buf)) != -1) {
                baos.write(buf, 0, read);
            }
            String jsonStr = baos.toString(StandardCharsets.UTF_8.name());
            JSONArray arr = new JSONArray(jsonStr);
            List<Movie> loaded = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Movie m = Movie.fromJson(obj);
                if (m != null && !"youtube".equalsIgnoreCase(m.getProvider().getId())) {
                    loaded.add(m);
                }
            }
            masterList.clear();
            masterList.addAll(loaded);
        } catch (Exception e) {
            android.util.Log.e("MovieDatabase", "Failed to parse seed catalog", e);
        }
    }

    public void scheduleSaveCache() {
        if (appContext == null) return;
        final Context ctx = appContext;
        cacheExecutor.execute(() -> saveCatalogCache(ctx));
    }

    public synchronized void saveCatalogCache(Context context) {
        if (context == null) return;
        try {
            JSONArray arr = new JSONArray();
            synchronized (this) {
                for (Movie m : masterList) {
                    arr.put(m.toJson());
                }
            }
            File file = new File(context.getFilesDir(), "cached_catalog.json");
            File tempFile = new File(context.getFilesDir(), "cached_catalog.tmp");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(arr.toString().getBytes(StandardCharsets.UTF_8));
                fos.flush();
            }
            if (tempFile.exists()) {
                tempFile.renameTo(file);
            }
        } catch (Exception e) {
            android.util.Log.w("MovieDatabase", "Failed to cache catalog to disk: " + e.getMessage());
        }
    }

    public static String normalizeTitle(String title) {
        if (title == null) return "";
        String s = title.replace('–', '-').replace('—', '-').replace('−', '-').toLowerCase(Locale.ROOT);
        s = s.replaceAll("[^a-z0-9äöüß\\s]", " ");
        s = s.trim();
        if (s.startsWith("der ")) s = s.substring(4).trim();
        else if (s.startsWith("die ")) s = s.substring(4).trim();
        else if (s.startsWith("das ")) s = s.substring(4).trim();
        else if (s.startsWith("the ")) s = s.substring(4).trim();
        else if (s.startsWith("ein ")) s = s.substring(4).trim();
        else if (s.startsWith("eine ")) s = s.substring(5).trim();
        s = s.replaceAll("\\s+", " ").trim();
        return s;
    }

    public static boolean isSameMovie(Movie a, Movie b) {
        if (a == null || b == null) return false;
        if (a == b) return true;

        if (a.getTmdbId() > 0 && b.getTmdbId() > 0 && a.getTmdbId() == b.getTmdbId()) {
            return true;
        }
        if (a.getId() != null && b.getId() != null && !a.getId().isEmpty() && a.getId().equals(b.getId())) {
            return true;
        }

        boolean yearMatches = Math.abs(a.getYear() - b.getYear()) <= 1;
        String normA = normalizeTitle(a.getTitle());
        String normB = normalizeTitle(b.getTitle());

        if (!normA.isEmpty() && normA.equals(normB)) {
            if (yearMatches || a.getProvider().equals(b.getProvider())) {
                return true;
            }
        }

        if (yearMatches && !normA.isEmpty() && !normB.isEmpty()) {
            if (normA.length() >= 5 && normB.length() >= 5) {
                if (normA.contains(normB) || normB.contains(normA)) {
                    return true;
                }
            }
        }

        String origA = normalizeTitle(a.getOriginalTitle());
        String origB = normalizeTitle(b.getOriginalTitle());
        if (yearMatches) {
            if (!origA.isEmpty() && (origA.equals(normB) || origA.equals(origB))) return true;
            if (!origB.isEmpty() && (origB.equals(normA) || origB.equals(origA))) return true;
        }

        return false;
    }

    public synchronized List<Movie> getLeavingSoon(Set<StreamingProvider> activeProviders) {
        List<Movie> results = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();
        for (Movie m : masterList) {
            if (m.isLeavingSoon()) {
                if (activeProviders == null || activeProviders.isEmpty() || activeProviders.contains(m.getProvider())) {
                    String key = normalizeTitle(m.getTitle()) + "_" + (m.getYear() / 5);
                    if (seenKeys.add(key)) {
                        results.add(m);
                    }
                }
            }
        }
        Collections.sort(results, (a, b) -> Double.compare(b.getImdbRating(), a.getImdbRating()));
        return results;
    }

    public synchronized List<Movie> getWatchlistMatches(Set<StreamingProvider> activeProviders, WatchlistManager watchlistManager) {
        List<Movie> results = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();
        for (Movie m : masterList) {
            if (watchlistManager.isWatchlisted(m.getId()) || watchlistManager.isWatchlisted(m.getTitle())) {
                if (activeProviders == null || activeProviders.isEmpty() || activeProviders.contains(m.getProvider())) {
                    String key = normalizeTitle(m.getTitle()) + "_" + (m.getYear() / 5);
                    if (seenKeys.add(key)) {
                        results.add(m);
                    }
                }
            }
        }
        Collections.sort(results, (a, b) -> Double.compare(b.getImdbRating(), a.getImdbRating()));
        return results;
    }

    public synchronized List<Movie> getWatchedMatches(Set<StreamingProvider> activeProviders, WatchedManager watchedManager, String searchQuery) {
        List<Movie> results = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();
        String q = searchQuery != null ? searchQuery.toLowerCase(Locale.ROOT).trim() : "";

        for (Movie m : masterList) {
            if (watchedManager != null && watchedManager.isWatched(m.getId(), m.getTitle())) {
                if (activeProviders == null || activeProviders.isEmpty() || activeProviders.contains(m.getProvider())) {
                    if (!q.isEmpty()) {
                        boolean matchTitle = m.getTitle().toLowerCase(Locale.ROOT).contains(q);
                        boolean matchOrig = m.getOriginalTitle().toLowerCase(Locale.ROOT).contains(q);
                        boolean matchGenre = m.getGenres().toLowerCase(Locale.ROOT).contains(q);
                        boolean matchActor = m.hasActor(q);
                        boolean matchDirector = m.hasDirector(q);
                        boolean matchCountry = m.getOriginCountry().equalsIgnoreCase(q) ||
                                               m.getOriginCountryName().toLowerCase(Locale.ROOT).contains(q);
                        if (!matchTitle && !matchOrig && !matchGenre && !matchActor && !matchDirector && !matchCountry) {
                            continue;
                        }
                    }
                    String key = normalizeTitle(m.getTitle()) + "_" + (m.getYear() / 5);
                    if (seenKeys.add(key)) {
                        results.add(m);
                    }
                }
            }
        }
        Collections.sort(results, (a, b) -> Double.compare(b.getImdbRating(), a.getImdbRating()));
        return results;
    }

    public synchronized List<Movie> getRelatedMovies(Movie target, int limit) {
        List<Movie> list = new ArrayList<>();
        if (target == null) return list;
        List<String> targetActors = target.getActorList();
        Set<String> seenKeys = new HashSet<>();
        seenKeys.add(normalizeTitle(target.getTitle()));

        for (Movie m : masterList) {
            if (m == target || isSameMovie(m, target)) continue;
            boolean match = false;
            for (String act : targetActors) {
                if (m.hasActor(act)) {
                    match = true;
                    break;
                }
            }
            if (!match && !target.getDirector().isEmpty() && m.hasDirector(target.getDirector())) {
                match = true;
            }
            if (match) {
                String key = normalizeTitle(m.getTitle());
                if (seenKeys.add(key)) {
                    list.add(m);
                    if (list.size() >= limit) break;
                }
            }
        }
        return list;
    }

    public synchronized int addMovies(List<Movie> newMovies) {
        return addMoviesInternal(newMovies, true);
    }

    private synchronized int addMoviesInternal(List<Movie> newMovies, boolean saveCache) {
        if (newMovies == null || newMovies.isEmpty()) return 0;
        int added = 0;
        for (Movie nm : newMovies) {
            if (nm == null || nm.getProvider() == null || "youtube".equalsIgnoreCase(nm.getProvider().getId())) {
                continue;
            }
            Movie existing = null;
            for (Movie em : masterList) {
                if (isSameMovie(em, nm)) {
                    existing = em;
                    break;
                }
            }
            if (existing != null) {
                if ((existing.getPosterUrl() == null || existing.getPosterUrl().isEmpty()) && nm.getPosterUrl() != null && !nm.getPosterUrl().isEmpty()) {
                    existing.setPosterUrl(nm.getPosterUrl());
                }
                if ((existing.getActors() == null || existing.getActors().isEmpty()) && !nm.getActors().isEmpty()) {
                    existing.setActors(nm.getActors());
                }
                if ((existing.getDirector() == null || existing.getDirector().isEmpty()) && !nm.getDirector().isEmpty()) {
                    existing.setDirector(nm.getDirector());
                }
                if (existing.getTmdbId() <= 0 && nm.getTmdbId() > 0) {
                    existing.setTmdbId(nm.getTmdbId());
                }
                if ((existing.getOriginalTitle() == null || existing.getOriginalTitle().isEmpty()) && !nm.getOriginalTitle().isEmpty()) {
                    existing.setOriginalTitle(nm.getOriginalTitle());
                }
                if (existing.getFsk() < 0 && nm.getFsk() >= 0) {
                    existing.setFsk(nm.getFsk());
                }
            } else {
                masterList.add(nm);
                added++;
            }
        }
        if (saveCache && (added > 0)) {
            scheduleSaveCache();
        }
        return added;
    }

    public synchronized List<Movie> getFilteredList(FilterCriteria criteria, Set<StreamingProvider> activeProviders, WatchedManager watchedManager) {
        List<Movie> results = new ArrayList<>();
        Set<String> seenKeys = new HashSet<>();

        for (Movie m : masterList) {
            if (criteria.matches(m, activeProviders, watchedManager)) {
                String key = normalizeTitle(m.getTitle()) + "_" + (m.getYear() / 5);
                if (seenKeys.add(key)) {
                    results.add(m);
                }
            }
        }

        final int sortMode = criteria.sortMode;
        Collections.sort(results, new Comparator<Movie>() {
            @Override
            public int compare(Movie a, Movie b) {
                if (sortMode == SORT_ROTTEN_TOMATOES) {
                    int c = Integer.compare(b.getRottenTomatoes(), a.getRottenTomatoes());
                    if (c != 0) return c;
                    return Double.compare(b.getImdbRating(), a.getImdbRating());
                } else if (sortMode == SORT_PERLE) {
                    int c = Double.compare(b.getCombinedScore(), a.getCombinedScore());
                    if (c != 0) return c;
                    return Integer.compare(b.getImdbVotes(), a.getImdbVotes());
                } else if (sortMode == SORT_YEAR) {
                    int c = Integer.compare(b.getYear(), a.getYear());
                    if (c != 0) return c;
                    return Double.compare(b.getImdbRating(), a.getImdbRating());
                } else {
                    int c = Double.compare(b.getImdbRating(), a.getImdbRating());
                    if (c != 0) return c;
                    return Integer.compare(b.getImdbVotes(), a.getImdbVotes());
                }
            }
        });

        if (criteria.targetCount > 0 && results.size() > criteria.targetCount) {
            return new ArrayList<>(results.subList(0, criteria.targetCount));
        }
        return results;
    }

    public synchronized List<Movie> getFilteredList(FilterCriteria criteria, Set<StreamingProvider> activeProviders) {
        return getFilteredList(criteria, activeProviders, WatchedManager.getInstance());
    }

    public synchronized List<Movie> getAllMovies() {
        return new ArrayList<>(masterList);
    }

    public synchronized Movie findById(String id) {
        if (id == null) return null;
        for (Movie m : masterList) {
            if (id.equalsIgnoreCase(m.getId())) return m;
        }
        return null;
    }

    public int getTotalCount() {
        return masterList.size();
    }
}
