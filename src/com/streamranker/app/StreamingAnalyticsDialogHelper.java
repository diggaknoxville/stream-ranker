package com.streamranker.app;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * USP 2: Streaming-Bilanz & Abo-Spar-Berater
 * Analysiert den wahren Nutzen aller Streaming-Dienste objektiv und unbestechlich:
 * Welche Abos lohnen sich noch, wo sind alle Perlen weggeschaut,
 * und welche Abos können pausiert werden, um bares Geld zu sparen.
 */
public final class StreamingAnalyticsDialogHelper {

    private StreamingAnalyticsDialogHelper() {}

    private static class ProviderStats {
        final StreamingProvider provider;
        boolean isActive;
        int totalMovies = 0;
        int topTierMovies = 0; // IMDb >= 7.5 or RT >= 80%
        int watchedMovies = 0;
        int watchlistMovies = 0;
        double avgImdb = 0.0;
        double totalImdbSum = 0.0;

        ProviderStats(StreamingProvider provider, boolean isActive) {
            this.provider = provider;
            this.isActive = isActive;
        }

        int getUnseenTopTier() {
            return Math.max(0, topTierMovies - watchedMovies);
        }

        int getCompletionPercentage() {
            if (totalMovies == 0) return 0;
            return Math.min(100, (int) Math.round((watchedMovies * 100.0) / totalMovies));
        }
    }

