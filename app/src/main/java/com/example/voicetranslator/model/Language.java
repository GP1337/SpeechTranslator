package com.example.voicetranslator.model;

import androidx.preference.ListPreference;

import com.example.voicetranslator.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Language implements Serializable {

    private Locale locale;
    private String name;

    private int id;
    private int flagId;
    private boolean modelDownloaded;
    private boolean modelDownloading;

    private static List<Language> languageList;
    private static FirebaseModelManager modelManager = FirebaseModelManager.getInstance();

    static {

        languageList = new ArrayList<>();

        languageList.add(new Language(Locale.ENGLISH, FirebaseTranslateLanguage.EN, R.drawable.flag_en, "English"));
        languageList.add(new Language(new Locale("ru", "RU"), FirebaseTranslateLanguage.RU, R.drawable.flag_ru, "Русский"));
        languageList.add(new Language(Locale.FRENCH, FirebaseTranslateLanguage.FR, R.drawable.flag_fr, "Le français"));
        languageList.add(new Language(Locale.GERMAN, FirebaseTranslateLanguage.DE, R.drawable.flag_fr, "Deutsch"));
        languageList.add(new Language(Locale.ITALIAN, FirebaseTranslateLanguage.IT, R.drawable.flag_fr, "italiano"));
        languageList.add(new Language(Locale.JAPANESE, FirebaseTranslateLanguage.JA, R.drawable.flag_fr, "日本語"));

    }

    private Language(Locale locale, int id, int flagId, String name) {

        this.locale = locale;
        this.id = id;
        this.flagId = flagId;
        this.name = name;

        FirebaseTranslateRemoteModel remoteModel = new FirebaseTranslateRemoteModel.Builder(id).build();

        Task<Boolean> booleanTask = modelManager.isModelDownloaded(remoteModel);

        booleanTask.addOnSuccessListener(aBoolean -> this.modelDownloaded = aBoolean);

    }

    public Locale getLocale() {
        return locale;
    }

    public int getFlagId() {
        return flagId;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isModelDownloaded() {
        return modelDownloaded;
    }

    public boolean isModelDownloading() {
        return modelDownloading;
    }

    public void setModelDownloading(boolean modelDownloading) {
        this.modelDownloading = modelDownloading;
    }

    public void setModelDownloaded(boolean modelDownloaded) {
        this.modelDownloaded = modelDownloaded;
    }

    public static List<Language> getAllLanguages(){

       return languageList;

    }

    public static Language defaultLanguage(){

        List<Language> list = getAllLanguages();

        Optional<Language> optionalLanguage = list.stream().filter(o -> o.getLocale().equals(Locale.getDefault())).findFirst();

        return optionalLanguage.orElseGet(() -> list.get(0));

    }

    public static Language getEnglish(){
        return languageList.get(0);
    }

    public boolean isDeletable(){
        return this != getEnglish();
    }

    public static Language getById(int id){

        Optional<Language> optionalLanguage = languageList.stream().filter(language -> {return language.getId() == id;}).findFirst();

        return optionalLanguage.orElseGet(() -> null);

    }

}
