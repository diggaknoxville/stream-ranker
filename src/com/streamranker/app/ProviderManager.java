package com.streamranker.app;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProviderManager {
    private static final String PREF_NAME = "stream_ranker_prefs";
    private static final String KEY_ACTIVE_PROVIDERS = "active_providers";
    private static final String KEY_SEEN_INSTALLED = "seen_installed_providers";
    private static final String KEY_INITIALIZED = "providers_initialized";

    private static ProviderManager instance;

    private final Set<StreamingProvider> installedProviders = new HashSet<>();
    private final Set<StreamingProvider> activeProviders = new HashSet<>();

    public static synchronized ProviderManager getInstance() {
        if (instance == null) {
            instance = new ProviderManager();
        }
        return instance;
    }

    private ProviderManager() {}

    public synchronized void init(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean initialized = prefs.getBoolean(KEY_INITIALIZED, false);

        installedProviders.clear();
        for (StreamingProvider provider : StreamingProvider.values()) {
            if (provider.isInstalled(context)) {
                installedProviders.add(provider);
            }
        }

        if (!initialized) {
            // First run: pre-select all installed providers!
            activeProviders.clear();
            if (!installedProviders.isEmpty()) {
                activeProviders.addAll(installedProviders);
            } else {
                // If phone has no streaming app installed yet, default to Netflix + Prime + WOW + YouTube
                activeProviders.add(StreamingProvider.NETFLIX);
                activeProviders.add(StreamingProvider.PRIME_VIDEO);
                activeProviders.add(StreamingProvider.WOW);
            }
            activeProviders.add(StreamingProvider.YOUTUBE);
            savePreferences(context);
            prefs.edit().putBoolean(KEY_INITIALIZED, true).putBoolean("v1_2_providers_seeded", true).apply();
        } else {
            // Load saved active providers
            Set<String> savedActive = prefs.getStringSet(KEY_ACTIVE_PROVIDERS, null);
            activeProviders.clear();
            if (savedActive != null) {
                for (String id : savedActive) {
                    activeProviders.add(StreamingProvider.fromId(id));
                }
            }

            // Check if any NEW apps have been installed since last check
            Set<String> previouslySeen = prefs.getStringSet(KEY_SEEN_INSTALLED, Collections.emptySet());
            for (StreamingProvider installed : installedProviders) {
                if (!previouslySeen.contains(installed.getId())) {
                    // Brand new app installed on phone! Auto-activate it
                    activeProviders.add(installed);
                }
            }

            // Ensure YouTube & WOW are activated for users upgrading to v1.2
            if (!prefs.getBoolean("v1_2_providers_seeded", false)) {
                activeProviders.add(StreamingProvider.YOUTUBE);
                activeProviders.add(StreamingProvider.WOW);
                prefs.edit().putBoolean("v1_2_providers_seeded", true).apply();
            }

            savePreferences(context);
        }
    }

    public synchronized void rescan(Context context) {
        Set<StreamingProvider> newlyInstalled = new HashSet<>();
        for (StreamingProvider provider : StreamingProvider.values()) {
            boolean installed = provider.isInstalled(context);
            if (installed && !installedProviders.contains(provider)) {
                newlyInstalled.add(provider);
            }
            if (installed) {
                installedProviders.add(provider);
            } else {
                installedProviders.remove(provider);
            }
        }

        // Auto-activate newly installed apps
        if (!newlyInstalled.isEmpty()) {
            activeProviders.addAll(newlyInstalled);
            savePreferences(context);
        }
    }

    public synchronized boolean isInstalled(StreamingProvider provider) {
        return installedProviders.contains(provider);
    }

    public synchronized boolean isActive(StreamingProvider provider) {
        return activeProviders.contains(provider);
    }

    public synchronized void toggleActive(Context context, StreamingProvider provider) {
        if (activeProviders.contains(provider)) {
            // Don't allow deselecting all providers
            if (activeProviders.size() > 1) {
                activeProviders.remove(provider);
            }
        } else {
            activeProviders.add(provider);
        }
        savePreferences(context);
    }

    public synchronized Set<StreamingProvider> getActiveProviders() {
        return new HashSet<>(activeProviders);
    }

    public synchronized Set<StreamingProvider> getInstalledProviders() {
        return new HashSet<>(installedProviders);
    }

    private void savePreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> activeIds = new HashSet<>();
        for (StreamingProvider p : activeProviders) {
            activeIds.add(p.getId());
        }
        Set<String> seenIds = new HashSet<>();
        for (StreamingProvider p : installedProviders) {
            seenIds.add(p.getId());
        }

        prefs.edit()
            .putStringSet(KEY_ACTIVE_PROVIDERS, activeIds)
            .putStringSet(KEY_SEEN_INSTALLED, seenIds)
            .apply();
    }
}

