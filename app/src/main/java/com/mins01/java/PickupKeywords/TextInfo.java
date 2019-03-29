package com.mins01.java.PickupKeywords;

import java.io.Serializable;

public class TextInfo  implements Serializable {
	public String tag="";
	public String text="";
	public double score=0;
	public String toString(){
		return tag+","+text+","+String.valueOf(score);
	}

}
