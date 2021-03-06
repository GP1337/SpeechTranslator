package com.example.voicetranslator.recognition;

import android.os.Bundle;
import android.speech.RecognitionListener;

public abstract class SpeechRecognitionListener implements RecognitionListener {

    @Override
    public void onReadyForSpeech(Bundle bundle){};

    @Override
    public void onRmsChanged(float v) {}

    @Override
    public void onBufferReceived(byte[] bytes){};

    @Override
    public void onEndOfSpeech(){};

    @Override
    public void onEvent(int i, Bundle bundle){};
}
