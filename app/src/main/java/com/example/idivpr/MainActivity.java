package com.example.idivpr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {
    public static DatabaseReference database;
    static StorageReference mStorageRef;
    DatabaseReference users;
    Intent i;
    static File f;
    static String android_id;
    static String user_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance("https://messanger-e14be-default-rtdb.firebaseio.com/").getReference();
        mStorageRef = FirebaseStorage.getInstance("gs://messanger-e14be.appspot.com/").getReference();
        users=database.child("users");
        setContentView(R.layout.activity_main);
        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        f = new File(this.getCacheDir()+"FileCategory.txt");
        if (!f.exists()){
            try {
                f = File.createTempFile("FileCategory","txt",this.getCacheDir());
                mStorageRef.child("FileCategory.txt").getFile(f).addOnSuccessListener(taskSnapshot -> {
                    if (f==null)
                        Log.i("TestCat","f==null");
                    toProgram();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else toProgram();

    }
    public static String get_android_id(){
        return android_id;
    }

    public void toProgram(){
        users.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean check=true;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String id = child.getKey();
                    if (id==null)break;
                    if (id.equals(android_id)) check=false;
                }
                if (check) {
                    i = new Intent(MainActivity.this,Registration.class);
                    startActivity(i);
                    finish();
                }
                else {
                    DatabaseReference user_data = users.child(android_id).child("user_data");
                    user_data.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            user_name = dataSnapshot.getValue(String.class);
                            i = new Intent(MainActivity.this, choose_act_to_show.class);
                            i.putExtra("from",1);
                            startActivity(i);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
