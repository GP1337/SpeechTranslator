package com.example.voicetranslator.translation;

import android.content.Context;

import com.example.voicetranslator.model.Language;

public abstract class Translator {

    protected Context context;
    protected Language languageSrc;
    protected Language languageTarget;
    protected OnResultListener onResultListener;
    protected OnErrorListener onErrorListener;

    public abstract void translate(String text);

    public Translator addOnResultListener(OnResultListener onResultListener){

        this.onResultListener = onResultListener;

        return this;

    }

    public Translator addOnErrorListener(OnErrorListener onErrorListener){

        this.onErrorListener = onErrorListener;

        return this;

    }

    @FunctionalInterface
    public interface OnResultListener{

        void onResult(String result);

    }

    @FunctionalInterface
    public interface OnErrorListener{

        void onError(Exception e);

    }

}
