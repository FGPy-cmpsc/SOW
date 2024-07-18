package com.example.idivpr;

import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.database;
import static com.example.idivpr.MainActivity.f;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class InfoAboutUser extends AppCompatDialogFragment {

    DataSnapshot snapshot;
    int start,end,unit;
    String friend_id;
    Context context;

    public InfoAboutUser(DataSnapshot snapshot, int start, int end, int unit, Context context) {
        this.snapshot = snapshot;
        this.start=start;
        this.end = end;
        this.unit = unit;
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_about_user, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_info_about_user,null);
        ArrayList<String> timetable=new ArrayList<>();
        ArrayList<String> idis = new ArrayList<>();
        for (DataSnapshot t : snapshot.getChildren()) {
            if (t == null) continue;
            Log.i("vaer", t.getKey());
            if (!Objects.equals(t.getValue(String.class), android_id)) {
                int i = Integer.parseInt(Objects.requireNonNull(t.getKey()));
                idis.add(t.getValue(String.class));
                String s="";
                if ((start+unit*i)/60<10)
                    s+="0";
                s+=(start+unit*i)/60+":";
                if ((start+unit*i)%60<10)
                    s+="0";
                s+=(start+unit*i)%60+" - ";
                if ((start+unit*(i+1))/60<10)
                    s+="0";
                s+=(start+unit*(i+1))/60+":";
                if ((start+unit*(i+1))%60<10)
                    s+="0";
                s+=(start+unit*(i+1))%60;
                timetable.add(s);
            }
        }
        friend_id = idis.get(0);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1
                .setSingleChoiceItems((CharSequence[]) timetable.toArray(new CharSequence[0]), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        friend_id = idis.get(i);
                    }
                })
                .setPositiveButton("Написать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        database.child("users").child(android_id).child("user_id_chats").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Intent i = new Intent(context,Message_Activity.class);
                                i.putExtra("friend_id",friend_id);
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    String chat_name = dataSnapshot.getValue(String.class);
                                    assert chat_name != null;
                                    if (chat_name.equals(android_id +" "+friend_id)||chat_name.equals(friend_id+" "+android_id)){
                                        i.putExtra("chat_name",chat_name);
                                        i.putExtra("if_chat_has_got_created",true);
                                        context.startActivity(i);
                                        return;
                                    }
                                }
                                i.putExtra("chat_name",android_id+" "+friend_id);
                                Log.i("INFO","fowkefwef");
                                context.startActivity(i);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
        return builder1.create();
    }
}