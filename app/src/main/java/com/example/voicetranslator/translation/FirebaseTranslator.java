package com.example.voicetranslator.translation;

import android.content.Context;

import com.example.voicetranslator.model.Language;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class FirebaseTranslator extends Translator{

    private com.google.mlkit.nl.translate.Translator translator;

    public FirebaseTranslator(Context context, Language languageSrc, Language languageTarget) {

        this.context = context;
        this.languageSrc = languageSrc;
        this.languageTarget = languageTarget;

        FirebaseApp.initializeApp(context);

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(languageSrc.getId())
                        .setTargetLanguage(languageTarget.getId())
                        .build();

        this.translator = Translation.getClient(options);;

    }

    @Override
    public void translate(String text) {

        Task<String> task = translator.translate(text);

        task.addOnSuccessListener(s -> onResultListener.onResult(s));
        task.addOnFailureListener(e -> onErrorListener.onError(e));

    }

    @Override
    public boolean useInternet() {
        return false;
    }
}
