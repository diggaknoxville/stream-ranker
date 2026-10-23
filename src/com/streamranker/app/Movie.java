package com.streamranker.app;

import org.json.JSONObject;

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
    private String posterUrl;
    private final boolean isBollywood;
    private final boolean isDoku;
    private final boolean isStandup;
    private String originCountry;
    private String actors;
    private String director;
    private boolean inWatchlist;
    private int fsk = -1; // -1 = unrated/unknown, 0, 6, 12, 16, 18

    private List<String> cachedActorList;
    private List<String> cachedGenreList;
    private String cachedDirectorLower;

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

    public static Movie fromJson(JSONObject obj) {
        if (obj == null) return null;
        String id = obj.optString("id", "");
        if (id.startsWith("yt_")) return null; // Reject legacy YouTube movies

        String title = obj.optString("title", "");
        int year = obj.optInt("year", 0);
        double imdbRating = obj.optDouble("imdbRating", 0.0);
        int imdbVotes = obj.optInt("imdbVotes", 0);
        int rottenTomatoes = obj.optInt("rottenTomatoes", 0);
        double combinedScore = obj.optDouble("combinedScore", 0.0);
        String genres = obj.optString("genres", "");
        int runtimeMinutes = obj.optInt("runtime", 0);

        String rawProv = obj.optString("provider", "");
        StreamingProvider provider = StreamingProvider.fromId(rawProv);
        if (provider == null) {
            return null; // Reject movies with invalid, missing or removed providers (e.g. YouTube)
        }

        String deepLinkId = obj.optString("deepLinkId", "");
        if (deepLinkId.contains("youtube.com") || deepLinkId.contains("youtu.be")) {
            return null; // Reject legacy YouTube movie URLs
        }

        boolean isLeavingSoon = obj.optBoolean("isLeavingSoon", false);
        String leavingDate = obj.isNull("leavingDate") ? null : obj.optString("leavingDate", null);
        String overview = obj.optString("overview", "");
        String posterUrl = obj.optString("posterUrl", "");
        boolean isBollywood = obj.optBoolean("isBollywood", false);
        boolean isDoku = obj.optBoolean("isDoku", false);
        boolean isStandup = obj.optBoolean("isStandup", false);
        String originCountry = obj.optString("country", "US");
        String actors = obj.optString("actors", "");
        String director = obj.optString("director", "");
        String origTitle = obj.optString("origTitle", title);
        int tmdbId = obj.optInt("tmdbId", 0);
        int fsk = obj.optInt("fsk", -1);

        Movie m = new Movie(
            id, title, year, imdbRating, imdbVotes, rottenTomatoes, combinedScore,
            genres, runtimeMinutes, provider, deepLinkId, isLeavingSoon, leavingDate,
            overview, posterUrl, isBollywood, isDoku, isStandup, originCountry,
            actors, director, origTitle, tmdbId
        );
        m.setFsk(fsk);
        return m;
    }

    public JSONObject toJson() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("title", title);
            obj.put("year", year);
            obj.put("imdbRating", imdbRating);
            obj.put("imdbVotes", imdbVotes);
            obj.put("rottenTomatoes", rottenTomatoes);
            obj.put("combinedScore", combinedScore);
            obj.put("genres", genres);
            obj.put("runtime", runtimeMinutes);
            obj.put("provider", provider != null ? provider.getId() : "netflix");
            obj.put("deepLinkId", deepLinkId != null ? deepLinkId : "");
            obj.put("isLeavingSoon", isLeavingSoon);
            if (leavingDate != null) obj.put("leavingDate", leavingDate);
            obj.put("overview", overview != null ? overview : "");
            obj.put("posterUrl", posterUrl != null ? posterUrl : "");
            obj.put("isBollywood", isBollywood);
            obj.put("isDoku", isDoku);
            obj.put("isStandup", isStandup);
            obj.put("country", originCountry != null ? originCountry : "US");
            obj.put("actors", actors != null ? actors : "");
            obj.put("director", director != null ? director : "");
            obj.put("origTitle", originalTitle != null ? originalTitle : title);
            obj.put("tmdbId", tmdbId);
            obj.put("fsk", fsk);
            return obj;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public int getFsk() { return fsk; }
    public void setFsk(int fsk) { this.fsk = fsk; }

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
    public void setPosterUrl(String posterUrl) {
        if (posterUrl != null && !posterUrl.trim().isEmpty()) {
            this.posterUrl = posterUrl.trim();
        }
    }
    public boolean isBollywood() { return isBollywood; }
    public boolean isDoku() { return isDoku; }
    public boolean isStandup() { return isStandup; }

    public boolean hasGermanAudio() {
        String t = (title != null ? title : "").toLowerCase();
        String o = (overview != null ? overview : "").toLowerCase();

        // 1. Explicit OmU / Original Version markers (frequently on Arte or festival films)
        if (t.contains("(omu)") || t.contains("(ov)") || t.contains("(omeu)") ||
            t.contains(" omu") || t.contains(" ov") ||
            o.contains("originalfassung mit untertitel") || o.contains("originalfassung mit dt. untertitel") ||
            o.contains("original mit untertitel") || o.contains("original mit dt. untertitel") ||
            o.contains("nur im original") || o.contains("französische originalfassung") ||
            o.contains("englische originalfassung") || o.contains("japanische originalfassung") ||
            o.contains("koreanische originalfassung") || o.contains("spanische originalfassung") ||
            o.contains("untertitelt")) {
            return false;
        }

        // 2. German / Austrian / Swiss productions always have German audio
        String c = originCountry != null ? originCountry.toUpperCase() : "";
        if ("DE".equals(c) || "AT".equals(c) || "CH".equals(c)) {
            return true;
        }

        // 3. Mainstream providers (Netflix, Prime, Disney+, WOW, Joyn, ARD, ZDF) offer German audio dubbing
        return true;
    }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) {
        if (originCountry != null && !originCountry.trim().isEmpty()) {
            this.originCountry = originCountry.trim().toUpperCase();
        }
    }

    public String getActors() { return actors != null ? actors : ""; }
    public synchronized void setActors(String actors) {
        this.actors = actors != null ? actors.trim() : "";
        this.cachedActorList = null;
    }

    public String getDirector() { return director != null ? director : ""; }
    public synchronized void setDirector(String director) {
        this.director = director != null ? director.trim() : "";
        this.cachedDirectorLower = null;
    }

    public boolean isInWatchlist() { return inWatchlist; }
    public void setInWatchlist(boolean inWatchlist) { this.inWatchlist = inWatchlist; }

    public synchronized List<String> getActorList() {
        if (cachedActorList != null) return cachedActorList;
        if (actors == null || actors.trim().isEmpty()) {
            cachedActorList = Collections.emptyList();
            return cachedActorList;
        }
        String[] parts = actors.split(",");
        List<String> list = new ArrayList<>(parts.length);
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        cachedActorList = Collections.unmodifiableList(list);
        return cachedActorList;
    }

    public synchronized List<String> getGenreList() {
        if (cachedGenreList != null) return cachedGenreList;
        if (genres == null || genres.trim().isEmpty()) {
            cachedGenreList = Collections.emptyList();
            return cachedGenreList;
        }
        String[] parts = genres.split("[,&/]");
        List<String> list = new ArrayList<>(parts.length);
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        cachedGenreList = Collections.unmodifiableList(list);
        return cachedGenreList;
    }

    public boolean hasActor(String actorName) {
        if (actorName == null || actorName.trim().isEmpty() || actors == null || actors.isEmpty()) return false;
        String query = actorName.trim().toLowerCase(Locale.ROOT);
        for (String a : getActorList()) {
            if (a.toLowerCase(Locale.ROOT).contains(query)) return true;
        }
        return false;
    }

    public synchronized boolean hasDirector(String directorName) {
        if (directorName == null || directorName.trim().isEmpty() || director == null || director.isEmpty()) return false;
        if (cachedDirectorLower == null) {
            cachedDirectorLower = director.toLowerCase(Locale.ROOT);
        }
        return cachedDirectorLower.contains(directorName.trim().toLowerCase(Locale.ROOT));
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
