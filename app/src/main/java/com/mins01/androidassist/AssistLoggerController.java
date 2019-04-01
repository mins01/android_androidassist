package com.mins01.androidassist;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.service.voice.VoiceInteractionSession;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mins01.java.PickupKeywords.TextInfo;
import com.mins01.java.PickupKeywords.WordInfo;

import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;


public class AssistLoggerController {
    protected VoiceInteractionSession voiceInteractionSession;
    public Bundle data;
    public AssistStructure structure;
    public AssistContent content;
    public String packagename = "";
    public Context context;
    public AssistPickupKeywords apk;
    public View view_assist_main = null;
    public Bitmap lastScreenshot;
    public ArrayList<NodeInfo> lastNis = null;
    public ArrayList<TextInfo> lastTis = null;
    public ArrayList<WordInfo> lastWis = null;
    public AssistStructure.WindowNode asw;
    public AssistLoggerController(Context context){
//        //-- 화면 너비 구하기
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int screenWidth = size.x;
//        int screenHeight = size.y;

        this.context = context;
        apk = new AssistPickupKeywords();

        apk.init();

        apk.conf_scores_for_packagename = loadPkScoresJson();
        apk.conf_scores_for_custom_selector = loadCustomSelectorJson();
        Log.v("@tt",apk.conf_scores_for_packagename.toString());
    }
    public void onHandleScreenshot(Bitmap screenshot) {
        this.lastScreenshot = screenshot;
    }
    public void onHandleAssist(@Nullable Bundle data, @Nullable AssistStructure structure, @Nullable AssistContent content){
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
            asw = structure.getWindowNodeAt(0);
            apk.screenWidth = asw.getWidth();
            apk.screenHeight = asw.getHeight();

            packagename = structure.getActivityComponent().getPackageName();
            apk.packagename = packagename;
            ((TextView)view_assist_main.findViewById(R.id.tvPackagename)).setText(packagename);
            ((TextView)view_assist_main.findViewById(R.id.tvAppName)).setText(getAppName(packagename));
            Log.v("@setAssistData","packagename : "+packagename);
        }

    }
    @Deprecated
    public ArrayList<NodeInfo> getNisFromStructure(AssistStructure structure) throws Exception {
        if(structure == null){
            throw new Exception("structure is null");
        }
//        Document doc = apk.getDomByViewNode(asw.getRootViewNode());
//        Log.v("@doc",doc.html());
//        apk.setHTML(doc.html());
//        System.out.println("=======");
//        System.out.println(doc.html());
//        System.out.println("=======");
        return apk.getNodeInfoByViewNode(asw.getRootViewNode());
    }
    public ArrayList<TextInfo> getTisFromStructure(AssistStructure structure) throws Exception {
        if(structure == null){
            throw new Exception("structure is null");
        }
        Document doc = apk.getDomByViewNode(asw.getRootViewNode());
        Log.v("@doc",doc.html());
        apk.setHTML(doc.html());
//        System.out.println("=======");
//        System.out.println(doc.html());
//        System.out.println("=======");
        ArrayList<TextInfo> tis = apk.getTexts();
        tis.addAll(apk.getCustomTexts());
        return tis;
    }
    public void onHandleAssist() throws Exception {
        actPickupKeyWords();
    }
    public void onCreateContentView(View view_assist_main){
        this.view_assist_main = view_assist_main;
        view_assist_main.findViewById(R.id.btnPickup).setOnClickListener(
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
        view_assist_main.findViewById(R.id.btnSaveLog).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        Log.v("@onClick",this.getClass().getName()+"."+new Object(){}.getClass().getEnclosingMethod().getName());
                        saveLogInfo();
                    }
                }
        );
        view_assist_main.findViewById(R.id.btnOpenSearchActivity).setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        openSearchActivity();
                    }
                }
        );
    }
    public void openSearchActivity(){
        Intent intent = new Intent(context,SearchActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP);
//        Bundle extras = intent.getExtras();
//        extras.putExtra("tis",lastNis);
        intent.putExtra("tis",lastTis);
        intent.putExtra("wis",lastWis);
//        intent.putExtra("tis",lastTis);
        context.startActivity(intent);


        voiceInteractionSession.finish();
    }
    public void actPickupKeyWords() throws Exception {
//        ArrayList<NodeInfo> nis = getNisFromStructure(structure);
//        ArrayList<TextInfo> tis = apk.nodeInfoToTextInfo(nis);
        ArrayList<TextInfo> tis = getTisFromStructure(structure);

        ArrayList<WordInfo> wis = apk.getWords(tis);
//        this.lastNis = nis;
        this.lastTis = tis;
        this.lastWis = wis;
        TextView tvTest = view_assist_main.findViewById(R.id.tvTest);
        tvTest.setText("");
//        for(int i=0,m=nis.size();i<m;i++){
//            NodeInfo ni = nis.get(i);
//            Log.v("@ni",ni.toString());
////            tvTest.append(ni.toString()+"\n");
//        }
//
        for(int i=0,m=tis.size();i<m;i++){
            TextInfo ti = tis.get(i);
            Log.v("@ti",ti.toString());
//            tvTest.append(ti.toString()+"\n");
        }
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
        tvTest.append("\nEND\n");
        Toast.makeText(context, "Finish : Pickup", Toast.LENGTH_SHORT).show();
    }

    public String getAppName(String packagename) {
//        final String packageName = "my.application.package"
        PackageManager packageManager= context.getPackageManager();
        String appName = null;
        try {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packagename, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appName = "";
        }
        return appName;
    }

    /**
     * write file
     * @param data
     */
    private String writeToFile(String data,String filename) {
        if(!isStoragePermissionGranted()){
            Toast.makeText(context,"permission deny",Toast.LENGTH_LONG).show();
            Log.e("@writeToFile","permission deny");
        }
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/"+context.getPackageName());
//        File dir = context.getFilesDir();
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        OutputStream out = null;
        try {
            file.createNewFile();
            out = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
            outputStreamWriter.write(data);
            outputStreamWriter.flush();
            outputStreamWriter.close();
            Log.v("@writeToFile","[Success] "+file.getAbsolutePath());
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception", "File write failed: " + e.toString());
        }
        MediaScannerConnection.scanFile(context, new String[] {file.toString()}, null, null);
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+Environment.getExternalStorageDirectory())));
        return file.getAbsolutePath();
    }

    private String writeToFile(Bitmap bitmap, String filename) {
        if(!isStoragePermissionGranted()){
            Toast.makeText(context,"permission deny",Toast.LENGTH_LONG).show();
            Log.e("@writeToFile","permission deny");
        }
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/"+context.getPackageName());
//        File dir = context.getFilesDir();
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        OutputStream out = null;

        try
        {
            file.createNewFile();
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        MediaScannerConnection.scanFile(context, new String[] {file.toString()}, null, null);
        return file.getAbsolutePath();

    }


    private String arraylistToString(ArrayList xis){
        if(xis==null){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object xi : xis)
        {
            sb.append(xi.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
    public String wisToHTML(String filename,ArrayList<WordInfo> wis,String imgUrl,String packagename,Date d){


        StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head><body><div style='margin:0 auto; max-width:1000px'>");
        sb.append("<h1> Assist test : "+packagename+"</h1>");
        sb.append("<h2> datetime : "+d.toString()+"</h2>");
        sb.append("<div style='display:inline-block; margin:5px;vertical-align: top;'><img src='"+imgUrl+"' style='max-width:400px'></div>");
        sb.append("<div style='display:inline-block; margin:5px;vertical-align: top;' >\n\n<table border=\"1\" style='border-spacing: 0;border-collapse: collapse;'>\n");
        sb.append("<tr>\n");
        sb.append("<th>NO</th>\n");
        sb.append("<th>word</th>\n");
        sb.append("<th>count</th>\n");
        sb.append("<th>score</th>\n");
        sb.append("</tr>\n");
        for(int i=0,m=Math.min(wis.size(),20);i<m;i++){
            WordInfo wi = wis.get(i);
            sb.append("<tr>\n");
            sb.append("<td>"+(i+1)+"</td>\n");
            sb.append("<td>"+wi.word+"</td>\n");
            sb.append("<td>"+Long.toString(wi.count)+"</td>\n");
            sb.append("<td>"+Double.toString(wi.score)+"</td>\n");
            sb.append("</tr>\n");
        }
        sb.append("</table>\n\n</div>");
        sb.append("</div></body></html>");
        return sb.toString();
    }
    public void saveLogInfo(){

//        this.lastNis
//        this.lastTis
//        this.lastWis
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = new Date();
        String filenamePrefix = "["+sdf.format(d)+"]["+packagename+"]";
        String filename = "",path = "";
//        // nis
//        filename = filenamePrefix+"nis.txt";
//        Log.v("@saveLogInfo",filename);
//        path = writeToFile(arraylistToString(lastNis),filename);
//        Log.v("@saveLogInfo","save path: "+path);
        // tis
        filename = filenamePrefix+"tis.txt";
        Log.v("@saveLogInfo",filename);
        path = writeToFile(arraylistToString(lastTis),filename);
        Log.v("@saveLogInfo","save path: "+path);
        // wis
        filename = filenamePrefix+"wis.txt";
        Log.v("@saveLogInfo",filename);
        path = writeToFile(arraylistToString(lastWis),filename);
        Log.v("@saveLogInfo","save path: "+path);
        // screenshot
        String imgUrl = filename = filenamePrefix+"screenshot.png";
        Log.v("@saveLogInfo",filename);
        path = writeToFile(lastScreenshot,filename);
        Log.v("@saveLogInfo","save path: "+filename);
        // raw.html
        filename = filenamePrefix+"raw.html";
        Log.v("@saveLogInfo",filename);
        path = writeToFile(apk.doc.html(),filename);
        Log.v("@saveLogInfo","save path: "+path);
        // report.html
        filename = filenamePrefix+"report.html";
        Log.v("@saveLogInfo",filename);
        path = writeToFile(wisToHTML(filename,lastWis,imgUrl,packagename,d),filename);
        Log.v("@saveLogInfo","save path: "+path);
        Toast.makeText(context.getApplicationContext(),"Finish : saveLogInfo",Toast.LENGTH_LONG).show();
    }

    /**
     * 외부 저장소 사용 가능 여부 체크
     * @return
     */
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    /**
     * 패키지용 점수 파일 읽어오기
     * @return
     */
    public JsonObject loadPkScoresJson(){
        InputStream in = context.getResources().openRawResource(R.raw.pkscores);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return (JsonObject)(new JsonParser()).parse(reader);
    }

    /**
     * 패키지용 점수 파일 읽어오기
     * @return
     */
    public JsonObject loadCustomSelectorJson(){
        InputStream in = context.getResources().openRawResource(R.raw.custom_selector);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        return (JsonObject)(new JsonParser()).parse(reader);
    }
}
