package com.example.voicetranslator.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.voicetranslator.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            PreferenceManager.setDefaultValues(this.getContext(), R.xml.root_preferences, true);
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            if (preference.getKey().equals(getResources().getString(R.string.languages_key))){

                Intent intent = LanguagesListActivity.getSettingsIntent(getContext());
                startActivity(intent);

            }

            return super.onPreferenceTreeClick(preference);

        }
    }
}