package com.mins01.androidassist;

import android.content.ComponentName;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAndSetAssist();
        setContentView(R.layout.activity_main);
        Log.v("@MainActivity","onCreate");
        finish();
    }

    private void checkAndSetAssist(){
        String assistant = Settings.Secure.getString(getContentResolver(),"voice_interaction_service");
        boolean areWeGood = false;

        if(assistant!=null || !assistant.equals("")){

            ComponentName cn = ComponentName.unflattenFromString(assistant);
            if(cn !=null && cn.getPackageName().equals(getPackageName())){
                areWeGood = true;
            }
        }

        if(areWeGood){
            Toast.makeText(this,R.string.msg_active,Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this,R.string.msg_activate,Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS));
        }


//        registerAssist();
        finish();
    }
}
