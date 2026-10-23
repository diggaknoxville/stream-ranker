package com.streamranker.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Helper class managing the Movie Detail dialog, including attribute badges,
 * one-click exclusions, related movie carousel, streaming launches, and social sharing.
 */
public final class MovieDetailDialogHelper {

    private MovieDetailDialogHelper() {}

    public static void show(final MainActivity activity, final Movie movie) {
        if (activity == null || activity.isFinishing() || movie == null) return;

        final WatchedManager watchedManager = activity.watchedManager;
        final WatchlistManager watchlistManager = activity.watchlistManager;
        final AsyncImageLoader imageLoader = activity.imageLoader;
        final LiveCatalogFetcher catalogFetcher = activity.catalogFetcher;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        ScrollView dialogScroll = new ScrollView(activity);
        dialogScroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFF161B22);
        root.setPadding(activity.dp(16), activity.dp(16), activity.dp(16), activity.dp(20));

        // 1. Header with Close Button
        LinearLayout topBar = new LinearLayout(activity);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(0, 0, 0, activity.dp(10));

        TextView dialogTitle = new TextView(activity);
        dialogTitle.setText("🎬 Film-Details & Tags");
        dialogTitle.setTextColor(0xFFF5C518);
        dialogTitle.setTextSize(16);
        dialogTitle.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams dTitleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        dialogTitle.setLayoutParams(dTitleLp);
        topBar.addView(dialogTitle);

        root.addView(topBar);

        // 2. Poster & Meta Section
        LinearLayout mediaRow = new LinearLayout(activity);
        mediaRow.setOrientation(LinearLayout.HORIZONTAL);
        mediaRow.setPadding(0, 0, 0, activity.dp(10));

        ImageView poster = new ImageView(activity);
        LinearLayout.LayoutParams posterLp = new LinearLayout.LayoutParams(activity.dp(95), activity.dp(140));
        poster.setLayoutParams(posterLp);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageLoader.load(poster, movie.getPosterUrl(), movie.getTitle());
        mediaRow.addView(poster);

        LinearLayout infoCol = new LinearLayout(activity);
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setPadding(activity.dp(12), 0, 0, 0);
        LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        infoCol.setLayoutParams(infoLp);

        TextView title = new TextView(activity);
        title.setText(movie.getTitle());
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        infoCol.addView(title);

        if (movie.getOriginalTitle() != null && !movie.getOriginalTitle().equalsIgnoreCase(movie.getTitle())) {
            TextView origTitle = new TextView(activity);
            origTitle.setText("Original: " + movie.getOriginalTitle());
            origTitle.setTextColor(0xFF8B949E);
            origTitle.setTextSize(11);
            origTitle.setPadding(0, activity.dp(2), 0, activity.dp(2));
            infoCol.addView(origTitle);
        }

        TextView subMeta = new TextView(activity);
        subMeta.setText(movie.getYear() + " • " + movie.getRuntimeMinutes() + " Min • " + movie.getProvider().getDisplayName());
        subMeta.setTextColor(0xFF8B949E);
        subMeta.setTextSize(11);
        subMeta.setPadding(0, activity.dp(3), 0, activity.dp(6));
        infoCol.addView(subMeta);

        // Scores
        LinearLayout scores = new LinearLayout(activity);
        scores.setOrientation(LinearLayout.HORIZONTAL);
        scores.setGravity(Gravity.CENTER_VERTICAL);

        TextView imdb = new TextView(activity);
        imdb.setText("★ " + String.format(Locale.ROOT, "%.1f", movie.getImdbRating()));
        imdb.setTextColor(0xFF000000);
        imdb.setTextSize(11);
        imdb.setTypeface(Typeface.DEFAULT_BOLD);
        imdb.setBackgroundResource(R.drawable.badge_imdb);
        imdb.setPadding(activity.dp(5), activity.dp(2), activity.dp(5), activity.dp(2));
        scores.addView(imdb);

        if (movie.getRottenTomatoes() > 0) {
            TextView rt = new TextView(activity);
            rt.setText(" 🍅 " + movie.getRottenTomatoes() + "%");
            rt.setTextColor(0xFFFFFFFF);
            rt.setTextSize(11);
            rt.setTypeface(Typeface.DEFAULT_BOLD);
            rt.setBackgroundResource(R.drawable.badge_rt);
            rt.setPadding(activity.dp(5), activity.dp(2), activity.dp(5), activity.dp(2));
            LinearLayout.LayoutParams rtLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rtLp.setMargins(activity.dp(4), 0, 0, 0);
            rt.setLayoutParams(rtLp);
            scores.addView(rt);
        }

        TextView perle = new TextView(activity);
        perle.setText(" 💎 Perle " + String.format(Locale.ROOT, "%.0f", movie.getCombinedScore()) + "%");
        perle.setTextColor(0xFF000000);
        perle.setTextSize(11);
        perle.setTypeface(Typeface.DEFAULT_BOLD);
        perle.setBackgroundResource(R.drawable.badge_perle);
        perle.setPadding(activity.dp(5), activity.dp(2), activity.dp(5), activity.dp(2));
        LinearLayout.LayoutParams perleLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        perleLp.setMargins(activity.dp(4), 0, 0, 0);
        perle.setLayoutParams(perleLp);
        scores.addView(perle);

