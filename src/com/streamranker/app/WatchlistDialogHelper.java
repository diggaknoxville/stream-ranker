package com.streamranker.app;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Helper class managing the IMDb Watchlist import and synchronization dialog.
 */
public final class WatchlistDialogHelper {

    private WatchlistDialogHelper() {}

    public static void show(final MainActivity activity) {
        if (activity == null || activity.isFinishing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("⭐ IMDb Watchlist & Wishlist verknüpfen");

        ScrollView scroll = new ScrollView(activity);
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(activity.dp(18), activity.dp(10), activity.dp(18), activity.dp(14));

        TextView countInfo = new TextView(activity);
        countInfo.setText("Aktuell gespeichert: " + activity.watchlistManager.getCount() + " Titel auf deiner Watchlist");
        countInfo.setTextSize(13);
        countInfo.setTextColor(0xFF58A6FF);
        countInfo.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(countInfo);

        // 1. One-click File Picker for CSV
        Button pickFileBtn = new Button(activity);
        pickFileBtn.setText("📂 WATCHLIST.csv aus Downloads wählen");
        pickFileBtn.setTextColor(0xFFFFFFFF);
        pickFileBtn.setTextSize(12);
        pickFileBtn.setTypeface(Typeface.DEFAULT_BOLD);
        pickFileBtn.setTransformationMethod(null);
        GradientDrawable pickGd = new GradientDrawable();
        pickGd.setColor(0xFF238636); // Green
        pickGd.setCornerRadius(activity.dp(8));
        pickFileBtn.setBackground(pickGd);
        LinearLayout.LayoutParams pickLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(42));
        pickLp.setMargins(0, activity.dp(12), 0, activity.dp(4));
        pickFileBtn.setLayoutParams(pickLp);
        pickFileBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                String[] mimeTypes = {"text/csv", "text/comma-separated-values", "text/plain", "application/csv", "application/octet-stream"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                activity.startActivityForResult(intent, MainActivity.REQUEST_CODE_PICK_CSV);
            } catch (Exception e) {
                Intent fallback = new Intent(Intent.ACTION_GET_CONTENT);
                fallback.setType("*/*");
                activity.startActivityForResult(fallback, MainActivity.REQUEST_CODE_PICK_CSV);
            }
        });
        layout.addView(pickFileBtn);

        TextView pickHint = new TextView(activity);
        pickHint.setText("💡 Tipp: Lade deine Watchlist auf imdb.com als CSV herunter (Exportieren). Ein Klick hier importiert alle Filme sofort!");
        pickHint.setTextSize(11);
        pickHint.setTextColor(0xFF8B949E);
        pickHint.setPadding(0, 0, 0, activity.dp(10));
        layout.addView(pickHint);

        // 2. Open installed IMDb app directly
        Button openImdbBtn = new Button(activity);
        boolean imdbInstalled = activity.watchlistManager.isImdbAppInstalled(activity);
        openImdbBtn.setText(imdbInstalled ? "📱 In IMDb-App öffnen (Meine Watchlist)" : "🌐 IMDb Watchlist im Browser öffnen");
        openImdbBtn.setTextColor(0xFFF5C518);
        openImdbBtn.setTextSize(12);
        openImdbBtn.setTypeface(Typeface.DEFAULT_BOLD);
        openImdbBtn.setTransformationMethod(null);
        openImdbBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams openLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(40));
        openLp.setMargins(0, activity.dp(4), 0, activity.dp(4));
        openImdbBtn.setLayoutParams(openLp);
        openImdbBtn.setOnClickListener(v -> activity.watchlistManager.openImdbWatchlist(activity));
        layout.addView(openImdbBtn);

        TextView shareHint = new TextView(activity);
        shareHint.setText("In der IMDb-App: Tippe auf deiner Watchlist auf 'Teilen' (Share) und wähle direkt 'StreamRanker'!");
        shareHint.setTextSize(11);
        shareHint.setTextColor(0xFF8B949E);
        shareHint.setPadding(0, 0, 0, activity.dp(10));
        layout.addView(shareHint);

        // 3. Paste from Clipboard
        Button pasteBtn = new Button(activity);
        pasteBtn.setText("📋 Aus Zwischenablage einfügen");
        pasteBtn.setTextColor(0xFFF0F6FC);
        pasteBtn.setTextSize(12);
        pasteBtn.setTransformationMethod(null);
        pasteBtn.setBackgroundResource(R.drawable.btn_generic);
        LinearLayout.LayoutParams pasteLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(38));
        pasteLp.setMargins(0, activity.dp(4), 0, activity.dp(10));
        pasteBtn.setLayoutParams(pasteLp);
        pasteBtn.setOnClickListener(v -> {
            try {
                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null && cm.hasPrimaryClip() && cm.getPrimaryClip().getItemCount() > 0) {
                    CharSequence cs = cm.getPrimaryClip().getItemAt(0).getText();
                    if (cs != null && cs.length() > 0) {
                        activity.importWatchlistText(cs.toString());
                        return;
                    }
                }
                Toast.makeText(activity, "Zwischenablage ist leer", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(activity, "Konnte Zwischenablage nicht lesen", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(pasteBtn);

        // 4. Manual Text Input
        TextView manualLabel = new TextView(activity);
        manualLabel.setText("Manuelle Eingabe (IMDb User-ID oder Text):");
        manualLabel.setTextColor(0xFF8B949E);
        manualLabel.setTextSize(11);
        layout.addView(manualLabel);

        final EditText input = new EditText(activity);
        input.setHint("z. B. ur12345678 oder tt0111161, tt0468569...");
        input.setText(activity.watchlistManager.getUserImdbId());
        input.setPadding(activity.dp(10), activity.dp(8), activity.dp(10), activity.dp(8));
        input.setTextColor(0xFFFFFFFF);
        layout.addView(input);

        scroll.addView(layout);
        builder.setView(scroll);

        builder.setPositiveButton("Speichern & Abgleichen", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                if (text.startsWith("ur")) {
                    activity.watchlistManager.setUserImdbId(activity, text);
                    Toast.makeText(activity, "IMDb-Konto " + text + " hinterlegt!", Toast.LENGTH_SHORT).show();
                } else {
                    activity.importWatchlistText(text);
                }
            }
            activity.refreshUi();
        });

        builder.setNeutralButton("Demo-Watchlist", (dialog, which) -> {
            activity.watchlistManager.init(activity);
            Toast.makeText(activity, "Demo-Watchlist mit Top-Titeln geladen!", Toast.LENGTH_SHORT).show();
            activity.refreshUi();
        });

        builder.setNegativeButton("Schließen", null);

        builder.show();
    }
}

