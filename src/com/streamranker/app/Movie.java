package com.streamranker.app;

public class Movie {
    private final String id;
    private final String title;
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
    private final String originCountry;
    private boolean inWatchlist;

    public Movie(String id, String title, int year, double imdbRating, int imdbVotes,
                 int rottenTomatoes, double combinedScore,
                 String genres, int runtimeMinutes, StreamingProvider provider,
                 String deepLinkId, boolean isLeavingSoon, String leavingDate,
                 String overview, String posterUrl,
                 boolean isBollywood, boolean isDoku, boolean isStandup, String originCountry) {
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
        this.inWatchlist = false;
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

    public boolean isInWatchlist() { return inWatchlist; }
    public void setInWatchlist(boolean inWatchlist) { this.inWatchlist = inWatchlist; }

    public String getFormattedVotes() {
        if (imdbVotes >= 1_000_000) {
            return String.format("%.1fM", imdbVotes / 1_000_000.0);
        } else if (imdbVotes >= 1_000) {
            return String.format("%.0fK", imdbVotes / 1_000.0);
        } else {
            return String.valueOf(imdbVotes);
        }
    }
}
