package com.mins01.androidassist;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mins01.java.PickupKeywords.TextInfo;
import com.mins01.java.PickupKeywords.WordInfo;

import java.util.ArrayList;


public class AssistLoggerController {

    public Bundle data;
    public AssistStructure structure;
    public AssistContent content;
    public String packagename = "";
    public Context context;
    public AssistPickupKeywords apk = new AssistPickupKeywords();
    public View view_assist_main = null;
    public AssistLoggerController(){

    }
    public void setAssistData(@Nullable Bundle data, @Nullable AssistStructure structure, @Nullable AssistContent content){
        if(data==null){
            Log.v("@setAssistData","data is null");
        }else{
            Log.v("@setAssistData","data is not null");
        }
        if(structure==null){
            Log.v("@setAssistData","structure is null");
        }else{
            Log.v("@setAssistData","structure is not null");
        }
        if(content==null){
            Log.v("@setAssistData","content is null");
        }else{
            Log.v("@setAssistData","content is not null");
        }
        this.data = data;
        this.structure = structure;
        this.content = content;
        if(structure != null){
            packagename = structure.getActivityComponent().getPackageName();
            Log.v("@setAssistData","packagename : "+packagename);
        }

    }
    public ArrayList<NodeInfo> getTextsFromStructure(AssistStructure structure) throws Exception {
        if(structure == null){
            throw new Exception("structure is null");
        }
        return apk.getNodeInfoByViewNode(structure.getWindowNodeAt(0).getRootViewNode());
    }
    public void onHandleAssist() throws Exception {
        actPickupKeyWords();
    }
    public void onCreateContentView(View view_assist_main){
        this.view_assist_main = view_assist_main;
        ((Button)view_assist_main.findViewById(R.id.buttonPickup)).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Log.v("@onClick",this.getClass().getName()+"."+new Object(){}.getClass().getEnclosingMethod().getName());
                        if(structure != null){
                            try {
                                actPickupKeyWords();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
        );
    }
    public void actPickupKeyWords() throws Exception {
        ArrayList<NodeInfo> nis = getTextsFromStructure(structure);
        ArrayList<TextInfo> tis = apk.nodeInfoToTextInfo(nis);
        ArrayList<WordInfo> wis = apk.getWords(tis);
        TextView tvTest = view_assist_main.findViewById(R.id.tvTest);
        tvTest.setText("");
        for(int i=0,m=nis.size();i<m;i++){
            NodeInfo ni = nis.get(i);
            Log.v("@ni",ni.toString());
//            tvTest.append(ni.toString()+"\n");
        }
//
//        for(int i=0,m=tis.size();i<m;i++){
//            TextInfo ti = tis.get(i);
//            Log.v("@ti",ti.toString());
//            tvTest.append(ti.toString()+"\n");
//        }
        for(int i=0,m=wis.size();i<m;i++){
            WordInfo wi = wis.get(i);
            Log.v("@wi",wi.toString());
//            tvTest.append(wi.toString()+"\n");
        }
        for(int i=0,m=Math.min(wis.size(),10);i<m;i++){
            WordInfo wi = wis.get(i);
//            Log.v("@wi",wi.toString());
            tvTest.append((i+1)+":"+wi.toString()+"\n");
        }
    }





}
