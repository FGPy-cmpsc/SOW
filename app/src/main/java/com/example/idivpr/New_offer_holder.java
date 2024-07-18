package com.example.idivpr;

import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

public class New_offer_holder extends RecyclerView.ViewHolder{
    ImageView imageView;
    ImageView clear;
    public New_offer_holder(View itemView){
        super(itemView);
        imageView = itemView.findViewById(R.id.new_offer_img);
        clear = itemView.findViewById(R.id.clear);
    }
}
