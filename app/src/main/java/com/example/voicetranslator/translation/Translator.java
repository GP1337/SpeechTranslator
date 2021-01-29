package com.example.voicetranslator.translation;
import com.google.android.gms.tasks.Task;

public interface Translator {

    public Task<String> translate(String text);

}
