package com.mins01.androidassist;

import com.mins01.java.PickupKeywords.TextInfo;
public class NodeInfo extends  TextInfo{
    public String idEntry = "";
    public String toString() {
        return this.idEntry+","+this.tag + "," + this.text + "," + Long.toString(this.score, 10);
    }
}
