package com.example.idivpr;


import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class productHolder extends RecyclerView.ViewHolder{
    TextView textView;
    public productHolder(View view){
        super(view);
        textView = view.findViewById(R.id.category);
    }
}
