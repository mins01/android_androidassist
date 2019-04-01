package com.mins01.java.PickupKeywords;
import org.jsoup.Jsoup; 
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PickupKeywords {
	public Document doc = null;
	public String jsonString_conf_scores = "{\"h1\":50,\"h2\":40,\"h3\":30,\"h4\":20,\"h5\":10,\"h6\":10,\"title\":100,\"span\":5,\"a\":1,\"li\":5,\"input\":50,\"meta-description\":25,\"meta-keywords\":25,\"meta-og:title\":25,\"meta-og:description\":25,\"script\":0,\"style\":0}";
//	public String userAgent = "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Mobile Safari/537.36"; //android
	public String userAgent = "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36"; //PC-windows
	public JsonObject conf_scores = null;
	
	public int min_length = 2; //word의 최소 길이
	public int max_length = 100; //word의 최대 길이
	public String search_tags = "h1,h2,h3,h4,h5,title,span,div,li,a,input[type=text][value]";
	public String search_metas = "meta[name=\"description\"],meta[name=\"keywords\"],meta[property=\"og:title\"],meta[property=\"og:description\"]";
	public double numericWeight = 1; //숫자형에 대한 점수 배수
	public boolean wordToLowerCase = false; //단어(wi,word)를 전부 소문자로 변환 처리
	
	public PickupKeywords(){

	}
	public void init(){
		conf_scores = (JsonObject)(new JsonParser()).parse(this.jsonString_conf_scores);
	}
	public String getHTML(String strURL) throws Exception{
		String html = Jsoup.connect(strURL).userAgent(userAgent).get().html();
		System.out.println(html.length());
		return html;
	}
	public String getHTML(URL iURL) throws Exception{

		return this.getHTML(iURL.toString());
	}
	public void setUrl(URL iURL) throws Exception{
		this.setHTML(this.getHTML(iURL));
	}
	public void setUrl(String strURL) throws Exception{
		this.setHTML(this.getHTML(strURL));
	}
	public void setHTML(String html){
		String charset = getCharset(html);
		if(charset != "utf-8"){
			html.replaceAll("(?i)"+charset, "utf-8");
		}
		this.doc = Jsoup.parse(html,charset);
//		System.out.print(doc.toString());
	}
	public String getCharset(String html){
//		System.out.println(html);
		Pattern p = Pattern.compile("(?:(?i)(?:charset=[\"\\']?)([^\"\\',\\s\\t\\n]+)(?:[\"\\',\\s\\t\\n]?))",1);
	    Matcher m = p.matcher(html);
	    if(m.find()){
	    	return m.group(1).toLowerCase();
	    }
		return "utf-8";
	}
	
	public Boolean isNumeric(String str){
		return str != null && str.matches("^[-+]?\\d*\\.?\\d+$");		
	}
	
	
	
			
	
	
	public ArrayList<TextInfo> getMetas(){
		Elements els = this.doc.select(this.search_metas);
		ArrayList<TextInfo> al = new ArrayList<TextInfo>();
		for(Element el : els){
			TextInfo ti = new TextInfo();
			if(el.hasAttr("property")){
				ti.tag = "meta-"+el.attr("property");
			}else{
				ti.tag = "meta-"+el.tagName();
			}
			ti.text = el.attr("content");
			syncScore(ti);
		
			al.add(ti);
//			System.out.println(ti.toString());
		}
		return al;
	}
	public ArrayList<TextInfo> getTags() {
		return getTags(this.search_tags);
	}
	public ArrayList<TextInfo> getTags(String search_tags){
		Elements els = this.doc.select(search_tags);
		ArrayList<TextInfo> al = new ArrayList<TextInfo>();
		for(Element el : els){
			
			if(el.childNodeSize()>1){
				continue;
			}
			
			
			TextInfo ti = new TextInfo();
			ti.tag = el.tagName();
			if(ti.tag.equals("input")){
				ti.text = el.hasAttr("value")?el.attr("value"):"";
			}else{
				ti.text = el.ownText();	
			}
			
			syncScore(ti);
			if(el.hasAttr("data-score-weight")){
				ti.score *=Double.parseDouble(el.attr("data-score-weight"));
			}
//			System.out.println(ti.toString());
			if(ti.text.length()==0){continue;}
//			System.out.println(el.tagName()+","+el.text().length()+","+el.childNodeSize()+","+el.childNodeSize()+","+el.text());
			al.add(ti);
//			System.out.println(ti.toString());
		}
		return al;
	}
	public void syncScore(TextInfo ti) {
		syncScore(ti,this.conf_scores);
	}
	public void syncScore(TextInfo ti,JsonObject conf_scores){
		if(conf_scores.has(ti.tag)){
			ti.score = conf_scores.get(ti.tag).getAsInt();
		}else{
			ti.score = 1;
		}
	}

	
	public ArrayList<TextInfo> getTexts(){
		ArrayList<TextInfo> metas = this.getMetas();
		metas.addAll(this.getTags());
		return metas;
	}
	
	public ArrayList<String> split_tags_string(String str){
		String strPattern = "([^#\\t\\s\\n\\x00-\\x2C\\x2E-\\x2F\\x3A-\\x40\\x5B-\\x5E\\x60\\x7B-\\x7F、，。]{"+String.valueOf(this.min_length)+","+String.valueOf(this.max_length)+"})";
		
		Pattern p = Pattern.compile(strPattern);
	    Matcher m = p.matcher(str);
	    ArrayList<String> matches = new ArrayList<String>();
	    
	    while(m.find()){
	    	matches.add(m.group());
	    }
	    return matches;
	}
	

	
	public ArrayList<WordInfo> getWords(ArrayList<TextInfo> texts ){
		HashMap<String, WordInfo> kv = new HashMap<String, WordInfo>();
		ArrayList<WordInfo> alwi = new ArrayList<WordInfo>();
		WordInfo wi = null;
		for(TextInfo text : texts){
			ArrayList<String> splited_texts = split_tags_string(text.text);
			for(int i = 0,m=splited_texts.size();i<m;i++){
				String st = splited_texts.get(i);
				if(wordToLowerCase){
					st = st.toLowerCase();
				}
				
				if((wi =kv.get(st))==null){
					wi = new WordInfo();
					wi.word = st;
					kv.put(st, wi);
					alwi.add(wi);
				}
				wi.count++;
				wi.score += text.score;
				
				if(isNumeric(st)){ //숫자로만 이루어져있을 경우
					wi.score += text.score*this.numericWeight;	
				}else{
					wi.score += text.score;
				}
			}
		}
		Collections.sort(alwi, new Comparator<WordInfo>() {
		    public int compare(WordInfo lhs, WordInfo rhs) {
		    	double r = rhs.score - lhs.score;
				if(r == 0){
					r = rhs.score/rhs.count - lhs.score/lhs.count;
				}
				if(r<0){
					return -1;
				}else if(r>0){
					return 1;
				}
				return 0;
		    }
		});
		return alwi;
	}
}

