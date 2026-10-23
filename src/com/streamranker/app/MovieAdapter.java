package com.streamranker.app;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * High-performance recycled adapter for displaying movie cards in a ListView,
 * utilizing the ViewHolder pattern to eliminate memory churn and UI stutter.
 */
public class MovieAdapter extends BaseAdapter {

    private final MainActivity activity;
    private final List<Movie> movies = new ArrayList<>();

    public MovieAdapter(MainActivity activity) {
        this.activity = activity;
    }

    public void setMovies(List<Movie> newMovies) {
        movies.clear();
        if (newMovies != null) {
            movies.addAll(newMovies);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieViewHolder holder;
        if (convertView == null) {
            holder = createViewHolder();
            convertView = holder.card;
            convertView.setTag(holder);
        } else {
            holder = (MovieViewHolder) convertView.getTag();
        }

        final Movie movie = getItem(position);
        final int rank = position + 1;
        bindViewHolder(holder, movie, rank);

        return convertView;
    }

    private MovieViewHolder createViewHolder() {
        MovieViewHolder holder = new MovieViewHolder();

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_movie);
        card.setPadding(activity.dp(14), activity.dp(14), activity.dp(14), activity.dp(14));

        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(0, 0, 0, activity.dp(12));
        card.setLayoutParams(cardLp);
        holder.card = card;

        LinearLayout topRow = new LinearLayout(activity);
        topRow.setOrientation(LinearLayout.HORIZONTAL);

        // Poster
        ImageView poster = new ImageView(activity);
        LinearLayout.LayoutParams posterLp = new LinearLayout.LayoutParams(activity.dp(85), activity.dp(125));
        poster.setLayoutParams(posterLp);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        topRow.addView(poster);
        holder.poster = poster;

        // Details Column
        LinearLayout details = new LinearLayout(activity);
        details.setOrientation(LinearLayout.VERTICAL);
        details.setPadding(activity.dp(12), 0, 0, 0);
        LinearLayout.LayoutParams detailsLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        details.setLayoutParams(detailsLp);

        // Title Row
        LinearLayout titleRow = new LinearLayout(activity);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        holder.titleRow = titleRow;

        TextView rankBadge = new TextView(activity);
        rankBadge.setTextSize(11);
        rankBadge.setTypeface(Typeface.DEFAULT_BOLD);
        rankBadge.setPadding(activity.dp(6), activity.dp(2), activity.dp(6), activity.dp(2));
        titleRow.addView(rankBadge);
        holder.rankBadge = rankBadge;

        TextView title = new TextView(activity);
        title.setTextColor(0xFFF0F6FC);
        title.setTextSize(15);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        titleRow.addView(title);
        holder.title = title;

        details.addView(titleRow);

        // Meta (Year, Runtime, Genres)
        TextView meta = new TextView(activity);
        meta.setTextColor(0xFF8B949E);
        meta.setTextSize(11);
        meta.setPadding(0, activity.dp(3), 0, activity.dp(4));
        details.addView(meta);
        holder.meta = meta;

        // Scores: IMDb + Rotten Tomatoes + Perlen-Score
        LinearLayout scoresRow = new LinearLayout(activity);
        scoresRow.setOrientation(LinearLayout.HORIZONTAL);
        scoresRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView imdbScore = new TextView(activity);
        imdbScore.setTextColor(0xFF000000);
        imdbScore.setTextSize(11);
        imdbScore.setTypeface(Typeface.DEFAULT_BOLD);
        imdbScore.setBackgroundResource(R.drawable.badge_imdb);
        imdbScore.setPadding(activity.dp(5), activity.dp(2), activity.dp(5), activity.dp(2));
        scoresRow.addView(imdbScore);
        holder.imdbScore = imdbScore;

        TextView rtScore = new TextView(activity);
        rtScore.setTextColor(0xFFFFFFFF);
        rtScore.setTextSize(11);
        rtScore.setTypeface(Typeface.DEFAULT_BOLD);
        rtScore.setBackgroundResource(R.drawable.badge_rt);
        rtScore.setPadding(activity.dp(5), activity.dp(2), activity.dp(5), activity.dp(2));
        LinearLayout.LayoutParams rtLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rtLp.setMargins(activity.dp(4), 0, 0, 0);
        rtScore.setLayoutParams(rtLp);
        scoresRow.addView(rtScore);
        holder.rtScore = rtScore;

        TextView pearlScore = new TextView(activity);
        pearlScore.setTextColor(0xFF000000);
        pearlScore.setTextSize(11);
        pearlScore.setTypeface(Typeface.DEFAULT_BOLD);
        pearlScore.setBackgroundResource(R.drawable.badge_perle);
        pearlScore.setPadding(activity.dp(5), activity.dp(2), activity.dp(5), activity.dp(2));
        LinearLayout.LayoutParams pearlLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pearlLp.setMargins(activity.dp(4), 0, 0, 0);
        pearlScore.setLayoutParams(pearlLp);
        scoresRow.addView(pearlScore);
        holder.pearlScore = pearlScore;

        TextView fskBadge = new TextView(activity);
        fskBadge.setTextSize(10);
        fskBadge.setTypeface(Typeface.DEFAULT_BOLD);
        fskBadge.setPadding(activity.dp(4), activity.dp(2), activity.dp(4), activity.dp(2));
        LinearLayout.LayoutParams fskLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fskLp.setMargins(activity.dp(4), 0, 0, 0);
        fskBadge.setLayoutParams(fskLp);
        scoresRow.addView(fskBadge);
        holder.fskBadge = fskBadge;

        details.addView(scoresRow);

        // Votes & Watchlist
        LinearLayout extraRow = new LinearLayout(activity);
        extraRow.setOrientation(LinearLayout.HORIZONTAL);
        extraRow.setGravity(Gravity.CENTER_VERTICAL);
        extraRow.setPadding(0, activity.dp(3), 0, 0);

        TextView votes = new TextView(activity);
        votes.setTextColor(0xFF8B949E);
        votes.setTextSize(10);
        extraRow.addView(votes);
        holder.votes = votes;

        TextView wlTag = new TextView(activity);
        wlTag.setText(" • ⭐ Auf Watchlist");
        wlTag.setTextColor(0xFF2EA043);
        wlTag.setTextSize(10);
        wlTag.setTypeface(Typeface.DEFAULT_BOLD);
        extraRow.addView(wlTag);
        holder.wlTag = wlTag;

        details.addView(extraRow);

        // Leaving Soon Badge
        TextView leavingNotice = new TextView(activity);
        leavingNotice.setTextColor(0xFFFFFFFF);
        leavingNotice.setTextSize(10);
        leavingNotice.setTypeface(Typeface.DEFAULT_BOLD);
        leavingNotice.setBackgroundResource(R.drawable.badge_leaving);
        leavingNotice.setPadding(activity.dp(6), activity.dp(3), activity.dp(6), activity.dp(3));
        LinearLayout.LayoutParams noticeLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        noticeLp.setMargins(0, activity.dp(4), 0, 0);
        leavingNotice.setLayoutParams(noticeLp);
        details.addView(leavingNotice);
        holder.leavingNotice = leavingNotice;

        topRow.addView(details);
        card.addView(topRow);

        // Overview
        TextView overview = new TextView(activity);
        overview.setTextColor(0xFF8B949E);
        overview.setTextSize(12);
        overview.setMaxLines(2);
        overview.setPadding(0, activity.dp(8), 0, activity.dp(6));
        card.addView(overview);
        holder.overview = overview;

        // Action Buttons Row
        LinearLayout actionRow = new LinearLayout(activity);
        actionRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowLp.setMargins(0, activity.dp(4), 0, 0);
        actionRow.setLayoutParams(rowLp);

        // 1. Launch Provider Button
        Button watchBtn = new Button(activity);
        watchBtn.setTextColor(0xFFFFFFFF);
        watchBtn.setTextSize(11);
        watchBtn.setTypeface(Typeface.DEFAULT_BOLD);
        watchBtn.setTransformationMethod(null);
        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(0, activity.dp(38), 0.38f);
        btnLp.setMargins(0, 0, activity.dp(3), 0);
        watchBtn.setLayoutParams(btnLp);
        actionRow.addView(watchBtn);
        holder.watchBtn = watchBtn;

        // 2. Watched Toggle Button
        Button watchedBtn = new Button(activity);
        LinearLayout.LayoutParams watchedBtnLp = new LinearLayout.LayoutParams(0, activity.dp(38), 0.24f);
        watchedBtnLp.setMargins(0, 0, activity.dp(3), 0);
        watchedBtn.setLayoutParams(watchedBtnLp);
        actionRow.addView(watchedBtn);
        holder.watchedBtn = watchedBtn;

        // 3. Details & Tags Button
        Button detailsBtn = new Button(activity);
        detailsBtn.setText("🏷️ Tags");
        detailsBtn.setTextColor(0xFF58A6FF);
        detailsBtn.setTextSize(10);
        detailsBtn.setTypeface(Typeface.DEFAULT_BOLD);
        detailsBtn.setTransformationMethod(null);
        detailsBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams detailsBtnLp = new LinearLayout.LayoutParams(0, activity.dp(38), 0.24f);
        detailsBtnLp.setMargins(0, 0, activity.dp(3), 0);
        detailsBtn.setLayoutParams(detailsBtnLp);
        actionRow.addView(detailsBtn);
        holder.detailsBtn = detailsBtn;

        // 4. Share Recommendation Button
        Button shareBtn = new Button(activity);
        shareBtn.setText("📤");
        shareBtn.setTextColor(0xFFF0F6FC);
        shareBtn.setTextSize(12);
        shareBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams shareBtnLp = new LinearLayout.LayoutParams(0, activity.dp(38), 0.14f);
        shareBtn.setLayoutParams(shareBtnLp);
        actionRow.addView(shareBtn);
        holder.shareBtn = shareBtn;

        card.addView(actionRow);

        return holder;
    }