    public static void show(final MainActivity activity) {
        if (activity == null || activity.isFinishing()) return;

        final ProviderManager pm = activity.providerManager;
        final WatchedManager wm = activity.watchedManager;
        final WatchlistManager wlm = activity.watchlistManager;
        final List<Movie> allMovies = activity.movieDatabase.getAllMovies();

        // 1. Calculate statistics per provider
        final Map<String, ProviderStats> statsMap = new HashMap<>();
        for (StreamingProvider sp : StreamingProvider.getAll()) {
            boolean active = pm != null && pm.isActive(sp);
            statsMap.put(sp.getId().toLowerCase(), new ProviderStats(sp, active));
        }

        int globalTotalMovies = 0;
        int globalWatchedMovies = 0;
        int globalWatchlistMovies = 0;

        for (Movie m : allMovies) {
            if (m == null || m.getProvider() == null) continue;
            String pid = m.getProvider().getId().toLowerCase();
            ProviderStats ps = statsMap.get(pid);
            if (ps == null) {
                ps = new ProviderStats(m.getProvider(), pm != null && pm.isActive(m.getProvider()));
                statsMap.put(pid, ps);
            }

            ps.totalMovies++;
            globalTotalMovies++;

            if (m.getImdbRating() >= 7.5 || m.getRottenTomatoes() >= 80) {
                ps.topTierMovies++;
            }

            if (m.getImdbRating() > 0) {
                ps.totalImdbSum += m.getImdbRating();
            }

            boolean isWatched = wm != null && wm.isWatched(m.getId(), m.getTitle());
            if (isWatched) {
                ps.watchedMovies++;
                globalWatchedMovies++;
            }

            boolean isWatchlisted = wlm != null && (wlm.isWatchlisted(m.getId()) || wlm.isWatchlisted(m.getTitle()));
            if (isWatchlisted) {
                ps.watchlistMovies++;
                globalWatchlistMovies++;
            }
        }

        for (ProviderStats ps : statsMap.values()) {
            if (ps.totalMovies > 0) {
                ps.avgImdb = ps.totalImdbSum / ps.totalMovies;
            }
        }

        // 2. Build the UI
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        final FrameLayout container = new FrameLayout(activity);
        container.setBackgroundColor(0xFF0D1117);
        builder.setView(container);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ScrollView scroll = new ScrollView(activity);
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(activity.dp(16), activity.dp(16), activity.dp(16), activity.dp(24));

        // Header Title
        TextView title = new TextView(activity);
        title.setText("📊 Streaming-Bilanz & Abo-Spar-Berater");
        title.setTextColor(0xFFF5C518);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(title);

        TextView sub = new TextView(activity);
        sub.setText("Objektive Nutzungsanalyse • JustWatch verschweigt Einsparpotenziale");
        sub.setTextColor(0xFF8B949E);
        sub.setTextSize(11);
        sub.setPadding(0, activity.dp(2), 0, activity.dp(12));
        layout.addView(sub);

        // Global Overview KPI Cards
        LinearLayout kpiRow = new LinearLayout(activity);
        kpiRow.setOrientation(LinearLayout.HORIZONTAL);
        kpiRow.setPadding(0, 0, 0, activity.dp(12));

        int activeCount = 0;
        for (ProviderStats ps : statsMap.values()) {
            if (ps.isActive) activeCount++;
        }

        kpiRow.addView(createKpiCard(activity, "Aktive Abos", String.valueOf(activeCount), 0xFF58A6FF));
        kpiRow.addView(createKpiCard(activity, "Gesehen", String.valueOf(globalWatchedMovies), 0xFF2EA043));
        kpiRow.addView(createKpiCard(activity, "Watchlist", String.valueOf(globalWatchlistMovies), 0xFFF5C518));
        kpiRow.addView(createKpiCard(activity, "Top-Katalog", String.valueOf(globalTotalMovies), 0xFF8957E5));
        layout.addView(kpiRow);

        // Smart Recommendation Engine (Abo-Spar-Tipps)
        TextView adviceHeader = new TextView(activity);
        adviceHeader.setText("💡 Konkrete Spar-Tipps & Abo-Empfehlungen:");
        adviceHeader.setTextColor(0xFF58A6FF);
        adviceHeader.setTextSize(14);
        adviceHeader.setTypeface(Typeface.DEFAULT_BOLD);
        adviceHeader.setPadding(0, activity.dp(6), 0, activity.dp(6));
        layout.addView(adviceHeader);

        LinearLayout adviceBox = new LinearLayout(activity);
        adviceBox.setOrientation(LinearLayout.VERTICAL);
        adviceBox.setPadding(activity.dp(12), activity.dp(10), activity.dp(12), activity.dp(10));
        GradientDrawable abGd = new GradientDrawable();
        abGd.setCornerRadius(activity.dp(10));
        abGd.setColor(0xFF161B22);
        abGd.setStroke(activity.dp(1), 0xFF30363D);
        adviceBox.setBackground(abGd);

        // Detect potential cancellation / pause candidates
        List<String> recommendations = new ArrayList<>();
        ProviderStats bestValueProvider = null;
        int bestRemaining = -1;

        for (ProviderStats ps : statsMap.values()) {
            if (!ps.isActive) continue;
            boolean isPaid = !ps.provider.isFreeMediathek();

            if (isPaid) {
                // If user watched a high percentage or has 0 unviewed watchlist titles
                if (ps.watchedMovies >= 10 && ps.getCompletionPercentage() >= 35 && ps.watchlistMovies <= 1) {
                    recommendations.add("💰 " + ps.provider.getDisplayName() + ": Du hast bereits " + ps.watchedMovies +
                        " Filme (" + ps.getCompletionPercentage() + "%) gesehen und kaum noch Watchlist-Titel. " +
                        "Tipp: Abo für 1-2 Monate pausieren und ~12 € sparen!");
                }
                if (ps.getUnseenTopTier() > bestRemaining) {
                    bestRemaining = ps.getUnseenTopTier();
                    bestValueProvider = ps;
                }
            }
        }

        if (bestValueProvider != null) {
            recommendations.add("🔥 Größter Mehrwert: Auf " + bestValueProvider.provider.getDisplayName() +
                " warten noch " + bestValueProvider.getUnseenTopTier() + " ungesehene Spitzenfilme und " +
                bestValueProvider.watchlistMovies + " Watchlist-Treffer auf dich.");
        }

        // Mediatheken Bonus check
        int mediathekTotal = 0;
        int mediathekTop = 0;
        for (ProviderStats ps : statsMap.values()) {
            if (ps.provider.isFreeMediathek()) {
                mediathekTotal += ps.totalMovies;
                mediathekTop += ps.topTierMovies;
            }
        }
        if (mediathekTotal > 0) {
            recommendations.add("🆓 Mediatheken-Bonus: In ARD, ZDF und Arte Mediathek stehen " +
                mediathekTop + " hochkarätige Filme (IMDb ≥ 7.5) völlig kostenlos & werbefrei ohne Zusatzabo bereit!");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("👍 Dein Streaming-Mix ist ausgewogen. Nutze deine aktiven Abos optimal aus, indem du ungesehene Perlen auf deine Watchlist setzt.");
        }

        for (String rec : recommendations) {
            TextView rTv = new TextView(activity);
            rTv.setText(rec);
            rTv.setTextColor(0xFFF0F6FC);
            rTv.setTextSize(12);
            rTv.setPadding(0, activity.dp(3), 0, activity.dp(5));
            adviceBox.addView(rTv);
        }
        layout.addView(adviceBox);

        // Per-Provider Detailed Breakdown
        TextView listHeader = new TextView(activity);
        listHeader.setText("📺 Streaming-Dienste im Detail:");
        listHeader.setTextColor(0xFFF5C518);
        listHeader.setTextSize(14);
        listHeader.setTypeface(Typeface.DEFAULT_BOLD);
        listHeader.setPadding(0, activity.dp(16), 0, activity.dp(8));
        layout.addView(listHeader);

        List<ProviderStats> sortedStats = new ArrayList<>(statsMap.values());
        Collections.sort(sortedStats, new Comparator<ProviderStats>() {
            @Override public int compare(ProviderStats a, ProviderStats b) {
                if (a.isActive != b.isActive) return a.isActive ? -1 : 1;
                return Integer.compare(b.totalMovies, a.totalMovies);
            }
        });

        for (final ProviderStats ps : sortedStats) {
            View card = buildProviderCard(activity, dialog, ps);
            layout.addView(card);
        }

        // Close Button
        Button closeBtn = new Button(activity);
        closeBtn.setText("Schließen");
        closeBtn.setTextColor(0xFFF0F6FC);
        closeBtn.setTextSize(12);
        closeBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(40));
        cbLp.setMargins(0, activity.dp(16), 0, 0);
        closeBtn.setLayoutParams(cbLp);
        closeBtn.setOnClickListener(v -> dialog.dismiss());
        layout.addView(closeBtn);

