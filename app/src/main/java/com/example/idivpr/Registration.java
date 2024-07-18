package com.example.idivpr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class Registration extends AppCompatActivity {
    static FirebaseDatabase database = FirebaseDatabase.getInstance("https://messanger-e14be-default-rtdb.firebaseio.com/");
    static DatabaseReference users = database.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Button b = findViewById(R.id.button);
        String android_id = MainActivity.get_android_id();
        EditText editText = findViewById(R.id.editT_name);
        EditText editTextSurname = findViewById(R.id.editT_surname);
        b.setOnClickListener(v -> {
            String name = editText.getText().toString();
            name = name.replaceAll("\\s+","");
            String surname = editTextSurname.getText().toString();
            surname = surname.replaceAll("\\s+","");
            if (name.length()==0){
                Toast.makeText(this,"Введите имя",Toast.LENGTH_SHORT).show();
                return;
            }
            if (surname.length()==0){
                Toast.makeText(this,"Введите имя",Toast.LENGTH_SHORT).show();
                return;
            }
            name = name + " " +surname;
            DatabaseReference id = users.child(android_id).child("user_data");
            String finalName = name;
            id.setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent i = new Intent(Registration.this, choose_act_to_show.class);
                    i.putExtra("name", finalName);
                    i.putExtra("id", android_id);
                    startActivity(i);
                    finish();
                }
            });
        });
    }
}