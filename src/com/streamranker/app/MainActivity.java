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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {

    public static final int TAB_LEADERBOARD = 0;
    public static final int TAB_LEAVING_SOON = 1;
    public static final int TAB_WATCHLIST = 2;
    public static final int TAB_WATCHED = 3;
    public static final int TAB_SEARCH = 4;
    static final int REQUEST_CODE_PICK_CSV = 1001;

    ProviderManager providerManager;
    WatchlistManager watchlistManager;
    WatchedManager watchedManager;
    MovieDatabase movieDatabase;
    AsyncImageLoader imageLoader;
    LiveCatalogFetcher catalogFetcher;

    // Active filters
    int currentTab = TAB_LEADERBOARD;
    final FilterCriteria criteria = new FilterCriteria();

    // UI references
    private LinearLayout providerChipsContainer;
    private LinearLayout presetChipsContainer;
    private LinearLayout sortChipsContainer;
    private LinearLayout genreChipsContainer;
    private LinearLayout exclusionChipsContainer;
    private LinearLayout tabsContainer;
    private FrameLayout movieContainer;
    ListView movieListView;
    MovieAdapter movieAdapter;
    GridView movieGridView;
    MovieGridAdapter movieGridAdapter;
    boolean isGridMode = false;
    Button viewModeToggleBtn;
    EditText searchEditText;
    private TextView headerSubtitle;
    private TextView emptyStateTextView;
    private TextView listCountHeader;
    private ProgressBar loadingIndicator;
    private LinearLayout footerLayout;
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

        movieDatabase = MovieDatabase.getInstance(this);
        imageLoader = AsyncImageLoader.getInstance(this);
        catalogFetcher = LiveCatalogFetcher.getInstance();

        isGridMode = getSharedPreferences("stream_ranker_prefs", MODE_PRIVATE).getBoolean("is_grid_mode", false);

        setContentView(buildRootLayout());

        refreshUi();

        handleIncomingIntent(getIntent());

        // Check for OTA updates in background on launch
        OtaUpdateManager.getInstance().checkUpdate(this, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CSV && resultCode == RESULT_OK && data != null && data.getData() != null) {
            handlePickedCsvUri(data.getData());
        }
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null && !sharedText.trim().isEmpty()) {
                importWatchlistText(sharedText);
                return;
            }
            Uri streamUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (streamUri != null) {
                handlePickedCsvUri(streamUri);
            }
        } else if (Intent.ACTION_VIEW.equals(action) && intent.getData() != null) {
            handlePickedCsvUri(intent.getData());
        }
    }

    private void handlePickedCsvUri(Uri uri) {
        if (uri == null) return;
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is != null) {
                Set<String> added = watchlistManager.importFromStream(this, is);
                Toast.makeText(this, "🎉 " + added.size() + " Filme aus CSV importiert!", Toast.LENGTH_LONG).show();
                if (!added.isEmpty()) {
                    catalogFetcher.syncWatchlistMovies(added, () -> {
                        refreshUi();
                    });
                    currentTab = TAB_WATCHLIST;
                    refreshUi();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Fehler beim Lesen der CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    void importWatchlistText(String text) {
        if (text == null || text.trim().isEmpty()) return;
        Set<String> added = watchlistManager.importCsvWithIds(this, text);
        if (!added.isEmpty()) {
            Toast.makeText(this, "🎉 " + added.size() + " Filme zur Watchlist hinzugefügt!", Toast.LENGTH_LONG).show();
            catalogFetcher.syncWatchlistMovies(added, () -> {
                refreshUi();
            });
            currentTab = TAB_WATCHLIST;
            refreshUi();
        } else {
            Toast.makeText(this, "Keine IMDb-Titel in den Daten gefunden", Toast.LENGTH_SHORT).show();
        }
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
        View tabsBar = buildTabsBar();
        root.addView(tabsBar);

        // 3. Provider Filter Dimension
        HorizontalScrollView providerScroll = new HorizontalScrollView(this);
        providerScroll.setHorizontalScrollBarEnabled(false);
        providerScroll.setPadding(dp(12), dp(4), dp(12), dp(4));
        providerChipsContainer = new LinearLayout(this);
        providerChipsContainer.setOrientation(LinearLayout.HORIZONTAL);
        providerScroll.addView(providerChipsContainer);
        root.addView(providerScroll);

        // 3b. Mood & Preset Dimension (1-Klick Stimmungen)
        HorizontalScrollView presetScroll = new HorizontalScrollView(this);
        presetScroll.setHorizontalScrollBarEnabled(false);
        presetScroll.setPadding(dp(12), dp(2), dp(12), dp(4));
        presetChipsContainer = new LinearLayout(this);
        presetChipsContainer.setOrientation(LinearLayout.HORIZONTAL);
        presetScroll.addView(presetChipsContainer);
        root.addView(presetScroll);

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

        viewModeToggleBtn = new Button(this);
        viewModeToggleBtn.setText(isGridMode ? "☰ Liste" : "⊞ Raster");
        viewModeToggleBtn.setTextColor(0xFFF0F6FC);
        viewModeToggleBtn.setTextSize(11);
        viewModeToggleBtn.setBackgroundResource(R.drawable.btn_generic);
        viewModeToggleBtn.setPadding(dp(8), dp(3), dp(8), dp(3));
        LinearLayout.LayoutParams vmBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(32));
        vmBtnLp.setMargins(dp(6), 0, 0, 0);
        viewModeToggleBtn.setLayoutParams(vmBtnLp);
        viewModeToggleBtn.setOnClickListener(v -> toggleViewMode());
        countRow.addView(viewModeToggleBtn);

        root.addView(countRow);

        // 9. Recycled Movie List & Grid View Container
        emptyStateTextView = new TextView(this);
        emptyStateTextView.setTextColor(0xFF8B949E);
        emptyStateTextView.setTextSize(15);
        emptyStateTextView.setGravity(Gravity.CENTER);
        emptyStateTextView.setPadding(dp(20), dp(50), dp(20), dp(50));
        emptyStateTextView.setText("Keine Filme mit den aktuellen Filtern gefunden.");
        emptyStateTextView.setVisibility(View.GONE);
        LinearLayout.LayoutParams emptyLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
        emptyStateTextView.setLayoutParams(emptyLp);
        root.addView(emptyStateTextView);

        movieContainer = new FrameLayout(this);
        LinearLayout.LayoutParams mcLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
        movieContainer.setLayoutParams(mcLp);

        movieListView = new ListView(this);
        movieListView.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        movieListView.setDivider(null);
        movieListView.setDividerHeight(0);
        movieListView.setSelector(android.R.color.transparent);
        movieListView.setPadding(dp(12), dp(2), dp(12), dp(12));
        movieListView.setClipToPadding(false);
        movieAdapter = new MovieAdapter(this);
        movieListView.setAdapter(movieAdapter);
        movieContainer.addView(movieListView);

        movieGridView = new GridView(this);
        movieGridView.setLayoutParams(new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        movieGridView.setNumColumns(2);
        movieGridView.setHorizontalSpacing(dp(8));
        movieGridView.setVerticalSpacing(dp(8));
        movieGridView.setPadding(dp(12), dp(2), dp(12), dp(12));
        movieGridView.setClipToPadding(false);
        movieGridView.setSelector(android.R.color.transparent);
        movieGridAdapter = new MovieGridAdapter(this);
        movieGridView.setAdapter(movieGridAdapter);
        movieContainer.addView(movieGridView);

        root.addView(movieContainer);

        // Footer layout for loading more items in both List and Grid mode
        footerLayout = new LinearLayout(this);
        footerLayout.setOrientation(LinearLayout.VERTICAL);
        footerLayout.setPadding(dp(12), dp(4), dp(12), dp(16));
        loadMoreButton = new Button(this);
        loadMoreButton.setText("⬇ Weitere 50 Filme nachladen");
        loadMoreButton.setTextColor(0xFFF0F6FC);
        loadMoreButton.setTextSize(13);
        loadMoreButton.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams lmBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(44));
        loadMoreButton.setLayoutParams(lmBtnLp);
        loadMoreButton.setOnClickListener(v -> {
            criteria.targetCount += 50;
            triggerLiveFetchIfNeeded();
        });
        footerLayout.addView(loadMoreButton);
        root.addView(footerLayout);

        updateViewModeVisuals();

        return root;
    }

    private View buildHeaderBar() {
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(14), dp(10), dp(14), dp(8));
        header.setBackgroundColor(0xFF161B22);

        // --- Row 1: Brand title & compact quick actions ---
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams trLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topRow.setLayoutParams(trLp);

        LinearLayout titleCol = new LinearLayout(this);
        titleCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        titleCol.setLayoutParams(titleLp);

        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setOrientation(LinearLayout.HORIZONTAL);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView logoIcon = new TextView(this);
        logoIcon.setText("🎬 ");
        logoIcon.setTextSize(18);
        brandRow.addView(logoIcon);

        TextView title = new TextView(this);
        title.setText("StreamRanker");
        title.setTextColor(0xFFF5C518);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setSingleLine(true);
        brandRow.addView(title);
        titleCol.addView(brandRow);

        headerSubtitle = new TextView(this);
        headerSubtitle.setTextColor(0xFF8B949E);
        headerSubtitle.setTextSize(11);
        headerSubtitle.setSingleLine(true);
        headerSubtitle.setText(movieDatabase.getTotalCount() + " Filme • Top Ranking");
        titleCol.addView(headerSubtitle);
        topRow.addView(titleCol);

        // 1. Zufall Button
        Button diceBtn = new Button(this);
        diceBtn.setText("🎲 Zufall");
        diceBtn.setTextColor(0xFFF5C518);
        diceBtn.setTextSize(11);
        diceBtn.setTransformationMethod(null);
        diceBtn.setBackgroundResource(R.drawable.btn_generic);
        diceBtn.setPadding(dp(8), dp(4), dp(8), dp(4));
        LinearLayout.LayoutParams diceBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(32));
        diceBtnLp.setMargins(0, 0, dp(4), 0);
        diceBtn.setLayoutParams(diceBtnLp);
        diceBtn.setOnClickListener(v -> showRandomMoviePick());
        topRow.addView(diceBtn);

        // 2. Filter Button
        Button filterBtn = new Button(this);
        filterBtn.setText("⚙️ Filter");
        filterBtn.setTextColor(0xFFF0F6FC);
        filterBtn.setTextSize(11);
        filterBtn.setTransformationMethod(null);
        filterBtn.setBackgroundResource(R.drawable.btn_generic);
        filterBtn.setPadding(dp(8), dp(4), dp(8), dp(4));
        LinearLayout.LayoutParams filterBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(32));
        filterBtnLp.setMargins(0, 0, dp(4), 0);
        filterBtn.setLayoutParams(filterBtnLp);
        filterBtn.setOnClickListener(v -> showDetailedFilterDialog());
        topRow.addView(filterBtn);

        // 3. OTA Update Button
        Button updateBtn = new Button(this);
        updateBtn.setText("🚀 Update");
        updateBtn.setTextColor(0xFF2EA043);
        updateBtn.setTextSize(11);
        updateBtn.setTransformationMethod(null);
        updateBtn.setBackgroundResource(R.drawable.btn_generic);
        updateBtn.setPadding(dp(8), dp(4), dp(8), dp(4));
        LinearLayout.LayoutParams updateBtnLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, dp(32));
        updateBtn.setLayoutParams(updateBtnLp);
        updateBtn.setOnClickListener(v -> OtaUpdateManager.getInstance().checkUpdate(MainActivity.this, true));
        topRow.addView(updateBtn);

        header.addView(topRow);

        // --- Row 2: Prominent Feature Action Row (USPs) ---
        LinearLayout uspRow = new LinearLayout(this);
        uspRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams uspLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        uspLp.setMargins(0, dp(8), 0, 0);
        uspRow.setLayoutParams(uspLp);

        // USP 1: Couch-Duell Button
        Button duellBtn = new Button(this);
        duellBtn.setText("⚔️ Couch-Duell (Showdown)");
        duellBtn.setTextColor(0xFFFFFFFF);
        duellBtn.setTextSize(11);
        duellBtn.setTypeface(Typeface.DEFAULT_BOLD);
        duellBtn.setTransformationMethod(null);
        GradientDrawable duellGd = new GradientDrawable();
        duellGd.setCornerRadius(dp(8));
        duellGd.setColor(0xFFB04300); // Warm fiery accent
        duellBtn.setBackground(duellGd);
        LinearLayout.LayoutParams duellLp = new LinearLayout.LayoutParams(0, dp(34), 1.0f);
        duellLp.setMargins(0, 0, dp(4), 0);
        duellBtn.setLayoutParams(duellLp);
        duellBtn.setOnClickListener(v -> showCouchDuell());
        uspRow.addView(duellBtn);

        // USP 2: Streaming-Bilanz Button
        Button bilanzBtn = new Button(this);
        bilanzBtn.setText("📊 Abo-Spar-Bilanz");
        bilanzBtn.setTextColor(0xFFFFFFFF);
        bilanzBtn.setTextSize(11);
        bilanzBtn.setTypeface(Typeface.DEFAULT_BOLD);
        bilanzBtn.setTransformationMethod(null);
        GradientDrawable bilanzGd = new GradientDrawable();
        bilanzGd.setCornerRadius(dp(8));
        bilanzGd.setColor(0xFF1F6FEB); // Clean blue accent
        bilanzBtn.setBackground(bilanzGd);
        LinearLayout.LayoutParams bilanzLp = new LinearLayout.LayoutParams(0, dp(34), 1.0f);
        bilanzLp.setMargins(dp(4), 0, 0, 0);
        bilanzBtn.setLayoutParams(bilanzLp);
        bilanzBtn.setOnClickListener(v -> showStreamingAnalytics());
        uspRow.addView(bilanzBtn);

        header.addView(uspRow);

        return header;
    }

    private View buildTabsBar() {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setBackgroundColor(0xFF161B22);
        scroll.setPadding(dp(6), dp(4), dp(6), dp(4));

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);

        tabs.addView(createTabButton("🏆 Top 100", TAB_LEADERBOARD));
        tabs.addView(createTabButton("⏳ Bald weg", TAB_LEAVING_SOON));
        tabs.addView(createTabButton("⭐ Watchlist", TAB_WATCHLIST));
        tabs.addView(createTabButton("👁️ Gesehen", TAB_WATCHED));
        tabs.addView(createTabButton("🔍 Suche", TAB_SEARCH));

        scroll.addView(tabs);
        tabsContainer = tabs;
        return scroll;
    }

    private Button createTabButton(String text, final int tab) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextSize(12);
        btn.setTransformationMethod(null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(38));
        lp.setMargins(dp(3), 0, dp(3), 0);
        btn.setLayoutParams(lp);
        btn.setPadding(dp(12), 0, dp(12), 0);

        btn.setOnClickListener(v -> {
            currentTab = tab;
            boolean showSearch = (tab == TAB_SEARCH || tab == TAB_WATCHED);
            searchEditText.setVisibility(showSearch ? View.VISIBLE : View.GONE);
            if (tab == TAB_WATCHED) {
                searchEditText.setHint("Gesehene Filme durchsuchen (Titel, Darsteller, Land, Genre)...");
            } else if (tab == TAB_SEARCH) {
                searchEditText.setHint("Titel, Darsteller, Regie, Land oder Genre suchen...");
            }
            if (tab != TAB_SEARCH && tab != TAB_WATCHED) {
                criteria.searchQuery = "";
                searchEditText.setText("");
            }
            refreshUi();
        });

        return btn;
    }

    void refreshUi() {
        int installedCount = providerManager.getInstalledProviders().size();
        headerSubtitle.setText(movieDatabase.getTotalCount() + " Filme im Pool • " + installedCount + " Dienste erkannt");

        renderProviderDimensionChips();
        renderPresetDimensionChips();
        renderSortModeChips();
        renderGenreChips();
        renderExclusionAndYearChips();

        if (tabsContainer != null && tabsContainer.getChildCount() > TAB_WATCHED) {
            Button watchedBtn = (Button) tabsContainer.getChildAt(TAB_WATCHED);
            if (watchedBtn != null) {
                int wCount = watchedManager != null ? watchedManager.getCount() : 0;
                watchedBtn.setText(wCount > 0 ? "👁️ Gesehen (" + wCount + ")" : "👁️ Gesehen");
            }
        }

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

        providerChipsContainer.addView(createFilterPill("Nur WOW", criteria.singleProviderId.equals("wow"), 0xFF008272, () -> {
            criteria.singleProviderId = criteria.singleProviderId.equals("wow") ? "all" : "wow";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        providerChipsContainer.addView(createFilterPill("Nur ARD", criteria.singleProviderId.equals("ard"), 0xFF003366, () -> {
            criteria.singleProviderId = criteria.singleProviderId.equals("ard") ? "all" : "ard";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        providerChipsContainer.addView(createFilterPill("Nur ZDF", criteria.singleProviderId.equals("zdf"), 0xFFFA7D00, () -> {
            criteria.singleProviderId = criteria.singleProviderId.equals("zdf") ? "all" : "zdf";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        providerChipsContainer.addView(createFilterPill("Nur Arte", criteria.singleProviderId.equals("arte"), 0xFFFA5000, () -> {
            criteria.singleProviderId = criteria.singleProviderId.equals("arte") ? "all" : "arte";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));

        providerChipsContainer.addView(createFilterPill("Nur Joyn", criteria.singleProviderId.equals("joyn"), 0xFF004B87, () -> {
            criteria.singleProviderId = criteria.singleProviderId.equals("joyn") ? "all" : "joyn";
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
        }));
    }

    private void renderPresetDimensionChips() {
        presetChipsContainer.removeAllViews();

        TextView label = new TextView(this);
        label.setText("Stimmung: ");
        label.setTextColor(0xFF8B949E);
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setPadding(0, 0, dp(4), 0);
        presetChipsContainer.addView(label);

        for (final FilterPreset preset : FilterPreset.getAll()) {
            boolean isSel = preset.id.equalsIgnoreCase(criteria.activePresetId);
            presetChipsContainer.addView(createFilterPill(preset.label, isSel, preset.accentColor, () -> {
                FilterPreset.apply(preset.id, criteria);
                criteria.save(MainActivity.this);
                refreshUi();
                triggerLiveFetchIfNeeded();
                Toast.makeText(MainActivity.this, preset.label + ": " + preset.description, Toast.LENGTH_SHORT).show();
            }));
        }
    }

    void toggleViewMode() {
        isGridMode = !isGridMode;
        getSharedPreferences("stream_ranker_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("is_grid_mode", isGridMode)
            .apply();
        updateViewModeVisuals();
    }

    void updateViewModeVisuals() {
        if (viewModeToggleBtn != null) {
            viewModeToggleBtn.setText(isGridMode ? "☰ Liste" : "⊞ Raster");
        }
        if (movieListView != null && movieGridView != null) {
            movieListView.setVisibility(isGridMode ? View.GONE : View.VISIBLE);
            movieGridView.setVisibility(isGridMode ? View.VISIBLE : View.GONE);
        }
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

    void renderExclusionAndYearChips() {
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

        exclusionChipsContainer.addView(createFilterPill("🎯 Goldener Konsens", criteria.bulletproofConsensus, 0xFFFFD700, () -> {
            criteria.bulletproofConsensus = !criteria.bulletproofConsensus;
            criteria.save(MainActivity.this);
            refreshUi();
            triggerLiveFetchIfNeeded();
            if (criteria.bulletproofConsensus) {
                Toast.makeText(MainActivity.this, "🎯 Goldener Konsens aktiv: Nur Filme mit IMDb ≥ 7.2 & Rotten Tomatoes ≥ 75%!", Toast.LENGTH_SHORT).show();
            }
        }));

        // Sprachfassung / Tonspur Filter Pills
        boolean isGermanFilter = (criteria.languageFilter == FilterCriteria.LANG_GERMAN_ONLY);
        exclusionChipsContainer.addView(createFilterPill("🇩🇪 Nur Deutsch", isGermanFilter, 0xFF2EA043, () -> {
            criteria.languageFilter = (criteria.languageFilter == FilterCriteria.LANG_GERMAN_ONLY)
                ? FilterCriteria.LANG_ALL
                : FilterCriteria.LANG_GERMAN_ONLY;
            criteria.save(MainActivity.this);
            refreshUi();
            Toast.makeText(MainActivity.this, criteria.languageFilter == FilterCriteria.LANG_GERMAN_ONLY
                ? "🇩🇪 Filter aktiv: Nur Filme mit deutscher Tonspur"
                : "🌐 Alle Sprachfassungen angezeigt", Toast.LENGTH_SHORT).show();
        }));

        boolean isOmuFilter = (criteria.languageFilter == FilterCriteria.LANG_ORIGINAL_OMU_ONLY);
        exclusionChipsContainer.addView(createFilterPill("🗣️ Nur OmU / OV", isOmuFilter, 0xFF8957E5, () -> {
            criteria.languageFilter = (criteria.languageFilter == FilterCriteria.LANG_ORIGINAL_OMU_ONLY)
                ? FilterCriteria.LANG_ALL
                : FilterCriteria.LANG_ORIGINAL_OMU_ONLY;
            criteria.save(MainActivity.this);
            refreshUi();
            Toast.makeText(MainActivity.this, criteria.languageFilter == FilterCriteria.LANG_ORIGINAL_OMU_ONLY
                ? "🗣️ Filter aktiv: Nur Originalfassungen (OmU / OV)"
                : "🌐 Alle Sprachfassungen angezeigt", Toast.LENGTH_SHORT).show();
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

        // Custom excluded actors
        for (final String exActor : criteria.excludedActors) {
            exclusionChipsContainer.addView(createFilterPill("🚫 Ohne " + exActor, true, 0xFFDA3633, () -> {
                criteria.excludedActors.remove(exActor);
                criteria.save(MainActivity.this);
                refreshUi();
                triggerLiveFetchIfNeeded();
            }));
        }

        // Custom excluded directors
        for (final String exDir : criteria.excludedDirectors) {
            exclusionChipsContainer.addView(createFilterPill("🚫 Ohne " + exDir, true, 0xFFDA3633, () -> {
                criteria.excludedDirectors.remove(exDir);
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

    View createFilterPill(String text, boolean active, int activeColor, Runnable onClick) {
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

    void renderMovieList() {
        Set<StreamingProvider> activeProviders = providerManager.getActiveProviders();
        List<Movie> movies;

        switch (currentTab) {
            case TAB_LEAVING_SOON:
                movies = movieDatabase.getLeavingSoon(activeProviders);
                listCountHeader.setText("⏳ Nur noch kurze Zeit verfügbar (" + movies.size() + " Titel)");
                if (footerLayout != null) footerLayout.setVisibility(View.GONE);
                break;
            case TAB_WATCHLIST:
                movies = movieDatabase.getWatchlistMatches(activeProviders, watchlistManager);
                listCountHeader.setText("⭐ Watchlist-Treffer (" + movies.size() + " Titel)");
                if (footerLayout != null) footerLayout.setVisibility(View.GONE);
                break;
            case TAB_WATCHED:
                movies = movieDatabase.getWatchedMatches(activeProviders, watchedManager, criteria.searchQuery);
                if (footerLayout != null) footerLayout.setVisibility(View.GONE);
                if (criteria.searchQuery != null && !criteria.searchQuery.trim().isEmpty()) {
                    listCountHeader.setText("👁️ Gesehene Filme: " + movies.size() + " Treffer für \"" + criteria.searchQuery + "\"");
                } else {
                    listCountHeader.setText("👁️ Bereits gesehene Filme (" + movies.size() + " Titel)");
                }
                break;
            case TAB_SEARCH:
            case TAB_LEADERBOARD:
            default:
                movies = movieDatabase.getFilteredList(criteria, activeProviders, watchedManager);

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
                break;
        }

        if (movies.isEmpty()) {
            emptyStateTextView.setVisibility(View.VISIBLE);
            movieContainer.setVisibility(View.GONE);
            if (footerLayout != null) footerLayout.setVisibility(View.GONE);
            if (currentTab == TAB_WATCHLIST) {
                emptyStateTextView.setText("Keine Treffer deiner Watchlist auf den aktiven Diensten.\nKlicke oben auf '⭐ Watchlist', um deine Titel abzugleichen.");
            } else if (currentTab == TAB_WATCHED) {
                if (watchedManager != null && watchedManager.getCount() == 0) {
                    emptyStateTextView.setText("Du hast noch keine Filme als gesehen markiert.\nMarkiere Filme in der Top 100 oder Suche mit '👁️ Gesehen', um sie hier zu sammeln und zu durchsuchen.");
                } else {
                    emptyStateTextView.setText("Keine gesehenen Filme für die aktuelle Suche oder die aktiven Streaming-Dienste gefunden.");
                }
            } else {
                emptyStateTextView.setText("Keine Filme für diese Filterkombination gefunden.\nLade weitere Titel nach oder lockere die Filter.");
            }
        } else {
            emptyStateTextView.setVisibility(View.GONE);
            movieContainer.setVisibility(View.VISIBLE);
            updateViewModeVisuals();

            boolean canLoadMore = (currentTab == TAB_LEADERBOARD || currentTab == TAB_SEARCH);
            if (footerLayout != null) {
                footerLayout.setVisibility(canLoadMore ? View.VISIBLE : View.GONE);
                loadMoreButton.setText("⬇ Weitere 50 Filme nachladen (" + movies.size() + " / " + criteria.targetCount + ")");
            }
        }
        movieAdapter.setMovies(movies);
        if (movieGridAdapter != null) {
            movieGridAdapter.setMovies(movies);
        }
    }

    void triggerLiveFetchIfNeeded() {
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

    void updateWatchedButtonVisual(Button btn, boolean isWatched) {
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

    void updateWatchlistButtonVisual(Button btn, boolean isWatchlisted) {
        btn.setText(isWatchlisted ? "⭐ Gemerkt" : "⭐ Watchlist");
        btn.setTextSize(10);
        btn.setTransformationMethod(null);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(dp(6));
        if (isWatchlisted) {
            gd.setColor(0xFF9E6A03); // Amber
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

    void showMovieDetailDialog(final Movie movie) {
        MovieDetailDialogHelper.show(this, movie);
    }

    public void showCouchDuell() {
        CouchDuellDialogHelper.show(this);
    }

    public void showStreamingAnalytics() {
        StreamingAnalyticsDialogHelper.show(this);
    }

    public void showRandomMoviePick() {
        Set<StreamingProvider> activeProviders = providerManager.getActiveProviders();
        List<Movie> currentPool = movieDatabase.getFilteredList(criteria, activeProviders, watchedManager);
        if (currentPool == null || currentPool.isEmpty()) {
            Toast.makeText(this, "🎲 Keine Filme im aktuellen Filter gefunden!", Toast.LENGTH_SHORT).show();
            return;
        }
        int randomIndex = new java.util.Random().nextInt(currentPool.size());
        Movie picked = currentPool.get(randomIndex);
        Toast.makeText(this, "🎲 Zufallsauswahl: " + picked.getTitle() + "!", Toast.LENGTH_SHORT).show();
        MovieDetailDialogHelper.show(this, picked);
    }

    void showDetailedFilterDialog() {
        FilterDialogHelper.show(this);
    }

    void showWatchlistDialog() {
        WatchlistDialogHelper.show(this);
    }

    void shareMovieRecommendation(Movie movie) {
        MovieDetailDialogHelper.shareMovieRecommendation(this, movie);
    }

    int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }
}
