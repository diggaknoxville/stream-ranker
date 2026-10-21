package com.streamranker.app;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LiveCatalogFetcher {
    private static final String TAG = "LiveCatalogFetcher";
    private static final String API_KEY = "4e44d9029b1270a757cddc766a1bcb63";

    public interface FetchCallback {
        void onProgress(int currentCount, int targetCount);
        void onComplete(int newMoviesAdded, int totalMatching);
        void onError(String message);
    }

    private static LiveCatalogFetcher instance;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isFetching = false;

    // TMDB Genre Map
    private static final Map<String, Integer> GENRE_NAME_TO_ID = new HashMap<>();
    private static final Map<Integer, String> GENRE_ID_TO_NAME = new HashMap<>();

    static {
        registerGenre(28, "Action");
        registerGenre(12, "Abenteuer");
        registerGenre(16, "Animation");
        registerGenre(35, "Komödie");
        registerGenre(80, "Krimi");
        registerGenre(99, "Dokumentarfilm");
        registerGenre(18, "Drama");
        registerGenre(10751, "Familie");
        registerGenre(14, "Fantasy");
        registerGenre(36, "Historie");
        registerGenre(27, "Horror");
        registerGenre(10402, "Musik");
        registerGenre(9648, "Mystery");
        registerGenre(10749, "Romantik");
        registerGenre(878, "Science-Fiction");
        registerGenre(53, "Thriller");
        registerGenre(10752, "Kriegsfilm");
        registerGenre(37, "Western");

        // Aliases
        GENRE_NAME_TO_ID.put("sci-fi", 878);
        GENRE_NAME_TO_ID.put("science fiction", 878);
        GENRE_NAME_TO_ID.put("doku", 99);
        GENRE_NAME_TO_ID.put("dokumentation", 99);
        GENRE_NAME_TO_ID.put("anime", 16);
        GENRE_NAME_TO_ID.put("zeichentrick", 16);
        GENRE_NAME_TO_ID.put("liebesfilm", 10749);
        GENRE_NAME_TO_ID.put("romance", 10749);
        GENRE_NAME_TO_ID.put("comedy", 35);
        GENRE_NAME_TO_ID.put("crime", 80);
        GENRE_NAME_TO_ID.put("war", 10752);
        GENRE_NAME_TO_ID.put("history", 36);
        GENRE_NAME_TO_ID.put("adventure", 12);
    }

    private static void registerGenre(int id, String name) {
        GENRE_NAME_TO_ID.put(name.toLowerCase(), id);
        GENRE_ID_TO_NAME.put(id, name);
    }

    public static synchronized LiveCatalogFetcher getInstance() {
        if (instance == null) {
            instance = new LiveCatalogFetcher();
        }
        return instance;
    }

    private LiveCatalogFetcher() {}

    public synchronized boolean isFetching() {
        return isFetching;
    }

    public void fetchDynamicMatches(final FilterCriteria criteria,
                                    final Set<StreamingProvider> activeProviders,
                                    final WatchedManager watchedManager,
                                    final FetchCallback callback) {
        if (isFetching) return;
        isFetching = true;

        executor.execute(() -> {
            try {
                final int target = criteria.targetCount;
                int addedTotal = 0;

                // Determine providers to query
                List<StreamingProvider> providersToQuery = new ArrayList<>();
                if (!criteria.singleProviderId.equalsIgnoreCase("all")) {
                    StreamingProvider p = StreamingProvider.fromId(criteria.singleProviderId);
                    if (p != null) providersToQuery.add(p);
                } else if (activeProviders != null && !activeProviders.isEmpty()) {
                    for (StreamingProvider p : activeProviders) {
                        providersToQuery.add(p);
                    }
                }
                if (providersToQuery.isEmpty()) {
                    providersToQuery.add(StreamingProvider.NETFLIX);
                    providersToQuery.add(StreamingProvider.PRIME_VIDEO);
                }

                int page = 1;
                int maxPages = 20;

                while (page <= maxPages) {
                    boolean anyResultsOnPage = false;

                    for (StreamingProvider prov : providersToQuery) {
                        List<Movie> pageMovies = queryTmdbPage(criteria, prov, page);
                        if (pageMovies != null && !pageMovies.isEmpty()) {
                            anyResultsOnPage = true;
                            int added = MovieDatabase.getInstance().addMovies(pageMovies);
                            addedTotal += added;
                        }

                        final int currentMatches = MovieDatabase.getInstance().getFilteredList(criteria, activeProviders, watchedManager).size();
                        mainHandler.post(() -> {
                            if (callback != null) {
                                callback.onProgress(currentMatches, target);
                            }
                        });

                        if (currentMatches >= target) {
                            break;
                        }
                    }

                    int currentMatches = MovieDatabase.getInstance().getFilteredList(criteria, activeProviders, watchedManager).size();
                    if (currentMatches >= target || !anyResultsOnPage) {
                        break;
                    }
                    page++;
                }

                final int finalCount = MovieDatabase.getInstance().getFilteredList(criteria, activeProviders, watchedManager).size();
                final int finalAdded = addedTotal;
                mainHandler.post(() -> {
                    isFetching = false;
                    if (callback != null) {
                        callback.onComplete(finalAdded, finalCount);
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error in fetchDynamicMatches: " + e.getMessage(), e);
                mainHandler.post(() -> {
                    isFetching = false;
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    }
                });
            }
        });
    }

    private List<Movie> queryTmdbPage(FilterCriteria criteria, StreamingProvider targetProvider, int page) {
        try {
            StringBuilder urlBuilder = new StringBuilder("https://api.themoviedb.org/3/discover/movie?");
            urlBuilder.append("api_key=").append(API_KEY);
            urlBuilder.append("&language=de-DE");
            urlBuilder.append("&watch_region=DE");
            urlBuilder.append("&with_watch_monetization_types=flatrate");
            urlBuilder.append("&page=").append(page);

            // Provider mapping
            String provCode = "8"; // Netflix default
            if (targetProvider.equals(StreamingProvider.PRIME_VIDEO)) provCode = "9";
            else if (targetProvider.equals(StreamingProvider.DISNEY_PLUS)) provCode = "337";
            else if (targetProvider.equals(StreamingProvider.WOW)) provCode = "30";
            urlBuilder.append("&with_watch_providers=").append(provCode);

            // Year range
            if (criteria.minYear > 0) {
                urlBuilder.append("&primary_release_date.gte=").append(criteria.minYear).append("-01-01");
            }
            if (criteria.maxYear < 9999) {
                urlBuilder.append("&primary_release_date.lte=").append(criteria.maxYear).append("-12-31");
            }

            // Excluded genres
            StringBuilder withoutGenres = new StringBuilder();
            if (criteria.excludeDoku) withoutGenres.append("99");
            if (criteria.excludeAnime) {
                if (withoutGenres.length() > 0) withoutGenres.append(",");
                withoutGenres.append("16");
            }
            for (String ex : criteria.excludedGenres) {
                Integer gid = GENRE_NAME_TO_ID.get(ex.toLowerCase());
                if (gid != null) {
                    if (withoutGenres.length() > 0) withoutGenres.append(",");
                    withoutGenres.append(gid);
                }
            }
            if (withoutGenres.length() > 0) {
                urlBuilder.append("&without_genres=").append(withoutGenres.toString());
            }

            // Included genres
            StringBuilder withGenres = new StringBuilder();
            for (String inc : criteria.includedGenres) {
                Integer gid = GENRE_NAME_TO_ID.get(inc.toLowerCase());
                if (gid != null) {
                    if (withGenres.length() > 0) withGenres.append(",");
                    withGenres.append(gid);
                }
            }
            if (withGenres.length() > 0) {
                urlBuilder.append("&with_genres=").append(withGenres.toString());
            }

            // Sorting & quality threshold
            if (criteria.sortMode == MovieDatabase.SORT_YEAR) {
                urlBuilder.append("&sort_by=primary_release_date.desc");
                urlBuilder.append("&vote_count.gte=50");
            } else {
                urlBuilder.append("&sort_by=vote_average.desc");
                urlBuilder.append("&vote_count.gte=100");
            }

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 16; StreamRanker)");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "TMDB HTTP error: " + conn.getResponseCode());
                return null;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            JSONObject response = new JSONObject(sb.toString());
            JSONArray results = response.optJSONArray("results");
            if (results == null) return null;

            List<Movie> list = new ArrayList<>();
            for (int i = 0; i < results.length(); i++) {
                JSONObject obj = results.optJSONObject(i);
                if (obj == null) continue;

                String title = obj.optString("title", "");
                if (title.isEmpty()) continue;

                int tmdbId = obj.optInt("id", 0);
                String releaseDate = obj.optString("release_date", "2000-01-01");
                int year = 2000;
                if (releaseDate.length() >= 4) {
                    try { year = Integer.parseInt(releaseDate.substring(0, 4)); } catch (Exception ignored) {}
                }

                double voteAverage = obj.optDouble("vote_average", 0.0);
                double imdbScore = Math.round(voteAverage * 10.0) / 10.0;
                int voteCount = obj.optInt("vote_count", 0);

                // Genres
                JSONArray gArray = obj.optJSONArray("genre_ids");
                StringBuilder gSb = new StringBuilder();
                boolean isDoku = false;
                boolean isAnime = false;
                if (gArray != null) {
                    for (int g = 0; g < gArray.length(); g++) {
                        int gid = gArray.optInt(g);
                        if (gid == 99) isDoku = true;
                        if (gid == 16) isAnime = true;
                        String gName = GENRE_ID_TO_NAME.get(gid);
                        if (gName != null) {
                            if (gSb.length() > 0) gSb.append(", ");
                            gSb.append(gName);
                        }
                    }
                }
                String genres = gSb.length() > 0 ? gSb.toString() : "Drama";

                // Poster
                String posterPath = obj.optString("poster_path", "");
                String posterUrl = "";
                if (!posterPath.isEmpty()) {
                    posterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                }

                String overview = obj.optString("overview", "");

                // Rotten Tomatoes and Perlen-Score estimation
                int rtScore = (int) Math.min(100, Math.max(50, Math.round(imdbScore * 10.0 + (imdbScore - 7.0) * 3)));
                double combined = Math.round(((imdbScore * 10.0 + rtScore) / 2.0) * 10.0) / 10.0;

                // Stand-up check
                boolean isStandup = false;
                String tLower = title.toLowerCase();
                String oLower = overview.toLowerCase();
                if (tLower.contains("stand-up") || tLower.contains("stand up") || tLower.contains("standup") ||
                    oLower.contains("stand-up") || oLower.contains("stand up") || oLower.contains("standup") ||
                    tLower.contains("comedy special") || oLower.contains("comedy-special") || oLower.contains("comedy special")) {
                    isStandup = true;
                }

                // Country detection
                String originCountry = "US";
                JSONArray origins = obj.optJSONArray("origin_country");
                if (origins != null && origins.length() > 0) {
                    originCountry = origins.optString(0, "US").toUpperCase();
                } else {
                    String origLang = obj.optString("original_language", "");
                    if ("de".equalsIgnoreCase(origLang)) originCountry = "DE";
                    else if ("fr".equalsIgnoreCase(origLang)) originCountry = "FR";
                    else if ("ja".equalsIgnoreCase(origLang)) originCountry = "JP";
                    else if ("ko".equalsIgnoreCase(origLang)) originCountry = "KR";
                    else if ("es".equalsIgnoreCase(origLang)) originCountry = "ES";
                    else if ("it".equalsIgnoreCase(origLang)) originCountry = "IT";
                    else if ("gb".equalsIgnoreCase(origLang)) originCountry = "GB";
                    else if ("hi".equalsIgnoreCase(origLang) || "ta".equalsIgnoreCase(origLang) || "te".equalsIgnoreCase(origLang)) originCountry = "IN";
                }

                // Bollywood / India check
                boolean isBolly = "IN".equalsIgnoreCase(originCountry);
                if (origins != null) {
                    for (int o = 0; o < origins.length(); o++) {
                        if ("IN".equalsIgnoreCase(origins.optString(o))) {
                            isBolly = true;
                            originCountry = "IN";
                            break;
                        }
                    }
                }

                String mid = "tmdb_" + tmdbId;
                String deepLinkId = ""; // In-app search handles TMDB movies seamlessly

                Movie m = new Movie(
                    mid, title, year, imdbScore, voteCount,
                    rtScore, combined, genres, 110, targetProvider,
                    deepLinkId, false, null, overview, posterUrl,
                    isBolly, isDoku, isStandup, originCountry
                );
                list.add(m);
            }
            return list;
        } catch (Exception e) {
            Log.e(TAG, "Failed queryTmdbPage: " + e.getMessage());
            return null;
        }
    }
}
