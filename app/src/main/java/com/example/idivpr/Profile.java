package com.example.idivpr;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.database;
import static com.example.idivpr.MainActivity.mStorageRef;
import static com.facebook.imagepipeline.producers.PriorityNetworkFetcher.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;

public class Profile extends AppCompatActivity {

    String name;
    String sur;
    String email;
    String phone;
    String birthday;
    String id;
    ImageView profile_pict;
    Uri uri_profile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent i = getIntent();
        id = i.getStringExtra("id");
        if (id==null) id = android_id;

        EditText MyName = findViewById(R.id.MyName);

        EditText MySurname = findViewById(R.id.MySurname);

        EditText MyEmailAddress = findViewById(R.id.MyEmailAddress);

        EditText MyPhoneNumber = findViewById(R.id.MyPhoneNumber);

        EditText MyBirthDay = findViewById(R.id.MyBirthDay);

        profile_pict = findViewById(R.id.profile_pict);


        mStorageRef.child(id).child("photo").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri!=null){
                    if (!Fresco.hasBeenInitialized())
                        Fresco.initialize(getApplicationContext());
                    Picasso.get().load(uri).into(profile_pict);
                }
                else profile_pict.setVisibility(View.INVISIBLE);
            }
        });

        database.child("users").child(id).child("user_data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name_sur = snapshot.getValue(String.class);
                assert name_sur != null;
                name = name_sur.split(" ")[0];
                sur = name_sur.split(" ")[1];
                Log.i("britititi",name_sur);
                MyName.setText(name);

                MySurname.setText(sur);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.child("users").child(id).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.getValue(String.class);
                MyEmailAddress.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.child("users").child(id).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phone = snapshot.getValue(String.class);
                MyPhoneNumber.setText(phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.child("users").child(id).child("birthday").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                birthday = snapshot.getValue(String.class);
                MyBirthDay.setText(birthday);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Button toSave = findViewById(R.id.toSave);

        Button b = findViewById(R.id.attach_photo);
        if (!id.equals(android_id)) {
            toSave.setVisibility(View.INVISIBLE);
            b.setVisibility(View.INVISIBLE);
            MyName.setFocusable(false);
            MySurname.setFocusable(false);
            MyEmailAddress.setFocusable(false);
            MyPhoneNumber.setFocusable(false);
            MyBirthDay.setFocusable(false);
        }
        else {
            toSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!name.equals("") && !sur.equals("")) {
                        database.child("users").child(android_id).child("user_data").setValue(MyName.getText().toString().replaceAll("\\s+","") + " " + MySurname.getText().toString().replaceAll("\\s+","")).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(@NonNull Void unused) {
                                database.child("users").child(android_id).child("email").setValue(MyEmailAddress.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        database.child("users").child(android_id).child("phone").setValue(MyPhoneNumber.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(@NonNull Void unused) {
                                                database.child("users").child(android_id).child("birthday").setValue(MyBirthDay.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(@NonNull Void unused) {
                                                        if (uri_profile!=null){
                                                            mStorageRef.child(android_id).child("photo").putFile(uri_profile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                @Override
                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                    setResult(RESULT_OK);
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                        else {
                                                            setResult(RESULT_OK);
                                                            finish();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Укажите имя и фамилию", Toast.LENGTH_LONG).show();
                    }
                }
            });

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    i.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i,"Choose Image"),2);

                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            uri_profile = data.getData();
            profile_pict.setImageURI(uri_profile);
        }
    }


}

