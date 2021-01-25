package com.example.voicetranslator;

import android.os.Bundle;
import android.speech.RecognitionListener;

public interface SpeechRecognitionListener extends RecognitionListener {

    @Override
    default void onReadyForSpeech(Bundle bundle){};

    @Override
    default void onRmsChanged(float v) {}

    @Override
    default void onBufferReceived(byte[] bytes){};

    @Override
    default void onEndOfSpeech(){};

    @Override
    default void onError(int i){};

    @Override
    default void onPartialResults(Bundle bundle){};

    @Override
    default void onEvent(int i, Bundle bundle){};
}
