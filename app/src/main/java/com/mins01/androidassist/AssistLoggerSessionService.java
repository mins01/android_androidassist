package com.mins01.androidassist;

import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;
import android.util.Log;

public class AssistLoggerSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(Bundle bundle) {
        Log.v("@SessionService","onCreate");
        return (new AssistLoggerSession(this));
//        return null;
    }
}
