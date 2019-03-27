package com.mins01.java.PickupKeywords;

public class WordInfo{
	public String word="";
	public long count=0;
	public double score=0;
	public String toString(){
		return word+","+String.valueOf(count)+","+String.valueOf(score);
	}
}
