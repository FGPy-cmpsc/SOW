package com.example.idivpr;

import android.net.Uri;

import androidx.appcompat.widget.MenuPopupWindow;

public class SecondCategories {

    private String name;
    private String desc;
    private Uri uri;
    private String ref;
    private String ref2;
    private int start,end,unit;

    SecondCategories(String ref,String ref2, String name,String desc,int start, int end, int unit, Uri uri){
        this.name = name;
        this.desc = desc;
        this.uri = uri;
        this.ref = ref;
        this.ref2 = ref2;
        this.start=start;
        this.end=end;
        this.unit=unit;
    }

    public String getRef(){
        return ref;
    }

    public String getName(){return name;}

    public Uri getUri(){return uri;}

    public void setUri(Uri u){
        uri = u;
    }

    public String getRef2(){return ref2;}

    public String getDesc(){return desc;}

    public int getStart(){return start;}
    public int getEnd(){return end;}
    public int getUnit(){return unit;}

}
