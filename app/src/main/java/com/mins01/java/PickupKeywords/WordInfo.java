package com.mins01.java.PickupKeywords;

public class WordInfo{
	public String word="";
	public long count=0;
	public double score=0;
	public String toString(){
		return word+","+Long.toString(count,10)+","+Double.toString(score);
	}
}
