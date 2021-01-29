package com.example.voicetranslator.model;

import com.example.voicetranslator.R;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Language implements Serializable {

    private String code;
    private String name;

    private int id;
    private int flagId;
    private boolean modelDownloaded;

    private Language(String code, int id, int flagId, String name) {

        this.code = code;
        this.id = id;
        this.flagId = flagId;
        this.name = name;

    }

    public String getCode() {
        return code;
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

    public void setModelDownloaded(boolean modelDownloaded) {
        this.modelDownloaded = modelDownloaded;
    }

    public static List<Language> getAllLanguages(){

        List<Language> languageList = new ArrayList<>();

        languageList.add(new Language("ru", FirebaseTranslateLanguage.RU, R.drawable.flag_ru, "Русский"));
        languageList.add(new Language("en", FirebaseTranslateLanguage.EN, R.drawable.flag_en, "English"));
        languageList.add(new Language("fr", FirebaseTranslateLanguage.FR, R.drawable.flag_fr, "Le français"));

        return languageList;

    }

}
