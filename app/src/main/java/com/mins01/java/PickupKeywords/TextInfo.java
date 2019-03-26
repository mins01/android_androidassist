package com.mins01.java.PickupKeywords;

public class TextInfo {
	public String tag="";
	public String text="";
	public long score=0;
	public String toString(){
		return tag+","+text+","+Long.toString(score,10);
	}

}
