package com.example.voicetranslator.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.voicetranslator.model.Language;
import com.example.voicetranslator.R;
import com.example.voicetranslator.recognition.SpeechRecognitionListener;
import com.example.voicetranslator.translation.FirebaseTranslator;
import com.example.voicetranslator.translation.Translator;
import com.example.voicetranslator.translation.TranslatorFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;

import java.util.ArrayList;

public class TranslationActivity extends AppCompatActivity {

    private Language language1;
    private Language language2;

    private TextView textViewText1;
    private TextView textViewText2;

    private int mode;

    private TextView textViewLanguage1;
    private TextView textViewLanguage2;

    private ImageView imageViewMic1;
    private ImageView imageViewMic2;

    private ImageView settingsButton;

    private SpeechRecognizer speechRecognizer;

    private Translator translator;
    private TranslatorFactory translatorFactory;

    private TextToSpeech textToSpeech1;
    private TextToSpeech textToSpeech2;

    public static final int RECORD_AUDIO_REQUEST_CODE = 1;
    public static final int SELECT_LANGUAGE1_REQUEST_CODE = 2;
    public static final int SELECT_LANGUAGE2_REQUEST_CODE = 3;

    public static final int TRANSLATION_MODE_TO_FOREIGN = 1;
    public static final int TRANSLATION_MODE_TO_NATIVE = 2;

    private boolean recordAudioAccessGranted;

    public void setLanguage1(Language language1) {

        this.language1 = language1;
        languageOnChange(textViewLanguage1, language1);

    }

    public void setLanguage2(Language language2) {

        this.language2 = language2;
        languageOnChange(textViewLanguage2, language2);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        textViewText1 = findViewById(R.id.speech_text1);
        textViewText2 = findViewById(R.id.speech_text2);

        textViewLanguage1 = findViewById(R.id.language1_name);
        textViewLanguage2 = findViewById(R.id.language2_name);
        settingsButton = findViewById(R.id.settings_button);

        imageViewMic1 = findViewById(R.id.mic1);
        imageViewMic2 = findViewById(R.id.mic2);

        textViewLanguage1.setOnClickListener(this::languageOnClickListener);
        textViewLanguage2.setOnClickListener(this::languageOnClickListener);

        imageViewMic1.setOnTouchListener(this::micOnTouchListener);
        imageViewMic2.setOnTouchListener(this::micOnTouchListener);

        settingsButton.setOnClickListener(this::settingsOnClickListener);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(speechRecognitionListener());

        translatorFactory = new TranslatorFactory();

        setLanguage1(Language.defaultLanguage());
        setLanguage2(Language.getAllLanguages().get(0));

        recordAudioAccessGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_LANGUAGE1_REQUEST_CODE) {

                Bundle extras = data.getExtras();

                setLanguage1((Language) extras.getSerializable(LanguagesListActivity.LANGUAGE_EXTRA_NAME));

            } else if (requestCode == SELECT_LANGUAGE2_REQUEST_CODE) {

                Bundle extras = data.getExtras();

                setLanguage2((Language) extras.getSerializable(LanguagesListActivity.LANGUAGE_EXTRA_NAME));

            }
        }

    }

    private void languageOnChange(TextView textView, Language language) {

        textView.setText(language.getName());
        textView.setCompoundDrawablesWithIntrinsicBounds(language.getFlagId(), 0, 0, 0);

        if (textView.getId() == R.id.language1_name){
            textToSpeech2 = new TextToSpeech(this, i -> textToSpeech2.setLanguage(language1.getLocale()));
        }
        else if (textView.getId() == R.id.language2_name){
            textToSpeech1 = new TextToSpeech(this, i -> textToSpeech1.setLanguage(language2.getLocale()));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RECORD_AUDIO_REQUEST_CODE){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                recordAudioAccessGranted = true;
            }

        }

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

    private boolean micOnTouchListener(View view, MotionEvent motionEvent){

        if (!recordAudioAccessGranted){
            checkPermission();
            return false;
        }

        ImageView mic = (ImageView) view;

        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            mic.setImageResource(R.drawable.ic_baseline_mic_none_64);
            speechRecognizer.stopListening();
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            if (view.getId() == R.id.mic1){
                mode = TRANSLATION_MODE_TO_FOREIGN;}
            else if (view.getId() == R.id.mic2){
                mode = TRANSLATION_MODE_TO_NATIVE;}

            getCurrentTextSpeech().setHint(R.string.hint_speak);

            Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, getUsingLanguage().getLocale());

            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            mic.setImageResource(R.drawable.ic_baseline_mic_64);

            speechRecognizer.startListening(speechRecognizerIntent);

            return true;

        }

        return false;

    }

    private void settingsOnClickListener(View view){

        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }

    private void checkPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.INTERNET}, RECORD_AUDIO_REQUEST_CODE);
    }

    private SpeechRecognitionListener speechRecognitionListener(){
        return new SpeechRecognitionListener() {

            @Override
            public void onBeginningOfSpeech() {

                TextView currentTextSpeech = getCurrentTextSpeech();
                currentTextSpeech.setHint(R.string.hint_listening);

            }

            @Override
            public void onError(int i) {

                TextView currentTextSpeech = getCurrentTextSpeech();
                currentTextSpeech.setText(null);
                currentTextSpeech.setHint(R.string.hint_start);

                FirebaseCrashlytics.getInstance().recordException(new Exception("Recognition on error, code: ".concat(String.valueOf(i))));

            }

            @Override
            public void onResults(Bundle bundle) {

                recognitionOnResult(bundle);

            }

            @Override
            public void onPartialResults(Bundle bundle) {

                TextView currentTextSpeech = getCurrentTextSpeech();
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                currentTextSpeech.setText(data.get(0));

            }
        };
    }

    private void recognitionOnResult(Bundle bundle) {

        TextView currentTextSpeech = getCurrentTextSpeech();
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String recognizedText = data.get(0);

        currentTextSpeech.setText(recognizedText);

        if (mode == TRANSLATION_MODE_TO_FOREIGN){

            translator = translatorFactory.getTranslator(currentTextSpeech.getContext(), language1, language2)
                    .addOnResultListener(s -> {
                        textViewText2.setText(s);
                        textToSpeech1.speak(s, TextToSpeech.QUEUE_ADD, bundle, null);})
                    .addOnErrorListener(e -> {
                        textViewText2.setText(e.toString());
                        FirebaseCrashlytics.getInstance().recordException(e);
                    });

        }
        else if (mode == TRANSLATION_MODE_TO_NATIVE){

            translator = translatorFactory.getTranslator(currentTextSpeech.getContext(), language1, language2)
                    .addOnResultListener(s -> {
                        textViewText1.setText(s);
                        textToSpeech2.speak(s, TextToSpeech.QUEUE_ADD, bundle, null);})
                    .addOnErrorListener(e -> {
                        textViewText1.setText(e.toString());
                        FirebaseCrashlytics.getInstance().recordException(e);
                    });

        }

        translator.translate(recognizedText);

    }

    private TextView getCurrentTextSpeech() {
        return mode == TRANSLATION_MODE_TO_FOREIGN ? textViewText1 : textViewText2;
    }

    private Language getUsingLanguage() {
        return mode == TRANSLATION_MODE_TO_FOREIGN?language1:language2;
    }

}