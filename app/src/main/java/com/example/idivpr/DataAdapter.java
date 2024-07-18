package com.example.idivpr;


import static com.example.idivpr.MainActivity.android_id;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.xml.xpath.XPathFunctionResolver;

public class DataAdapter extends RecyclerView.Adapter<ViewHolder> {
    ArrayList<String[]> messages;
    LayoutInflater layoutInflater;
    String myName;
    public DataAdapter(Context context,ArrayList<String[]> messages,String name) {
        this.messages = messages;
        myName = name;
        layoutInflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_message,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] msges = messages.get(position);
        ConstraintLayout constraintLayout = holder.constraintLayout;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        if (msges[0].equals(android_id)){
            holder.layout.setBackgroundColor(0x670E2F73);
            Log.i("constraintSet.connect","fwefjwf");
            constraintSet.clear(R.id.cardview_item,ConstraintSet.LEFT);
            constraintSet.clear(R.id.cardview_item,ConstraintSet.RIGHT);
            constraintSet.connect(R.id.cardview_item, ConstraintSet.RIGHT,R.id.par_item,ConstraintSet.RIGHT);
        }
        else{
            holder.layout.setBackgroundColor(0xAA060D1B);
            constraintSet.clear(R.id.cardview_item,ConstraintSet.LEFT);
            constraintSet.clear(R.id.cardview_item,ConstraintSet.RIGHT);
            constraintSet.connect(R.id.cardview_item, ConstraintSet.LEFT,R.id.par_item,ConstraintSet.LEFT);
        }
        constraintSet.applyTo(constraintLayout);
        Log.i("LG",msges[1]);
        if (msges[1].length()>24 && msges[1].startsWith("https://firebasestorage")){
            holder.message.setText("");
            holder.draweeView.setImageURI(Uri.parse(msges[1]));
            holder.draweeView.setMinimumHeight(1000);
        }
        else {
            holder.message.setText(msges[1]);
            Log.i("LG",msges[1]+" "+position);
        }
        holder.date.setText(msges[2]);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
