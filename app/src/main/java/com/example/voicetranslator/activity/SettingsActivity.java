package com.example.voicetranslator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.voicetranslator.R;
import com.example.voicetranslator.SpeechTranslatorApplication;

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

            ListPreference listPreference = findPreference(getString(R.string.default_mode_key));
            if (listPreference.getEntry() == null){
                listPreference.setValue(getString(R.string.mode_online));
            }

            listPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue == getString(R.string.mode_offline)){

                    Intent intent = LanguagesListActivity.getSettingsIntent(getContext());
                    startActivity(intent);

                    Toast.makeText(getContext().getApplicationContext(),
                            getString(R.string.offline_mode_warning), Toast.LENGTH_LONG).show();

                }
                return true;
            });

        }


        @Override
        public boolean onPreferenceTreeClick(Preference preference) {

            if (preference.getKey().equals(getResources().getString(R.string.languages_key))) {
                Intent intent = LanguagesListActivity.getSettingsIntent(getContext());
                startActivity(intent);
            }

            preference.setOnPreferenceChangeListener((preference1, newValue) -> {
                if (preference.getKey().equals(getString(R.string.default_mode_key))) {
                    SpeechTranslatorApplication.setDefaultTranslationMode((String) newValue);
                }
                return true;
            });

            return super.onPreferenceTreeClick(preference);

        }
    }
}