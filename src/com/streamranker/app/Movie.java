package com.streamranker.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Movie {
    private final String id;
    private final String title;
    private String originalTitle;
    private int tmdbId;
    private final int year;
    private final double imdbRating;
    private final int imdbVotes;
    private final int rottenTomatoes;
    private final double combinedScore;
    private final String genres;
    private final int runtimeMinutes;
    private final StreamingProvider provider;
    private final String deepLinkId;
    private final boolean isLeavingSoon;
    private final String leavingDate;
    private final String overview;
    private final String posterUrl;
    private final boolean isBollywood;
    private final boolean isDoku;
    private final boolean isStandup;
    private String originCountry;
    private String actors;
    private String director;
    private boolean inWatchlist;

    private static final Map<String, String> COUNTRY_NAMES = new HashMap<>();
    private static final Map<String, String> COUNTRY_FLAGS = new HashMap<>();

    static {
        registerCountry("KR", "Südkorea", "🇰🇷");
        registerCountry("US", "USA", "🇺🇸");
        registerCountry("DE", "Deutschland", "🇩🇪");
        registerCountry("GB", "Großbritannien", "🇬🇧");
        registerCountry("FR", "Frankreich", "🇫🇷");
        registerCountry("JP", "Japan", "🇯🇵");
        registerCountry("IT", "Italien", "🇮🇹");
        registerCountry("ES", "Spanien", "🇪🇸");
        registerCountry("IN", "Indien", "🇮🇳");
        registerCountry("CA", "Kanada", "🇨🇦");
        registerCountry("AU", "Australien", "🇦🇺");
        registerCountry("SE", "Schweden", "🇸🇪");
        registerCountry("DK", "Dänemark", "🇩🇰");
        registerCountry("NO", "Norwegen", "🇳🇴");
        registerCountry("PL", "Polen", "🇵🇱");
        registerCountry("AT", "Österreich", "🇦🇹");
        registerCountry("CH", "Schweiz", "🇨🇭");
        registerCountry("TR", "Türkei", "🇹🇷");
        registerCountry("CN", "China", "🇨🇳");
        registerCountry("HK", "Hongkong", "🇭🇰");
        registerCountry("TW", "Taiwan", "🇹🇼");
        registerCountry("NZ", "Neuseeland", "🇳🇿");
        registerCountry("MX", "Mexiko", "🇲🇽");
        registerCountry("BR", "Brasilien", "🇧🇷");
        registerCountry("AR", "Argentinien", "🇦🇷");
        registerCountry("IE", "Irland", "🇮🇪");
        registerCountry("BE", "Belgien", "🇧🇪");
        registerCountry("NL", "Niederlande", "🇳🇱");
    }

    private static void registerCountry(String code, String name, String flag) {
        COUNTRY_NAMES.put(code, name);
        COUNTRY_FLAGS.put(code, flag);
    }

    public Movie(String id, String title, int year, double imdbRating, int imdbVotes,
                 int rottenTomatoes, double combinedScore,
                 String genres, int runtimeMinutes, StreamingProvider provider,
                 String deepLinkId, boolean isLeavingSoon, String leavingDate,
                 String overview, String posterUrl,
                 boolean isBollywood, boolean isDoku, boolean isStandup, String originCountry,
                 String actors, String director, String originalTitle, int tmdbId) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.imdbRating = imdbRating;
        this.imdbVotes = imdbVotes;
        this.rottenTomatoes = rottenTomatoes;
        this.combinedScore = combinedScore;
        this.genres = genres;
        this.runtimeMinutes = runtimeMinutes;
        this.provider = provider;
        this.deepLinkId = deepLinkId;
        this.isLeavingSoon = isLeavingSoon;
        this.leavingDate = leavingDate;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.isBollywood = isBollywood;
        this.isDoku = isDoku;
        this.isStandup = isStandup;
        this.originCountry = originCountry != null ? originCountry.trim().toUpperCase() : "US";
        this.actors = actors != null ? actors.trim() : "";
        this.director = director != null ? director.trim() : "";
        this.originalTitle = originalTitle != null ? originalTitle.trim() : title;
        this.tmdbId = tmdbId;
        this.inWatchlist = false;
    }

    public Movie(String id, String title, int year, double imdbRating, int imdbVotes,
                 int rottenTomatoes, double combinedScore,
                 String genres, int runtimeMinutes, StreamingProvider provider,
                 String deepLinkId, boolean isLeavingSoon, String leavingDate,
                 String overview, String posterUrl,
                 boolean isBollywood, boolean isDoku, boolean isStandup, String originCountry) {
        this(id, title, year, imdbRating, imdbVotes, rottenTomatoes, combinedScore,
             genres, runtimeMinutes, provider, deepLinkId, isLeavingSoon, leavingDate,
             overview, posterUrl, isBollywood, isDoku, isStandup, originCountry,
             "", "", title, 0);
    }

    // Backwards-compatible constructor for seeded list
    public Movie(String id, String title, int year, double imdbRating, int imdbVotes,
                 int rottenTomatoes, double combinedScore,
                 String genres, int runtimeMinutes, StreamingProvider provider,
                 String deepLinkId, boolean isLeavingSoon, String leavingDate,
                 String overview, String posterUrl,
                 boolean isBollywood, boolean isDoku) {
        this(id, title, year, imdbRating, imdbVotes, rottenTomatoes, combinedScore,
             genres, runtimeMinutes, provider, deepLinkId, isLeavingSoon, leavingDate,
             overview, posterUrl, isBollywood, isDoku, false, isBollywood ? "IN" : "US");
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getOriginalTitle() { return (originalTitle != null && !originalTitle.isEmpty()) ? originalTitle : title; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public int getTmdbId() { return tmdbId; }
    public void setTmdbId(int tmdbId) { this.tmdbId = tmdbId; }
    public int getYear() { return year; }
    public double getImdbRating() { return imdbRating; }
    public int getImdbVotes() { return imdbVotes; }
    public int getRottenTomatoes() { return rottenTomatoes; }
    public double getCombinedScore() { return combinedScore; }
    public String getGenres() { return genres; }
    public int getRuntimeMinutes() { return runtimeMinutes; }
    public StreamingProvider getProvider() { return provider; }
    public String getDeepLinkId() { return deepLinkId; }
    public boolean isLeavingSoon() { return isLeavingSoon; }
    public String getLeavingDate() { return leavingDate; }
    public String getOverview() { return overview; }
    public String getPosterUrl() { return posterUrl; }
    public boolean isBollywood() { return isBollywood; }
    public boolean isDoku() { return isDoku; }
    public boolean isStandup() { return isStandup; }
    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) {
        if (originCountry != null && !originCountry.trim().isEmpty()) {
            this.originCountry = originCountry.trim().toUpperCase();
        }
    }

    public String getActors() { return actors != null ? actors : ""; }
    public void setActors(String actors) { this.actors = actors != null ? actors.trim() : ""; }

    public String getDirector() { return director != null ? director : ""; }
    public void setDirector(String director) { this.director = director != null ? director.trim() : ""; }

    public boolean isInWatchlist() { return inWatchlist; }
    public void setInWatchlist(boolean inWatchlist) { this.inWatchlist = inWatchlist; }

    public List<String> getActorList() {
        if (actors == null || actors.trim().isEmpty()) return Collections.emptyList();
        String[] parts = actors.split(",");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        return list;
    }

    public List<String> getGenreList() {
        if (genres == null || genres.trim().isEmpty()) return Collections.emptyList();
        String[] parts = genres.split("[,&/]");
        List<String> list = new ArrayList<>();
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        return list;
    }

    public boolean hasActor(String actorName) {
        if (actorName == null || actorName.trim().isEmpty() || actors == null || actors.isEmpty()) return false;
        String query = actorName.trim().toLowerCase(Locale.ROOT);
        for (String a : getActorList()) {
            if (a.toLowerCase(Locale.ROOT).contains(query)) return true;
        }
        return false;
    }

    public boolean hasDirector(String directorName) {
        if (directorName == null || directorName.trim().isEmpty() || director == null || director.isEmpty()) return false;
        return director.toLowerCase(Locale.ROOT).contains(directorName.trim().toLowerCase(Locale.ROOT));
    }

    public String getOriginCountryName() {
        if (originCountry == null) return "USA";
        String name = COUNTRY_NAMES.get(originCountry);
        return name != null ? name : originCountry;
    }

    public String getOriginCountryFlag() {
        if (originCountry == null) return "🇺🇸";
        String flag = COUNTRY_FLAGS.get(originCountry);
        return flag != null ? flag : "🌍";
    }

    public String getFormattedVotes() {
        if (imdbVotes >= 1_000_000) {
            return String.format(Locale.ROOT, "%.1fM", imdbVotes / 1_000_000.0);
        } else if (imdbVotes >= 1_000) {
            return String.format(Locale.ROOT, "%.0fK", imdbVotes / 1_000.0);
        } else {
            return String.valueOf(imdbVotes);
        }
    }
}
