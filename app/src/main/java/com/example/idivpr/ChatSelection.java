package com.example.idivpr;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.user_name;
import static com.example.idivpr.MainActivity.database;

public class ChatSelection extends Fragment {
    private final DatabaseReference chats = database.child("chats");
    private final DatabaseReference users = database.child("users");
    private final ArrayList<String []> chat_data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_selection,container,false);
    }
    public void onStart(){
        super.onStart();
        RecyclerView recyclerView = requireActivity().findViewById(R.id.chat_Selection);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        CelectionAdapter adapter = new CelectionAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        DatabaseReference user_id_chats = users.child(android_id).child("user_id_chats");
        user_id_chats.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String chat_name = dataSnapshot.getValue(String.class);
                chats.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot a : dataSnapshot.getChildren()) {
                            String chat_name_in_all_chat = a.getKey();
                            if (chat_name.equals(chat_name_in_all_chat)) {

                                chats.child(chat_name).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String val="";
                                        for (DataSnapshot b:dataSnapshot.getChildren()){
                                            for (DataSnapshot c:b.getChildren()){
                                                val = c.getValue(String.class);
                                            }
                                        }
                                        for (int i=0;i<chat_data.size();i++){
                                            if (chat_data.get(i)[0].equals(chat_name)){
                                                chat_data.set(i, new String[]{chat_name, val});
                                                adapter.notifyDataSetChanged();
                                                return;
                                            }
                                        }
                                        chat_data.add(new String[]{chat_name, val});
                                        adapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent i = new Intent(getActivity(), Message_Activity.class);
        assert data != null;
        String friend_id = data.getStringExtra("friend_id");
        i.putExtra("id", android_id);
        i.putExtra("name", user_name);
        i.putExtra("friend_id", friend_id);
        if (friend_id.equals("-1")) return;
        chats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Цикл для нахождения существующего чата
                for (DataSnapshot a: dataSnapshot.getChildren()){
                    String chat_name = a.getKey();
                    assert chat_name != null;
                    if (chat_name.equals(friend_id+" "+android_id)||chat_name.equals(android_id+" "+friend_id)){
                        i.putExtra("chat_name", chat_name);
                        i.putExtra("if_chat_has_got_created",true);
                        startActivity(i);
                        return;
                    }
                }
                //если чат с нужным именем не найден, то передаём соответствующие данные
                String chat_name = android_id + " " + friend_id;

                i.putExtra("chat_name", chat_name);
                i.putExtra("if_chat_has_got_created",false);
                startActivity(i);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class CelectionAdapter extends RecyclerView.Adapter<CelectionHolder>{
        LayoutInflater layoutInflater;
        public CelectionAdapter(Context context){
            layoutInflater=LayoutInflater.from(context);
        }
        @NonNull
        @Override
        public CelectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.celection_item,parent,false);
            return new CelectionHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CelectionHolder holder, int position) {
            String[] data = chat_data.get(position);
            holder.chatMessage.setText(data[1]);
            String[] chat_name = data[0].split(" ");
            String friend_id;
            if (chat_name[0].equals(android_id)) friend_id=chat_name[1];
            else friend_id = chat_name[0];
            users.child(friend_id).child("user_data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String friend_name = dataSnapshot.getValue(String.class);
                    holder.chatName.setText(friend_name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(getActivity(),Message_Activity.class);
                i.putExtra("chat_name",data[0]);
                i.putExtra("name",user_name);
                i.putExtra("friend_id", friend_id);
                i.putExtra("id",android_id);
                i.putExtra("if_chat_has_got_created",true);
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return chat_data.size();
        }
    }

}