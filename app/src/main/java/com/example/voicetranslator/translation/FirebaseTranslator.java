package com.example.voicetranslator.translation;

import android.content.Context;

import com.example.voicetranslator.model.Language;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

public class FirebaseTranslator implements Translator{

    private com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator translator;

    public FirebaseTranslator(Context context, Language languageSrc, Language languageTarget) {

        FirebaseApp.initializeApp(context);

        FirebaseTranslatorOptions options =
                new FirebaseTranslatorOptions.Builder()
                        .setSourceLanguage(languageSrc.getId())
                        .setTargetLanguage(languageTarget.getId())
                        .build();

        this.translator = FirebaseNaturalLanguage.getInstance().getTranslator(options);

    }

    @Override
    public Task<String> translate(String text) {

        return translator.translate(text);

    }
}
