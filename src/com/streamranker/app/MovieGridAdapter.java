package com.streamranker.app;

import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 2-Column visual poster grid adapter for rich cover browsing.
 */
public class MovieGridAdapter extends BaseAdapter {

    private final MainActivity activity;
    private final List<Movie> movies = new ArrayList<>();

    public MovieGridAdapter(MainActivity activity) {
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
        GridMovieViewHolder holder;
        if (convertView == null) {
            holder = createViewHolder();
            convertView = holder.card;
            convertView.setTag(holder);
        } else {
            holder = (GridMovieViewHolder) convertView.getTag();
        }

        final Movie movie = getItem(position);
        final int rank = position + 1;
        bindViewHolder(holder, movie, rank);

        return convertView;
    }

    private GridMovieViewHolder createViewHolder() {
        GridMovieViewHolder holder = new GridMovieViewHolder();

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_movie);
        card.setPadding(activity.dp(6), activity.dp(6), activity.dp(6), activity.dp(8));

        // Poster container FrameLayout for overlays
        FrameLayout posterFrame = new FrameLayout(activity);
        LinearLayout.LayoutParams pfLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(190));
        posterFrame.setLayoutParams(pfLp);

        ImageView poster = new ImageView(activity);
        FrameLayout.LayoutParams posterLp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        poster.setLayoutParams(posterLp);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        posterFrame.addView(poster);
        holder.poster = poster;

        // Rank Badge Overlay (Top Left)
        TextView rankBadge = new TextView(activity);
        rankBadge.setTextSize(10);
        rankBadge.setTypeface(Typeface.DEFAULT_BOLD);
        rankBadge.setPadding(activity.dp(6), activity.dp(3), activity.dp(6), activity.dp(3));
        FrameLayout.LayoutParams rLp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.TOP | Gravity.START);
        rLp.setMargins(activity.dp(4), activity.dp(4), 0, 0);
        rankBadge.setLayoutParams(rLp);
        posterFrame.addView(rankBadge);
        holder.rankBadge = rankBadge;

        // FSK Badge Overlay (Top Right)
        TextView fskBadge = new TextView(activity);
        fskBadge.setTextSize(9);
        fskBadge.setTypeface(Typeface.DEFAULT_BOLD);
        fskBadge.setPadding(activity.dp(5), activity.dp(2), activity.dp(5), activity.dp(2));
        FrameLayout.LayoutParams fskLp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.TOP | Gravity.END);
        fskLp.setMargins(0, activity.dp(4), activity.dp(4), 0);
        fskBadge.setLayoutParams(fskLp);
        posterFrame.addView(fskBadge);
        holder.fskBadge = fskBadge;

        // Score Pill Overlay (Bottom)
        LinearLayout scoreOverlay = new LinearLayout(activity);
        scoreOverlay.setOrientation(LinearLayout.HORIZONTAL);
        scoreOverlay.setGravity(Gravity.CENTER_VERTICAL);
        scoreOverlay.setBackgroundColor(0xCC0D1117);
        scoreOverlay.setPadding(activity.dp(6), activity.dp(3), activity.dp(6), activity.dp(3));
        FrameLayout.LayoutParams scLp = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM);
        scoreOverlay.setLayoutParams(scLp);

        TextView imdbScore = new TextView(activity);
        imdbScore.setTextSize(10);
        imdbScore.setTextColor(0xFFF5C518);
        imdbScore.setTypeface(Typeface.DEFAULT_BOLD);
        scoreOverlay.addView(imdbScore);
        holder.imdbScore = imdbScore;

        TextView rtScore = new TextView(activity);
        rtScore.setTextSize(10);
        rtScore.setTextColor(0xFFFFFFFF);
        rtScore.setTypeface(Typeface.DEFAULT_BOLD);
        rtScore.setPadding(activity.dp(6), 0, 0, 0);
        scoreOverlay.addView(rtScore);
        holder.rtScore = rtScore;

        TextView wlBadge = new TextView(activity);
        wlBadge.setText(" ⭐");
        wlBadge.setTextSize(10);
        wlBadge.setTextColor(0xFFF5C518);
        wlBadge.setVisibility(View.GONE);
        scoreOverlay.addView(wlBadge);
        holder.wlBadge = wlBadge;

        posterFrame.addView(scoreOverlay);
        card.addView(posterFrame);

        // Title
        TextView title = new TextView(activity);
        title.setTextColor(0xFFF0F6FC);
        title.setTextSize(12);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setMaxLines(2);
        title.setPadding(activity.dp(2), activity.dp(6), activity.dp(2), activity.dp(2));
        card.addView(title);
        holder.title = title;

        // Meta & Year Row
        TextView meta = new TextView(activity);
        meta.setTextColor(0xFF8B949E);
        meta.setTextSize(10);
        meta.setMaxLines(1);
        meta.setPadding(activity.dp(2), 0, activity.dp(2), activity.dp(4));
        card.addView(meta);
        holder.meta = meta;

        // Provider Badge Pill
        TextView providerPill = new TextView(activity);
        providerPill.setTextSize(10);
        providerPill.setTypeface(Typeface.DEFAULT_BOLD);
        providerPill.setTextColor(0xFFFFFFFF);
        providerPill.setGravity(Gravity.CENTER);
        providerPill.setPadding(activity.dp(4), activity.dp(3), activity.dp(4), activity.dp(3));
        LinearLayout.LayoutParams ppLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ppLp.setMargins(activity.dp(2), 0, activity.dp(2), 0);
        providerPill.setLayoutParams(ppLp);
        card.addView(providerPill);
        holder.providerPill = providerPill;

        holder.card = card;
        return holder;
    }

    private void bindViewHolder(GridMovieViewHolder holder, final Movie movie, int rank) {
        // Poster load
        activity.imageLoader.load(holder.poster, movie.getPosterUrl(), movie.getTitle());

        // Rank Badge
        holder.rankBadge.setText("#" + rank);
        GradientDrawable rankGd = new GradientDrawable();
        rankGd.setCornerRadius(activity.dp(4));
        if (rank == 1) {
            rankGd.setColor(0xFFF5C518); // Gold
            holder.rankBadge.setTextColor(0xFF000000);
        } else if (rank == 2) {
            rankGd.setColor(0xFFC0C0C0); // Silver
            holder.rankBadge.setTextColor(0xFF000000);
        } else if (rank == 3) {
            rankGd.setColor(0xFFCD7F32); // Bronze
            holder.rankBadge.setTextColor(0xFF000000);
        } else {
            rankGd.setColor(0xCC21262D);
            holder.rankBadge.setTextColor(0xFFF0F6FC);
        }
        holder.rankBadge.setBackground(rankGd);

        // FSK Badge
        if (movie.getFsk() >= 0) {
            holder.fskBadge.setVisibility(View.VISIBLE);
            holder.fskBadge.setText("FSK " + movie.getFsk());
            GradientDrawable fskGd = new GradientDrawable();
            fskGd.setCornerRadius(activity.dp(3));
            if (movie.getFsk() == 0) {
                fskGd.setColor(0xFF2EA043);
                holder.fskBadge.setTextColor(0xFFFFFFFF);
            } else if (movie.getFsk() == 6) {
                fskGd.setColor(0xFFE3B341);
                holder.fskBadge.setTextColor(0xFF000000);
            } else if (movie.getFsk() == 12) {
                fskGd.setColor(0xFF1F6FEB);
                holder.fskBadge.setTextColor(0xFFFFFFFF);
            } else if (movie.getFsk() == 16) {
                fskGd.setColor(0xFF8957E5);
                holder.fskBadge.setTextColor(0xFFFFFFFF);
            } else {
                fskGd.setColor(0xFFDA3633);
                holder.fskBadge.setTextColor(0xFFFFFFFF);
            }
            holder.fskBadge.setBackground(fskGd);
        } else {
            holder.fskBadge.setVisibility(View.GONE);
        }

        // Scores
        holder.imdbScore.setText("★ " + String.format(Locale.ROOT, "%.1f", movie.getImdbRating()));
        if (movie.getRottenTomatoes() > 0) {
            holder.rtScore.setVisibility(View.VISIBLE);
            holder.rtScore.setText("🍅 " + movie.getRottenTomatoes() + "%");
        } else {
            holder.rtScore.setVisibility(View.GONE);
        }

        // Watchlist indicator
        boolean isWl = activity.watchlistManager != null &&
            (activity.watchlistManager.isWatchlisted(movie.getId()) || activity.watchlistManager.isWatchlisted(movie.getTitle()));
        holder.wlBadge.setVisibility(isWl ? View.VISIBLE : View.GONE);

        // Title & Meta
        holder.title.setText(movie.getTitle());
        holder.meta.setText(movie.getYear() + " • " + movie.getRuntimeMinutes() + "m");

        // Provider Pill
        StreamingProvider prov = movie.getProvider();
        holder.providerPill.setText("▶ " + prov.getDisplayName());
        GradientDrawable provGd = new GradientDrawable();
        provGd.setCornerRadius(activity.dp(4));
        provGd.setColor(prov.getBrandColor() != 0 ? prov.getBrandColor() : 0xFF30363D);
        holder.providerPill.setBackground(provGd);

        // Click on entire grid card opens Detail dialog
        holder.card.setOnClickListener(v -> activity.showMovieDetailDialog(movie));
    }

    private static class GridMovieViewHolder {
        LinearLayout card;
        ImageView poster;
        TextView rankBadge;
        TextView fskBadge;
        TextView imdbScore;
        TextView rtScore;
        TextView wlBadge;
        TextView title;
        TextView meta;
        TextView providerPill;
    }
}
