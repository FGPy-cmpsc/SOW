package com.example.idivpr;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CelectionHolder extends RecyclerView.ViewHolder{
    TextView chatName;
    TextView chatMessage;
    public CelectionHolder(@NonNull View itemView) {
        super(itemView);
        chatName = itemView.findViewById(R.id.chat_name);
        chatMessage = itemView.findViewById(R.id.chat_message);
    }
}
