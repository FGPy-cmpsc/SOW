package com.example.idivpr;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView message;
    TextView date;
    SimpleDraweeView draweeView;
    ConstraintLayout constraintLayout;
    ConstraintLayout layout;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        constraintLayout = itemView.findViewById(R.id.par_item);
        layout = itemView.findViewById(R.id.constLay);
        message=itemView.findViewById(R.id.message_item);
        date = itemView.findViewById(R.id.timedate);
        draweeView = itemView.findViewById(R.id.photo_in_chat);
    }
}