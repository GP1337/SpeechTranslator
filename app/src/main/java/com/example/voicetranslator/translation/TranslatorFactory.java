package com.example.voicetranslator.translation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import androidx.preference.PreferenceManager;

import com.example.voicetranslator.R;
import com.example.voicetranslator.model.Language;

public class TranslatorFactory {

    public Translator getTranslator(Context context, Language languageSrc, Language languageTarget){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String mode = preferences.getString(context.getString(R.string.default_mode_key), context.getString(R.string.mode_online));

        if (mode.equals(context.getString(R.string.mode_online)) && isNetworkConnected(context)){
            return new GoogleTranslator(context, languageSrc, languageTarget);
        }
        else {
            return new FirebaseTranslator(context, languageSrc, languageTarget);
        }

    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
