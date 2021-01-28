package com.example.voicetranslator.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.voicetranslator.ModelManager;
import com.example.voicetranslator.R;
import com.example.voicetranslator.SpeechRecognitionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final int RECORD_AUDIO_REQUEST_CODE = 1;
    private SpeechRecognizer speechRecognizer;
    private FirebaseTranslator translator;
    private TextView text;
    private ImageView mic;
    private ImageView settings;
    private ModelManager modelManager;
    private TextToSpeech textToSpeech;

    private boolean recordAudioAccessGranted;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        FirebaseApp.initializeApp(this);

        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(FirebaseTranslateLanguage.RU)
                        .setTargetLanguage(FirebaseTranslateLanguage.EN)
                        .build();
        translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

        modelManager = new ModelManager();
        modelManager.checkAndDownloadModel(translator);

        text = findViewById(R.id.text);
        mic = findViewById(R.id.button);
        settings = findViewById(R.id.settings);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener() {
            @Override
            public void onBeginningOfSpeech() {
                text.setHint("Listening...");
            }

            @Override
            public void onError(int i) {
                System.out.println(i);
            }

            @Override
            public void onResults(Bundle bundle) {

                text.setHint("Translating");

                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                translator.translate(data.get(0))
                        .addOnSuccessListener(
                                new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(@NonNull String translatedText) {

                                        text.setText(translatedText);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            textToSpeech.speak(translatedText, TextToSpeech.QUEUE_ADD, bundle, null);
                                        }

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        text.setText(e.toString());
                                    }
                                });

            }
        });

        mic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (recordAudioAccessGranted) {

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        mic.setImageResource(R.drawable.microphone_off);
                        speechRecognizer.stopListening();
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        mic.setImageResource(R.drawable.microphone_on);
                        speechRecognizer.startListening(speechRecognizerIntent);
                        return true;
                    }
                } else {

                    checkPermission();

                }
                return false;
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setClass(MainActivity.this.getApplicationContext(), SettingsActivity.class);

                startActivity(intent);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
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

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_REQUEST_CODE);
        }
    }

}