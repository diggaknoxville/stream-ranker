package com.streamranker.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Predefined 1-click mood / profile filter configurations.
 */
public final class FilterPreset {

    public static final String ID_DEFAULT = "default";
    public static final String ID_FEIERABEND = "feierabend";
    public static final String ID_DATE_NIGHT = "date_night";
    public static final String ID_FAMILIE = "familie";
    public static final String ID_MINDFUCK = "mindfuck";
    public static final String ID_PERLEN = "perlen";
    public static final String ID_KLASSIKER = "klassiker";

    public final String id;
    public final String label;
    public final String description;
    public final int accentColor;

    public FilterPreset(String id, String label, String description, int accentColor) {
        this.id = id;
        this.label = label;
        this.description = description;
        this.accentColor = accentColor;
    }

    public static List<FilterPreset> getAll() {
        List<FilterPreset> list = new ArrayList<>();
        list.add(new FilterPreset(ID_DEFAULT, "✨ Standard", "Alle Filter zurücksetzen", 0xFF388BFD));
        list.add(new FilterPreset(ID_FEIERABEND, "🍿 Feierabend", "Popcorn & Action • Max. 115 Min • FSK ≤ 16", 0xFFFA7D00));
        list.add(new FilterPreset(ID_DATE_NIGHT, "🍷 Date Night", "Mitreißend & Anspruchsvoll • IMDb ≥ 7.2", 0xFFE50914));
        list.add(new FilterPreset(ID_FAMILIE, "👨‍👩‍👧 Familie", "Für Groß & Klein • Bis FSK 6 • Animation & Abenteuer", 0xFF2EA043));
        list.add(new FilterPreset(ID_MINDFUCK, "🧠 Mindfuck", "Unerwartete Wendungen • Mystery, Sci-Fi & Thriller", 0xFF8957E5));
        list.add(new FilterPreset(ID_PERLEN, "💎 Perlen", "Versteckte Geheimtipps • Hoher Score & wenig Mainstream", 0xFF00B4D8));
        list.add(new FilterPreset(ID_KLASSIKER, "📻 Klassiker", "Bis 1999 • Meilensteine der Filmgeschichte", 0xFFF5C518));
        return list;
    }

    public static void apply(String presetId, FilterCriteria criteria) {
        if (criteria == null) return;
        criteria.activePresetId = presetId != null ? presetId : ID_DEFAULT;

        // Reset base filter values first
        criteria.includedGenres.clear();
        criteria.excludedGenres.clear();
        criteria.minYear = 0;
        criteria.maxYear = 9999;
        criteria.minRuntime = 0;
        criteria.maxRuntime = 999;
        criteria.minFsk = 0;
        criteria.maxFsk = 18;
        criteria.minImdbScore = 0.0;
        criteria.minImdbVotes = 0;
        criteria.minRottenTomatoes = 0;
        criteria.excludeDoku = false;
        criteria.excludeBollywood = false;
        criteria.excludeAnime = false;
        criteria.excludeShorts = false;
        criteria.excludeStandup = false;
        criteria.sortMode = MovieDatabase.SORT_IMDB;

        switch (criteria.activePresetId) {
            case ID_FEIERABEND:
                criteria.maxRuntime = 115;
                criteria.maxFsk = 16;
                criteria.minImdbScore = 6.8;
                criteria.excludeDoku = true;
                criteria.excludeShorts = true;
                criteria.includedGenres.add("Action");
                criteria.includedGenres.add("Komödie");
                criteria.includedGenres.add("Abenteuer");
                criteria.includedGenres.add("Science-Fiction");
                break;

            case ID_DATE_NIGHT:
                criteria.minImdbScore = 7.2;
                criteria.excludeDoku = true;
                criteria.excludeShorts = true;
                criteria.excludeBollywood = true;
                criteria.includedGenres.add("Drama");
                criteria.includedGenres.add("Romantik");
                criteria.includedGenres.add("Komödie");
                criteria.includedGenres.add("Mystery");
                break;

            case ID_FAMILIE:
                criteria.maxFsk = 6;
                criteria.minImdbScore = 6.5;
                criteria.excludeDoku = true;
                criteria.excludeStandup = true;
                criteria.includedGenres.add("Animation");
                criteria.includedGenres.add("Familie");
                criteria.includedGenres.add("Abenteuer");
                break;

            case ID_MINDFUCK:
                criteria.minImdbScore = 7.2;
                criteria.minRottenTomatoes = 70;
                criteria.includedGenres.add("Mystery");
                criteria.includedGenres.add("Thriller");
                criteria.includedGenres.add("Science-Fiction");
                criteria.excludedGenres.add("Komödie");
                criteria.excludedGenres.add("Animation");
                break;

            case ID_PERLEN:
                criteria.minImdbScore = 7.0;
                criteria.sortMode = MovieDatabase.SORT_PERLE;
                break;

            case ID_KLASSIKER:
                criteria.maxYear = 1999;
                criteria.minImdbScore = 7.5;
                criteria.sortMode = MovieDatabase.SORT_IMDB;
                break;

            case ID_DEFAULT:
            default:
                criteria.activePresetId = ID_DEFAULT;
                break;
        }
    }
}

