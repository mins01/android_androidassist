package com.mins01.java.PickupKeywords;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GetAppInfoByPackagename extends PickupKeywords {
	public String jsonString_conf_scores = "{\"title\":100,\"meta-description\":90,\"meta-keywords\":100,\"meta-og:title\":100,\"meta-og:description\":90}";
//	public String search_tags = "title";
	public GetAppInfoByPackagename(){
		super();			
	}
	public void init(){
		this.search_tags = "title";
		conf_scores = (JsonObject)(new JsonParser()).parse(this.jsonString_conf_scores);
	}
	public void setPackagename(String packagename) throws Exception{
		this.setUrl("https://play.google.com/store/apps/details?id="+packagename);
	}
	public String getHTML(String strURL) throws Exception{
		return super.getHTML(strURL).replace("Apps on Google Play", ""); //불필요 단어 삭제
	}
}
