package com.mins01.androidassist;

import android.app.assist.AssistStructure;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mins01.java.PickupKeywords.PickupKeywords;
import com.mins01.java.PickupKeywords.TextInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class AssistPickupKeywords extends PickupKeywords {
    public String packagename = "";
    public JsonObject conf_scores_for_packagename  = null;
    public JsonObject conf_scores_for_custom_selector  = null;
    public int screenWidth = 0;
    public int screenHeight = 0;

    public AssistPickupKeywords(){

    }
    public void init(){
        super.init();
        search_tags = "h1,h2,h3,h4,h5,title,span,div,li,a,input[type=text][value],android_view_view,android_widget_textview,android_widget_edittext,android_webkit_webview";

//        conf_scores.addProperty("android.view.View",1);
//        conf_scores.addProperty("android.widget.EditText",200);
//        conf_scores.addProperty("android.webkit.WebView",100); //웹 뷰일 경우 타이틀값
//        conf_scores.addProperty("android.widget.Button",0);

        conf_scores.addProperty("android_view_view",1);
        conf_scores.addProperty("android_widget_textview",1);
        conf_scores.addProperty("android_widget_edittext",200);
        conf_scores.addProperty("android_webkit_webview",100); //웹 뷰일 경우 타이틀값
        conf_scores.addProperty("android_widget_button",0);

        conf_scores.addProperty("node",0);
        conf_scores.addProperty("input",200);
        Log.v("@AssistPickupKeywords",conf_scores.toString());
        numericWeight = 0; //숫자 가중치 줄임
        wordToLowerCase = true; //강제 소문자 처리
    }
    public Document getDomByViewNode(AssistStructure.ViewNode viewNode){
        Document doc = Jsoup.parse("<!doctype html><html><head><meta charset=\"utf-8\">" +
                "<style>" +
                "body *{display:block;box-sizing: border-box; outline:1px dashed #666; position: absolute;}" +
                "android_widget_edittext{outline:3px inset #abc; background-color:#abc;}"+
                "android_widget_button{outline: 3px outset #cba; background-color:#cba;}"+
                "android_widget_imageview,android_widget_image{ background-color:rgba(255,0,0,0.2); border-color:#f99;}"+
                "</style>" +
                "</head><body data-visibility=\"1\"></body></html>", "file://temp/local");
        Element p = doc.body();
        appendDom(doc,p,viewNode);
        return doc;
    }
    public void appendDom(Document doc,Element p,AssistStructure.ViewNode viewNode){
        String idEntry = viewNode.getIdEntry()!=null?viewNode.getIdEntry():"";
        String text = viewNode.getText()!=null?viewNode.getText().toString().trim():"";
        String contentDescription = viewNode.getContentDescription()!=null?viewNode.getContentDescription().toString():"";
        String tag = (viewNode.getClassName()!=null? viewNode.getClassName() :"view").replace(".","_").toLowerCase();

        int visibility = 1;
        if(viewNode.getTop()<0 || viewNode.getLeft() < 0
                //|| viewNode.getTop() > screenHeight || viewNode.getLeft() > screenWidth
                || viewNode.getVisibility() != View.VISIBLE){
            visibility = 0;
        }
        if(p.attr("data-visibility").equals("0")){ //부모값
            visibility = 0;
        }
        Element el = p.appendElement(tag);
        el.attr("data-top", String.valueOf(viewNode.getTop()));
        el.attr("data-left", String.valueOf(viewNode.getLeft()));
        el.attr("data-width", String.valueOf(viewNode.getWidth()));
        el.attr("data-height", String.valueOf(viewNode.getHeight()));
        el.attr("data-score-weight", String.valueOf((((double)viewNode.getWidth()/screenWidth)*(visibility)))); //화면 width 기준으로 view의 width의 비율을 구해서 가중치로 사용.
        el.attr("data-visibility", String.valueOf(visibility)); //화면 width 기준으로 view의 width의 비율을 구해서 가중치로 사용.

        StringBuilder sb = new StringBuilder();
        sb.append("top:"+String.valueOf(viewNode.getTop())+"px;");
        sb.append("left:"+String.valueOf(viewNode.getLeft())+"px;");
        sb.append("width:"+String.valueOf(viewNode.getWidth())+"px;");
        sb.append("height:"+String.valueOf(viewNode.getHeight())+"px;");
        if(visibility==0){
            sb.append("display:none;");
        }
        el.attr("style",sb.toString());

        if(text.length()>0){
            if(tag.equals("android_webkit_webview")){
                doc.title(text);
            }
            el.text(text);
        }
        if(idEntry.length()>0){ el.addClass(idEntry); }
        if(contentDescription.length()>0){ el.attr("title",contentDescription); }

//        p.appendChild(el);
        for(int i2 =0,m2=viewNode.getChildCount();i2<m2;i2++) {
            appendDom(doc,el,viewNode.getChildAt(i2));
        }
//        return ;
    }
    @Deprecated
    public ArrayList<NodeInfo> getNodeInfoByViewNode(AssistStructure.ViewNode viewNode) {
        ArrayList<NodeInfo> nis= new ArrayList<NodeInfo>();
        getNodeInfo(nis,viewNode);
        return nis;
    }
    @Deprecated
    public void getNodeInfo(ArrayList<NodeInfo> nis, AssistStructure.ViewNode viewNode){
//        Log.v("@ni_classname",viewNode.getClassName());
        if (viewNode.getVisibility() != View.VISIBLE) {
            return;
        }

        String idEntry = viewNode.getIdEntry()!=null?viewNode.getIdEntry():"";
        String text = viewNode.getText()!=null?viewNode.getText().toString().trim():"";
        String contentDescription = viewNode.getContentDescription()!=null?viewNode.getContentDescription().toString():"";
        String tag = viewNode.getClassName()!=null? viewNode.getClassName() :"view";

        int score = conf_scores.has(tag) ? conf_scores.get(tag).getAsInt():1;
        NodeInfo ni = null;

        if(text.length()>0 && viewNode.getTop() >= 0 && viewNode.getLeft() >= 0){
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
    @Deprecated
    private long getScoreFromViewNode( AssistStructure.ViewNode viewNode,long score){
        String pkn_id = packagename+"#"+viewNode.getIdEntry();
        Log.v("@pkn_id",pkn_id);
        score = Math.round(viewNode.getWidth()*viewNode.getHeight()/viewNode.getText().length())*score;
        if(conf_scores_for_packagename !=null && conf_scores_for_packagename.has(pkn_id)){

            score *= conf_scores_for_packagename.get(pkn_id).getAsLong();
            Log.v("@getScoreFromViewNode",pkn_id+"="+score);
        }



        return score;
    }

    @Deprecated
    public ArrayList<TextInfo> nodeInfoToTextInfo(ArrayList<NodeInfo> nis){
        ArrayList<TextInfo> tis = new ArrayList<TextInfo>();
        for (NodeInfo ni : nis){
            tis.add(ni);
        }
        return tis;
    }

    public ArrayList<TextInfo> getCustomTexts(){
        ArrayList<TextInfo> tis = new ArrayList<TextInfo>();
        if(conf_scores_for_custom_selector.has(packagename)){
            Log.v("@getCustomTexts","has packagename custom_selector");
            JsonObject pcs = conf_scores_for_custom_selector.get(packagename).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = pcs.entrySet();
            for(Map.Entry<String,JsonElement> entry : entrySet){
                String selector = entry.getKey();
                int score = entry.getValue().getAsInt();
                Elements els = this.doc.select(selector);
                for(Element el : els){
                    if(el.childNodeSize()>1){
                        continue;
                    }
                    if(el.hasAttr("data-visibility") && el.attr("data-visibility").equals("0")){ //invisibility skip
                        continue;
                    }
                    TextInfo ti = new TextInfo();
                    ti.tag = el.tagName();
                    if(ti.tag.equals("input")){
                        ti.text = el.hasAttr("value")?el.attr("value"):"";
                    }else{
                        ti.text = el.ownText();
                    }
                    if(ti.text.length()==0){continue;}
                    ti.score = score;
                    tis.add(ti);
                    Log.v("@ti_getCustomTexts","selector : "+selector+", ti:"+ti.toString());
                }
            }
        }
        return tis;
    }
}
