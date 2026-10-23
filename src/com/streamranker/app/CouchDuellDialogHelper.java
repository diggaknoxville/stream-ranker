package com.streamranker.app;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * USP 1: Couch-Duell (Showdown)
 * Behebt die Entscheidungslähmung ("Was gucken wir heute Abend?") durch
 * ein schnelles 1-gegen-1 Ausscheidungs-Turnier der besten Treffer.
 */
public final class CouchDuellDialogHelper {

    private CouchDuellDialogHelper() {}

    public static void show(final MainActivity activity) {
        if (activity == null || activity.isFinishing()) return;

        Set<StreamingProvider> activeProviders = activity.providerManager != null
            ? activity.providerManager.getActiveProviders()
            : Collections.emptySet();

        List<Movie> pool = activity.movieDatabase.getFilteredList(
            activity.criteria, activeProviders, activity.watchedManager
        );

        if (pool == null || pool.size() < 2) {
            Toast.makeText(activity, "⚔️ Mindestens 2 Filme im aktuellen Filter benötigt! Bitte Filter lockern.", Toast.LENGTH_LONG).show();
            return;
        }

        // Shuffle a subset for a fast tournament (4 or 8 movies)
        List<Movie> contestants = new ArrayList<>(pool);
        Collections.shuffle(contestants);
        int tournamentSize = 8;
        if (contestants.size() < 8) {
            tournamentSize = contestants.size() >= 4 ? 4 : 2;
        }
        contestants = new ArrayList<>(contestants.subList(0, tournamentSize));

        startTournament(activity, contestants);
    }

    private static void startTournament(final MainActivity activity, final List<Movie> initialContestants) {
        final List<Movie> currentRound = new ArrayList<>(initialContestants);
        final List<Movie> nextRound = new ArrayList<>();
        final int[] matchIndex = new int[]{0};

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        final FrameLayout container = new FrameLayout(activity);
        container.setBackgroundColor(0xFF0D1117);
        builder.setView(container);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        final Runnable[] renderHolder = new Runnable[1];
        renderHolder[0] = () -> {
            container.removeAllViews();

            // Check if tournament is finished
            if (currentRound.size() == 1) {
                renderWinner(activity, dialog, currentRound.get(0), initialContestants, container);
                return;
            }

            int pairIndex = matchIndex[0] * 2;
            if (pairIndex >= currentRound.size() - 1) {
                // Round completed, advance to next round
                currentRound.clear();
                currentRound.addAll(nextRound);
                nextRound.clear();
                matchIndex[0] = 0;
                renderHolder[0].run();
                return;
            }

            final Movie movieA = currentRound.get(pairIndex);
            final Movie movieB = currentRound.get(pairIndex + 1);
            int totalMatchesInRound = currentRound.size() / 2;
            int currentMatchNumber = matchIndex[0] + 1;

            String roundName = "⚔️ Runde";
            if (currentRound.size() == 8) roundName = "⚔️ Viertelfinale";
            else if (currentRound.size() == 4) roundName = "⚔️ Halbfinale";
            else if (currentRound.size() == 2) roundName = "🏆 Das Große Finale";

            ScrollView scroll = new ScrollView(activity);
            LinearLayout layout = new LinearLayout(activity);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(activity.dp(16), activity.dp(16), activity.dp(16), activity.dp(20));

            // Header Row
            TextView title = new TextView(activity);
            title.setText("⚔️ Couch-Duell: Was gucken wir?");
            title.setTextColor(0xFFF5C518);
            title.setTextSize(18);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setGravity(Gravity.CENTER);
            layout.addView(title);

            TextView subtitle = new TextView(activity);
            subtitle.setText(roundName + " • Match " + currentMatchNumber + " von " + totalMatchesInRound);
            subtitle.setTextColor(0xFF58A6FF);
            subtitle.setTextSize(13);
            subtitle.setTypeface(Typeface.DEFAULT_BOLD);
            subtitle.setGravity(Gravity.CENTER);
            subtitle.setPadding(0, activity.dp(4), 0, activity.dp(12));
            layout.addView(subtitle);

            TextView hint = new TextView(activity);
            hint.setText("Tippe auf den Film, den ihr heute Abend eher sehen wollt:");
            hint.setTextColor(0xFF8B949E);
            hint.setTextSize(12);
            hint.setGravity(Gravity.CENTER);
            hint.setPadding(0, 0, 0, activity.dp(12));
            layout.addView(hint);

            // Movie Card A
            View cardA = buildDuellCard(activity, movieA, () -> {
                nextRound.add(movieA);
                matchIndex[0]++;
                renderHolder[0].run();
            });
            layout.addView(cardA);

            // VS Divider
            LinearLayout vsRow = new LinearLayout(activity);
            vsRow.setOrientation(LinearLayout.HORIZONTAL);
            vsRow.setGravity(Gravity.CENTER);
            vsRow.setPadding(0, activity.dp(10), 0, activity.dp(10));

            TextView vsText = new TextView(activity);
            vsText.setText("⚡ VS ⚡");
            vsText.setTextColor(0xFFFA7D00);
            vsText.setTextSize(16);
            vsText.setTypeface(Typeface.DEFAULT_BOLD);
            vsRow.addView(vsText);
            layout.addView(vsRow);

            // Movie Card B
            View cardB = buildDuellCard(activity, movieB, () -> {
                nextRound.add(movieB);
                matchIndex[0]++;
                renderHolder[0].run();
            });
            layout.addView(cardB);

            // Cancel / Dismiss button
            Button cancelBtn = new Button(activity);
            cancelBtn.setText("Duell abbrechen");
            cancelBtn.setTextColor(0xFF8B949E);
            cancelBtn.setTextSize(11);
            cancelBtn.setBackgroundResource(R.drawable.btn_generic);
            LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(36));
            cbLp.setMargins(0, activity.dp(14), 0, 0);
            cancelBtn.setLayoutParams(cbLp);
            cancelBtn.setOnClickListener(v -> dialog.dismiss());
            layout.addView(cancelBtn);

            scroll.addView(layout);
            container.addView(scroll);
        };