        if (movie.getFsk() >= 0) {
            TextView fsk = new TextView(activity);
            fsk.setText("FSK " + movie.getFsk());
            fsk.setTextSize(10);
            fsk.setTypeface(Typeface.DEFAULT_BOLD);
            GradientDrawable fskGd = new GradientDrawable();
            fskGd.setCornerRadius(activity.dp(3));
            if (movie.getFsk() == 0) {
                fskGd.setColor(0xFF2EA043);
                fsk.setTextColor(0xFFFFFFFF);
            } else if (movie.getFsk() == 6) {
                fskGd.setColor(0xFFE3B341);
                fsk.setTextColor(0xFF000000);
            } else if (movie.getFsk() == 12) {
                fskGd.setColor(0xFF1F6FEB);
                fsk.setTextColor(0xFFFFFFFF);
            } else if (movie.getFsk() == 16) {
                fskGd.setColor(0xFF8957E5);
                fsk.setTextColor(0xFFFFFFFF);
            } else {
                fskGd.setColor(0xFFDA3633);
                fsk.setTextColor(0xFFFFFFFF);
            }
            fsk.setBackground(fskGd);
            fsk.setPadding(activity.dp(5), activity.dp(2), activity.dp(5), activity.dp(2));
            LinearLayout.LayoutParams fskLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fskLp.setMargins(activity.dp(4), 0, 0, 0);
            fsk.setLayoutParams(fskLp);
            scores.addView(fsk);
        }

        infoCol.addView(scores);

        TextView votes = new TextView(activity);
        votes.setText(movie.getFormattedVotes() + " IMDb-Stimmen");
        votes.setTextColor(0xFF8B949E);
        votes.setTextSize(10);
        votes.setPadding(0, activity.dp(4), 0, 0);
        infoCol.addView(votes);

        mediaRow.addView(infoCol);
        root.addView(mediaRow);

        // Divider
        root.addView(createDialogDivider(activity));

        // 3. Badges & Tags Section Container
        final LinearLayout badgesSection = new LinearLayout(activity);
        badgesSection.setOrientation(LinearLayout.VERTICAL);
        root.addView(badgesSection);

        // 4. Related Movies Container
        final LinearLayout relatedSection = new LinearLayout(activity);
        relatedSection.setOrientation(LinearLayout.VERTICAL);
        root.addView(relatedSection);

        // 5. Overview Section
        TextView overviewHeader = new TextView(activity);
        overviewHeader.setText("📝 Handlung");
        overviewHeader.setTextColor(0xFFF5C518);
        overviewHeader.setTextSize(12);
        overviewHeader.setTypeface(Typeface.DEFAULT_BOLD);
        overviewHeader.setPadding(0, activity.dp(8), 0, activity.dp(4));
        root.addView(overviewHeader);

        TextView overviewText = new TextView(activity);
        overviewText.setText(movie.getOverview() != null && !movie.getOverview().isEmpty() ? movie.getOverview() : "Keine Beschreibung verfügbar.");
        overviewText.setTextColor(0xFFC9D1D9);
        overviewText.setTextSize(12);
        overviewText.setLineSpacing(0, 1.25f);
        overviewText.setPadding(0, 0, 0, activity.dp(12));
        root.addView(overviewText);

        root.addView(createDialogDivider(activity));

        // 6. Action Buttons in Dialog
        LinearLayout actions = new LinearLayout(activity);
        actions.setOrientation(LinearLayout.VERTICAL);
        actions.setPadding(0, activity.dp(6), 0, 0);

        // Primary Row: Stream Launch & Search
        LinearLayout btnRow1 = new LinearLayout(activity);
        btnRow1.setOrientation(LinearLayout.HORIZONTAL);

        Button streamBtn = new Button(activity);
        boolean isYt = movie.getProvider().equals(StreamingProvider.YOUTUBE);
        streamBtn.setText(isYt ? "▶ In YouTube (Deutsch)" : ("▶ In " + movie.getProvider().getDisplayName() + " öffnen"));
        streamBtn.setTextColor(0xFFFFFFFF);
        streamBtn.setTextSize(12);
        streamBtn.setTypeface(Typeface.DEFAULT_BOLD);
        streamBtn.setTransformationMethod(null);
        if (movie.getProvider().equals(StreamingProvider.NETFLIX)) streamBtn.setBackgroundResource(R.drawable.btn_netflix);
        else if (movie.getProvider().equals(StreamingProvider.PRIME_VIDEO)) streamBtn.setBackgroundResource(R.drawable.btn_prime);
        else if (movie.getProvider().equals(StreamingProvider.DISNEY_PLUS)) streamBtn.setBackgroundResource(R.drawable.btn_disney);
        else if (movie.getProvider().equals(StreamingProvider.WOW)) streamBtn.setBackgroundResource(R.drawable.btn_wow);
        else if (movie.getProvider().equals(StreamingProvider.YOUTUBE)) streamBtn.setBackgroundResource(R.drawable.btn_youtube);
        else if (movie.getProvider().equals(StreamingProvider.ARD_MEDIATHEK)) streamBtn.setBackgroundResource(R.drawable.btn_ard);
        else if (movie.getProvider().equals(StreamingProvider.ZDF_MEDIATHEK)) streamBtn.setBackgroundResource(R.drawable.btn_zdf);
        else if (movie.getProvider().equals(StreamingProvider.ARTE)) streamBtn.setBackgroundResource(R.drawable.btn_arte);
        else if (movie.getProvider().equals(StreamingProvider.JOYN)) streamBtn.setBackgroundResource(R.drawable.btn_joyn);
        else streamBtn.setBackgroundResource(R.drawable.btn_generic);

