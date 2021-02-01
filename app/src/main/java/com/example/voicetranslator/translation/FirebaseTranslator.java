package com.example.voicetranslator.translation;

import android.content.Context;

import com.example.voicetranslator.model.Language;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class FirebaseTranslator extends Translator{

    private com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator translator;

    public FirebaseTranslator(Context context, Language languageSrc, Language languageTarget) {

        this.context = context;
        this.languageSrc = languageSrc;
        this.languageTarget = languageTarget;

        FirebaseApp.initializeApp(context);

        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(languageSrc.getId())
                        .setTargetLanguage(languageTarget.getId())
                        .build();

        this.translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

    }

    @Override
    public void translate(String text) {

        Task<String> task = translator.translate(text);

        task.addOnSuccessListener(s -> onResultListener.onResult(s));
        task.addOnFailureListener(e -> onErrorListener.onError(e));

    }
}