        scroll.addView(layout);
        container.addView(scroll);
        dialog.show();
    }

    private static View createKpiCard(MainActivity activity, String label, String value, int accentColor) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(activity.dp(6), activity.dp(8), activity.dp(6), activity.dp(8));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        lp.setMargins(activity.dp(2), 0, activity.dp(2), 0);
        card.setLayoutParams(lp);

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(activity.dp(8));
        gd.setColor(0xFF161B22);
        gd.setStroke(activity.dp(1), 0xFF30363D);
        card.setBackground(gd);

        TextView valTv = new TextView(activity);
        valTv.setText(value);
        valTv.setTextColor(accentColor);
        valTv.setTextSize(16);
        valTv.setTypeface(Typeface.DEFAULT_BOLD);
        valTv.setGravity(Gravity.CENTER);
        card.addView(valTv);

        TextView lblTv = new TextView(activity);
        lblTv.setText(label);
        lblTv.setTextColor(0xFF8B949E);
        lblTv.setTextSize(10);
        lblTv.setGravity(Gravity.CENTER);
        card.addView(lblTv);

        return card;
    }

    private static View buildProviderCard(final MainActivity activity, final AlertDialog dialog, final ProviderStats ps) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(activity.dp(12), activity.dp(10), activity.dp(12), activity.dp(10));

        LinearLayout.LayoutParams cLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cLp.setMargins(0, 0, 0, activity.dp(8));
        card.setLayoutParams(cLp);

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(activity.dp(8));
        gd.setColor(0xFF161B22);
        gd.setStroke(activity.dp(1), ps.isActive ? 0xFF30363D : 0xFF21262D);
        card.setBackground(gd);

        // Header Row: Provider Badge + Active Switch Status
        LinearLayout headRow = new LinearLayout(activity);
        headRow.setOrientation(LinearLayout.HORIZONTAL);
        headRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView pBadge = new TextView(activity);
        pBadge.setText(ps.provider.getDisplayName());
        pBadge.setTextColor(0xFFFFFFFF);
        pBadge.setTextSize(11);
        pBadge.setTypeface(Typeface.DEFAULT_BOLD);
        pBadge.setPadding(activity.dp(8), activity.dp(3), activity.dp(8), activity.dp(3));
        GradientDrawable bGd = new GradientDrawable();
        bGd.setCornerRadius(activity.dp(4));
        bGd.setColor(ps.provider.getColor());
        pBadge.setBackground(bGd);
        headRow.addView(pBadge);

        TextView statusTv = new TextView(activity);
        statusTv.setText(ps.isActive ? "  ✓ Aktiv im Pool" : "  ○ Inaktiv");
        statusTv.setTextColor(ps.isActive ? 0xFF2EA043 : 0xFF8B949E);
        statusTv.setTextSize(11);
        statusTv.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams sLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        statusTv.setLayoutParams(sLp);
        headRow.addView(statusTv);

        TextView totalTv = new TextView(activity);
        totalTv.setText(ps.totalMovies + " Filme");
        totalTv.setTextColor(0xFF8B949E);
        totalTv.setTextSize(11);
        headRow.addView(totalTv);

        card.addView(headRow);

        // Metrics breakdown row
        LinearLayout metricsRow = new LinearLayout(activity);
        metricsRow.setOrientation(LinearLayout.HORIZONTAL);
        metricsRow.setPadding(0, activity.dp(6), 0, activity.dp(4));

        TextView topTierTv = new TextView(activity);
        topTierTv.setText("⭐ Top-Perlen: " + ps.topTierMovies);
        topTierTv.setTextColor(0xFFF5C518);
        topTierTv.setTextSize(11);
        topTierTv.setPadding(0, 0, activity.dp(10), 0);
        metricsRow.addView(topTierTv);

        TextView wlTv = new TextView(activity);
        wlTv.setText("⭐ Watchlist: " + ps.watchlistMovies);
        wlTv.setTextColor(0xFF58A6FF);
        wlTv.setTextSize(11);
        wlTv.setPadding(0, 0, activity.dp(10), 0);
        metricsRow.addView(wlTv);

        TextView seenTv = new TextView(activity);
        seenTv.setText("👁️ Gesehen: " + ps.watchedMovies + " (" + ps.getCompletionPercentage() + "%)");
        seenTv.setTextColor(0xFF2EA043);
        seenTv.setTextSize(11);
        metricsRow.addView(seenTv);

        card.addView(metricsRow);

        // Progress bar for completion
        ProgressBar pb = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax(100);
        pb.setProgress(ps.getCompletionPercentage());
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(6));
        pbLp.setMargins(0, activity.dp(2), 0, activity.dp(8));
        pb.setLayoutParams(pbLp);
        card.addView(pb);

        // Action Buttons Row: Filter by this provider or toggle subscription
        LinearLayout actRow = new LinearLayout(activity);
        actRow.setOrientation(LinearLayout.HORIZONTAL);

        Button showMoviesBtn = new Button(activity);
        showMoviesBtn.setText("Filme anzeigen (" + ps.totalMovies + ")");
        showMoviesBtn.setTextColor(0xFFF0F6FC);
        showMoviesBtn.setTextSize(11);
        showMoviesBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams smLp = new LinearLayout.LayoutParams(0, activity.dp(34), 1.0f);
        smLp.setMargins(0, 0, activity.dp(4), 0);
        showMoviesBtn.setLayoutParams(smLp);
        showMoviesBtn.setOnClickListener(v -> {
            dialog.dismiss();
            activity.criteria.singleProviderId = ps.provider.getId();
            activity.criteria.save(activity);
            activity.refreshUi();
            activity.triggerLiveFetchIfNeeded();
            Toast.makeText(activity, "Filter gesetzt auf: " + ps.provider.getDisplayName(), Toast.LENGTH_SHORT).show();
        });
        actRow.addView(showMoviesBtn);

        Button toggleSubBtn = new Button(activity);
        toggleSubBtn.setText(ps.isActive ? "Abo deaktivieren" : "Abo aktivieren");
        toggleSubBtn.setTextColor(ps.isActive ? 0xFFDA3633 : 0xFF2EA043);
        toggleSubBtn.setTextSize(11);
        toggleSubBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams tsLp = new LinearLayout.LayoutParams(0, activity.dp(34), 1.0f);
        tsLp.setMargins(activity.dp(4), 0, 0, 0);
        toggleSubBtn.setLayoutParams(tsLp);
        toggleSubBtn.setOnClickListener(v -> {
            if (activity.providerManager != null) {
                activity.providerManager.toggleActive(activity, ps.provider);
                dialog.dismiss();
                show(activity);
                activity.refreshUi();
            }
        });
        actRow.addView(toggleSubBtn);

        card.addView(actRow);
        return card;
    }
}
