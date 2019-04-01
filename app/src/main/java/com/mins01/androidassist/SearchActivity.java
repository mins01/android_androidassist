package com.mins01.androidassist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.mins01.java.PickupKeywords.TextInfo;
import com.mins01.java.PickupKeywords.WordInfo;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    public ArrayList<TextInfo> tis = null;
    public ArrayList<WordInfo> wis = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Intent intent = getIntent();
        syncBundle(intent);


        this.findViewById(R.id.btnSearchGoogle).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Log.v("@onClick",this.getClass().getName()+"."+new Object(){}.getClass().getEnclosingMethod().getName());
                        try {
                            openURL(createUrlGoogle(getCheckedWords()));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        Log.v("@on","onResume");
        super.onResume();
        syncBundle(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.v("@on","onNewIntent");
        super.onNewIntent(intent);
        if(intent != null){
            setIntent(intent);
        }

    }
    public void syncBundle(@Nullable Intent intent){
        if(intent == null){
            return;
        }
//        Intent intent = getIntent();
        if(intent.hasExtra("tis")){
            tis = (ArrayList<TextInfo> ) intent.getSerializableExtra("tis");
        }
        if(intent.hasExtra("wis")){
            wis = (ArrayList<WordInfo> ) intent.getSerializableExtra("wis");
        }
        if(wis != null){
            appendWords(wis);
        }
    }

    public void syncTeQuery(){
        EditText etQuery = (EditText) findViewById(R.id.etQuery);
        ArrayList<String> words = getCheckedWords();
        StringBuilder sb = new StringBuilder();
        for (String s : words)
        {
            sb.append(s);
            sb.append(" ");
        }
        String q = sb.toString().trim();
        etQuery.setText(q);
    }

    public void appendWords(ArrayList<WordInfo> wis){
        LinearLayout lLayoutWords = (LinearLayout)findViewById(R.id.lLayoutWords);
        lLayoutWords.removeAllViews();
        for(int i=0,m=Math.min(20,wis.size());i<m;i++){
            WordInfo wi = wis.get(i);
            Switch sw = new Switch(this);
            sw.setChecked(false);
            sw.setText(wi.word);
            sw.setTextAppearance(R.style.TextAppearance_AppCompat_Body1);
            sw.setTextOff("off");
            sw.setTextOn("on");
            sw.setContentDescription("searchWords");
            sw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    syncTeQuery();
                }
            });
            lLayoutWords.addView(sw);
        }
    }

    public ArrayList<String> getCheckedWords(){
        LinearLayout lLayoutWords = (LinearLayout)findViewById(R.id.lLayoutWords);

        ArrayList<View> arr = new ArrayList<>();
        lLayoutWords.findViewsWithText(arr,"searchWords", View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        ArrayList<String> words = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for(int i=0,m=arr.size();i<m;i++){
            Switch sw =(Switch)arr.get(i);
            if( sw.isChecked() ){
                words.add((String) sw.getText());
            }
        }
        return words;
    }
    public String createUrlGoogle(ArrayList<String> words) throws UnsupportedEncodingException {
        EditText etQuery = (EditText) findViewById(R.id.etQuery);
        String q = etQuery.getText().toString();
        String url = "https://www.google.com/search?q="+ URLEncoder.encode(q,"utf-8");
        return url;
    }

    public void openURL(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

}
