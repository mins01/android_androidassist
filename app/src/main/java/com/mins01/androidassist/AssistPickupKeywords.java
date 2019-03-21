package com.mins01.androidassist;

import android.app.assist.AssistStructure;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;
import com.mins01.java.PickupKeywords.PickupKeywords;
import com.mins01.java.PickupKeywords.TextInfo;

import java.util.ArrayList;

public class AssistPickupKeywords extends PickupKeywords {
    String packagename = "";
    JsonObject conf_scores_for_packagename  = null;

    public AssistPickupKeywords(){
        init();
    }
    private void init(){
        conf_scores.addProperty("android.view.View",1);
        conf_scores.addProperty("android.widget.Edittext",200);
        conf_scores.addProperty("android.widget.Button",0);

        conf_scores.addProperty("node",0);
        conf_scores.addProperty("input",200);
        Log.v("@AssistPickupKeywords",conf_scores.toString());
        numeric_multiple = 0.1;
    }
    public ArrayList<NodeInfo> getNodeInfoByViewNode(AssistStructure.ViewNode viewNode) throws Exception {
        ArrayList<NodeInfo> nis= new ArrayList<NodeInfo>();
        getNodeInfo(nis,viewNode);
        return nis;
    }

    public void getNodeInfo(ArrayList<NodeInfo> nis, AssistStructure.ViewNode viewNode){
        if (viewNode.getVisibility() != View.VISIBLE) {
            return;
        }

        String idEntry = viewNode.getIdEntry()!=null?viewNode.getIdEntry():"";
        String text = viewNode.getText()!=null?viewNode.getText().toString().trim():"";
        String contentDescription = viewNode.getContentDescription()!=null?viewNode.getContentDescription().toString():"";
        String tag = viewNode.getClassName()!=null?viewNode.getClassName().toString():"view";

        int score = conf_scores.has(tag) ? conf_scores.get(tag).getAsInt():1;
        NodeInfo ni = null;

        if(text != "" && text.length()>0 && viewNode.getTop() >= 0 && viewNode.getLeft() >= 0){
            ni = new NodeInfo();
            ni.tag = tag;
            ni.text = text;
            ni.idEntry = idEntry;
            ni.score = getScoreFromViewNode(viewNode,score);
            nis.add(ni);
//            Log.v("@ni",ni.toString());
        }
        for(int i2 =0,m2=viewNode.getChildCount();i2<m2;i2++) {
            getNodeInfo(nis,viewNode.getChildAt(i2));
        }
//        return ti;
    }
    private long getScoreFromViewNode( AssistStructure.ViewNode viewNode,long score){
        String pkn_id = packagename+"#"+viewNode.getIdEntry();
        if(conf_scores_for_packagename !=null && conf_scores_for_packagename.has(pkn_id)){

            score = conf_scores_for_packagename.get(pkn_id).getAsLong();
            Log.v("@getScoreFromViewNode",pkn_id+"="+score);
        }else{
            score = Math.round(viewNode.getWidth()*viewNode.getHeight()/100)*score;
        }


        return score;
    }


    public ArrayList<TextInfo> nodeInfoToTextInfo(ArrayList<NodeInfo> nis){
        ArrayList<TextInfo> tis = new ArrayList<TextInfo>();
        for (NodeInfo ni : nis){
            tis.add((TextInfo) ni);
        }
        return tis;
    }
}
