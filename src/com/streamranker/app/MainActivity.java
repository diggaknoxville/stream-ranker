package com.streamranker.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    public static final int TAB_LEADERBOARD = 0;
    public static final int TAB_LEAVING_SOON = 1;
    public static final int TAB_WATCHLIST = 2;
    public static final int TAB_SEARCH = 3;

    private ProviderManager providerManager;
    private WatchlistManager watchlistManager;
    private WatchedManager watchedManager;
    private MovieDatabase movieDatabase;
    private AsyncImageLoader imageLoader;
    private LiveCatalogFetcher catalogFetcher;

    // Active filters
    private int currentTab = TAB_LEADERBOARD;
    private final FilterCriteria criteria = new FilterCriteria();

    // UI references
    private LinearLayout providerChipsContainer;
    private LinearLayout sortChipsContainer;
    private LinearLayout genreChipsContainer;
    private LinearLayout exclusionChipsContainer;
    private LinearLayout tabsContainer;
    private LinearLayout movieCardsContainer;
    private EditText searchEditText;
    private TextView headerSubtitle;
    private TextView emptyStateTextView;
    private TextView listCountHeader;
    private ProgressBar loadingIndicator;
    private Button loadMoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        providerManager = ProviderManager.getInstance();
        providerManager.init(this);

        watchlistManager = WatchlistManager.getInstance();
        watchlistManager.init(this);

        watchedManager = WatchedManager.getInstance();
        watchedManager.init(this);

        // Load persisted filter preferences
        criteria.load(this);
        if (watchedManager != null) {
            criteria.excludeWatched = watchedManager.isExcludeWatched();
        }

        movieDatabase = MovieDatabase.getInstance();
        imageLoader = AsyncImageLoader.getInstance(this);
        catalogFetcher = LiveCatalogFetcher.getInstance();

        setContentView(buildRootLayout());

        refreshUi();

        // Check for OTA updates in background on launch
        OtaUpdateManager.getInstance().checkUpdate(this, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        providerManager.rescan(this);
        refreshUi();
    }

    private View buildRootLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF0D1117);
        root.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // 1. Header Bar
        root.addView(buildHeaderBar());

        // 2. Navigation Tabs
        tabsContainer = buildTabsBar();
        root.addView(tabsContainer);

        // 3. Provider Filter Dimension
        HorizontalScrollView providerScroll = new HorizontalScrollView(this);
        providerScroll.setHorizontalScrollBarEnabled(false);
        providerScroll.setPadding(dp(12), dp(4), dp(12), dp(4));
        providerChipsContainer = new LinearLayout(this);
        providerChipsContainer.setOrientation(LinearLayout.HORIZONTAL);
        providerScroll.addView(providerChipsContainer);
        root.addView(providerScroll);

        // 4. Sort Dimension
        HorizontalScrollView sortScroll = new HorizontalScrollView(this);
        sortScroll.setHorizontalScrollBarEnabled(false);
        sortScroll.setPadding(dp(12), dp(2), dp(12), dp(4));
        sortChipsContainer = new LinearLayout(this);
        sortChipsContainer.setOrientation(LinearLayout.HORIZONTAL);
        sortScroll.addView(sortChipsContainer);
        root.addView(sortScroll);

        // 5. Genre Quick Dimension
        HorizontalScrollView genreScroll = new HorizontalScrollView(this);
        genreScroll.setHorizontalScrollBarEnabled(false);
        genreScroll.setPadding(dp(12), dp(2), dp(12), dp(4));
        genreChipsContainer = new LinearLayout(this);
        genreChipsContainer.setOrientation(LinearLayout.HORIZONTAL);
        genreScroll.addView(genreChipsContainer);
        root.addView(genreScroll);

        // 6. Exclusion & Year Presets
        HorizontalScrollView exclusionScroll = new HorizontalScrollView(this);
        exclusionScroll.setHorizontalScrollBarEnabled(false);
        exclusionScroll.setPadding(dp(12), dp(2), dp(12), dp(4));
        exclusionChipsContainer = new LinearLayout(this);
        exclusionChipsContainer.setOrientation(LinearLayout.HORIZONTAL);
        exclusionScroll.addView(exclusionChipsContainer);
        root.addView(exclusionScroll);

        // 7. Search Input
        searchEditText = new EditText(this);
        searchEditText.setHint("Titel oder Genre suchen...");
        searchEditText.setHintTextColor(0xFF6E7681);
        searchEditText.setTextColor(0xFFFFFFFF);
        searchEditText.setTextSize(14);
        searchEditText.setBackgroundResource(R.drawable.search_box_background);
        searchEditText.setPadding(dp(14), dp(10), dp(14), dp(10));
        LinearLayout.LayoutParams searchLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        searchLp.setMargins(dp(14), dp(4), dp(14), dp(4));
        searchEditText.setLayoutParams(searchLp);
        searchEditText.setVisibility(View.GONE);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                criteria.searchQuery = s.toString();
                renderMovieList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        root.addView(searchEditText);

        // 8. Results counter & loading indicator row
        LinearLayout countRow = new LinearLayout(this);
        countRow.setOrientation(LinearLayout.HORIZONTAL);
        countRow.setGravity(Gravity.CENTER_VERTICAL);
        countRow.setPadding(dp(16), dp(4), dp(16), dp(4));

        listCountHeader = new TextView(this);
        listCountHeader.setTextColor(0xFF8B949E);
        listCountHeader.setTextSize(12);
        LinearLayout.LayoutParams countLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        listCountHeader.setLayoutParams(countLp);
        listCountHeader.setText("Top 100 Filme");
        countRow.addView(listCountHeader);

        loadingIndicator = new ProgressBar(this, null, android.R.attr.progressBarStyleSmall);
        loadingIndicator.setVisibility(View.GONE);
        countRow.addView(loadingIndicator);

        root.addView(countRow);

        // 9. Scrollable Movie List
        ScrollView movieScrollView = new ScrollView(this);
        movieScrollView.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
        movieScrollView.setLayoutParams(scrollLp);

        LinearLayout scrollContent = new LinearLayout(this);
        scrollContent.setOrientation(LinearLayout.VERTICAL);
        scrollContent.setPadding(dp(12), dp(2), dp(12), dp(24));

        emptyStateTextView = new TextView(this);
        emptyStateTextView.setTextColor(0xFF8B949E);
        emptyStateTextView.setTextSize(15);
        emptyStateTextView.setGravity(Gravity.CENTER);
        emptyStateTextView.setPadding(dp(20), dp(50), dp(20), dp(50));
        emptyStateTextView.setText("Keine Filme mit den aktuellen Filtern gefunden.");
        emptyStateTextView.setVisibility(View.GONE);
        scrollContent.addView(emptyStateTextView);

        movieCardsContainer = new LinearLayout(this);
        movieCardsContainer.setOrientation(LinearLayout.VERTICAL);
        scrollContent.addView(movieCardsContainer);

        // Load more button
        loadMoreButton = new Button(this);
        loadMoreButton.setText("⬇ Weitere 50 Filme nachladen");
        loadMoreButton.setTextColor(0xFFF0F6FC);
        loadMoreButton.setTextSize(13);
        loadMoreButton.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams lmBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(44));
        lmBtnLp.setMargins(0, dp(12), 0, dp(20));
        loadMoreButton.setLayoutParams(lmBtnLp);
        loadMoreButton.setOnClickListener(v -> {
            criteria.targetCount += 50;
            triggerLiveFetchIfNeeded();
        });
        scrollContent.addView(loadMoreButton);

        movieScrollView.addView(scrollContent);
        root.addView(movieScrollView);

        return root;
    }

    private View buildHeaderBar() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(16), dp(14), dp(16), dp(8));
        header.setBackgroundColor(0xFF161B22);

        LinearLayout titleCol = new LinearLayout(this);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        titleCol.setLayoutParams(titleLp);

        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setOrientation(LinearLayout.HORIZONTAL);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView logoIcon = new TextView(this);
        logoIcon.setText("🎬 ");
        logoIcon.setTextSize(20);
        brandRow.addView(logoIcon);

        TextView title = new TextView(this);
        title.setText("StreamRanker");
        title.setTextColor(0xFFF5C518);
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        brandRow.addView(title);

        titleCol.addView(brandRow);

        headerSubtitle = new TextView(this);
        headerSubtitle.setTextColor(0xFF8B949E);
        headerSubtitle.setTextSize(11);
        headerSubtitle.setText(movieDatabase.getTotalCount() + " Filme • Dynamischer Nachlade-Modus");
        titleCol.addView(headerSubtitle);

        header.addView(titleCol);

        // Precise Filter Manager Button
        Button filterBtn = new Button(this);
        filterBtn.setText("⚙️ Filter");
        filterBtn.setTextColor(0xFFF0F6FC);
        filterBtn.setTextSize(12);
        filterBtn.setBackgroundResource(R.drawable.btn_generic);
        filterBtn.setPadding(dp(10), dp(6), dp(10), dp(6));
        LinearLayout.LayoutParams filterBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(36));
        filterBtnLp.setMargins(0, 0, dp(6), 0);
        filterBtn.setLayoutParams(filterBtnLp);
        filterBtn.setOnClickListener(v -> showDetailedFilterDialog());
        header.addView(filterBtn);

        // Watchlist Button
        Button syncBtn = new Button(this);
        syncBtn.setText("⭐ Watchlist");
        syncBtn.setTextColor(0xFFF0F6FC);
        syncBtn.setTextSize(12);
        syncBtn.setBackgroundResource(R.drawable.btn_generic);
        syncBtn.setPadding(dp(10), dp(6), dp(10), dp(6));
        LinearLayout.LayoutParams syncBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(36));
        syncBtnLp.setMargins(0, 0, dp(6), 0);
        syncBtn.setLayoutParams(syncBtnLp);
        syncBtn.setOnClickListener(v -> showWatchlistDialog());
        header.addView(syncBtn);

        // OTA Update Button
        Button updateBtn = new Button(this);
        updateBtn.setText("🚀 Update");
        updateBtn.setTextColor(0xFF58A6FF);
        updateBtn.setTextSize(12);
        updateBtn.setBackgroundResource(R.drawable.btn_generic);
        updateBtn.setPadding(dp(10), dp(6), dp(10), dp(6));
        LinearLayout.LayoutParams updateBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(36));
        updateBtn.setLayoutParams(updateBtnLp);
        updateBtn.setOnClickListener(v -> OtaUpdateManager.getInstance().checkUpdate(MainActivity.this, true));
        header.addView(updateBtn);

        return header;
    }

    private LinearLayout buildTabsBar() {
        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);
        tabs.setPadding(dp(12), dp(4), dp(12), dp(4));
        tabs.setBackgroundColor(0xFF161B22);

        tabs.addView(createTabButton("🏆 Top 100", TAB_LEADERBOARD));
        tabs.addView(createTabButton("⏳ Bald weg", TAB_LEAVING_SOON));
        tabs.addView(createTabButton("⭐ Watchlist", TAB_WATCHLIST));
        tabs.addView(createTabButton("🔍 Suche", TAB_SEARCH));

        return tabs;
    }

    private Button createTabButton(String text, final int tab) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(12);
        btn.setTransformationMethod(null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dp(38), 1.0f);
        lp.setMargins(dp(2), 0, dp(2), 0);
        btn.setLayoutParams(lp);
        btn.setPadding(0, 0, 0, 0);

        btn.setOnClickListener(v -> {
            currentTab = tab;
            searchEditText.setVisibility(tab == TAB_SEARCH ? View.VISIBLE : View.GONE);
            if (tab != TAB_SEARCH) {
                criteria.searchQuery = "";
                searchEditText.setText("");
            }
            refreshUi();
        });

        return btn;
    }

    private void refreshUi() {
        int installedCount = providerManager.getInstalledProviders().size();
        headerSubtitle.setText(movieDatabase.getTotalCount() + " Filme im Pool • " + installedCount + " Dienste erkannt");

        renderProviderDimensionChips();
        renderSortModeChips();
        renderGenreChips();
        renderExclusionAndYearChips();

        for (int i = 0; i < tabsContainer.getChildCount(); i++) {
            Button btn = (Button) tabsContainer.getChildAt(i);
            boolean isSelected = (i == currentTab);
            if (isSelected) {
                btn.setBackgroundColor(0xFF21262D);
                btn.setTextColor(0xFFF5C518);
                btn.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                btn.setBackgroundColor(0x00000000);
                btn.setTextColor(0xFF8B949E);
                btn.setTypeface(Typeface.DEFAULT);
            }
        }

        renderMovieList();
    }

    private void renderProviderDimensionChips() {
        providerChipsContainer.removeAllViews();

        TextView label = new TextView(this);
        label.setText("Anbieter: ");
        label.setTextColor(0xFF8B949E);
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setPadding(0, 0, dp(4), 0);
        providerChipsContainer.addView(label);

        providerChipsContainer.addView(createFilterPill("Alle aktiven", criteria.singleProviderId.equals("all"), 0xFF388BFD, () -> {
            criteria.singleProviderId = "all";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        providerChipsContainer.addView(createFilterPill("Nur Netflix", criteria.singleProviderId.equals("netflix"), 0xFFE50914, () -> {
            criteria.singleProviderId = criteria.singleProviderId.equals("netflix") ? "all" : "netflix";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        providerChipsContainer.addView(createFilterPill("Nur Prime Video", criteria.singleProviderId.equals("prime"), 0xFF00A8E1, () -> {
            criteria.singleProviderId = criteria.singleProviderId.equals("prime") ? "all" : "prime";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        providerChipsContainer.addView(createFilterPill("Nur Disney+", criteria.singleProviderId.equals("disney"), 0xFF113CCF, () -> {
            criteria.singleProviderId = criteria.singleProviderId.equals("disney") ? "all" : "disney";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));
    }

    private void renderSortModeChips() {
        sortChipsContainer.removeAllViews();

        TextView label = new TextView(this);
        label.setText("Sortieren: ");
        label.setTextColor(0xFF8B949E);
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setPadding(0, 0, dp(4), 0);
        sortChipsContainer.addView(label);

        sortChipsContainer.addView(createFilterPill("🏆 IMDb", criteria.sortMode == MovieDatabase.SORT_IMDB, 0xFFF5C518, () -> {
            criteria.sortMode = MovieDatabase.SORT_IMDB;
            criteria.save(MainActivity.this);
            refreshUi();
        }));

        sortChipsContainer.addView(createFilterPill("🍅 Rotten Tomatoes", criteria.sortMode == MovieDatabase.SORT_ROTTEN_TOMATOES, 0xFFFA320A, () -> {
            criteria.sortMode = MovieDatabase.SORT_ROTTEN_TOMATOES;
            criteria.save(MainActivity.this);
            refreshUi();
        }));

        sortChipsContainer.addView(createFilterPill("💎 Perlen-Score", criteria.sortMode == MovieDatabase.SORT_PERLE, 0xFF00B4D8, () -> {
            criteria.sortMode = MovieDatabase.SORT_PERLE;
            criteria.save(MainActivity.this);
            refreshUi();
        }));

        sortChipsContainer.addView(createFilterPill("📅 Neueste", criteria.sortMode == MovieDatabase.SORT_YEAR, 0xFF8B949E, () -> {
            criteria.sortMode = MovieDatabase.SORT_YEAR;
            criteria.save(MainActivity.this);
            refreshUi();
        }));
    }

    private void renderGenreChips() {
        genreChipsContainer.removeAllViews();

        TextView label = new TextView(this);
        label.setText("Genre: ");
        label.setTextColor(0xFF8B949E);
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setPadding(0, 0, dp(4), 0);
        genreChipsContainer.addView(label);

        String[] genres = {"Alle", "Action", "Drama", "Krimi", "Science-Fiction", "Thriller", "Komödie", "Animation", "Horror", "Abenteuer", "Fantasy"};
        for (final String g : genres) {
            boolean active = (g.equals("Alle") && criteria.includedGenres.isEmpty()) || criteria.includedGenres.contains(g);
            genreChipsContainer.addView(createFilterPill(g, active, 0xFF388BFD, () -> {
                if (g.equals("Alle")) {
                    criteria.includedGenres.clear();
                } else {
                    if (criteria.includedGenres.contains(g)) {
                        criteria.includedGenres.remove(g);
                    } else {
                        criteria.includedGenres.clear();
                        criteria.includedGenres.add(g);
                    }
                }
                criteria.save(MainActivity.this);
                refreshUi();
                triggerLiveFetchIfNeeded();
            }));
        }
    }

    private void renderExclusionAndYearChips() {
        exclusionChipsContainer.removeAllViews();

        TextView label = new TextView(this);
        label.setText("Filter: ");
        label.setTextColor(0xFF8B949E);
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setPadding(0, 0, dp(4), 0);
        exclusionChipsContainer.addView(label);

        // 0. Exclude watched chip
        int watchedCount = watchedManager != null ? watchedManager.getCount() : 0;
        String watchedLabel = criteria.excludeWatched
            ? "👁️ Ohne Gesehene (" + watchedCount + ")"
            : "👁️ Gesehene (" + watchedCount + ")";
        exclusionChipsContainer.addView(createFilterPill(watchedLabel, criteria.excludeWatched, 0xFF2EA043, () -> {
            criteria.excludeWatched = !criteria.excludeWatched;
            if (watchedManager != null) {
                watchedManager.setExcludeWatched(MainActivity.this, criteria.excludeWatched);
            }
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        exclusionChipsContainer.addView(createFilterPill("🚫 Keine Dokus", criteria.excludeDoku, 0xFFDA3633, () -> {
            criteria.excludeDoku = !criteria.excludeDoku;
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        exclusionChipsContainer.addView(createFilterPill("🚫 Kein Stand-up", criteria.excludeStandup, 0xFFDA3633, () -> {
            criteria.excludeStandup = !criteria.excludeStandup;
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        exclusionChipsContainer.addView(createFilterPill("🚫 Kein Bollywood", criteria.excludeBollywood, 0xFFDA3633, () -> {
            criteria.excludeBollywood = !criteria.excludeBollywood;
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        exclusionChipsContainer.addView(createFilterPill("🚫 Kein Anime", criteria.excludeAnime, 0xFFDA3633, () -> {
            criteria.excludeAnime = !criteria.excludeAnime;
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        // Preset 1970 - 2010
        boolean is70to10 = (criteria.minYear == 1970 && criteria.maxYear == 2010);
        exclusionChipsContainer.addView(createFilterPill("📅 1970–2010", is70to10, 0xFFF5C518, () -> {
            if (is70to10) {
                criteria.minYear = 0;
                criteria.maxYear = 9999;
            } else {
                criteria.minYear = 1970;
                criteria.maxYear = 2010;
            }
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        // Preset 80er & 90er
        boolean is80to99 = (criteria.minYear == 1980 && criteria.maxYear == 1999);
        exclusionChipsContainer.addView(createFilterPill("📅 80er & 90er", is80to99, 0xFFF5C518, () -> {
            if (is80to99) {
                criteria.minYear = 0;
                criteria.maxYear = 9999;
            } else {
                criteria.minYear = 1980;
                criteria.maxYear = 1999;
            }
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        // Preset Ab 2010
        boolean isAfter2010 = (criteria.minYear == 2010 && criteria.maxYear == 9999);
        exclusionChipsContainer.addView(createFilterPill("📅 Ab 2010", isAfter2010, 0xFFF5C518, () -> {
            if (isAfter2010) {
                criteria.minYear = 0;
                criteria.maxYear = 9999;
            } else {
                criteria.minYear = 2010;
                criteria.maxYear = 9999;
            }
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        // Custom excluded genres
        for (final String exGenre : criteria.excludedGenres) {
            exclusionChipsContainer.addView(createFilterPill("🚫 Ohne " + exGenre, true, 0xFFDA3633, () -> {
                criteria.excludedGenres.remove(exGenre);
                criteria.save(MainActivity.this);
                refreshUi();
                triggerLiveFetchIfNeeded();
            }));
        }

        // Custom excluded countries
        for (final String exCountry : criteria.excludedCountries) {
            exclusionChipsContainer.addView(createFilterPill("🚫 Ohne " + exCountry, true, 0xFFDA3633, () -> {
                criteria.excludedCountries.remove(exCountry);
                criteria.save(MainActivity.this);
                refreshUi();
                triggerLiveFetchIfNeeded();
            }));
        }

        // Reset
        exclusionChipsContainer.addView(createFilterPill("✕ Reset", false, 0xFF6E7681, () -> {
            criteria.reset();
            criteria.save(MainActivity.this);
            if (watchedManager != null) {
                watchedManager.setExcludeWatched(MainActivity.this, false);
            }
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));
    }

    private View createFilterPill(String text, boolean active, int activeColor, Runnable onClick) {
        TextView chip = new TextView(this);
        chip.setText(text);
        chip.setTextSize(11);
        chip.setPadding(dp(9), dp(4), dp(9), dp(4));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, dp(6), 0);
        chip.setLayoutParams(lp);

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(12));
        if (active) {
            gd.setColor(activeColor);
            chip.setTextColor(activeColor == 0xFFF5C518 ? 0xFF000000 : 0xFFFFFFFF);
            chip.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            gd.setColor(0xFF21262D);
            gd.setStroke(dp(1), 0xFF30363D);
            chip.setTextColor(0xFF8B949E);
            chip.setTypeface(Typeface.DEFAULT);
        }
        chip.setBackground(gd);

        chip.setOnClickListener(v -> onClick.run());
        return chip;
    }

    private void renderMovieList() {
        movieCardsContainer.removeAllViews();

        Set<StreamingProvider> activeProviders = providerManager.getActiveProviders();
        List<Movie> movies;

        switch (currentTab) {
            case TAB_LEAVING_SOON:
                movies = movieDatabase.getLeavingSoon(activeProviders);
                listCountHeader.setText("⏳ Nur noch kurze Zeit verfügbar (" + movies.size() + " Titel)");
                loadMoreButton.setVisibility(View.GONE);
                break;
            case TAB_WATCHLIST:
                movies = movieDatabase.getWatchlistMatches(activeProviders, watchlistManager);
                listCountHeader.setText("⭐ Watchlist-Treffer (" + movies.size() + " Titel)");
                loadMoreButton.setVisibility(View.GONE);
                break;
            case TAB_SEARCH:
            case TAB_LEADERBOARD:
            default:
                movies = movieDatabase.getFilteredList(criteria, activeProviders, watchedManager);
                loadMoreButton.setVisibility(View.VISIBLE);

                StringBuilder filterSummary = new StringBuilder("Top " + movies.size() + " von " + criteria.targetCount + " Filmen");
                if (!criteria.singleProviderId.equals("all")) {
                    filterSummary.append(" • ").append(criteria.singleProviderId.toUpperCase());
                }
                if (!criteria.includedGenres.isEmpty()) {
                    filterSummary.append(" • ").append(criteria.includedGenres.iterator().next());
                }
                if (criteria.minYear > 0 || criteria.maxYear < 9999) {
                    filterSummary.append(" • ").append(criteria.minYear > 0 ? criteria.minYear : "vor").append("-").append(criteria.maxYear < 9999 ? criteria.maxYear : "");
                }
                if (criteria.excludeWatched) {
                    filterSummary.append(" • Ohne Gesehene");
                }
                listCountHeader.setText(filterSummary.toString());

                // Auto-fetch more if needed to satisfy targetCount!
                if (movies.size() < criteria.targetCount) {
                    triggerLiveFetchIfNeeded();
                }
                break;
        }

        if (movies.isEmpty()) {
            emptyStateTextView.setVisibility(View.VISIBLE);
            if (currentTab == TAB_WATCHLIST) {
                emptyStateTextView.setText("Keine Treffer deiner Watchlist auf den aktiven Diensten.\nKlicke oben auf '⭐ Watchlist', um deine Titel abzugleichen.");
            } else {
                emptyStateTextView.setText("Keine Filme für diese Filterkombination gefunden.\nLade weitere Titel nach oder lockere die Filter.");
            }
        } else {
            emptyStateTextView.setVisibility(View.GONE);
            int rank = 1;
            for (Movie movie : movies) {
                movieCardsContainer.addView(createMovieCard(movie, rank++));
            }
        }
    }

    private void triggerLiveFetchIfNeeded() {
        if (catalogFetcher.isFetching()) return;

        Set<StreamingProvider> activeProviders = providerManager.getActiveProviders();
        int currentCount = movieDatabase.getFilteredList(criteria, activeProviders, watchedManager).size();
        if (currentCount >= criteria.targetCount) return;

        loadingIndicator.setVisibility(View.VISIBLE);
        listCountHeader.setText("Lade weitere passende Filme nach (" + currentCount + " / " + criteria.targetCount + ")...");

        catalogFetcher.fetchDynamicMatches(criteria, activeProviders, watchedManager, new LiveCatalogFetcher.FetchCallback() {
            @Override
            public void onProgress(int currentCount, int targetCount) {
                listCountHeader.setText("Lade weitere passende Filme nach (" + currentCount + " / " + targetCount + ")...");
                renderMovieList();
            }

            @Override
            public void onComplete(int newMoviesAdded, int totalMatching) {
                loadingIndicator.setVisibility(View.GONE);
                headerSubtitle.setText(movieDatabase.getTotalCount() + " Filme im Pool • Dynamisch nachgeladen");
                renderMovieList();
            }

            @Override
            public void onError(String message) {
                loadingIndicator.setVisibility(View.GONE);
                renderMovieList();
            }
        });
    }

    private View createMovieCard(final Movie movie, int rank) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_movie);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));

        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cardLp);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);

        // Poster
        ImageView poster = new ImageView(this);
        LinearLayout.LayoutParams posterLp = new LinearLayout.LayoutParams(dp(85), dp(125));
        poster.setLayoutParams(posterLp);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageLoader.load(poster, movie.getPosterUrl(), movie.getTitle());
        topRow.addView(poster);

        // Details Column
        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(dp(12), 0, 0, 0);
        LinearLayout.LayoutParams detailsLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        details.setLayoutParams(detailsLp);

        // Title Row
        LinearLayout titleRow = new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView rankBadge = new TextView(this);
        rankBadge.setText("#" + rank);
        rankBadge.setTextSize(11);
        rankBadge.setTypeface(Typeface.DEFAULT_BOLD);
        rankBadge.setPadding(dp(6), dp(2), dp(6), dp(2));
        GradientDrawable rankGd = new GradientDrawable();
        rankGd.setCornerRadius(dp(4));
        if (rank == 1) {
            rankGd.setColor(0xFFFFD700);
            rankBadge.setTextColor(0xFF000000);
        } else if (rank == 2) {
            rankGd.setColor(0xFFC0C0C0);
            rankBadge.setTextColor(0xFF000000);
        } else if (rank == 3) {
            rankGd.setColor(0xFFCD7F32);
            rankBadge.setTextColor(0xFF000000);
        } else {
            rankGd.setColor(0xFF30363D);
            rankBadge.setTextColor(0xFF8B949E);
        }
        rankBadge.setBackground(rankGd);
        titleRow.addView(rankBadge);

        TextView title = new TextView(this);
        title.setText(" " + movie.getTitle());
        title.setTextColor(0xFFF0F6FC);
        title.setTextSize(15);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        titleRow.addView(title);

        details.addView(titleRow);

        // Meta (Year, Runtime, Genres)
        TextView meta = new TextView(this);
        meta.setText(movie.getYear() + " • " + movie.getRuntimeMinutes() + " Min • " + movie.getGenres());
        meta.setTextColor(0xFF8B949E);
        meta.setTextSize(11);
        meta.setPadding(0, dp(3), 0, dp(4));
        details.addView(meta);

        // Scores: IMDb + Rotten Tomatoes + Perlen-Score
        LinearLayout scoresRow = new LinearLayout(this);
        scoresRow.setOrientation(LinearLayout.HORIZONTAL);
        scoresRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView imdbScore = new TextView(this);
        imdbScore.setText("★ " + String.format("%.1f", movie.getImdbRating()));
        imdbScore.setTextColor(0xFF000000);
        imdbScore.setTextSize(11);
        imdbScore.setTypeface(Typeface.DEFAULT_BOLD);
        imdbScore.setBackgroundResource(R.drawable.badge_imdb);
        imdbScore.setPadding(dp(5), dp(2), dp(5), dp(2));
        scoresRow.addView(imdbScore);

        if (movie.getRottenTomatoes() > 0) {
            TextView rtScore = new TextView(this);
            rtScore.setText(" 🍅 " + movie.getRottenTomatoes() + "%");
            rtScore.setTextColor(0xFFFFFFFF);
            rtScore.setTextSize(11);
            rtScore.setTypeface(Typeface.DEFAULT_BOLD);
            rtScore.setBackgroundResource(R.drawable.badge_rt);
            rtScore.setPadding(dp(5), dp(2), dp(5), dp(2));
            LinearLayout.LayoutParams rtLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rtLp.setMargins(dp(4), 0, 0, 0);
            rtScore.setLayoutParams(rtLp);
            scoresRow.addView(rtScore);
        }

        TextView pearlScore = new TextView(this);
        pearlScore.setText(" 💎 Perle " + String.format("%.0f", movie.getCombinedScore()) + "%");
        pearlScore.setTextColor(0xFF000000);
        pearlScore.setTextSize(11);
        pearlScore.setTypeface(Typeface.DEFAULT_BOLD);
        pearlScore.setBackgroundResource(R.drawable.badge_perle);
        pearlScore.setPadding(dp(5), dp(2), dp(5), dp(2));
        LinearLayout.LayoutParams pearlLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pearlLp.setMargins(dp(4), 0, 0, 0);
        pearlScore.setLayoutParams(pearlLp);
        scoresRow.addView(pearlScore);

        details.addView(scoresRow);

        // Votes & Watchlist
        LinearLayout extraRow = new LinearLayout(this);
        extraRow.setOrientation(LinearLayout.HORIZONTAL);
        extraRow.setGravity(Gravity.CENTER_VERTICAL);
        extraRow.setPadding(0, dp(3), 0, 0);

        TextView votes = new TextView(this);
        votes.setText(movie.getFormattedVotes() + " Votes");
        votes.setTextColor(0xFF8B949E);
        votes.setTextSize(10);
        extraRow.addView(votes);

        if (watchlistManager.isWatchlisted(movie.getId()) || watchlistManager.isWatchlisted(movie.getTitle())) {
            TextView wlTag = new TextView(this);
            wlTag.setText(" • ⭐ Auf Watchlist");
            wlTag.setTextColor(0xFF2EA043);
            wlTag.setTextSize(10);
            wlTag.setTypeface(Typeface.DEFAULT_BOLD);
            extraRow.addView(wlTag);
        }

        details.addView(extraRow);

        // Leaving Soon Badge
        if (movie.isLeavingSoon()) {
            TextView leavingNotice = new TextView(this);
            leavingNotice.setText("⏳ " + (movie.getLeavingDate() != null ? movie.getLeavingDate() : "Nur noch kurze Zeit!"));
            leavingNotice.setTextColor(0xFFFFFFFF);
            leavingNotice.setTextSize(10);
            leavingNotice.setTypeface(Typeface.DEFAULT_BOLD);
            leavingNotice.setBackgroundResource(R.drawable.badge_leaving);
            leavingNotice.setPadding(dp(6), dp(3), dp(6), dp(3));
            LinearLayout.LayoutParams noticeLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            noticeLp.setMargins(0, dp(4), 0, 0);
            leavingNotice.setLayoutParams(noticeLp);
            details.addView(leavingNotice);
        }

        topRow.addView(details);
        card.addView(topRow);

        // Overview
        if (movie.getOverview() != null && !movie.getOverview().isEmpty()) {
            TextView overview = new TextView(this);
            overview.setText(movie.getOverview());
            overview.setTextColor(0xFF8B949E);
            overview.setTextSize(12);
            overview.setMaxLines(2);
            overview.setPadding(0, dp(8), 0, dp(6));
            card.addView(overview);
        }

        // Action Buttons Row
        LinearLayout actionRow = new LinearLayout(this);
        actionRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLp.setMargins(0, dp(4), 0, 0);
        actionRow.setLayoutParams(rowLp);

        // 1. Launch Provider Button
        Button watchBtn = new Button(this);
        String btnText = "▶ " + movie.getProvider().getDisplayName();
        watchBtn.setText(btnText);
        watchBtn.setTextColor(0xFFFFFFFF);
        watchBtn.setTextSize(11);
        watchBtn.setTypeface(Typeface.DEFAULT_BOLD);
        watchBtn.setTransformationMethod(null);

        if (movie.getProvider().equals(StreamingProvider.NETFLIX)) {
            watchBtn.setBackgroundResource(R.drawable.btn_netflix);
        } else if (movie.getProvider().equals(StreamingProvider.PRIME_VIDEO)) {
            watchBtn.setBackgroundResource(R.drawable.btn_prime);
        } else {
            watchBtn.setBackgroundResource(R.drawable.btn_generic);
        }

        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, dp(38), 0.48f);
        btnLp.setMargins(0, 0, dp(4), 0);
        watchBtn.setLayoutParams(btnLp);
        watchBtn.setOnClickListener(v -> {
            boolean success = movie.getProvider().launchTitle(MainActivity.this, movie.getDeepLinkId(), movie.getTitle());
            if (!success) {
                Toast.makeText(MainActivity.this, "Konnte " + movie.getProvider().getDisplayName() + " nicht öffnen", Toast.LENGTH_SHORT).show();
            }
        });
        actionRow.addView(watchBtn);

        // 2. Watched Toggle Button
        final Button watchedBtn = new Button(this);
        final boolean isWatchedInitially = watchedManager != null && watchedManager.isWatched(movie.getId(), movie.getTitle());
        updateWatchedButtonVisual(watchedBtn, isWatchedInitially);

        LinearLayout.LayoutParams watchedBtnLp = new LinearLayout.LayoutParams(0, dp(38), 0.32f);
        watchedBtnLp.setMargins(0, 0, dp(4), 0);
        watchedBtn.setLayoutParams(watchedBtnLp);
        watchedBtn.setOnClickListener(v -> {
            if (watchedManager == null) return;
            boolean nowWatched = watchedManager.toggleWatched(MainActivity.this, movie.getId(), movie.getTitle());
            Toast.makeText(MainActivity.this, (nowWatched ? "✓ Als gesehen markiert: " : "👁️ Als ungesehen markiert: ") + movie.getTitle(), Toast.LENGTH_SHORT).show();
            if (criteria.excludeWatched) {
                renderMovieList();
                triggerLiveFetchIfNeeded();
            } else {
                updateWatchedButtonVisual(watchedBtn, nowWatched);
                renderExclusionAndYearChips();
            }
        });
        actionRow.addView(watchedBtn);

        // 3. Search in App Button
        Button searchInAppBtn = new Button(this);
        searchInAppBtn.setText("🔍 In App");
        searchInAppBtn.setTextColor(0xFFF0F6FC);
        searchInAppBtn.setTextSize(10);
        searchInAppBtn.setTransformationMethod(null);
        searchInAppBtn.setBackgroundResource(R.drawable.btn_generic);

        LinearLayout.LayoutParams searchBtnLp = new LinearLayout.LayoutParams(0, dp(38), 0.20f);
        searchInAppBtn.setLayoutParams(searchBtnLp);
        searchInAppBtn.setOnClickListener(v -> {
            boolean success = movie.getProvider().launchSearch(MainActivity.this, movie.getTitle());
            if (!success) {
                Toast.makeText(MainActivity.this, "Konnte Suche in " + movie.getProvider().getDisplayName() + " nicht öffnen", Toast.LENGTH_SHORT).show();
            }
        });
        actionRow.addView(searchInAppBtn);

        card.addView(actionRow);

        return card;
    }

    private void updateWatchedButtonVisual(Button btn, boolean isWatched) {
        btn.setText(isWatched ? "✓ Gesehen" : "👁️ Gesehen");
        btn.setTextSize(10);
        btn.setTransformationMethod(null);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(6));
        if (isWatched) {
            gd.setColor(0xFF238636); // Github Green
            btn.setTextColor(0xFFFFFFFF);
            btn.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            gd.setColor(0xFF21262D);
            gd.setStroke(dp(1), 0xFF30363D);
            btn.setTextColor(0xFF8B949E);
            btn.setTypeface(Typeface.DEFAULT);
        }
        btn.setBackground(gd);
    }

    private static final String[] ALL_GENRES = new String[] {
        "Action", "Abenteuer", "Animation", "Komödie", "Krimi", "Dokumentarfilm",
        "Drama", "Familie", "Fantasy", "Historie", "Horror", "Musik",
        "Mystery", "Romantik", "Science-Fiction", "Thriller", "Kriegsfilm", "Western"
    };

    private void showDetailedFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⚙️ Präziser Filter- & Ausschluss-Manager");

        ScrollView dialogScroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(18), dp(10), dp(18), dp(14));

        // 1. Streaming-Dienst Dimension
        TextView provLabel = new TextView(this);
        provLabel.setText("1. Streaming-Dienst:");
        provLabel.setTextColor(0xFFF5C518);
        provLabel.setTextSize(13);
        provLabel.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(provLabel);

        final String[] selectedProviderId = new String[] { criteria.singleProviderId };
        final LinearLayout provChips = new LinearLayout(this);
        provChips.setOrientation(LinearLayout.HORIZONTAL);
        provChips.setPadding(0, dp(4), 0, dp(10));

        final String[][] provOptions = new String[][] {
            {"all", "Alle"},
            {"netflix", "Netflix"},
            {"prime", "Prime"},
            {"disney", "Disney+"},
            {"wow", "WOW"}
        };

        final Runnable updateProvChips = () -> {
            provChips.removeAllViews();
            for (String[] opt : provOptions) {
                final String pid = opt[0];
                final String pname = opt[1];
                boolean isSel = selectedProviderId[0].equalsIgnoreCase(pid);
                provChips.addView(createFilterPill(pname, isSel, 0xFF388BFD, () -> {
                    selectedProviderId[0] = pid;
                    // refresh local chips
                    for (int c = 0; c < provChips.getChildCount(); c++) {
                        View child = provChips.getChildAt(c);
                        if (child instanceof TextView) {
                            String cText = ((TextView) child).getText().toString();
                            boolean matched = cText.equalsIgnoreCase(pname);
                            GradientDrawable gd = new GradientDrawable();
                            gd.setCornerRadius(dp(12));
                            if (matched) {
                                gd.setColor(0xFF388BFD);
                                ((TextView) child).setTextColor(0xFFFFFFFF);
                            } else {
                                gd.setColor(0xFF21262D);
                                gd.setStroke(dp(1), 0xFF30363D);
                                ((TextView) child).setTextColor(0xFF8B949E);
                            }
                            child.setBackground(gd);
                        }
                    }
                }));
            }
        };
        updateProvChips.run();
        layout.addView(provChips);

        // 2. Bereits Gesehene Filme
        final CheckBox watchedBox = new CheckBox(this);
        int seenCount = watchedManager != null ? watchedManager.getCount() : 0;
        watchedBox.setText("Bereits gesehene Filme ausblenden (" + seenCount + " markiert)");
        watchedBox.setTextColor(0xFF2EA043);
        watchedBox.setChecked(criteria.excludeWatched);
        layout.addView(watchedBox);

        // 3. Alle 18 Genres (präzise Ein- und Ausschlüsse)
        TextView genresHeading = new TextView(this);
        genresHeading.setText("3. Alle Genres (Neutral / [+] Einschließen / [-] Ausschließen):");
        genresHeading.setTextColor(0xFFF5C518);
        genresHeading.setTextSize(13);
        genresHeading.setTypeface(Typeface.DEFAULT_BOLD);
        genresHeading.setPadding(0, dp(10), 0, dp(4));
        layout.addView(genresHeading);

        final Set<String> tempIncludedGenres = new HashSet<>(criteria.includedGenres);
        final Set<String> tempExcludedGenres = new HashSet<>(criteria.excludedGenres);

        for (final String gName : ALL_GENRES) {
            LinearLayout gRow = new LinearLayout(this);
            gRow.setOrientation(LinearLayout.HORIZONTAL);
            gRow.setGravity(Gravity.CENTER_VERTICAL);
            gRow.setPadding(0, dp(3), 0, dp(3));

            TextView gTitle = new TextView(this);
            gTitle.setText(gName);
            gTitle.setTextColor(0xFFF0F6FC);
            gTitle.setTextSize(12);
            LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.42f);
            gTitle.setLayoutParams(titleLp);
            gRow.addView(gTitle);

            final Button btnNeutral = new Button(this);
            btnNeutral.setText("Egal");
            btnNeutral.setTextSize(10);
            btnNeutral.setTransformationMethod(null);
            LinearLayout.LayoutParams bLp1 = new LinearLayout.LayoutParams(0, dp(32), 0.18f);
            bLp1.setMargins(dp(2), 0, dp(2), 0);
            btnNeutral.setLayoutParams(bLp1);

            final Button btnInc = new Button(this);
            btnInc.setText("+ Ein");
            btnInc.setTextSize(10);
            btnInc.setTransformationMethod(null);
            LinearLayout.LayoutParams bLp2 = new LinearLayout.LayoutParams(0, dp(32), 0.20f);
            bLp2.setMargins(dp(2), 0, dp(2), 0);
            btnInc.setLayoutParams(bLp2);

            final Button btnEx = new Button(this);
            btnEx.setText("- Aus");
            btnEx.setTextSize(10);
            btnEx.setTransformationMethod(null);
            LinearLayout.LayoutParams bLp3 = new LinearLayout.LayoutParams(0, dp(32), 0.20f);
            bLp3.setMargins(dp(2), 0, dp(2), 0);
            btnEx.setLayoutParams(bLp3);

            final Runnable updateButtons = () -> {
                boolean isInc = tempIncludedGenres.contains(gName);
                boolean isEx = tempExcludedGenres.contains(gName);

                styleGenreStateBtn(btnNeutral, !isInc && !isEx, 0xFF30363D, 0xFFFFFFFF);
                styleGenreStateBtn(btnInc, isInc, 0xFF238636, 0xFFFFFFFF);
                styleGenreStateBtn(btnEx, isEx, 0xFFDA3633, 0xFFFFFFFF);
            };

            btnNeutral.setOnClickListener(v -> {
                tempIncludedGenres.remove(gName);
                tempExcludedGenres.remove(gName);
                updateButtons.run();
            });

            btnInc.setOnClickListener(v -> {
                tempIncludedGenres.add(gName);
                tempExcludedGenres.remove(gName);
                updateButtons.run();
            });

            btnEx.setOnClickListener(v -> {
                tempExcludedGenres.add(gName);
                tempIncludedGenres.remove(gName);
                updateButtons.run();
            });

            updateButtons.run();

            gRow.addView(btnNeutral);
            gRow.addView(btnInc);
            gRow.addView(btnEx);
            layout.addView(gRow);
        }

        // 4. Besondere Ausschlüsse (Blacklist)
        TextView exclLabel = new TextView(this);
        exclLabel.setText("4. Formate & Ausschlüsse:");
        exclLabel.setTextColor(0xFFFA320A);
        exclLabel.setTextSize(13);
        exclLabel.setTypeface(Typeface.DEFAULT_BOLD);
        exclLabel.setPadding(0, dp(10), 0, dp(4));
        layout.addView(exclLabel);

        final CheckBox dokuBox = new CheckBox(this);
        dokuBox.setText("Keine Dokumentationen");
        dokuBox.setTextColor(0xFFF0F6FC);
        dokuBox.setChecked(criteria.excludeDoku);
        layout.addView(dokuBox);

        final CheckBox bollyBox = new CheckBox(this);
        bollyBox.setText("Kein Bollywood / Keine indischen Filme");
        bollyBox.setTextColor(0xFFF0F6FC);
        bollyBox.setChecked(criteria.excludeBollywood);
        layout.addView(bollyBox);

        final CheckBox animeBox = new CheckBox(this);
        animeBox.setText("Kein Anime / Zeichentrick");
        animeBox.setTextColor(0xFFF0F6FC);
        animeBox.setChecked(criteria.excludeAnime);
        layout.addView(animeBox);

        final CheckBox shortsBox = new CheckBox(this);
        shortsBox.setText("Keine Kurzfilme (< 60 Min)");
        shortsBox.setTextColor(0xFFF0F6FC);
        shortsBox.setChecked(criteria.excludeShorts);
        layout.addView(shortsBox);

        final CheckBox standupBox = new CheckBox(this);
        standupBox.setText("Keine Stand-up Specials / Comedians");
        standupBox.setTextColor(0xFFF0F6FC);
        standupBox.setChecked(criteria.excludeStandup);
        layout.addView(standupBox);

        // 5. Produktionsländer ausschließen (Blacklist)
        TextView countryLabel = new TextView(this);
        countryLabel.setText("5. Produktionsländer ausschließen:");
        countryLabel.setTextColor(0xFFFA320A);
        countryLabel.setTextSize(13);
        countryLabel.setTypeface(Typeface.DEFAULT_BOLD);
        countryLabel.setPadding(0, dp(10), 0, dp(4));
        layout.addView(countryLabel);

        final Set<String> tempExcludedCountries = new HashSet<>(criteria.excludedCountries);

        HorizontalScrollView countryScroll = new HorizontalScrollView(this);
        countryScroll.setHorizontalScrollBarEnabled(false);
        final LinearLayout countryChipsRow = new LinearLayout(this);
        countryChipsRow.setOrientation(LinearLayout.HORIZONTAL);
        countryChipsRow.setPadding(0, dp(2), 0, dp(4));

        final String[][] commonCountries = new String[][] {
            {"IN", "Indien (IN)"},
            {"US", "USA (US)"},
            {"DE", "Deutschland (DE)"},
            {"GB", "Großbritannien (GB)"},
            {"FR", "Frankreich (FR)"},
            {"JP", "Japan (JP)"},
            {"KR", "Südkorea (KR)"},
            {"ES", "Spanien (ES)"},
            {"IT", "Italien (IT)"},
            {"TR", "Türkei (TR)"},
            {"CN", "China (CN)"}
        };

        final Runnable[] renderCountriesHolder = new Runnable[1];
        renderCountriesHolder[0] = () -> {
            countryChipsRow.removeAllViews();
            for (String[] pair : commonCountries) {
                final String code = pair[0];
                final String labelText = pair[1];
                boolean isEx = tempExcludedCountries.contains(code);
                countryChipsRow.addView(createFilterPill(
                    (isEx ? "🚫 " : "") + labelText,
                    isEx,
                    0xFFDA3633,
                    () -> {
                        if (tempExcludedCountries.contains(code)) {
                            tempExcludedCountries.remove(code);
                        } else {
                            tempExcludedCountries.add(code);
                        }
                        renderCountriesHolder[0].run();
                    }
                ));
            }
        };
        renderCountriesHolder[0].run();
        countryScroll.addView(countryChipsRow);
        layout.addView(countryScroll);

        LinearLayout customCountryRow = new LinearLayout(this);
        customCountryRow.setOrientation(LinearLayout.HORIZONTAL);
        customCountryRow.setGravity(Gravity.CENTER_VERTICAL);
        customCountryRow.setPadding(0, dp(2), 0, dp(6));

        final EditText customCountryInput = new EditText(this);
        customCountryInput.setHint("Anderes Land / ISO-Code (z. B. PL, SE, CA)");
        customCountryInput.setTextColor(0xFFFFFFFF);
        customCountryInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        customCountryRow.addView(customCountryInput);

        Button addCountryBtn = new Button(this);
        addCountryBtn.setText("+ Aus");
        addCountryBtn.setTextColor(0xFFFFFFFF);
        addCountryBtn.setTextSize(11);
        addCountryBtn.setBackgroundResource(R.drawable.btn_generic);
        addCountryBtn.setPadding(dp(8), dp(4), dp(8), dp(4));
        LinearLayout.LayoutParams addCountryBtnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(34));
        addCountryBtnLp.setMargins(dp(6), 0, 0, 0);
        addCountryBtn.setLayoutParams(addCountryBtnLp);
        addCountryBtn.setOnClickListener(v -> {
            String c = customCountryInput.getText().toString().trim().toUpperCase();
            if (!c.isEmpty()) {
                tempExcludedCountries.add(c);
                customCountryInput.setText("");
                renderCountriesHolder[0].run();
                Toast.makeText(MainActivity.this, "Land ausgeschlossen: " + c, Toast.LENGTH_SHORT).show();
            }
        });
        customCountryRow.addView(addCountryBtn);
        layout.addView(customCountryRow);

        // 6. Produktionszeitraum (Jahr)
        TextView yearLabel = new TextView(this);
        yearLabel.setText("6. Produktionszeitraum (Jahr):");
        yearLabel.setTextColor(0xFFF5C518);
        yearLabel.setTextSize(13);
        yearLabel.setTypeface(Typeface.DEFAULT_BOLD);
        yearLabel.setPadding(0, dp(10), 0, dp(4));
        layout.addView(yearLabel);

        LinearLayout yearRow = new LinearLayout(this);
        yearRow.setOrientation(LinearLayout.HORIZONTAL);
        yearRow.setPadding(0, dp(2), 0, dp(4));

        final EditText minYearInput = new EditText(this);
        minYearInput.setHint("Von (z. B. 1970)");
        minYearInput.setText(criteria.minYear > 0 ? String.valueOf(criteria.minYear) : "");
        minYearInput.setTextColor(0xFFFFFFFF);
        minYearInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        yearRow.addView(minYearInput);

        final EditText maxYearInput = new EditText(this);
        maxYearInput.setHint("Bis (z. B. 2010)");
        maxYearInput.setText(criteria.maxYear < 9999 ? String.valueOf(criteria.maxYear) : "");
        maxYearInput.setTextColor(0xFFFFFFFF);
        maxYearInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        yearRow.addView(maxYearInput);
        layout.addView(yearRow);

        // Quick year chips
        HorizontalScrollView yearChipScroll = new HorizontalScrollView(this);
        yearChipScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout yearChips = new LinearLayout(this);
        yearChips.setOrientation(LinearLayout.HORIZONTAL);
        yearChips.setPadding(0, 0, 0, dp(6));

        yearChips.addView(createDialogQuickChip("1970–2010", () -> { minYearInput.setText("1970"); maxYearInput.setText("2010"); }));
        yearChips.addView(createDialogQuickChip("70er", () -> { minYearInput.setText("1970"); maxYearInput.setText("1979"); }));
        yearChips.addView(createDialogQuickChip("80er", () -> { minYearInput.setText("1980"); maxYearInput.setText("1989"); }));
        yearChips.addView(createDialogQuickChip("90er", () -> { minYearInput.setText("1990"); maxYearInput.setText("1999"); }));
        yearChips.addView(createDialogQuickChip("2000er", () -> { minYearInput.setText("2000"); maxYearInput.setText("2009"); }));
        yearChips.addView(createDialogQuickChip("Ab 2010", () -> { minYearInput.setText("2010"); maxYearInput.setText(""); }));
        yearChips.addView(createDialogQuickChip("Alle Jahre", () -> { minYearInput.setText(""); maxYearInput.setText(""); }));
        yearChipScroll.addView(yearChips);
        layout.addView(yearChipScroll);

        // 7. Laufzeit (Minuten)
        TextView runtimeLabel = new TextView(this);
        runtimeLabel.setText("7. Laufzeit eingrenzen (Minuten):");
        runtimeLabel.setTextColor(0xFFF5C518);
        runtimeLabel.setTextSize(13);
        runtimeLabel.setTypeface(Typeface.DEFAULT_BOLD);
        runtimeLabel.setPadding(0, dp(10), 0, dp(4));
        layout.addView(runtimeLabel);

        LinearLayout runtimeRow = new LinearLayout(this);
        runtimeRow.setOrientation(LinearLayout.HORIZONTAL);

        final EditText minRuntimeInput = new EditText(this);
        minRuntimeInput.setHint("Min (z. B. 80)");
        minRuntimeInput.setText(criteria.minRuntime > 0 ? String.valueOf(criteria.minRuntime) : "");
        minRuntimeInput.setTextColor(0xFFFFFFFF);
        minRuntimeInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        runtimeRow.addView(minRuntimeInput);

        final EditText maxRuntimeInput = new EditText(this);
        maxRuntimeInput.setHint("Max (z. B. 180)");
        maxRuntimeInput.setText(criteria.maxRuntime < 999 ? String.valueOf(criteria.maxRuntime) : "");
        maxRuntimeInput.setTextColor(0xFFFFFFFF);
        maxRuntimeInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        runtimeRow.addView(maxRuntimeInput);
        layout.addView(runtimeRow);

        // 8. Mindestbewertungen
        TextView ratingLabel = new TextView(this);
        ratingLabel.setText("8. Mindest-Bewertungen:");
        ratingLabel.setTextColor(0xFFF5C518);
        ratingLabel.setTextSize(13);
        ratingLabel.setTypeface(Typeface.DEFAULT_BOLD);
        ratingLabel.setPadding(0, dp(10), 0, dp(4));
        layout.addView(ratingLabel);

        LinearLayout ratingRow = new LinearLayout(this);
        ratingRow.setOrientation(LinearLayout.HORIZONTAL);

        final EditText minImdbInput = new EditText(this);
        minImdbInput.setHint("Min. IMDb (z. B. 7.5)");
        minImdbInput.setText(criteria.minImdbScore > 0 ? String.valueOf(criteria.minImdbScore) : "");
        minImdbInput.setTextColor(0xFFFFFFFF);
        minImdbInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        ratingRow.addView(minImdbInput);

        final EditText minRtInput = new EditText(this);
        minRtInput.setHint("Min. Rotten % (z. B. 80)");
        minRtInput.setText(criteria.minRottenTomatoes > 0 ? String.valueOf(criteria.minRottenTomatoes) : "");
        minRtInput.setTextColor(0xFFFFFFFF);
        minRtInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        ratingRow.addView(minRtInput);
        layout.addView(ratingRow);

        // 9. Sortierung
        TextView sortLabel = new TextView(this);
        sortLabel.setText("9. Ranglisten-Sortierung:");
        sortLabel.setTextColor(0xFFF5C518);
        sortLabel.setTextSize(13);
        sortLabel.setTypeface(Typeface.DEFAULT_BOLD);
        sortLabel.setPadding(0, dp(10), 0, dp(4));
        layout.addView(sortLabel);

        final int[] selectedSort = new int[] { criteria.sortMode };
        final LinearLayout sortRow = new LinearLayout(this);
        sortRow.setOrientation(LinearLayout.HORIZONTAL);
        sortRow.setPadding(0, dp(2), 0, dp(6));

        final Runnable updateSortRow = () -> {
            sortRow.removeAllViews();
            sortRow.addView(createFilterPill("🏆 IMDb", selectedSort[0] == MovieDatabase.SORT_IMDB, 0xFFF5C518, () -> {
                selectedSort[0] = MovieDatabase.SORT_IMDB;
            }));
            sortRow.addView(createFilterPill("🍅 Rotten", selectedSort[0] == MovieDatabase.SORT_ROTTEN_TOMATOES, 0xFFFA320A, () -> {
                selectedSort[0] = MovieDatabase.SORT_ROTTEN_TOMATOES;
            }));
            sortRow.addView(createFilterPill("💎 Perle", selectedSort[0] == MovieDatabase.SORT_PERLE, 0xFF00B4D8, () -> {
                selectedSort[0] = MovieDatabase.SORT_PERLE;
            }));
            sortRow.addView(createFilterPill("📅 Neueste", selectedSort[0] == MovieDatabase.SORT_YEAR, 0xFF8B949E, () -> {
                selectedSort[0] = MovieDatabase.SORT_YEAR;
            }));
        };
        updateSortRow.run();
        layout.addView(sortRow);

        // 10. Zielanzahl der Filme (Default 100)
        TextView countLabel = new TextView(this);
        countLabel.setText("10. Zielanzahl der Filme (immer auffüllen):");
        countLabel.setTextColor(0xFFF5C518);
        countLabel.setTextSize(13);
        countLabel.setTypeface(Typeface.DEFAULT_BOLD);
        countLabel.setPadding(0, dp(10), 0, dp(4));
        layout.addView(countLabel);

        final EditText targetCountInput = new EditText(this);
        targetCountInput.setHint("Zielanzahl (z. B. 100)");
        targetCountInput.setText(String.valueOf(criteria.targetCount));
        targetCountInput.setTextColor(0xFFFFFFFF);
        layout.addView(targetCountInput);

        // In-dialog Big Action Button
        final Button applyInDialogBtn = new Button(this);
        applyInDialogBtn.setText("✓ Filter anwenden & 100 Filme laden");
        applyInDialogBtn.setTextColor(0xFFFFFFFF);
        applyInDialogBtn.setTextSize(13);
        applyInDialogBtn.setTypeface(Typeface.DEFAULT_BOLD);
        applyInDialogBtn.setTransformationMethod(null);
        GradientDrawable applyGd = new GradientDrawable();
        applyGd.setCornerRadius(dp(8));
        applyGd.setColor(0xFF238636); // Github Green
        applyInDialogBtn.setBackground(applyGd);

        LinearLayout.LayoutParams applyLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(46));
        applyLp.setMargins(0, dp(14), 0, dp(8));
        applyInDialogBtn.setLayoutParams(applyLp);
        layout.addView(applyInDialogBtn);

        dialogScroll.addView(layout);
        builder.setView(dialogScroll);

        final Runnable applyAction = () -> {
            criteria.singleProviderId = selectedProviderId[0];
            criteria.excludeWatched = watchedBox.isChecked();
            if (watchedManager != null) {
                watchedManager.setExcludeWatched(MainActivity.this, criteria.excludeWatched);
            }

            criteria.includedGenres.clear();
            criteria.includedGenres.addAll(tempIncludedGenres);

            criteria.excludedGenres.clear();
            criteria.excludedGenres.addAll(tempExcludedGenres);

            criteria.excludeDoku = dokuBox.isChecked();
            criteria.excludeStandup = standupBox.isChecked();
            criteria.excludeBollywood = bollyBox.isChecked();
            criteria.excludeAnime = animeBox.isChecked();
            criteria.excludeShorts = shortsBox.isChecked();

            criteria.excludedCountries.clear();
            criteria.excludedCountries.addAll(tempExcludedCountries);

            criteria.sortMode = selectedSort[0];

            try {
                String minS = minYearInput.getText().toString().trim();
                criteria.minYear = minS.isEmpty() ? 0 : Integer.parseInt(minS);
            } catch (Exception e) { criteria.minYear = 0; }

            try {
                String maxS = maxYearInput.getText().toString().trim();
                criteria.maxYear = maxS.isEmpty() ? 9999 : Integer.parseInt(maxS);
            } catch (Exception e) { criteria.maxYear = 9999; }

            try {
                String minR = minRuntimeInput.getText().toString().trim();
                criteria.minRuntime = minR.isEmpty() ? 0 : Integer.parseInt(minR);
            } catch (Exception e) { criteria.minRuntime = 0; }

            try {
                String maxR = maxRuntimeInput.getText().toString().trim();
                criteria.maxRuntime = maxR.isEmpty() ? 999 : Integer.parseInt(maxR);
            } catch (Exception e) { criteria.maxRuntime = 999; }

            try {
                String minI = minImdbInput.getText().toString().trim().replace(",", ".");
                criteria.minImdbScore = minI.isEmpty() ? 0.0 : Double.parseDouble(minI);
            } catch (Exception e) { criteria.minImdbScore = 0.0; }

            try {
                String minRt = minRtInput.getText().toString().trim();
                criteria.minRottenTomatoes = minRt.isEmpty() ? 0 : Integer.parseInt(minRt);
            } catch (Exception e) { criteria.minRottenTomatoes = 0; }

            try {
                String tc = targetCountInput.getText().toString().trim();
                criteria.targetCount = tc.isEmpty() ? 100 : Integer.parseInt(tc);
            } catch (Exception e) { criteria.targetCount = 100; }

            criteria.save(MainActivity.this);

            refreshUi();
            triggerLiveFetchIfNeeded();
            Toast.makeText(MainActivity.this, "Filter aktiv – lade dynamisch bis zu " + criteria.targetCount + " Filme...", Toast.LENGTH_SHORT).show();
        };

        builder.setPositiveButton("✓ Anwenden", (dialog, which) -> applyAction.run());

        builder.setNeutralButton("✕ Reset", (dialog, which) -> {
            criteria.reset();
            criteria.save(MainActivity.this);
            if (watchedManager != null) {
                watchedManager.setExcludeWatched(MainActivity.this, false);
            }
            refreshUi();
            triggerLiveFetchIfNeeded();
            Toast.makeText(MainActivity.this, "Filter zurückgesetzt", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Abbrechen", null);

        final AlertDialog dialog = builder.create();
        applyInDialogBtn.setOnClickListener(v -> {
            applyAction.run();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void styleGenreStateBtn(Button btn, boolean active, int activeColor, int textColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(6));
        if (active) {
            gd.setColor(activeColor);
            btn.setTextColor(textColor);
            btn.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            gd.setColor(0xFF161B22);
            gd.setStroke(dp(1), 0xFF30363D);
            btn.setTextColor(0xFF8B949E);
            btn.setTypeface(Typeface.DEFAULT);
        }
        btn.setBackground(gd);
    }

    private View createDialogQuickChip(String text, Runnable onClick) {
        TextView chip = new TextView(this);
        chip.setText(text);
        chip.setTextSize(11);
        chip.setTextColor(0xFFF0F6FC);
        chip.setPadding(dp(8), dp(4), dp(8), dp(4));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, dp(4), 0);
        chip.setLayoutParams(lp);

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(10));
        gd.setColor(0xFF21262D);
        gd.setStroke(dp(1), 0xFF30363D);
        chip.setBackground(gd);

        chip.setOnClickListener(v -> onClick.run());
        return chip;
    }

    private void showWatchlistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⭐ IMDb Watchlist verknüpfen");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(10), dp(20), dp(10));

        TextView info = new TextView(this);
        info.setText("Gib deine IMDb User-ID (z. B. ur12345678) ein oder füge eine Liste von IMDb-IDs / CSV-Inhalt ein:\nAktuell gemerkt: " + watchlistManager.getCount() + " Titel");
        info.setTextSize(13);
        info.setTextColor(0xFF8B949E);
        layout.addView(info);

        final EditText input = new EditText(this);
        input.setHint("z. B. ur12345678 oder tt0111161, tt0468569...");
        input.setText(watchlistManager.getUserImdbId());
        input.setPadding(dp(10), dp(10), dp(10), dp(10));
        input.setTextColor(0xFFFFFFFF);
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton("Speichern & Abgleichen", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                if (text.startsWith("ur")) {
                    watchlistManager.setUserImdbId(MainActivity.this, text);
                    Toast.makeText(MainActivity.this, "IMDb-Konto " + text + " hinterlegt!", Toast.LENGTH_SHORT).show();
                } else {
                    int imported = watchlistManager.importCsv(MainActivity.this, text);
                    Toast.makeText(MainActivity.this, imported + " Titel aus CSV importiert!", Toast.LENGTH_SHORT).show();
                }
            }
            refreshUi();
        });

        builder.setNeutralButton("Demo-Watchlist laden", (dialog, which) -> {
            watchlistManager.init(MainActivity.this);
            Toast.makeText(MainActivity.this, "Demo-Watchlist mit Blockbustern geladen!", Toast.LENGTH_SHORT).show();
            refreshUi();
        });

        builder.setNegativeButton("Abbrechen", null);

        builder.show();
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }
}
