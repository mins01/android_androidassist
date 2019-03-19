package com.mins01.androidassist;

import android.service.voice.VoiceInteractionService;
import android.util.Log;

public class AssistLoggerService extends VoiceInteractionService {
    @Override
    public void onReady() {
        super.onReady();
        Log.v("@AssistLoggerService","onReady");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("@AssistLoggerService","onCreate");
    }
}
