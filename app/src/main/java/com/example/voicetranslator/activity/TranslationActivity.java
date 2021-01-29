package com.example.voicetranslator.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeechService;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.voicetranslator.model.Language;
import com.example.voicetranslator.R;
import com.example.voicetranslator.recognition.SpeechRecognitionListener;
import com.example.voicetranslator.translation.FirebaseTranslator;
import com.example.voicetranslator.translation.Translator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

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
    private TextToSpeech textToSpeech;

    public static final int RECORD_AUDIO_REQUEST_CODE = 1;
    public static final int SELECT_LANGUAGE1_REQUEST_CODE = 2;
    public static final int SELECT_LANGUAGE2_REQUEST_CODE = 3;

    public static final int TRANSLATION_MODE_TO_FOREIGN = 1;
    public static final int TRANSLATION_MODE_TO_NATIVE = 2;

    private boolean recordAudioAccessGranted;

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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == SELECT_LANGUAGE1_REQUEST_CODE) {

                Bundle extras = data.getExtras();

                language1 = (Language) extras.getSerializable(LanguagesListActivity.LANGUAGE_EXTRA_NAME);

                textViewLanguage1.setText(language1.getName());
                textViewLanguage1.setCompoundDrawablesWithIntrinsicBounds(language1.getFlagId(), 0, 0, 0);

            } else if (requestCode == SELECT_LANGUAGE2_REQUEST_CODE) {

                Bundle extras = data.getExtras();

                language2 = (Language) extras.getSerializable(LanguagesListActivity.LANGUAGE_EXTRA_NAME);

                textViewLanguage2.setText(language2.getName());
                textViewLanguage2.setCompoundDrawablesWithIntrinsicBounds(language2.getFlagId(), 0, 0, 0);

            }
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
            mic.setImageResource(R.drawable.microphone_off);
            speechRecognizer.stopListening();
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

            if (view.getId() == R.id.mic1){
                mode = TRANSLATION_MODE_TO_FOREIGN;}
            else if (view.getId() == R.id.mic2){
                mode = TRANSLATION_MODE_TO_NATIVE;}


            Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale(language1.getCode()));
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            mic.setImageResource(R.drawable.microphone_on);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
        }
    }

    private SpeechRecognitionListener speechRecognitionListener(){

        return new SpeechRecognitionListener() {

            @Override
            public void onBeginningOfSpeech() {

                TextView currentTextSpeech = getCurrentTextSpeech();
                currentTextSpeech.setText(R.string.listening);

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResults(Bundle bundle) {

                TextView currentTextSpeech = getCurrentTextSpeech();
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                String recognizedText = data.get(0);

                currentTextSpeech.setText(recognizedText);

                if (mode == TRANSLATION_MODE_TO_FOREIGN){

                    translator = new FirebaseTranslator(currentTextSpeech.getContext(), language1, language2);

                    Task<String> task = translator.translate(recognizedText);

                    task.addOnSuccessListener(s -> {textViewText2.setText(s);});

                    task.addOnSuccessListener(s -> {
                        textViewText2.setText(s);
                        textToSpeech = new TextToSpeech(currentTextSpeech.getContext(), i -> textToSpeech.setLanguage(new Locale(language2.getCode())));
                        textToSpeech.speak(s, TextToSpeech.QUEUE_ADD, bundle, null);});

                }
                else if (mode == TRANSLATION_MODE_TO_NATIVE){

                    translator = new FirebaseTranslator(currentTextSpeech.getContext(), language2, language1);

                    Task<String> task = translator.translate(recognizedText);

                    task.addOnSuccessListener(s -> {
                        textViewText1.setText(s);
                        textToSpeech = new TextToSpeech(currentTextSpeech.getContext(), i -> textToSpeech.setLanguage(new Locale(language1.getCode())));
                        textToSpeech.speak(s, TextToSpeech.QUEUE_ADD, bundle, null);});

                }


            }

            @Override
            public void onPartialResults(Bundle bundle) {

                TextView currentTextSpeech = getCurrentTextSpeech();
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                currentTextSpeech.setText(data.get(0));

            }
        };
    }

    private TextView getCurrentTextSpeech() {
        return mode == TRANSLATION_MODE_TO_FOREIGN ? textViewText1 : textViewText2;
    }


}