        renderHolder[0].run();
        dialog.show();
    }

    private static View buildDuellCard(final MainActivity activity, final Movie movie, final Runnable onSelect) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(activity.dp(12), activity.dp(10), activity.dp(12), activity.dp(10));

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(activity.dp(12));
        gd.setColor(0xFF161B22);
        gd.setStroke(activity.dp(1), 0xFF30363D);
        card.setBackground(gd);

        // Poster
        ImageView poster = new ImageView(activity);
        LinearLayout.LayoutParams pLp = new LinearLayout.LayoutParams(activity.dp(70), activity.dp(105));
        pLp.setMargins(0, 0, activity.dp(12), 0);
        poster.setLayoutParams(pLp);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        AsyncImageLoader.getInstance(activity).load(poster, movie.getPosterUrl(), movie.getTitle());
        card.addView(poster);

        // Info Column
        LinearLayout info = new LinearLayout(activity);
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams iLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        info.setLayoutParams(iLp);

        TextView title = new TextView(activity);
        title.setText(movie.getTitle());
        title.setTextColor(0xFFF0F6FC);
        title.setTextSize(14);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setMaxLines(2);
        info.addView(title);

        TextView meta = new TextView(activity);
        String metaStr = (movie.getYear() > 0 ? movie.getYear() + " • " : "") +
            (movie.getRuntimeMinutes() > 0 ? movie.getRuntimeMinutes() + " Min" : "");
        meta.setText(metaStr);
        meta.setTextColor(0xFF8B949E);
        meta.setTextSize(11);
        meta.setPadding(0, activity.dp(2), 0, activity.dp(2));
        info.addView(meta);

        // Rating and Provider Row
        LinearLayout statsRow = new LinearLayout(activity);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setGravity(Gravity.CENTER_VERTICAL);
        statsRow.setPadding(0, activity.dp(2), 0, activity.dp(6));

        TextView imdbView = new TextView(activity);
        imdbView.setText("⭐ " + (movie.getImdbRating() > 0 ? movie.getImdbRating() : "—"));
        imdbView.setTextColor(0xFFF5C518);
        imdbView.setTextSize(11);
        imdbView.setTypeface(Typeface.DEFAULT_BOLD);
        imdbView.setPadding(0, 0, activity.dp(8), 0);
        statsRow.addView(imdbView);

        if (movie.getRottenTomatoes() > 0) {
            TextView rtView = new TextView(activity);
            rtView.setText("🍅 " + movie.getRottenTomatoes() + "%");
            rtView.setTextColor(0xFFFA320A);
            rtView.setTextSize(11);
            rtView.setTypeface(Typeface.DEFAULT_BOLD);
            rtView.setPadding(0, 0, activity.dp(8), 0);
            statsRow.addView(rtView);
        }

        TextView provBadge = new TextView(activity);
        provBadge.setText(movie.getProvider().getDisplayName());
        provBadge.setTextColor(0xFFFFFFFF);
        provBadge.setTextSize(10);
        provBadge.setTypeface(Typeface.DEFAULT_BOLD);
        provBadge.setPadding(activity.dp(6), activity.dp(2), activity.dp(6), activity.dp(2));
        GradientDrawable pGd = new GradientDrawable();
        pGd.setCornerRadius(activity.dp(4));
        pGd.setColor(movie.getProvider().getColor());
        provBadge.setBackground(pGd);
        statsRow.addView(provBadge);
        info.addView(statsRow);

        // Pick Button
        Button pickBtn = new Button(activity);
        pickBtn.setText("👉 Diesen wählen");
        pickBtn.setTextColor(0xFFFFFFFF);
        pickBtn.setTextSize(12);
        pickBtn.setTypeface(Typeface.DEFAULT_BOLD);
        pickBtn.setTransformationMethod(null);
        GradientDrawable bGd = new GradientDrawable();
        bGd.setCornerRadius(activity.dp(6));
        bGd.setColor(0xFF238636); // Github Green
        pickBtn.setBackground(bGd);
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(34));
        pickBtn.setLayoutParams(pbLp);
        pickBtn.setOnClickListener(v -> onSelect.run());
        info.addView(pickBtn);

        card.addView(info);
        card.setOnClickListener(v -> onSelect.run());
        return card;
    }

    private static void renderWinner(final MainActivity activity, final AlertDialog dialog, final Movie winner, final List<Movie> initialContestants, final FrameLayout container) {
        ScrollView scroll = new ScrollView(activity);
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(activity.dp(20), activity.dp(20), activity.dp(20), activity.dp(24));

        TextView trophy = new TextView(activity);
        trophy.setText("🏆");
        trophy.setTextSize(44);
        trophy.setGravity(Gravity.CENTER);
        layout.addView(trophy);

        TextView header = new TextView(activity);
        header.setText("Euer Film für heute Abend steht fest!");
        header.setTextColor(0xFFF5C518);
        header.setTextSize(18);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setGravity(Gravity.CENTER);
        header.setPadding(0, activity.dp(4), 0, activity.dp(4));
        layout.addView(header);

        TextView sub = new TextView(activity);
        sub.setText("Einstimmig im Couch-Duell gekürt:");
        sub.setTextColor(0xFF8B949E);
        sub.setTextSize(12);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 0, 0, activity.dp(14));
        layout.addView(sub);

        // Winner Poster
        ImageView poster = new ImageView(activity);
        LinearLayout.LayoutParams pLp = new LinearLayout.LayoutParams(activity.dp(150), activity.dp(225));
        pLp.setMargins(0, 0, 0, activity.dp(12));
        poster.setLayoutParams(pLp);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        AsyncImageLoader.getInstance(activity).load(poster, winner.getPosterUrl(), winner.getTitle());
        layout.addView(poster);

        // Title
        TextView title = new TextView(activity);
        title.setText(winner.getTitle());
        title.setTextColor(0xFFFFFFFF);
        title.setTextSize(19);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        // Meta info
        TextView meta = new TextView(activity);
        String metaText = (winner.getYear() > 0 ? String.valueOf(winner.getYear()) : "") +
            (winner.getRuntimeMinutes() > 0 ? " • " + winner.getRuntimeMinutes() + " Min" : "") +
            (winner.getGenres() != null && !winner.getGenres().isEmpty() ? " • " + winner.getGenres() : "");
        meta.setText(metaText);
        meta.setTextColor(0xFF8B949E);
        meta.setTextSize(12);
        meta.setGravity(Gravity.CENTER);
        meta.setPadding(0, activity.dp(4), 0, activity.dp(8));
        layout.addView(meta);

        // Stats Row
        LinearLayout stats = new LinearLayout(activity);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        stats.setGravity(Gravity.CENTER);
        stats.setPadding(0, 0, 0, activity.dp(12));

        TextView imdb = new TextView(activity);
        imdb.setText("⭐ IMDb: " + (winner.getImdbRating() > 0 ? winner.getImdbRating() : "—"));
        imdb.setTextColor(0xFFF5C518);
        imdb.setTextSize(13);
        imdb.setTypeface(Typeface.DEFAULT_BOLD);
        imdb.setPadding(0, 0, activity.dp(12), 0);
        stats.addView(imdb);

        if (winner.getRottenTomatoes() > 0) {
            TextView rt = new TextView(activity);
            rt.setText("🍅 Rotten: " + winner.getRottenTomatoes() + "%");
            rt.setTextColor(0xFFFA320A);
            rt.setTextSize(13);
            rt.setTypeface(Typeface.DEFAULT_BOLD);
            rt.setPadding(0, 0, activity.dp(12), 0);
            stats.addView(rt);
        }

        TextView prov = new TextView(activity);
        prov.setText("📺 " + winner.getProvider().getDisplayName());
        prov.setTextColor(0xFFFFFFFF);
        prov.setTextSize(12);
        prov.setTypeface(Typeface.DEFAULT_BOLD);
        prov.setPadding(activity.dp(8), activity.dp(3), activity.dp(8), activity.dp(3));
        GradientDrawable prGd = new GradientDrawable();
        prGd.setCornerRadius(activity.dp(4));
        prGd.setColor(winner.getProvider().getColor());
        prov.setBackground(prGd);
        stats.addView(prov);
        layout.addView(stats);

        // Short plot synopsis
        if (winner.getOverview() != null && !winner.getOverview().trim().isEmpty()) {
            TextView plot = new TextView(activity);
            plot.setText(winner.getOverview());
            plot.setTextColor(0xFFC9D1D9);
            plot.setTextSize(12);
            plot.setMaxLines(4);
            plot.setGravity(Gravity.CENTER);
            plot.setPadding(activity.dp(8), 0, activity.dp(8), activity.dp(14));
            layout.addView(plot);
        }

        // Action Buttons
        // 1. Direct Stream Launch
        Button launchBtn = new Button(activity);
        launchBtn.setText("▶ Jetzt auf " + winner.getProvider().getDisplayName() + " ansehen");
        launchBtn.setTextColor(0xFFFFFFFF);
        launchBtn.setTextSize(13);
        launchBtn.setTypeface(Typeface.DEFAULT_BOLD);
        launchBtn.setTransformationMethod(null);
        GradientDrawable lGd = new GradientDrawable();
        lGd.setCornerRadius(activity.dp(8));
        lGd.setColor(0xFF238636); // Github Green
        launchBtn.setBackground(lGd);
        LinearLayout.LayoutParams lmLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(44));
        lmLp.setMargins(0, activity.dp(4), 0, activity.dp(6));
        launchBtn.setLayoutParams(lmLp);
        launchBtn.setOnClickListener(v -> {
            dialog.dismiss();
            winner.getProvider().launchTitle(activity, winner.getDeepLinkId(), winner.getTitle());
        });
        layout.addView(launchBtn);

        // 2. Official Trailer
        Button trailerBtn = new Button(activity);
        trailerBtn.setText("🎬 Trailer (Deutsch) ansehen");
        trailerBtn.setTextColor(0xFFF0F6FC);
        trailerBtn.setTextSize(12);
        trailerBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams tbLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(40));
        tbLp.setMargins(0, 0, 0, activity.dp(6));
        trailerBtn.setLayoutParams(tbLp);
        trailerBtn.setOnClickListener(v -> MovieDetailDialogHelper.openMovieTrailer(activity, winner));
        layout.addView(trailerBtn);

        // 3. Share with partner
        Button shareBtn = new Button(activity);
        shareBtn.setText("📤 Gewinner an Couch-Partner senden");
        shareBtn.setTextColor(0xFF58A6FF);
        shareBtn.setTextSize(12);
        shareBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams sbLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(40));
        sbLp.setMargins(0, 0, 0, activity.dp(6));
        shareBtn.setLayoutParams(sbLp);
        shareBtn.setOnClickListener(v -> MovieDetailDialogHelper.shareMovieRecommendation(activity, winner));
        layout.addView(shareBtn);

        // 4. Restart tournament
        Button restartBtn = new Button(activity);
        restartBtn.setText("🔄 Neues Couch-Duell starten");
        restartBtn.setTextColor(0xFFFA7D00);
        restartBtn.setTextSize(12);
        restartBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams rbLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(40));
        rbLp.setMargins(0, 0, 0, activity.dp(6));
        restartBtn.setLayoutParams(rbLp);
        restartBtn.setOnClickListener(v -> {
            dialog.dismiss();
            show(activity);
        });
        layout.addView(restartBtn);

        // 5. Close
        Button closeBtn = new Button(activity);
        closeBtn.setText("Fertig");
        closeBtn.setTextColor(0xFF8B949E);
        closeBtn.setTextSize(12);
        closeBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(36));
        closeBtn.setLayoutParams(cbLp);
        closeBtn.setOnClickListener(v -> dialog.dismiss());
        layout.addView(closeBtn);

        scroll.addView(layout);
        container.addView(scroll);
    }
}