    private void bindViewHolder(MovieViewHolder holder, final Movie movie, int rank) {
        // Poster
        activity.imageLoader.load(holder.poster, movie.getPosterUrl(), movie.getTitle());

        // Rank Badge
        holder.rankBadge.setText("#" + rank);
        GradientDrawable rankGd = new GradientDrawable();
        rankGd.setCornerRadius(activity.dp(4));
        if (rank == 1) {
            rankGd.setColor(0xFFFFD700);
            holder.rankBadge.setTextColor(0xFF000000);
        } else if (rank == 2) {
            rankGd.setColor(0xFFC0C0C0);
            holder.rankBadge.setTextColor(0xFF000000);
        } else if (rank == 3) {
            rankGd.setColor(0xFFCD7F32);
            holder.rankBadge.setTextColor(0xFF000000);
        } else {
            rankGd.setColor(0xFF30363D);
            holder.rankBadge.setTextColor(0xFF8B949E);
        }
        holder.rankBadge.setBackground(rankGd);

        // Title
        holder.title.setText(" " + movie.getTitle());

        // Meta
        holder.meta.setText(movie.getYear() + " • " + movie.getRuntimeMinutes() + " Min • " + movie.getGenres());

        // Scores
        holder.imdbScore.setText("★ " + String.format(Locale.ROOT, "%.1f", movie.getImdbRating()));
        if (movie.getRottenTomatoes() > 0) {
            holder.rtScore.setVisibility(View.VISIBLE);
            holder.rtScore.setText(" 🍅 " + movie.getRottenTomatoes() + "%");
        } else {
            holder.rtScore.setVisibility(View.GONE);
        }
        holder.pearlScore.setText(" 💎 Perle " + String.format(Locale.ROOT, "%.0f", movie.getCombinedScore()) + "%");

        // FSK Badge
        if (movie.getFsk() >= 0) {
            holder.fskBadge.setVisibility(View.VISIBLE);
            holder.fskBadge.setText("FSK " + movie.getFsk());
            GradientDrawable fskGd = new GradientDrawable();
            fskGd.setCornerRadius(activity.dp(3));
            if (movie.getFsk() == 0) {
                fskGd.setColor(0xFF2EA043); // Green
                holder.fskBadge.setTextColor(0xFFFFFFFF);
            } else if (movie.getFsk() == 6) {
                fskGd.setColor(0xFFE3B341); // Yellow
                holder.fskBadge.setTextColor(0xFF000000);
            } else if (movie.getFsk() == 12) {
                fskGd.setColor(0xFF1F6FEB); // Blue
                holder.fskBadge.setTextColor(0xFFFFFFFF);
            } else if (movie.getFsk() == 16) {
                fskGd.setColor(0xFF8957E5); // Purple
                holder.fskBadge.setTextColor(0xFFFFFFFF);
            } else {
                fskGd.setColor(0xFFDA3633); // Red
                holder.fskBadge.setTextColor(0xFFFFFFFF);
            }
            holder.fskBadge.setBackground(fskGd);
        } else {
            holder.fskBadge.setVisibility(View.GONE);
        }

        // Votes
        holder.votes.setText(movie.getFormattedVotes() + " Votes");

        // Watchlist Tag
        boolean isWl = activity.watchlistManager != null &&
            (activity.watchlistManager.isWatchlisted(movie.getId()) || activity.watchlistManager.isWatchlisted(movie.getTitle()));
        holder.wlTag.setVisibility(isWl ? View.VISIBLE : View.GONE);

        // Leaving Notice
        if (movie.isLeavingSoon()) {
            holder.leavingNotice.setVisibility(View.VISIBLE);
            holder.leavingNotice.setText("⏳ " + (movie.getLeavingDate() != null ? movie.getLeavingDate() : "Nur noch kurze Zeit!"));
        } else {
            holder.leavingNotice.setVisibility(View.GONE);
        }

        // Overview
        if (movie.getOverview() != null && !movie.getOverview().isEmpty()) {
            holder.overview.setVisibility(View.VISIBLE);
            holder.overview.setText(movie.getOverview());
        } else {
            holder.overview.setVisibility(View.GONE);
        }

        // Stream Button
        boolean isYt = movie.getProvider().equals(StreamingProvider.YOUTUBE);
        holder.watchBtn.setText(isYt ? "▶ YouTube (DE)" : ("▶ " + movie.getProvider().getDisplayName()));
        if (movie.getProvider().equals(StreamingProvider.NETFLIX)) {
            holder.watchBtn.setBackgroundResource(R.drawable.btn_netflix);
        } else if (movie.getProvider().equals(StreamingProvider.PRIME_VIDEO)) {
            holder.watchBtn.setBackgroundResource(R.drawable.btn_prime);
        } else if (movie.getProvider().equals(StreamingProvider.DISNEY_PLUS)) {
            holder.watchBtn.setBackgroundResource(R.drawable.btn_disney);
        } else if (movie.getProvider().equals(StreamingProvider.WOW)) {
            holder.watchBtn.setBackgroundResource(R.drawable.btn_wow);
        } else if (movie.getProvider().equals(StreamingProvider.YOUTUBE)) {
            holder.watchBtn.setBackgroundResource(R.drawable.btn_youtube);
        } else if (movie.getProvider().equals(StreamingProvider.ARD_MEDIATHEK)) {
            holder.watchBtn.setBackgroundResource(R.drawable.btn_ard);
        } else if (movie.getProvider().equals(StreamingProvider.ZDF_MEDIATHEK)) {
            holder.watchBtn.setBackgroundResource(R.drawable.btn_zdf);
        } else {
            holder.watchBtn.setBackgroundResource(R.drawable.btn_generic);
        }
        holder.watchBtn.setOnClickListener(v -> {
            boolean success = movie.getProvider().launchTitle(activity, movie.getDeepLinkId(), movie.getTitle());
            if (!success) {
                Toast.makeText(activity, "Konnte " + movie.getProvider().getDisplayName() + " nicht öffnen", Toast.LENGTH_SHORT).show();
            }
        });

        // Watched Button
        final boolean isWatchedInitially = activity.watchedManager != null && activity.watchedManager.isWatched(movie.getId(), movie.getTitle());
        activity.updateWatchedButtonVisual(holder.watchedBtn, isWatchedInitially);
        holder.watchedBtn.setOnClickListener(v -> {
            if (activity.watchedManager == null) return;
            boolean nowWatched = activity.watchedManager.toggleWatched(activity, movie.getId(), movie.getTitle());
            Toast.makeText(activity, (nowWatched ? "✓ Als gesehen markiert: " : "👁️ Als ungesehen markiert: ") + movie.getTitle(), Toast.LENGTH_SHORT).show();
            if (activity.criteria.excludeWatched) {
                activity.renderMovieList();
                activity.triggerLiveFetchIfNeeded();
            } else {
                activity.updateWatchedButtonVisual(holder.watchedBtn, nowWatched);
                activity.renderExclusionAndYearChips();
            }
        });

        // Details Button
        holder.detailsBtn.setOnClickListener(v -> activity.showMovieDetailDialog(movie));

        // Share Button
        holder.shareBtn.setOnClickListener(v -> activity.shareMovieRecommendation(movie));

        // Detail dialog clicks on card/poster/title
        holder.poster.setOnClickListener(v -> activity.showMovieDetailDialog(movie));
        holder.titleRow.setOnClickListener(v -> activity.showMovieDetailDialog(movie));
        holder.card.setOnClickListener(v -> activity.showMovieDetailDialog(movie));
    }

    private static class MovieViewHolder {
        LinearLayout card;
        ImageView poster;
        LinearLayout titleRow;
        TextView rankBadge;
        TextView title;
        TextView meta;
        TextView imdbScore;
        TextView rtScore;
        TextView pearlScore;
        TextView fskBadge;
        TextView votes;
        TextView wlTag;
        TextView leavingNotice;
        TextView overview;
        Button watchBtn;
        Button watchedBtn;
        Button detailsBtn;
        Button shareBtn;
    }
}

