package com.mins01.androidassist;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG  = "tag";
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

        isStoragePermissionGranted();

//        registerAssist();
        finish();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
