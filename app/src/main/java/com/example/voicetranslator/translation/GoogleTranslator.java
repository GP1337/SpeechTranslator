package com.example.voicetranslator.translation;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.voicetranslator.model.Language;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GoogleTranslator extends Translator{

    public GoogleTranslator(Context context, Language languageSrc, Language languageTarget) {

        this.context = context;
        this.languageSrc = languageSrc;
        this.languageTarget = languageTarget;

    }

    @Override
    public void translate(String text) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = null;
        try {
            url = "https://script.google.com/macros/s/AKfycbyswQaSSoMgMCWCDRMesSlF79obXjioqldfvOhKgc9TlL79vSa2chrN/exec" +
                    "?q=" + URLEncoder.encode(text, "UTF-8") +
                    "&target=" + languageTarget.getLocale().getLanguage() +
                    "&source=" + languageSrc.getLocale().getLanguage();
        } catch (UnsupportedEncodingException e) {
            onErrorListener.onError(e);
            return;
        }

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> onResultListener.onResult(response),
                error -> onErrorListener.onError(error));

        queue.add(stringRequest);

    }

}

