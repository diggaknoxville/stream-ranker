package com.streamranker.app;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

/**
 * Helper class managing the detailed filter, exclusions, and criteria configuration dialog.
 */
public final class FilterDialogHelper {

    public static final String[] ALL_GENRES = new String[] {
        "Action", "Abenteuer", "Animation", "Komödie", "Krimi", "Dokumentarfilm",
        "Drama", "Familie", "Fantasy", "Historie", "Horror", "Musik",
        "Mystery", "Romantik", "Science-Fiction", "Thriller", "Kriegsfilm", "Western"
    };

    private static final String[][] COMMON_COUNTRIES = new String[][] {
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

    private FilterDialogHelper() {}

    public static void show(final MainActivity activity) {
        if (activity == null || activity.isFinishing()) return;

        final FilterCriteria criteria = activity.criteria;
        final ProviderManager providerManager = activity.providerManager;
        final WatchedManager watchedManager = activity.watchedManager;

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("⚙️ Präziser Filter- & Ausschluss-Manager");

        ScrollView dialogScroll = new ScrollView(activity);
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(activity.dp(18), activity.dp(10), activity.dp(18), activity.dp(14));

        // 1. Streaming-Dienst Dimension
        TextView provLabel = new TextView(activity);
        provLabel.setText("1. Streaming-Dienst:");
        provLabel.setTextColor(0xFFF5C518);
        provLabel.setTextSize(13);
        provLabel.setTypeface(Typeface.DEFAULT_BOLD);
        layout.addView(provLabel);

        final String[] selectedProviderId = new String[] { criteria.singleProviderId };
        HorizontalScrollView provScroll = new HorizontalScrollView(activity);
        provScroll.setHorizontalScrollBarEnabled(false);
        final LinearLayout provChips = new LinearLayout(activity);
        provChips.setOrientation(LinearLayout.HORIZONTAL);
        provChips.setPadding(0, activity.dp(4), 0, activity.dp(10));

        final String[][] provOptions = new String[][] {
            {"all", "Alle aktiven"},
            {"netflix", "Netflix"},
            {"prime", "Prime"},
            {"disney", "Disney+"},
            {"wow", "WOW"},
            {"ard", "ARD Mediathek"},
            {"zdf", "ZDF Mediathek"},
            {"arte", "Arte"},
            {"joyn", "Joyn (Free)"}
        };

        final Runnable updateProvChips = () -> {
            provChips.removeAllViews();
            for (String[] opt : provOptions) {
                final String pid = opt[0];
                final String pname = opt[1];
                boolean isSel = selectedProviderId[0].equalsIgnoreCase(pid);
                provChips.addView(activity.createFilterPill(pname, isSel, 0xFF388BFD, () -> {
                    selectedProviderId[0] = pid;
                    for (int c = 0; c < provChips.getChildCount(); c++) {
                        View child = provChips.getChildAt(c);
                        if (child instanceof TextView) {
                            String cText = ((TextView) child).getText().toString();
                            boolean matched = cText.equalsIgnoreCase(pname);
                            GradientDrawable gd = new GradientDrawable();
                            gd.setCornerRadius(activity.dp(12));
                            if (matched) {
                                gd.setColor(0xFF388BFD);
                                ((TextView) child).setTextColor(0xFFFFFFFF);
                            } else {
                                gd.setColor(0xFF21262D);
                                gd.setStroke(activity.dp(1), 0xFF30363D);
                                ((TextView) child).setTextColor(0xFF8B949E);
                            }
                            child.setBackground(gd);
                        }
                    }
                }));
            }
        };
        updateProvChips.run();
        provScroll.addView(provChips);
        layout.addView(provScroll);

        // Abonnierte Streaming-Dienste für "Alle aktiven"
        TextView poolLabel = new TextView(activity);
        poolLabel.setText("Abonnierte Dienste (Pool für 'Alle aktiven'):");
        poolLabel.setTextColor(0xFF8B949E);
        poolLabel.setTextSize(11);
        poolLabel.setPadding(0, activity.dp(2), 0, activity.dp(2));
        layout.addView(poolLabel);

        LinearLayout poolRow = new LinearLayout(activity);
        poolRow.setOrientation(LinearLayout.HORIZONTAL);
        poolRow.setPadding(0, 0, 0, activity.dp(8));

        for (final StreamingProvider sp : new StreamingProvider[]{
            StreamingProvider.NETFLIX, StreamingProvider.PRIME_VIDEO, StreamingProvider.DISNEY_PLUS,
            StreamingProvider.WOW, StreamingProvider.ARD_MEDIATHEK,
            StreamingProvider.ZDF_MEDIATHEK, StreamingProvider.ARTE, StreamingProvider.JOYN
        }) {
            final CheckBox pBox = new CheckBox(activity);
            pBox.setText(sp.getDisplayName());
            pBox.setTextSize(11);
            pBox.setTextColor(0xFFF0F6FC);
            pBox.setChecked(providerManager.isActive(sp));
            pBox.setOnCheckedChangeListener((btn, isChecked) -> {
                if (isChecked != providerManager.isActive(sp)) {
                    providerManager.toggleActive(activity, sp);
                }
            });
            poolRow.addView(pBox);
        }
        HorizontalScrollView poolScroll = new HorizontalScrollView(activity);
        poolScroll.addView(poolRow);
        layout.addView(poolScroll);

        // 2. Bereits Gesehene Filme
        final CheckBox watchedBox = new CheckBox(activity);
        int seenCount = watchedManager != null ? watchedManager.getCount() : 0;
        watchedBox.setText("Bereits gesehene Filme ausblenden (" + seenCount + " markiert)");
        watchedBox.setTextColor(0xFF2EA043);
        watchedBox.setChecked(criteria.excludeWatched);
        layout.addView(watchedBox);

        // 3. Alle 18 Genres (präzise Ein- und Ausschlüsse)
        TextView genresHeading = new TextView(activity);
        genresHeading.setText("3. Alle Genres (Neutral / [+] Einschließen / [-] Ausschließen):");
        genresHeading.setTextColor(0xFFF5C518);
        genresHeading.setTextSize(13);
        genresHeading.setTypeface(Typeface.DEFAULT_BOLD);
        genresHeading.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(genresHeading);

        final Set<String> tempIncludedGenres = new HashSet<>(criteria.includedGenres);
        final Set<String> tempExcludedGenres = new HashSet<>(criteria.excludedGenres);

        for (final String gName : ALL_GENRES) {
            LinearLayout gRow = new LinearLayout(activity);
            gRow.setOrientation(LinearLayout.HORIZONTAL);
            gRow.setGravity(Gravity.CENTER_VERTICAL);
            gRow.setPadding(0, activity.dp(3), 0, activity.dp(3));

            TextView gTitle = new TextView(activity);
            gTitle.setText(gName);
            gTitle.setTextColor(0xFFF0F6FC);
            gTitle.setTextSize(12);
            LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.42f);
            gTitle.setLayoutParams(titleLp);
            gRow.addView(gTitle);

            final Button btnNeutral = new Button(activity);
            btnNeutral.setText("Egal");
            btnNeutral.setTextSize(10);
            btnNeutral.setTransformationMethod(null);
            LinearLayout.LayoutParams bLp1 = new LinearLayout.LayoutParams(0, activity.dp(32), 0.18f);
            bLp1.setMargins(activity.dp(2), 0, activity.dp(2), 0);
            btnNeutral.setLayoutParams(bLp1);

            final Button btnInc = new Button(activity);
            btnInc.setText("+ Ein");
            btnInc.setTextSize(10);
            btnInc.setTransformationMethod(null);
            LinearLayout.LayoutParams bLp2 = new LinearLayout.LayoutParams(0, activity.dp(32), 0.20f);
            bLp2.setMargins(activity.dp(2), 0, activity.dp(2), 0);
            btnInc.setLayoutParams(bLp2);

            final Button btnEx = new Button(activity);
            btnEx.setText("- Aus");
            btnEx.setTextSize(10);
            btnEx.setTransformationMethod(null);
            LinearLayout.LayoutParams bLp3 = new LinearLayout.LayoutParams(0, activity.dp(32), 0.20f);
            bLp3.setMargins(activity.dp(2), 0, activity.dp(2), 0);
            btnEx.setLayoutParams(bLp3);

            final Runnable updateButtons = () -> {
                boolean isInc = tempIncludedGenres.contains(gName);
                boolean isEx = tempExcludedGenres.contains(gName);

                styleGenreStateBtn(activity, btnNeutral, !isInc && !isEx, 0xFF30363D, 0xFFFFFFFF);
                styleGenreStateBtn(activity, btnInc, isInc, 0xFF238636, 0xFFFFFFFF);
                styleGenreStateBtn(activity, btnEx, isEx, 0xFFDA3633, 0xFFFFFFFF);
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
        TextView exclLabel = new TextView(activity);
        exclLabel.setText("4. Formate & Ausschlüsse:");
        exclLabel.setTextColor(0xFFFA320A);
        exclLabel.setTextSize(13);
        exclLabel.setTypeface(Typeface.DEFAULT_BOLD);
        exclLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(exclLabel);

        final CheckBox dokuBox = new CheckBox(activity);
        dokuBox.setText("Keine Dokumentationen");
        dokuBox.setTextColor(0xFFF0F6FC);
        dokuBox.setChecked(criteria.excludeDoku);
        layout.addView(dokuBox);

        final CheckBox bollyBox = new CheckBox(activity);
        bollyBox.setText("Kein Bollywood / Keine indischen Filme");
        bollyBox.setTextColor(0xFFF0F6FC);
        bollyBox.setChecked(criteria.excludeBollywood);
        layout.addView(bollyBox);

        final CheckBox animeBox = new CheckBox(activity);
        animeBox.setText("Kein Anime / Zeichentrick");
        animeBox.setTextColor(0xFFF0F6FC);
        animeBox.setChecked(criteria.excludeAnime);
        layout.addView(animeBox);

        final CheckBox shortsBox = new CheckBox(activity);
        shortsBox.setText("Keine Kurzfilme (< 60 Min)");
        shortsBox.setTextColor(0xFFF0F6FC);
        shortsBox.setChecked(criteria.excludeShorts);
        layout.addView(shortsBox);

        final CheckBox standupBox = new CheckBox(activity);
        standupBox.setText("Keine Stand-up Specials / Comedians");
        standupBox.setTextColor(0xFFF0F6FC);
        standupBox.setChecked(criteria.excludeStandup);
        layout.addView(standupBox);

        // 4b. Sprachfassung / Tonspur Filter
        TextView langHeading = new TextView(activity);
        langHeading.setText("Sprachfassung & Tonspur:");
        langHeading.setTextColor(0xFF58A6FF);
        langHeading.setTextSize(13);
        langHeading.setTypeface(Typeface.DEFAULT_BOLD);
        langHeading.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(langHeading);

        final int[] tempLangFilter = new int[] { criteria.languageFilter };
        LinearLayout langRow = new LinearLayout(activity);
        langRow.setOrientation(LinearLayout.HORIZONTAL);
        langRow.setPadding(0, activity.dp(2), 0, activity.dp(6));

        final Runnable[] renderLangHolder = new Runnable[1];
        renderLangHolder[0] = () -> {
            langRow.removeAllViews();
            langRow.addView(activity.createFilterPill("Alle Sprachen", tempLangFilter[0] == FilterCriteria.LANG_ALL, 0xFF388BFD, () -> {
                tempLangFilter[0] = FilterCriteria.LANG_ALL;
                renderLangHolder[0].run();
            }));
            langRow.addView(activity.createFilterPill("🇩🇪 Nur auf Deutsch", tempLangFilter[0] == FilterCriteria.LANG_GERMAN_ONLY, 0xFF2EA043, () -> {
                tempLangFilter[0] = FilterCriteria.LANG_GERMAN_ONLY;
                renderLangHolder[0].run();
            }));
            langRow.addView(activity.createFilterPill("🗣️ Nur OmU / OV", tempLangFilter[0] == FilterCriteria.LANG_ORIGINAL_OMU_ONLY, 0xFF8957E5, () -> {
                tempLangFilter[0] = FilterCriteria.LANG_ORIGINAL_OMU_ONLY;
                renderLangHolder[0].run();
            }));
        };
        renderLangHolder[0].run();
        layout.addView(langRow);

        // 5. Produktionsländer ausschließen (Blacklist)
        TextView countryLabel = new TextView(activity);
        countryLabel.setText("5. Produktionsländer ausschließen:");
        countryLabel.setTextColor(0xFFFA320A);
        countryLabel.setTextSize(13);
        countryLabel.setTypeface(Typeface.DEFAULT_BOLD);
        countryLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(countryLabel);

        final Set<String> tempExcludedCountries = new HashSet<>(criteria.excludedCountries);

        HorizontalScrollView countryScroll = new HorizontalScrollView(activity);
        countryScroll.setHorizontalScrollBarEnabled(false);
        final LinearLayout countryChipsRow = new LinearLayout(activity);
        countryChipsRow.setOrientation(LinearLayout.HORIZONTAL);
        countryChipsRow.setPadding(0, activity.dp(2), 0, activity.dp(4));

        final Runnable[] renderCountriesHolder = new Runnable[1];
        renderCountriesHolder[0] = () -> {
            countryChipsRow.removeAllViews();
            for (String[] pair : COMMON_COUNTRIES) {
                final String code = pair[0];
                final String labelText = pair[1];
                boolean isEx = tempExcludedCountries.contains(code);
                countryChipsRow.addView(activity.createFilterPill(
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

        LinearLayout customCountryRow = new LinearLayout(activity);
        customCountryRow.setOrientation(LinearLayout.HORIZONTAL);
        customCountryRow.setGravity(Gravity.CENTER_VERTICAL);
        customCountryRow.setPadding(0, activity.dp(2), 0, activity.dp(6));

        final EditText customCountryInput = new EditText(activity);
        customCountryInput.setHint("Anderes Land / ISO-Code (z. B. PL, SE, CA)");
        customCountryInput.setTextColor(0xFFFFFFFF);
        customCountryInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        customCountryRow.addView(customCountryInput);

        Button addCountryBtn = new Button(activity);
        addCountryBtn.setText("+ Aus");
        addCountryBtn.setTextColor(0xFFFFFFFF);
        addCountryBtn.setTextSize(11);
        addCountryBtn.setBackgroundResource(R.drawable.btn_generic);
        addCountryBtn.setPadding(activity.dp(8), activity.dp(4), activity.dp(8), activity.dp(4));
        LinearLayout.LayoutParams addCountryBtnLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, activity.dp(34));
        addCountryBtnLp.setMargins(activity.dp(6), 0, 0, 0);
        addCountryBtn.setLayoutParams(addCountryBtnLp);
        addCountryBtn.setOnClickListener(v -> {
            String c = customCountryInput.getText().toString().trim().toUpperCase();
            if (!c.isEmpty()) {
                tempExcludedCountries.add(c);
                customCountryInput.setText("");
                renderCountriesHolder[0].run();
                Toast.makeText(activity, "Land ausgeschlossen: " + c, Toast.LENGTH_SHORT).show();
            }
        });
        customCountryRow.addView(addCountryBtn);
        layout.addView(customCountryRow);

        // 5b. Kombi-Ausschlüsse (Conditional Rules)
        TextView compLabel = new TextView(activity);
        compLabel.setText("5b. Kombi-Ausschlüsse (z. B. KR:Romantik):");
        compLabel.setTextColor(0xFFFA320A);
        compLabel.setTextSize(13);
        compLabel.setTypeface(Typeface.DEFAULT_BOLD);
        compLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(compLabel);

        final Set<String> tempCompound = new HashSet<>(criteria.compoundExclusions);
        final LinearLayout compContainer = new LinearLayout(activity);
        compContainer.setOrientation(LinearLayout.VERTICAL);
        layout.addView(compContainer);

        final Runnable[] renderCompoundHolder = new Runnable[1];
        renderCompoundHolder[0] = () -> {
            compContainer.removeAllViews();
            if (tempCompound.isEmpty()) {
                TextView emptyComp = new TextView(activity);
                emptyComp.setText("Keine Kombi-Ausschlüsse aktiv (z. B. in Film-Details anlegen oder unten eingeben).");
                emptyComp.setTextColor(0xFF8B949E);
                emptyComp.setTextSize(11);
                emptyComp.setPadding(0, activity.dp(2), 0, activity.dp(4));
                compContainer.addView(emptyComp);
            } else {
                HorizontalScrollView cScroll = new HorizontalScrollView(activity);
                cScroll.setHorizontalScrollBarEnabled(false);
                LinearLayout cRow = new LinearLayout(activity);
                cRow.setOrientation(LinearLayout.HORIZONTAL);
                cRow.setPadding(0, 0, 0, activity.dp(6));
                for (final String rule : tempCompound) {
                    cRow.addView(activity.createFilterPill("🚫 " + rule + " ✕", true, 0xFFDA3633, () -> {
                        tempCompound.remove(rule);
                        renderCompoundHolder[0].run();
                    }));
                }
                cScroll.addView(cRow);
                compContainer.addView(cScroll);
            }
        };
        renderCompoundHolder[0].run();

        LinearLayout addCompRow = new LinearLayout(activity);
        addCompRow.setOrientation(LinearLayout.HORIZONTAL);
        addCompRow.setGravity(Gravity.CENTER_VERTICAL);
        addCompRow.setPadding(0, activity.dp(2), 0, activity.dp(6));

        final EditText compPrefixInput = new EditText(activity);
        compPrefixInput.setHint("Land oder Schauspieler (z. B. KR)");
        compPrefixInput.setTextColor(0xFFFFFFFF);
        compPrefixInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        addCompRow.addView(compPrefixInput);

        final EditText compGenreInput = new EditText(activity);
        compGenreInput.setHint("Genre (z. B. Romantik)");
        compGenreInput.setTextColor(0xFFFFFFFF);
        compGenreInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        addCompRow.addView(compGenreInput);

        Button addCompBtn = new Button(activity);
        addCompBtn.setText("+ Regel");
        addCompBtn.setTextColor(0xFFFFFFFF);
        addCompBtn.setTextSize(11);
        addCompBtn.setBackgroundResource(R.drawable.btn_generic);
        addCompBtn.setPadding(activity.dp(8), activity.dp(4), activity.dp(8), activity.dp(4));
        LinearLayout.LayoutParams acbLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, activity.dp(34));
        acbLp.setMargins(activity.dp(4), 0, 0, 0);
        addCompBtn.setLayoutParams(acbLp);
        addCompBtn.setOnClickListener(v -> {
            String p = compPrefixInput.getText().toString().trim();
            String g = compGenreInput.getText().toString().trim();
            if (!p.isEmpty() && !g.isEmpty()) {
                String newRule = p + ":" + g;
                tempCompound.add(newRule);
                compPrefixInput.setText("");
                compGenreInput.setText("");
                renderCompoundHolder[0].run();
                Toast.makeText(activity, "Kombi-Ausschluss hinzugefügt: " + newRule, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Bitte Präfix und Genre angeben", Toast.LENGTH_SHORT).show();
            }
        });
        addCompRow.addView(addCompBtn);
        layout.addView(addCompRow);

        // 6. Produktionszeitraum (Jahr)
        TextView yearLabel = new TextView(activity);
        yearLabel.setText("6. Produktionszeitraum (Jahr):");
        yearLabel.setTextColor(0xFFF5C518);
        yearLabel.setTextSize(13);
        yearLabel.setTypeface(Typeface.DEFAULT_BOLD);
        yearLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(yearLabel);

        LinearLayout yearRow = new LinearLayout(activity);
        yearRow.setOrientation(LinearLayout.HORIZONTAL);
        yearRow.setPadding(0, activity.dp(2), 0, activity.dp(4));

        final EditText minYearInput = new EditText(activity);
        minYearInput.setHint("Von (z. B. 1970)");
        minYearInput.setText(criteria.minYear > 0 ? String.valueOf(criteria.minYear) : "");
        minYearInput.setTextColor(0xFFFFFFFF);
        minYearInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        yearRow.addView(minYearInput);

        final EditText maxYearInput = new EditText(activity);
        maxYearInput.setHint("Bis (z. B. 2010)");
        maxYearInput.setText(criteria.maxYear < 9999 ? String.valueOf(criteria.maxYear) : "");
        maxYearInput.setTextColor(0xFFFFFFFF);
        maxYearInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        yearRow.addView(maxYearInput);
        layout.addView(yearRow);

        // Quick year chips
        HorizontalScrollView yearChipScroll = new HorizontalScrollView(activity);
        yearChipScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout yearChips = new LinearLayout(activity);
        yearChips.setOrientation(LinearLayout.HORIZONTAL);
        yearChips.setPadding(0, 0, 0, activity.dp(6));

        yearChips.addView(createDialogQuickChip(activity, "1970–2010", () -> { minYearInput.setText("1970"); maxYearInput.setText("2010"); }));
        yearChips.addView(createDialogQuickChip(activity, "70er", () -> { minYearInput.setText("1970"); maxYearInput.setText("1979"); }));
        yearChips.addView(createDialogQuickChip(activity, "80er", () -> { minYearInput.setText("1980"); maxYearInput.setText("1989"); }));
        yearChips.addView(createDialogQuickChip(activity, "90er", () -> { minYearInput.setText("1990"); maxYearInput.setText("1999"); }));
        yearChips.addView(createDialogQuickChip(activity, "2000er", () -> { minYearInput.setText("2000"); maxYearInput.setText("2009"); }));
        yearChips.addView(createDialogQuickChip(activity, "Ab 2010", () -> { minYearInput.setText("2010"); maxYearInput.setText(""); }));
        yearChips.addView(createDialogQuickChip(activity, "Alle Jahre", () -> { minYearInput.setText(""); maxYearInput.setText(""); }));
        yearChipScroll.addView(yearChips);
        layout.addView(yearChipScroll);

        // 7. Laufzeit (Minuten)
        TextView runtimeLabel = new TextView(activity);
        runtimeLabel.setText("7. Laufzeit eingrenzen (Minuten):");
        runtimeLabel.setTextColor(0xFFF5C518);
        runtimeLabel.setTextSize(13);
        runtimeLabel.setTypeface(Typeface.DEFAULT_BOLD);
        runtimeLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(runtimeLabel);

        LinearLayout runtimeRow = new LinearLayout(activity);
        runtimeRow.setOrientation(LinearLayout.HORIZONTAL);

        final EditText minRuntimeInput = new EditText(activity);
        minRuntimeInput.setHint("Min (z. B. 80)");
        minRuntimeInput.setText(criteria.minRuntime > 0 ? String.valueOf(criteria.minRuntime) : "");
        minRuntimeInput.setTextColor(0xFFFFFFFF);
        minRuntimeInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        runtimeRow.addView(minRuntimeInput);

        final EditText maxRuntimeInput = new EditText(activity);
        maxRuntimeInput.setHint("Max (z. B. 180)");
        maxRuntimeInput.setText(criteria.maxRuntime < 999 ? String.valueOf(criteria.maxRuntime) : "");
        maxRuntimeInput.setTextColor(0xFFFFFFFF);
        maxRuntimeInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        runtimeRow.addView(maxRuntimeInput);
        layout.addView(runtimeRow);

        // 7b. Altersfreigabe (FSK)
        TextView fskLabel = new TextView(activity);
        fskLabel.setText("Altersfreigabe (FSK):");
        fskLabel.setTextColor(0xFFF5C518);
        fskLabel.setTextSize(13);
        fskLabel.setTypeface(Typeface.DEFAULT_BOLD);
        fskLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(fskLabel);

        final int[] tempMinFsk = new int[] { criteria.minFsk };
        final int[] tempMaxFsk = new int[] { criteria.maxFsk };

        HorizontalScrollView fskScroll = new HorizontalScrollView(activity);
        fskScroll.setHorizontalScrollBarEnabled(false);
        final LinearLayout fskChips = new LinearLayout(activity);
        fskChips.setOrientation(LinearLayout.HORIZONTAL);
        fskChips.setPadding(0, 0, 0, activity.dp(6));

        final Object[][] fskOptions = new Object[][] {
            {"Alle (0–18)", 0, 18},
            {"Kinder (FSK 0)", 0, 0},
            {"Bis FSK 6", 0, 6},
            {"Bis FSK 12", 0, 12},
            {"Bis FSK 16", 0, 16},
            {"Nur FSK 18", 18, 18}
        };

        final Runnable[] renderFskHolder = new Runnable[1];
        renderFskHolder[0] = () -> {
            fskChips.removeAllViews();
            for (Object[] opt : fskOptions) {
                String label = (String) opt[0];
                int minF = (Integer) opt[1];
                int maxF = (Integer) opt[2];
                boolean isSel = (tempMinFsk[0] == minF && tempMaxFsk[0] == maxF);
                fskChips.addView(activity.createFilterPill(label, isSel, 0xFF8957E5, () -> {
                    tempMinFsk[0] = minF;
                    tempMaxFsk[0] = maxF;
                    renderFskHolder[0].run();
                }));
            }
        };
        renderFskHolder[0].run();
        fskScroll.addView(fskChips);
        layout.addView(fskScroll);

        // 8. Mindestbewertungen
        TextView ratingLabel = new TextView(activity);
        ratingLabel.setText("8. Mindest-Bewertungen:");
        ratingLabel.setTextColor(0xFFF5C518);
        ratingLabel.setTextSize(13);
        ratingLabel.setTypeface(Typeface.DEFAULT_BOLD);
        ratingLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(ratingLabel);

        LinearLayout ratingRow = new LinearLayout(activity);
        ratingRow.setOrientation(LinearLayout.HORIZONTAL);

        final EditText minImdbInput = new EditText(activity);
        minImdbInput.setHint("Min. IMDb (z. B. 7.5)");
        minImdbInput.setText(criteria.minImdbScore > 0 ? String.valueOf(criteria.minImdbScore) : "");
        minImdbInput.setTextColor(0xFFFFFFFF);
        minImdbInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        ratingRow.addView(minImdbInput);

        final EditText minRtInput = new EditText(activity);
        minRtInput.setHint("Min. Rotten % (z. B. 80)");
        minRtInput.setText(criteria.minRottenTomatoes > 0 ? String.valueOf(criteria.minRottenTomatoes) : "");
        minRtInput.setTextColor(0xFFFFFFFF);
        minRtInput.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        ratingRow.addView(minRtInput);
        layout.addView(ratingRow);

        final CheckBox consensusBox = new CheckBox(activity);
        consensusBox.setText("🎯 Goldener Konsens (IMDb ≥ 7.2 & Rotten Tomatoes ≥ 75%)");
        consensusBox.setTextColor(0xFFFFD700);
        consensusBox.setChecked(criteria.bulletproofConsensus);
        layout.addView(consensusBox);

        // 9. Sortierung
        TextView sortLabel = new TextView(activity);
        sortLabel.setText("9. Ranglisten-Sortierung:");
        sortLabel.setTextColor(0xFFF5C518);
        sortLabel.setTextSize(13);
        sortLabel.setTypeface(Typeface.DEFAULT_BOLD);
        sortLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(sortLabel);

        final int[] selectedSort = new int[] { criteria.sortMode };
        final LinearLayout sortRow = new LinearLayout(activity);
        sortRow.setOrientation(LinearLayout.HORIZONTAL);
        sortRow.setPadding(0, activity.dp(2), 0, activity.dp(6));

        final Runnable updateSortRow = () -> {
            sortRow.removeAllViews();
            sortRow.addView(activity.createFilterPill("🏆 IMDb", selectedSort[0] == MovieDatabase.SORT_IMDB, 0xFFF5C518, () -> {
                selectedSort[0] = MovieDatabase.SORT_IMDB;
            }));
            sortRow.addView(activity.createFilterPill("🍅 Rotten", selectedSort[0] == MovieDatabase.SORT_ROTTEN_TOMATOES, 0xFFFA320A, () -> {
                selectedSort[0] = MovieDatabase.SORT_ROTTEN_TOMATOES;
            }));
            sortRow.addView(activity.createFilterPill("💎 Perle", selectedSort[0] == MovieDatabase.SORT_PERLE, 0xFF00B4D8, () -> {
                selectedSort[0] = MovieDatabase.SORT_PERLE;
            }));
            sortRow.addView(activity.createFilterPill("📅 Neueste", selectedSort[0] == MovieDatabase.SORT_YEAR, 0xFF8B949E, () -> {
                selectedSort[0] = MovieDatabase.SORT_YEAR;
            }));
        };
        updateSortRow.run();
        layout.addView(sortRow);

        // 10. Zielanzahl der Filme (Default 100)
        TextView countLabel = new TextView(activity);
        countLabel.setText("10. Zielanzahl der Filme (immer auffüllen):");
        countLabel.setTextColor(0xFFF5C518);
        countLabel.setTextSize(13);
        countLabel.setTypeface(Typeface.DEFAULT_BOLD);
        countLabel.setPadding(0, activity.dp(10), 0, activity.dp(4));
        layout.addView(countLabel);

        final EditText targetCountInput = new EditText(activity);
        targetCountInput.setHint("Zielanzahl (z. B. 100)");
        targetCountInput.setText(String.valueOf(criteria.targetCount));
        targetCountInput.setTextColor(0xFFFFFFFF);
        layout.addView(targetCountInput);

        // In-dialog Big Action Button
        final Button applyInDialogBtn = new Button(activity);
        applyInDialogBtn.setText("✓ Filter anwenden & 100 Filme laden");
        applyInDialogBtn.setTextColor(0xFFFFFFFF);
        applyInDialogBtn.setTextSize(13);
        applyInDialogBtn.setTypeface(Typeface.DEFAULT_BOLD);
        applyInDialogBtn.setTransformationMethod(null);
        GradientDrawable applyGd = new GradientDrawable();
        applyGd.setCornerRadius(activity.dp(8));
        applyGd.setColor(0xFF238636); // Github Green
        applyInDialogBtn.setBackground(applyGd);

        LinearLayout.LayoutParams applyLp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, activity.dp(46));
        applyLp.setMargins(0, activity.dp(14), 0, activity.dp(8));
        applyInDialogBtn.setLayoutParams(applyLp);
        layout.addView(applyInDialogBtn);

        dialogScroll.addView(layout);
        builder.setView(dialogScroll);

        final Runnable applyAction = () -> {
            criteria.singleProviderId = selectedProviderId[0];
            criteria.excludeWatched = watchedBox.isChecked();
            if (watchedManager != null) {
                watchedManager.setExcludeWatched(activity, criteria.excludeWatched);
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
            criteria.bulletproofConsensus = consensusBox.isChecked();
            criteria.languageFilter = tempLangFilter[0];

            criteria.excludedCountries.clear();
            criteria.excludedCountries.addAll(tempExcludedCountries);

            criteria.compoundExclusions.clear();
            criteria.compoundExclusions.addAll(tempCompound);

            criteria.minFsk = tempMinFsk[0];
            criteria.maxFsk = tempMaxFsk[0];

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

            criteria.save(activity);

            activity.refreshUi();
            activity.triggerLiveFetchIfNeeded();
            Toast.makeText(activity, "Filter aktiv – lade dynamisch bis zu " + criteria.targetCount + " Filme...", Toast.LENGTH_SHORT).show();
        };

        builder.setPositiveButton("✓ Anwenden", (dialog, which) -> applyAction.run());

        builder.setNeutralButton("✕ Reset", (dialog, which) -> {
            criteria.reset();
            criteria.save(activity);
            if (watchedManager != null) {
                watchedManager.setExcludeWatched(activity, false);
            }
            activity.refreshUi();
            activity.triggerLiveFetchIfNeeded();
            Toast.makeText(activity, "Filter zurückgesetzt", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Abbrechen", null);

        final AlertDialog dialog = builder.create();
        applyInDialogBtn.setOnClickListener(v -> {
            applyAction.run();
            dialog.dismiss();
        });
        dialog.show();
    }

    public static void styleGenreStateBtn(MainActivity activity, Button btn, boolean active, int activeColor, int textColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(activity.dp(6));
        if (active) {
            gd.setColor(activeColor);
            btn.setTextColor(textColor);
            btn.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            gd.setColor(0xFF161B22);
            gd.setStroke(activity.dp(1), 0xFF30363D);
            btn.setTextColor(0xFF8B949E);
            btn.setTypeface(Typeface.DEFAULT);
        }
        btn.setBackground(gd);
    }

    public static View createDialogQuickChip(MainActivity activity, String text, Runnable onClick) {
        TextView chip = new TextView(activity);
        chip.setText(text);
        chip.setTextSize(11);
        chip.setTextColor(0xFFF0F6FC);
        chip.setPadding(activity.dp(8), activity.dp(4), activity.dp(8), activity.dp(4));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, activity.dp(4), 0);
        chip.setLayoutParams(lp);

        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(activity.dp(10));
        gd.setColor(0xFF21262D);
        gd.setStroke(activity.dp(1), 0xFF30363D);
        chip.setBackground(gd);

        chip.setOnClickListener(v -> onClick.run());
        return chip;
    }
}

