package com.streamranker.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FilterCriteria {
    private static final String PREF_NAME = "stream_ranker_filter_prefs";

    public String singleProviderId = "all"; // "all", "netflix", "prime", "disney"
    public String activePresetId = "default"; // "default", "feierabend", "date_night", "familie", "mindfuck", "perlen", "klassiker"
    public final Set<String> includedGenres = new HashSet<>();
    public final Set<String> excludedGenres = new HashSet<>();
    public final Set<String> excludedCountries = new HashSet<>();
    public final Set<String> excludedActors = new HashSet<>();
    public final Set<String> excludedDirectors = new HashSet<>();
    public final Set<String> compoundExclusions = new HashSet<>(); // Conditional rules e.g. "KR:Romantik"

    public boolean excludeDoku = false;
    public boolean excludeBollywood = false;
    public boolean excludeAnime = false;
    public boolean excludeShorts = false; // < 60 Min
    public boolean excludeStandup = false; // Stand-up comedy specials
    public boolean excludeWatched = false; // Exclude already seen movies!
    public boolean bulletproofConsensus = false; // Goldener Konsens: IMDb >= 7.2 AND Rotten Tomatoes >= 75%

    public int minYear = 0;
    public int maxYear = 9999;
    public int minRuntime = 0;
    public int maxRuntime = 999;
    public int minFsk = 0;
    public int maxFsk = 18;
    public double minImdbScore = 0.0;
    public int minImdbVotes = 0;
    public int minRottenTomatoes = 0;
    public int sortMode = MovieDatabase.SORT_IMDB;
    public String searchQuery = "";
    public int targetCount = 100;

    public void reset() {
        singleProviderId = "all";
        activePresetId = "default";
        includedGenres.clear();
        excludedGenres.clear();
        excludedCountries.clear();
        excludedActors.clear();
        excludedDirectors.clear();
        compoundExclusions.clear();
        excludeDoku = false;
        excludeBollywood = false;
        excludeAnime = false;
        excludeShorts = false;
        excludeStandup = false;
        excludeWatched = false;
        bulletproofConsensus = false;
        minYear = 0;
        maxYear = 9999;
        minRuntime = 0;
        maxRuntime = 999;
        minFsk = 0;
        maxFsk = 18;
        minImdbScore = 0.0;
        minImdbVotes = 0;
        minRottenTomatoes = 0;
        sortMode = MovieDatabase.SORT_IMDB;
        searchQuery = "";
        targetCount = 100;
    }

    public void save(Context context) {
        if (context == null) return;
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit()
                .putString("singleProviderId", singleProviderId)
                .putString("activePresetId", activePresetId)
                .putBoolean("excludeWatched", excludeWatched)
                .putBoolean("excludeDoku", excludeDoku)
                .putBoolean("excludeBollywood", excludeBollywood)
                .putBoolean("excludeAnime", excludeAnime)
                .putBoolean("excludeShorts", excludeShorts)
                .putBoolean("excludeStandup", excludeStandup)
                .putBoolean("bulletproofConsensus", bulletproofConsensus)
                .putStringSet("excludedCountries", new HashSet<>(excludedCountries))
                .putStringSet("excludedActors", new HashSet<>(excludedActors))
                .putStringSet("excludedDirectors", new HashSet<>(excludedDirectors))
                .putStringSet("compoundExclusions", new HashSet<>(compoundExclusions))
                .putStringSet("includedGenres", new HashSet<>(includedGenres))
                .putStringSet("excludedGenres", new HashSet<>(excludedGenres))
                .putInt("minYear", minYear)
                .putInt("maxYear", maxYear)
                .putInt("minRuntime", minRuntime)
                .putInt("maxRuntime", maxRuntime)
                .putInt("minFsk", minFsk)
                .putInt("maxFsk", maxFsk)
                .putFloat("minImdbScore", (float) minImdbScore)
                .putInt("minImdbVotes", minImdbVotes)
                .putInt("minRottenTomatoes", minRottenTomatoes)
                .putInt("sortMode", sortMode)
                .putInt("targetCount", targetCount)
                .apply();
        } catch (Exception ignored) {}
    }

    public void load(Context context) {
        if (context == null) return;
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            singleProviderId = prefs.getString("singleProviderId", "all");
            activePresetId = prefs.getString("activePresetId", "default");
            excludeWatched = prefs.getBoolean("excludeWatched", false);
            excludeDoku = prefs.getBoolean("excludeDoku", false);
            excludeBollywood = prefs.getBoolean("excludeBollywood", false);
            excludeAnime = prefs.getBoolean("excludeAnime", false);
            excludeShorts = prefs.getBoolean("excludeShorts", false);
            excludeStandup = prefs.getBoolean("excludeStandup", false);
            bulletproofConsensus = prefs.getBoolean("bulletproofConsensus", false);

            Set<String> savedCountries = prefs.getStringSet("excludedCountries", null);
            excludedCountries.clear();
            if (savedCountries != null) excludedCountries.addAll(savedCountries);

            Set<String> savedActors = prefs.getStringSet("excludedActors", null);
            excludedActors.clear();
            if (savedActors != null) excludedActors.addAll(savedActors);

            Set<String> savedDirectors = prefs.getStringSet("excludedDirectors", null);
            excludedDirectors.clear();
            if (savedDirectors != null) excludedDirectors.addAll(savedDirectors);

            Set<String> savedCompound = prefs.getStringSet("compoundExclusions", null);
            compoundExclusions.clear();
            if (savedCompound != null) compoundExclusions.addAll(savedCompound);

            Set<String> savedInc = prefs.getStringSet("includedGenres", null);
            includedGenres.clear();
            if (savedInc != null) includedGenres.addAll(savedInc);

            Set<String> savedEx = prefs.getStringSet("excludedGenres", null);
            excludedGenres.clear();
            if (savedEx != null) excludedGenres.addAll(savedEx);

            minYear = prefs.getInt("minYear", 0);
            maxYear = prefs.getInt("maxYear", 9999);
            minRuntime = prefs.getInt("minRuntime", 0);
            maxRuntime = prefs.getInt("maxRuntime", 999);
            minFsk = prefs.getInt("minFsk", 0);
            maxFsk = prefs.getInt("maxFsk", 18);
            minImdbScore = prefs.getFloat("minImdbScore", 0.0f);
            minImdbVotes = prefs.getInt("minImdbVotes", 0);
            minRottenTomatoes = prefs.getInt("minRottenTomatoes", 0);
            sortMode = prefs.getInt("sortMode", MovieDatabase.SORT_IMDB);
            targetCount = prefs.getInt("targetCount", 100);
        } catch (Exception ignored) {}
    }

    public boolean matches(Movie m, Set<StreamingProvider> activeProviders, WatchedManager watchedManager) {
        // 0. Exclude Watched
        if (excludeWatched && watchedManager != null && watchedManager.isWatched(m.getId(), m.getTitle())) {
            return false;
        }

        // 1. Provider
        if (!singleProviderId.equalsIgnoreCase("all")) {
            if (!m.getProvider().getId().equalsIgnoreCase(singleProviderId)) {
                return false;
            }
        } else if (activeProviders != null && !activeProviders.isEmpty()) {
            if (!activeProviders.contains(m.getProvider())) {
                return false;
            }
        }

        // 2. Year Range
        if (minYear > 0 && m.getYear() < minYear) return false;
        if (maxYear < 9999 && m.getYear() > maxYear) return false;

        // 3. Runtime
        if (minRuntime > 0 && m.getRuntimeMinutes() < minRuntime) return false;
        if (maxRuntime < 999 && m.getRuntimeMinutes() > maxRuntime) return false;
        if (excludeShorts && m.getRuntimeMinutes() < 60) return false;

        // 4. Rating thresholds
        if (m.getImdbRating() < minImdbScore) return false;
        if (minImdbVotes > 0 && m.getImdbVotes() < minImdbVotes) return false;
        if (minRottenTomatoes > 0 && m.getRottenTomatoes() < minRottenTomatoes) return false;
        if (bulletproofConsensus && (m.getImdbRating() < 7.2 || m.getRottenTomatoes() < 75)) return false;

        // 4b. FSK / Altersfreigabe
        if (m.getFsk() >= 0) {
            if (m.getFsk() > maxFsk || m.getFsk() < minFsk) return false;
        }

        // 5. Exclusions
        if (excludeDoku && m.isDoku()) return false;
        if (excludeBollywood && m.isBollywood()) return false;

        String gLower = m.getGenres() != null ? m.getGenres().toLowerCase() : "";
        if (excludeAnime && (gLower.contains("anime") || gLower.contains("animation"))) {
            return false;
        }

        // Stand-Up comedy exclusion
        if (excludeStandup) {
            if (m.isStandup()) return false;
            String tLower = m.getTitle() != null ? m.getTitle().toLowerCase() : "";
            String oLower = m.getOverview() != null ? m.getOverview().toLowerCase() : "";
            if (tLower.contains("stand-up") || tLower.contains("stand up") || tLower.contains("standup") ||
                oLower.contains("stand-up") || oLower.contains("stand up") || oLower.contains("standup") ||
                oLower.contains("comedy-special") || oLower.contains("comedy special") ||
                tLower.contains("comedy special") || gLower.contains("stand-up") || gLower.contains("standup") ||
                oLower.contains("soloprogramm") || oLower.contains("bühnenprogramm")) {
                return false;
            }
        }

        // Excluded Countries
        if (!excludedCountries.isEmpty()) {
            String c = m.getOriginCountry();
            if (c != null && !c.isEmpty() && excludedCountries.contains(c.toUpperCase())) {
                return false;
            }
        }

        // Excluded Actors
        if (!excludedActors.isEmpty()) {
            for (String exActor : excludedActors) {
                if (m.hasActor(exActor)) {
                    return false;
                }
            }
        }

        // Excluded Directors
        if (!excludedDirectors.isEmpty()) {
            for (String exDir : excludedDirectors) {
                if (m.hasDirector(exDir)) {
                    return false;
                }
            }
        }

        // 5b. Compound / Conditional Exclusions (e.g. "KR:Romantik" -> excludes only Korean Romances)
        if (!compoundExclusions.isEmpty()) {
            String country = m.getOriginCountry() != null ? m.getOriginCountry().toUpperCase() : "";
            for (String rule : compoundExclusions) {
                int colon = rule.indexOf(':');
                if (colon != -1) {
                    String prefix = rule.substring(0, colon).trim();
                    String genreTarget = rule.substring(colon + 1).trim();

                    if (isGenreMatch(gLower, genreTarget)) {
                        if (!prefix.isEmpty() && country.equalsIgnoreCase(prefix)) {
                            return false;
                        }
                        if (!prefix.isEmpty() && m.hasActor(prefix)) {
                            return false;
                        }
                    }
                }
            }
        }

        // Excluded Genres by name
        for (String ex : excludedGenres) {
            if (isGenreMatch(gLower, ex)) {
                return false;
            }
        }

        // Included Genres by name (Must contain at least one of selected included genres)
        if (!includedGenres.isEmpty()) {
            boolean matched = false;
            for (String inc : includedGenres) {
                if (isGenreMatch(gLower, inc)) {
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        // Search query
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String q = searchQuery.toLowerCase().trim();
            boolean matchTitle = m.getTitle() != null && m.getTitle().toLowerCase().contains(q);
            boolean matchOrigTitle = m.getOriginalTitle() != null && m.getOriginalTitle().toLowerCase().contains(q);
            boolean matchGenre = gLower.contains(q);
            boolean matchActor = m.hasActor(q);
            boolean matchDirector = m.hasDirector(q);
            boolean matchCountry = (m.getOriginCountry() != null && m.getOriginCountry().equalsIgnoreCase(q)) ||
                                   m.getOriginCountryName().toLowerCase().contains(q);
            if (!matchTitle && !matchOrigTitle && !matchGenre && !matchActor && !matchDirector && !matchCountry) return false;
        }

        return true;
    }

    public static boolean isGenreMatch(String genresLower, String target) {
        if (genresLower == null || target == null || target.trim().isEmpty()) return false;
        String t = target.trim().toLowerCase();
        if (genresLower.contains(t)) return true;
        if (t.equals("science-fiction") || t.equals("sci-fi")) {
            return genresLower.contains("sci-fi") || genresLower.contains("science-fiction") || genresLower.contains("science fiction");
        }
        if (t.equals("dokumentarfilm") || t.equals("doku")) {
            return genresLower.contains("doku") || genresLower.contains("dokumentar") || genresLower.contains("documentary");
        }
        if (t.equals("animation") || t.equals("anime")) {
            return genresLower.contains("animation") || genresLower.contains("anime") || genresLower.contains("zeichentrick");
        }
        if (t.equals("romantik") || t.equals("liebesfilm") || t.equals("romance")) {
            return genresLower.contains("romant") || genresLower.contains("liebesfilm") || genresLower.contains("romance");
        }
        if (t.equals("komödie") || t.equals("comedy")) {
            return genresLower.contains("komödie") || genresLower.contains("comedy");
        }
        if (t.equals("krimi") || t.equals("crime")) {
            return genresLower.contains("krimi") || genresLower.contains("crime");
        }
        if (t.equals("kriegsfilm") || t.equals("war")) {
            return genresLower.contains("krieg") || genresLower.contains("war");
        }
        if (t.equals("historie") || t.equals("history")) {
            return genresLower.contains("histor") || genresLower.contains("history");
        }
        return false;
    }
}
