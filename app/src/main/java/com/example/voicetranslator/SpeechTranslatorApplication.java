package com.example.voicetranslator;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SpeechTranslatorApplication extends Application {

    private static String defaultTranslationMode;

    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SpeechTranslatorApplication.defaultTranslationMode = preferences.getString(context.getString(R.string.default_mode_key), context.getString(R.string.mode_offline));

    }

    public static String getDefaultTranslationMode() {
        return defaultTranslationMode;
    }

    public static void setDefaultTranslationMode(String defaultTranslationMode) {
        SpeechTranslatorApplication.defaultTranslationMode = defaultTranslationMode;
    }
}
