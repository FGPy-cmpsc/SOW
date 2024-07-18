package com.example.idivpr;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SearchHolder extends RecyclerView.ViewHolder {
    TextView name;
    ImageView view;
    ImageView information;
    public SearchHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.textView2);
        view = itemView.findViewById(R.id.imageView);
        information = itemView.findViewById(R.id.information);
    }
}