        LinearLayout.LayoutParams sLp = new LinearLayout.LayoutParams(0, activity.dp(40), 1.0f);
        sLp.setMargins(0, 0, activity.dp(6), 0);
        streamBtn.setLayoutParams(sLp);
        streamBtn.setOnClickListener(v -> {
            if (isYt) {
                movie.getProvider().launchSearch(activity, movie.getTitle() + " ganzer film deutsch");
            } else {
                movie.getProvider().launchTitle(activity, movie.getDeepLinkId(), movie.getTitle());
            }
        });
        btnRow1.addView(streamBtn);

        Button inAppSearchBtn = new Button(activity);
        inAppSearchBtn.setText(isYt ? "🌐 Original (OV)" : "🔍 In App suchen");
        inAppSearchBtn.setTextColor(0xFFF0F6FC);
        inAppSearchBtn.setTextSize(11);
        inAppSearchBtn.setTransformationMethod(null);
        inAppSearchBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams iasLp = new LinearLayout.LayoutParams(0, activity.dp(40), 0.8f);
        inAppSearchBtn.setLayoutParams(iasLp);
        inAppSearchBtn.setOnClickListener(v -> {
            if (isYt) {
                movie.getProvider().launchSearch(activity, movie.getOriginalTitle() != null ? movie.getOriginalTitle() : movie.getTitle());
            } else {
                movie.getProvider().launchSearch(activity, movie.getTitle());
            }
        });
        btnRow1.addView(inAppSearchBtn);
        actions.addView(btnRow1);

        // Secondary Row: Watched & Watchlist
        LinearLayout btnRow2 = new LinearLayout(activity);
        btnRow2.setOrientation(LinearLayout.HORIZONTAL);
        btnRow2.setPadding(0, activity.dp(8), 0, 0);

        final Button watchedToggleBtn = new Button(activity);
        boolean isWatched = watchedManager != null && watchedManager.isWatched(movie.getId(), movie.getTitle());
        activity.updateWatchedButtonVisual(watchedToggleBtn, isWatched);
        LinearLayout.LayoutParams wLp = new LinearLayout.LayoutParams(0, activity.dp(38), 1.0f);
        wLp.setMargins(0, 0, activity.dp(4), 0);
        watchedToggleBtn.setLayoutParams(wLp);
        watchedToggleBtn.setOnClickListener(v -> {
            if (watchedManager != null) {
                boolean nw = watchedManager.toggleWatched(activity, movie.getId(), movie.getTitle());
                activity.updateWatchedButtonVisual(watchedToggleBtn, nw);
                Toast.makeText(activity, (nw ? "✓ Als gesehen markiert: " : "👁️ Als ungesehen markiert: ") + movie.getTitle(), Toast.LENGTH_SHORT).show();
                activity.refreshUi();
            }
        });
        btnRow2.addView(watchedToggleBtn);

        final Button watchlistToggleBtn = new Button(activity);
        boolean isWl = watchlistManager != null && (watchlistManager.isWatchlisted(movie.getId()) || watchlistManager.isWatchlisted(movie.getTitle()));
        activity.updateWatchlistButtonVisual(watchlistToggleBtn, isWl);
        LinearLayout.LayoutParams wlBtnLp = new LinearLayout.LayoutParams(0, activity.dp(38), 1.0f);
        wlBtnLp.setMargins(activity.dp(4), 0, 0, 0);
        watchlistToggleBtn.setLayoutParams(wlBtnLp);
        watchlistToggleBtn.setOnClickListener(v -> {
            if (watchlistManager != null) {
                boolean nWl = watchlistManager.toggleWatchlist(activity, movie.getId(), movie.getTitle());
                activity.updateWatchlistButtonVisual(watchlistToggleBtn, nWl);
                Toast.makeText(activity, (nWl ? "⭐ Zur Watchlist hinzugefügt: " : "⭐ Von Watchlist entfernt: ") + movie.getTitle(), Toast.LENGTH_SHORT).show();
                activity.refreshUi();
            }
        });
        btnRow2.addView(watchlistToggleBtn);

        actions.addView(btnRow2);

        // Row 3: Trailer (Deutsch) & JustWatch
        LinearLayout btnRow3 = new LinearLayout(activity);
        btnRow3.setOrientation(LinearLayout.HORIZONTAL);
        btnRow3.setPadding(0, activity.dp(8), 0, 0);

        Button trailerBtn = new Button(activity);
        trailerBtn.setText("🎬 Trailer (Deutsch)");
        trailerBtn.setTextColor(0xFFFFFFFF);
        trailerBtn.setTextSize(11);
        trailerBtn.setTypeface(Typeface.DEFAULT_BOLD);
        trailerBtn.setTransformationMethod(null);
        GradientDrawable trGd = new GradientDrawable();
        trGd.setColor(0xFFCC0000); // Red
        trGd.setCornerRadius(activity.dp(6));
        trailerBtn.setBackground(trGd);
        LinearLayout.LayoutParams trLp = new LinearLayout.LayoutParams(0, activity.dp(36), 1.0f);
        trLp.setMargins(0, 0, activity.dp(4), 0);
        trailerBtn.setLayoutParams(trLp);
        trailerBtn.setOnClickListener(v -> openMovieTrailer(activity, movie));
        btnRow3.addView(trailerBtn);

        Button justWatchBtn = new Button(activity);
        justWatchBtn.setText("🌐 JustWatch DE");
        justWatchBtn.setTextColor(0xFF8B949E);
        justWatchBtn.setTextSize(11);
        justWatchBtn.setTransformationMethod(null);
        justWatchBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams jwLp = new LinearLayout.LayoutParams(0, activity.dp(36), 1.0f);
        jwLp.setMargins(activity.dp(4), 0, 0, 0);
        justWatchBtn.setLayoutParams(jwLp);
        justWatchBtn.setOnClickListener(v -> StreamingProvider.openJustWatch(activity, movie.getTitle()));
        btnRow3.addView(justWatchBtn);
        actions.addView(btnRow3);

