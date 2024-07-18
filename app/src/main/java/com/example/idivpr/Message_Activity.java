package com.example.idivpr;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.mStorageRef;
import static com.example.idivpr.MainActivity.user_name;
import static com.example.idivpr.MainActivity.database;

import de.hdodenhof.circleimageview.CircleImageView;

public class Message_Activity extends Activity {
    DatabaseReference myRef;
    EditText editText;
    ImageButton button;
    boolean if_chat_has_got_created;
    RecyclerView recyclerView;
    String chat_name;
    ArrayList<String[]> messages = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        chat_name = getIntent().getStringExtra("chat_name");
        String friend_id = getIntent().getStringExtra("friend_id");
        CircleImageView circleImageView = findViewById(R.id.circle_photo);
        mStorageRef.child(friend_id).child("photo").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(@NonNull Uri uri) {

                Picasso.get().load(uri).into(circleImageView);
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Profile.class);
                i.putExtra("id",friend_id);
                startActivity(i);
            }
        });
        ImageButton come_back = findViewById(R.id.comeback);
        come_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        ImageButton attach = findViewById(R.id.attachment);
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                i.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i,"Choose Image"),2);
            }
        });
        if_chat_has_got_created = getIntent().getBooleanExtra("if_chat_has_got_created",false);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editT);
        recyclerView = findViewById(R.id.recycle);
        TextView friend_nameB = findViewById(R.id.friend_name_bar);
        database.child("users").child(friend_id).child("user_data").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(@NonNull DataSnapshot dataSnapshot) {
                friend_nameB.setText(dataSnapshot.getValue().toString());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataAdapter adapter = new DataAdapter(this,messages,user_name);
        recyclerView.setAdapter(adapter);
        myRef = database.child("chats").child(chat_name);
        add_Listener(adapter);
        if (!if_chat_has_got_created) myRef.setValue(" ");
        button.setOnClickListener(v -> {
            String msg = editText.getText().toString();
            if (msg.equals("")) {
                Toast.makeText(getApplicationContext(),"Введите сообщение",Toast.LENGTH_LONG).show();
                return;
            }
            if (!if_chat_has_got_created){
                database.child("users").child(friend_id).child("user_id_chats").push().setValue(chat_name);
                database.child("users").child(android_id).child("user_id_chats").push().setValue(chat_name);
                if_chat_has_got_created=true;
            }
            send_msg(msg);
        });
    }

    void send_msg(String msg){
        Date dateNow = new Date();
        Log.i("RTT",Long.toString(dateNow.getTime()));
        HashMap<String,String> hm2 = new HashMap<>();
        hm2.put(android_id,msg);
        myRef.child(Long.toString(dateNow.getTime())).setValue(hm2);
        editText.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data!=null;
        Uri uri = data.getData();
        mStorageRef.child(chat_name).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(@NonNull ListResult listResult) {
                mStorageRef.child(chat_name).child(String.valueOf(listResult.getItems().size())).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    final int t = listResult.getItems().size();
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        mStorageRef.child(chat_name).child(String.valueOf(t)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri) {
                                Log.i("onSucces147",uri.toString());
                                send_msg(uri.toString());
                            }
                        });
                    }
                });
            }
        });
        Log.i("LOHLOH", String.valueOf(uri));
    }

    public void add_Listener(DataAdapter adapter){
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String[] msgData = new String[3];
                Long time  = Long.parseLong(Objects.requireNonNull(dataSnapshot.getKey()));
                Date date = new Date(time);
                /*    Date currentTime = Calendar.getInstance().getTime();
                int timezoneOffset = currentTime.getTimezoneOffset()/60;
                //Log.i("LOGLOG", String.valueOf(timezoneOffset));
                //Log.i("LOGLOG", String.valueOf(currentTime.getHours()));
                Log.i("LOGLOG",(date.getHours()-timezoneOffset)%60+" "+date.getMinutes());
                //Log.i("LOGLOG", String.valueOf(dateFormat.getTimeZone().getID()));
                //Log.i("LOGLOG", String.valueOf(t.getHours()));
                Log.i("LOGLOG", date.getHours() +" "+ date.getMinutes());
                //Log.i("LOGLOG",dataSnapshot.getKey());*/
                for (DataSnapshot a: dataSnapshot.getChildren()){
                    msgData[0] = a.getKey();
                    msgData[1] = a.getValue(String.class);
                    msgData[2] = date.getDate() + "."+(date.getMonth()+1)+"."+(2000-100+date.getYear()) + " " + date.getHours()+":"+date.getMinutes();
                }
                Log.i("POwe",msgData[1]);
                messages.add(msgData);
                recyclerView.scrollToPosition(messages.size()-1);
                adapter.notifyDataSetChanged();
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
}