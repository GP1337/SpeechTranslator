package com.example.voicetranslator.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.voicetranslator.Language;
import com.example.voicetranslator.R;

public class TranslationActivity extends AppCompatActivity {

    private Language language1;
    private Language language2;

    private TextView textViewLanguage1;
    private TextView textViewLanguage2;

    private ImageView settingsButton;

    public static final int SELECT_LANGUAGE1_REQUEST_CODE = 2;
    public static final int SELECT_LANGUAGE2_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_translation);


        textViewLanguage1 = findViewById(R.id.language1_name);
        textViewLanguage2 = findViewById(R.id.language2_name);
        settingsButton = findViewById(R.id.settings_button);

        textViewLanguage1.setOnClickListener(this::languageOnClickListener);
        textViewLanguage2.setOnClickListener(this::languageOnClickListener);

        settingsButton.setOnClickListener(this::settingsOnClickListener);

    }

    private void languageOnClickListener(View view){

        Intent intent = LanguagesListActivity.getSelectIntent(this);

        if (view.getId() == R.id.language1_name){
            startActivityForResult(intent, SELECT_LANGUAGE1_REQUEST_CODE);
        }
        else if (view.getId() == R.id.language2_name){
            startActivityForResult(intent, SELECT_LANGUAGE2_REQUEST_CODE);
        }

    }

    private void settingsOnClickListener(View view){

        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_LANGUAGE1_REQUEST_CODE){

            Bundle extras = data.getExtras();

            language1 = (Language) extras.getSerializable(LanguagesListActivity.LANGUAGE_EXTRA_NAME);

            textViewLanguage1.setText(language1.getName());
            textViewLanguage1.setCompoundDrawablesWithIntrinsicBounds(language1.getFlagId(), 0, 0, 0);

        }
        else if (requestCode == SELECT_LANGUAGE2_REQUEST_CODE){

            Bundle extras = data.getExtras();

            language2 = (Language) extras.getSerializable(LanguagesListActivity.LANGUAGE_EXTRA_NAME);

            textViewLanguage2.setText(language2.getName());
            textViewLanguage2.setCompoundDrawablesWithIntrinsicBounds(language2.getFlagId(), 0, 0, 0);

        }


    }

}