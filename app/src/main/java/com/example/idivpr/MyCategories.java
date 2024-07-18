package com.example.idivpr;

import android.net.Uri;

public class MyCategories {

    private final String ref;
    private final int count;
    private final String name;
    private Uri uri;

    MyCategories(String r,int c,String n,Uri u){
        ref = r;
        count = c;
        name = n;
        uri = u;
    }

    public String getRef(){
        return ref;
    }

    public int getCount(){
        return count;
    }

    public String getName(){return name;}

    public Uri getUri(){return uri;}

    public void setUri(Uri u){
        uri = u;
    }

}