        // Row 4: Share Tipp (WhatsApp, Telegram...)
        LinearLayout btnRow4 = new LinearLayout(activity);
        btnRow4.setOrientation(LinearLayout.HORIZONTAL);
        btnRow4.setPadding(0, activity.dp(8), 0, 0);

        Button shareTippBtn = new Button(activity);
        shareTippBtn.setText("📤 Film-Tipp teilen (WhatsApp, Telegram...)");
        shareTippBtn.setTextColor(0xFFFFFFFF);
        shareTippBtn.setTextSize(12);
        shareTippBtn.setTypeface(Typeface.DEFAULT_BOLD);
        shareTippBtn.setTransformationMethod(null);
        GradientDrawable shareGd = new GradientDrawable();
        shareGd.setColor(0xFF238636); // Attractive share green
        shareGd.setCornerRadius(activity.dp(8));
        shareTippBtn.setBackground(shareGd);
        LinearLayout.LayoutParams shareLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(40));
        shareTippBtn.setLayoutParams(shareLp);
        shareTippBtn.setOnClickListener(v -> shareMovieRecommendation(activity, movie));
        btnRow4.addView(shareTippBtn);
        actions.addView(btnRow4);

        if (movie.getProvider().equals(StreamingProvider.YOUTUBE)) {
            TextView ytHint = new TextView(activity);
            ytHint.setText("💡 Tipp: Filme auf YouTube sind teils auf Deutsch synchronisiert (z. B. Netzkino) oder in englischer Originalfassung verfügbar.");
            ytHint.setTextSize(11);
            ytHint.setTextColor(0xFF8B949E);
            ytHint.setPadding(0, activity.dp(8), 0, 0);
            actions.addView(ytHint);
        }

        root.addView(actions);

        dialogScroll.addView(root);
        builder.setView(dialogScroll);
        final AlertDialog dialog = builder.create();

        final Runnable renderBadgesAndRelated = new Runnable() {
            @Override
            public void run() {
                populateDetailBadges(activity, badgesSection, movie, dialog, this);
                populateRelatedMovies(activity, relatedSection, movie, dialog);
            }
        };

        renderBadgesAndRelated.run();

        // If actors are not loaded yet, fetch async from TMDB!
        if (movie.getActorList().isEmpty()) {
            catalogFetcher.fetchCreditsAsync(movie, () -> {
                if (dialog.isShowing()) {
                    renderBadgesAndRelated.run();
                }
            });
        }

        dialog.show();
    }

    public static void populateDetailBadges(final MainActivity activity, final LinearLayout container, final Movie movie, final AlertDialog dialog, final Runnable onUpdate) {
        final FilterCriteria criteria = activity.criteria;
        container.removeAllViews();

        TextView h = new TextView(activity);
        h.setText("🏷️ Badges & Attribute (1-Klick Ausschluss):");
        h.setTextColor(0xFFF5C518);
        h.setTextSize(12);
        h.setTypeface(Typeface.DEFAULT_BOLD);
        h.setPadding(0, 0, 0, activity.dp(2));
        container.addView(h);

        // Active Compound Exclusions matching this movie
        List<String> activeRulesForMovie = new ArrayList<>();
        String mCountry = movie.getOriginCountry() != null ? movie.getOriginCountry().toUpperCase() : "";
        String mGenresLower = movie.getGenres().toLowerCase(Locale.ROOT);
        for (String rule : criteria.compoundExclusions) {
            int colon = rule.indexOf(':');
            if (colon != -1) {
                String prefix = rule.substring(0, colon).trim();
                String genre = rule.substring(colon + 1).trim();
                boolean matchesPrefix = prefix.equalsIgnoreCase(mCountry) || movie.hasActor(prefix) || movie.hasDirector(prefix);
                if (matchesPrefix && FilterCriteria.isGenreMatch(mGenresLower, genre)) {
                    activeRulesForMovie.add(rule);
                }
            }
        }
        if (!activeRulesForMovie.isEmpty()) {
            TextView compH = new TextView(activity);
            compH.setText("⚡ Aktive Kombi-Ausschlüsse für diesen Film (Tippen zum Aufheben):");
            compH.setTextColor(0xFFFA7D00);
            compH.setTextSize(11);
            compH.setTypeface(Typeface.DEFAULT_BOLD);
            compH.setPadding(0, activity.dp(2), 0, activity.dp(4));
            container.addView(compH);

            HorizontalScrollView compScroll = new HorizontalScrollView(activity);
            compScroll.setHorizontalScrollBarEnabled(false);
            LinearLayout compRow = new LinearLayout(activity);
            compRow.setOrientation(LinearLayout.HORIZONTAL);
            compRow.setPadding(0, 0, 0, activity.dp(8));

            for (final String rule : activeRulesForMovie) {
                compRow.addView(createTagPill(activity, "🚫 " + rule + " ✕", true, 0xFFDA3633, () -> {
                    criteria.compoundExclusions.remove(rule);
                    criteria.save(activity);
                    activity.refreshUi();
                    onUpdate.run();
                    Toast.makeText(activity, "✓ Kombi-Ausschluss \"" + rule + "\" aufgehoben", Toast.LENGTH_SHORT).show();
                }));
            }
            compScroll.addView(compRow);
            container.addView(compScroll);
        }

        TextView tip = new TextView(activity);
        tip.setText("Tippe auf [🚫] an einem Tag, um es global für alle zukünftigen Vorschläge auszuschließen:");
        tip.setTextColor(0xFF8B949E);
        tip.setTextSize(10);
        tip.setPadding(0, 0, 0, activity.dp(8));
        container.addView(tip);

        // 1. Produktionsland Tag
        TextView landLabel = new TextView(activity);
        landLabel.setText("🌍 Produktionsland:");
        landLabel.setTextColor(0xFFC9D1D9);
        landLabel.setTextSize(11);
        landLabel.setTypeface(Typeface.DEFAULT_BOLD);
        landLabel.setPadding(0, activity.dp(2), 0, activity.dp(4));
        container.addView(landLabel);

        LinearLayout countryRow = new LinearLayout(activity);
        countryRow.setOrientation(LinearLayout.HORIZONTAL);
        countryRow.setPadding(0, 0, 0, activity.dp(8));

        final String countryCode = movie.getOriginCountry();
        final String countryName = movie.getOriginCountryName();
        final boolean isCountryExcluded = criteria.excludedCountries.contains(countryCode);

        if (isCountryExcluded) {
            countryRow.addView(createTagPill(activity, "🚫 " + movie.getOriginCountryFlag() + " " + countryName + " (Ausgeschlossen - Tippen zum Aufheben)", true, 0xFFDA3633, () -> {
                criteria.excludedCountries.remove(countryCode);
                criteria.save(activity);
                activity.refreshUi();
                onUpdate.run();
                Toast.makeText(activity, "✓ Ausschluss für " + countryName + " aufgehoben", Toast.LENGTH_SHORT).show();
            }));
        } else {
            countryRow.addView(createTagPill(activity, movie.getOriginCountryFlag() + " " + countryName + " (" + countryCode + ")", false, 0, null));
            countryRow.addView(createTagPill(activity, "🚫 Land ausschließen", true, 0xFFDA3633, () -> {
                criteria.excludedCountries.add(countryCode);
                criteria.save(activity);
                activity.refreshUi();
                activity.triggerLiveFetchIfNeeded();
                onUpdate.run();
                Toast.makeText(activity, "🚫 Filme aus " + countryName + " (" + countryCode + ") global ausgeschlossen", Toast.LENGTH_SHORT).show();
            }));
            countryRow.addView(createTagPill(activity, "⚡ Kombi: " + countryCode + " + Genre", true, 0xFF8957E5, () -> {
                showCompoundExclusionPicker(activity, movie, countryCode, countryName, onUpdate);
            }));
        }
        container.addView(countryRow);

        // 2. Hauptdarsteller / Cast Tags
        TextView castLabel = new TextView(activity);
        castLabel.setText("🎭 Hauptdarsteller / Cast:");
        castLabel.setTextColor(0xFFC9D1D9);
        castLabel.setTextSize(11);
        castLabel.setTypeface(Typeface.DEFAULT_BOLD);
        castLabel.setPadding(0, activity.dp(2), 0, activity.dp(4));
        container.addView(castLabel);

        List<String> actors = movie.getActorList();
        if (actors.isEmpty()) {
            TextView loadingText = new TextView(activity);
            loadingText.setText("⏳ Lade Darsteller aus Live-Katalog...");
            loadingText.setTextColor(0xFF8B949E);
            loadingText.setTextSize(10);
            loadingText.setPadding(0, activity.dp(2), 0, activity.dp(8));
            container.addView(loadingText);
        } else {
            HorizontalScrollView actorScroll = new HorizontalScrollView(activity);
            actorScroll.setHorizontalScrollBarEnabled(false);
            actorScroll.setPadding(0, 0, 0, activity.dp(8));
            LinearLayout actorRow = new LinearLayout(activity);
            actorRow.setOrientation(LinearLayout.HORIZONTAL);

            for (final String actor : actors) {
                final boolean isActExcluded = criteria.excludedActors.contains(actor);
                if (isActExcluded) {
                    actorRow.addView(createTagPill(activity, "🚫 " + actor + " (Ausgeschlossen) ✕", true, 0xFFDA3633, () -> {
                        criteria.excludedActors.remove(actor);
                        criteria.save(activity);
                        activity.refreshUi();
                        onUpdate.run();
                        Toast.makeText(activity, "✓ Ausschluss für " + actor + " aufgehoben", Toast.LENGTH_SHORT).show();
                    }));
                } else {
                    actorRow.addView(createTagPill(activity, actor + "  🚫", false, 0, () -> {
                        String[] actOptions = new String[] {
                            "🚫 Global ausschließen",
                            "⚡ Kombi: " + actor + " + Genre ausschließen",
                            "🔍 Filme im Katalog suchen"
                        };
                        new AlertDialog.Builder(activity)
                            .setTitle("🎭 " + actor)
                            .setItems(actOptions, (d, which) -> {
                                if (which == 0) {
                                    criteria.excludedActors.add(actor);
                                    criteria.save(activity);
                                    activity.refreshUi();
                                    activity.triggerLiveFetchIfNeeded();
                                    onUpdate.run();
                                    Toast.makeText(activity, "🚫 Schauspieler \"" + actor + "\" global ausgeschlossen", Toast.LENGTH_SHORT).show();
                                } else if (which == 1) {
                                    showCompoundExclusionPicker(activity, movie, actor, actor, onUpdate);
                                } else if (which == 2) {
                                    if (dialog != null) dialog.dismiss();
                                    activity.currentTab = MainActivity.TAB_SEARCH;
                                    activity.searchEditText.setVisibility(View.VISIBLE);
                                    activity.searchEditText.setText(actor);
                                    criteria.searchQuery = actor;
                                    activity.refreshUi();
                                }
                            })
                            .setNegativeButton("Abbrechen", null)
                            .show();
                    }));
                }
            }
            actorScroll.addView(actorRow);
            container.addView(actorScroll);
        }

        // 3. Regie Tags
        if (movie.getDirector() != null && !movie.getDirector().trim().isEmpty()) {
            TextView dirLabel = new TextView(activity);
            dirLabel.setText("🎬 Regie:");
            dirLabel.setTextColor(0xFFC9D1D9);
            dirLabel.setTextSize(11);
            dirLabel.setTypeface(Typeface.DEFAULT_BOLD);
            dirLabel.setPadding(0, activity.dp(2), 0, activity.dp(4));
            container.addView(dirLabel);

            LinearLayout dirRow = new LinearLayout(activity);
            dirRow.setOrientation(LinearLayout.HORIZONTAL);
            dirRow.setPadding(0, 0, 0, activity.dp(8));

            final String director = movie.getDirector().trim();
            final boolean isDirExcluded = criteria.excludedDirectors.contains(director);

            if (isDirExcluded) {
                dirRow.addView(createTagPill(activity, "🚫 " + director + " (Ausgeschlossen) ✕", true, 0xFFDA3633, () -> {
                    criteria.excludedDirectors.remove(director);
                    criteria.save(activity);
                    activity.refreshUi();
                    onUpdate.run();
                    Toast.makeText(activity, "✓ Ausschluss für " + director + " aufgehoben", Toast.LENGTH_SHORT).show();
                }));
            } else {
                dirRow.addView(createTagPill(activity, director + "  🚫", false, 0, () -> {
                    new AlertDialog.Builder(activity)
                        .setTitle("🎬 Regie: " + director)
                        .setMessage("Wähle eine Aktion für diesen Regisseur:")
                        .setPositiveButton("🚫 Global ausschließen", (d, w) -> {
                            criteria.excludedDirectors.add(director);
                            criteria.save(activity);
                            activity.refreshUi();
                            activity.triggerLiveFetchIfNeeded();
                            onUpdate.run();
                            Toast.makeText(activity, "🚫 Regisseur \"" + director + "\" global ausgeschlossen", Toast.LENGTH_SHORT).show();
                        })
                        .setNeutralButton("🔍 Filme dieser Regie suchen", (d, w) -> {
                            if (dialog != null) dialog.dismiss();
                            activity.currentTab = MainActivity.TAB_SEARCH;
                            activity.searchEditText.setVisibility(View.VISIBLE);
                            activity.searchEditText.setText(director);
                            criteria.searchQuery = director;
                            activity.refreshUi();
                        })
                        .setNegativeButton("Abbrechen", null)
                        .show();
                }));
            }
            container.addView(dirRow);
        }

        // 4. Genre Tags
        TextView genreLabel = new TextView(activity);
        genreLabel.setText("🏷️ Genres:");
        genreLabel.setTextColor(0xFFC9D1D9);
        genreLabel.setTextSize(11);
        genreLabel.setTypeface(Typeface.DEFAULT_BOLD);
        genreLabel.setPadding(0, activity.dp(2), 0, activity.dp(4));
        container.addView(genreLabel);

        HorizontalScrollView genreScroll = new HorizontalScrollView(activity);
        genreScroll.setHorizontalScrollBarEnabled(false);
        genreScroll.setPadding(0, 0, 0, activity.dp(8));
        LinearLayout genreRow = new LinearLayout(activity);
        genreRow.setOrientation(LinearLayout.HORIZONTAL);

        for (final String genre : movie.getGenreList()) {
            final boolean isGenExcluded = criteria.excludedGenres.contains(genre);
            if (isGenExcluded) {
                genreRow.addView(createTagPill(activity, "🚫 " + genre + " (Ausgeschlossen) ✕", true, 0xFFDA3633, () -> {
                    criteria.excludedGenres.remove(genre);
                    criteria.save(activity);
                    activity.refreshUi();
                    onUpdate.run();
                    Toast.makeText(activity, "✓ Ausschluss für " + genre + " aufgehoben", Toast.LENGTH_SHORT).show();
                }));
            } else {
                genreRow.addView(createTagPill(activity, genre + "  🚫", false, 0, () -> {
                    String[] genOptions = new String[] {
                        "🚫 Global ausschließen",
                        "⚡ Nur für " + countryName + " (" + countryCode + ") ausschließen",
                        "➕ Nach Genre filtern"
                    };
                    new AlertDialog.Builder(activity)
                        .setTitle("🏷️ Genre: " + genre)
                        .setItems(genOptions, (d, which) -> {
                            if (which == 0) {
                                criteria.excludedGenres.add(genre);
                                criteria.save(activity);
                                activity.refreshUi();
                                activity.triggerLiveFetchIfNeeded();
                                onUpdate.run();
                                Toast.makeText(activity, "🚫 Genre \"" + genre + "\" global ausgeschlossen", Toast.LENGTH_SHORT).show();
                            } else if (which == 1) {
                                String rule = countryCode + ":" + genre;
                                criteria.compoundExclusions.add(rule);
                                criteria.save(activity);
                                activity.refreshUi();
                                activity.triggerLiveFetchIfNeeded();
                                onUpdate.run();
                                Toast.makeText(activity, "🚫 Kombi-Ausschluss aktiv: " + rule, Toast.LENGTH_LONG).show();
                            } else if (which == 2) {
                                criteria.includedGenres.clear();
                                criteria.includedGenres.add(genre);
                                criteria.save(activity);
                                activity.refreshUi();
                                activity.triggerLiveFetchIfNeeded();
                                onUpdate.run();
                                Toast.makeText(activity, "Filter auf \"" + genre + "\" gesetzt", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Abbrechen", null)
                        .show();
                }));
            }
        }
        genreScroll.addView(genreRow);
        container.addView(genreScroll);

        // 5. Jahr & Dekade
        TextView yearLabel = new TextView(activity);
        yearLabel.setText("📅 Erscheinungsjahr:");
        yearLabel.setTextColor(0xFFC9D1D9);
        yearLabel.setTextSize(11);
        yearLabel.setTypeface(Typeface.DEFAULT_BOLD);
        yearLabel.setPadding(0, activity.dp(2), 0, activity.dp(4));
        container.addView(yearLabel);

        LinearLayout yearRow = new LinearLayout(activity);
        yearRow.setOrientation(LinearLayout.HORIZONTAL);
        yearRow.setPadding(0, 0, 0, activity.dp(8));
        yearRow.addView(createTagPill(activity, "📅 " + movie.getYear(), false, 0, null));
        yearRow.addView(createTagPill(activity, "📅 " + (movie.getYear() / 10 * 10) + "er Jahre", false, 0, null));
        container.addView(yearRow);

        container.addView(createDialogDivider(activity));
    }

    public static void populateRelatedMovies(final MainActivity activity, LinearLayout container, final Movie movie, final AlertDialog dialog) {
        container.removeAllViews();
        List<Movie> related = activity.movieDatabase.getRelatedMovies(movie, 6);
        if (related.isEmpty()) return;

        TextView h = new TextView(activity);
        h.setText("🎬 Weitere Filme mit Darstellern aus diesem Film:");
        h.setTextColor(0xFFF5C518);
        h.setTextSize(12);
        h.setTypeface(Typeface.DEFAULT_BOLD);
        h.setPadding(0, activity.dp(2), 0, activity.dp(2));
        container.addView(h);

        TextView sub = new TextView(activity);
        sub.setText("Tippe auf einen Film, um Tags & Rollen zu vergleichen:");
        sub.setTextColor(0xFF8B949E);
        sub.setTextSize(10);
        sub.setPadding(0, 0, 0, activity.dp(6));
        container.addView(sub);

        HorizontalScrollView scroll = new HorizontalScrollView(activity);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setPadding(0, 0, 0, activity.dp(8));
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);

        for (final Movie rel : related) {
            LinearLayout relCard = new LinearLayout(activity);
            relCard.setOrientation(LinearLayout.HORIZONTAL);
            relCard.setBackgroundResource(R.drawable.card_movie);
            relCard.setPadding(activity.dp(8), activity.dp(8), activity.dp(8), activity.dp(8));
            LinearLayout.LayoutParams rcLp = new LinearLayout.LayoutParams(activity.dp(220), ViewGroup.LayoutParams.WRAP_CONTENT);
            rcLp.setMargins(0, 0, activity.dp(8), 0);
            relCard.setLayoutParams(rcLp);

            ImageView rPoster = new ImageView(activity);
            LinearLayout.LayoutParams rpLp = new LinearLayout.LayoutParams(activity.dp(44), activity.dp(66));
            rPoster.setLayoutParams(rpLp);
            rPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            activity.imageLoader.load(rPoster, rel.getPosterUrl(), rel.getTitle());
            relCard.addView(rPoster);

            LinearLayout rDetails = new LinearLayout(activity);
            rDetails.setOrientation(LinearLayout.VERTICAL);
            rDetails.setPadding(activity.dp(8), 0, 0, 0);

            TextView rTitle = new TextView(activity);
            rTitle.setText(rel.getTitle());
            rTitle.setTextColor(0xFFF0F6FC);
            rTitle.setTextSize(11);
            rTitle.setTypeface(Typeface.DEFAULT_BOLD);
            rTitle.setMaxLines(1);
            rDetails.addView(rTitle);

            TextView rMeta = new TextView(activity);
            rMeta.setText(rel.getYear() + " • ★ " + String.format(Locale.ROOT, "%.1f", rel.getImdbRating()) + " • " + rel.getProvider().getDisplayName());
            rMeta.setTextColor(0xFF8B949E);
            rMeta.setTextSize(10);
            rDetails.addView(rMeta);

            String common = findCommonActors(movie, rel);
            if (!common.isEmpty()) {
                TextView rCommon = new TextView(activity);
                rCommon.setText("🎭 " + common);
                rCommon.setTextColor(0xFF58A6FF);
                rCommon.setTextSize(9);
                rCommon.setMaxLines(1);
                rDetails.addView(rCommon);
            }

            relCard.addView(rDetails);

            relCard.setOnClickListener(v -> {
                if (dialog != null) dialog.dismiss();
                show(activity, rel);
            });

            row.addView(relCard);
        }

        scroll.addView(row);
        container.addView(scroll);
        container.addView(createDialogDivider(activity));
    }

    public static String findCommonActors(Movie a, Movie b) {
        if (a == null || b == null) return "";
        List<String> listA = a.getActorList();
        List<String> shared = new ArrayList<>();
        for (String act : listA) {
            if (b.hasActor(act)) {
                shared.add(act);
            }
        }
        if (shared.isEmpty() && !a.getDirector().isEmpty() && b.hasDirector(a.getDirector())) {
            shared.add("Regie: " + a.getDirector());
        }
        return shared.isEmpty() ? "" : shared.get(0);
    }

    public static View createTagPill(MainActivity activity, String text, boolean isAction, int actionColor, final Runnable onClick) {
        TextView chip = new TextView(activity);
        chip.setText(text);
        chip.setTextSize(11);
        chip.setPadding(activity.dp(9), activity.dp(5), activity.dp(9), activity.dp(5));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, activity.dp(6), 0);
        chip.setLayoutParams(lp);

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(activity.dp(12));
        if (isAction) {
            gd.setColor(actionColor != 0 ? actionColor : 0xFFDA3633);
            chip.setTextColor(0xFFFFFFFF);
            chip.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            gd.setColor(0xFF21262D);
            gd.setStroke(activity.dp(1), 0xFF30363D);
            chip.setTextColor(0xFFF0F6FC);
            chip.setTypeface(Typeface.DEFAULT);
        }
        chip.setBackground(gd);

        if (onClick != null) {
            chip.setOnClickListener(v -> onClick.run());
        }
        return chip;
    }

    public static View createDialogDivider(MainActivity activity) {
        View v = new View(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(1));
        lp.setMargins(0, activity.dp(12), 0, activity.dp(12));
        v.setLayoutParams(lp);
        v.setBackgroundColor(0xFF30363D);
        return v;
    }

    public static void shareMovieRecommendation(MainActivity activity, Movie movie) {
        if (activity == null || movie == null) return;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("🍿 Film-Tipp: *").append(movie.getTitle()).append("*");
            if (movie.getYear() > 0) {
                sb.append(" (").append(movie.getYear()).append(")");
            }
            sb.append("\n");
            sb.append("⭐ IMDb: ").append(movie.getImdbRating()).append("/10");
            if (movie.getRottenTomatoes() > 0) {
                sb.append(" • 🍅 Rotten Tomatoes: ").append(movie.getRottenTomatoes()).append("%");
            }
            sb.append("\n");

            if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
                sb.append("🎬 Regie: ").append(movie.getDirector()).append("\n");
            }
            if (movie.getActors() != null && !movie.getActors().isEmpty()) {
                sb.append("🎭 Mit: ").append(movie.getActors()).append("\n");
            }

            sb.append("📺 Jetzt verfügbar auf: ").append(movie.getProvider().getDisplayName());
            if (movie.getProvider().equals(StreamingProvider.YOUTUBE)) {
                sb.append(" (Kostenlos!)");
            }
            sb.append("\n\n");

            if (movie.getProvider().equals(StreamingProvider.YOUTUBE)) {
                String encoded = URLEncoder.encode(movie.getTitle() + " ganzer film deutsch", StandardCharsets.UTF_8.name());
                sb.append("👉 Direkt ansehen: https://www.youtube.com/results?search_query=").append(encoded).append("\n");
            } else {
                String encoded = URLEncoder.encode(movie.getTitle(), StandardCharsets.UTF_8.name());
                sb.append("👉 Info & Stream: https://www.justwatch.com/de/suche?q=").append(encoded).append("\n");
            }

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Film-Tipp: " + movie.getTitle());
            sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
            activity.startActivity(Intent.createChooser(sendIntent, "Film-Tipp teilen via..."));
        } catch (Exception e) {
            Toast.makeText(activity, "Konnte Teilen-Dialog nicht öffnen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void openMovieTrailer(MainActivity activity, Movie movie) {
        if (activity == null || movie == null) return;
        try {
            String query = movie.getTitle() + " " + (movie.getYear() > 0 ? movie.getYear() + " " : "") + "trailer deutsch";
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://results?search_query=" + encoded));
            intent.setPackage("com.google.android.youtube");
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
                return;
            }
        } catch (Exception ignored) {}
        try {
            String query = movie.getTitle() + " " + (movie.getYear() > 0 ? movie.getYear() + " " : "") + "trailer deutsch";
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name());
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + encoded));
            activity.startActivity(webIntent);
        } catch (Exception e) {
            Toast.makeText(activity, "Konnte Trailer nicht aufrufen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void showCompoundExclusionPicker(final MainActivity activity, final Movie movie, final String prefix, final String prefixDisplay, final Runnable onUpdate) {
        if (activity == null || movie == null || prefix == null || prefix.trim().isEmpty()) return;

        List<String> movieGenres = movie.getGenreList();
        final List<String> options = new ArrayList<>();
        if (!movieGenres.isEmpty()) {
            for (String g : movieGenres) {
                if (!options.contains(g)) options.add(g);
            }
        }
        for (String g : FilterDialogHelper.ALL_GENRES) {
            if (!options.contains(g)) options.add(g);
        }

        final String[] items = options.toArray(new String[0]);
        new AlertDialog.Builder(activity)
            .setTitle("⚡ Kombi-Ausschluss für " + prefixDisplay)
            .setItems(items, (dialog, which) -> {
                String targetGenre = items[which];
                String rule = prefix.trim() + ":" + targetGenre.trim();
                activity.criteria.compoundExclusions.add(rule);
                activity.criteria.save(activity);
                activity.refreshUi();
                activity.triggerLiveFetchIfNeeded();
                if (onUpdate != null) onUpdate.run();
                Toast.makeText(activity, "🚫 Kombi-Ausschluss aktiv: " + rule, Toast.LENGTH_LONG).show();
            })
            .setNegativeButton("Abbrechen", null)
            .show();
    }
}

