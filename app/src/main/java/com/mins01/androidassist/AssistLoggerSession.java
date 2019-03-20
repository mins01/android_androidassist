package com.mins01.androidassist;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AssistLoggerSession extends VoiceInteractionSession {
    public Context context=null;
    private AssistLoggerController alc = new AssistLoggerController();
    public View view_assist_main;
    public AssistLoggerSession(Context context) {
        super(context);
        this.context = context;
        alc.context = context;
        Log.v("@AssistLoggerSession","AssistLoggerSession");
    }

    /**
     * 스크린샷 처리부
     * @param screenshot
     */
    @Override
    public void onHandleScreenshot(@Nullable Bitmap screenshot) {
        Log.v("@AssistLoggerSession","onHandleScreenshot");
        super.onHandleScreenshot(screenshot);
        alc.onHandleScreenshot(screenshot);
    }


//    private Bundle lastData;
//    private AssistStructure lastStructure;
//    private AssistContent lastContent;
    /**
     * 화면 내용 처리부
     * @param data
     * @param structure
     * @param content
     */
    @Override
    public void onHandleAssist(@Nullable Bundle data, @Nullable AssistStructure structure, @Nullable AssistContent content) {
        Log.v("@AssistLoggerSession","onHandleAssist");

        super.onHandleAssist(data, structure, content);
        Log.v("@onHandleAssist","tvTest clear");
//        Toast.makeText(getContext(),"onHandleAssist",Toast.LENGTH_LONG).show();
        TextView tvTest = view_assist_main.findViewById(R.id.tvTest);
        tvTest.setText("");

//        lastData = data;
//        lastStructure = structure;
//        lastContent = content;
        View view_assist_main = getLayoutInflater().inflate(R.layout.assist_main,null);
        alc.onHandleAssist(data, structure, content);
        try {
//            alc.onHandleAssist();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateContentView() {
        Log.v("@AssistLoggerSession","onCreateContentView");
        super.onCreateContentView();
        view_assist_main = getLayoutInflater().inflate(R.layout.assist_main,null);
        setContentView(view_assist_main);
        ((TextView) view_assist_main.findViewById(R.id.tvTest)).setMovementMethod(ScrollingMovementMethod.getInstance());


//        alc.view_assist_main = view_assist_main;
        alc.onCreateContentView(view_assist_main);



        return null;
    }
}
