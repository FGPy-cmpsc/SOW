package com.example.idivpr;


import static android.content.Intent.ACTION_PICK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
//import com.stfalcon.imageviewer.StfalconImageViewer;
//import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class Photo_review extends AppCompatActivity implements View.OnClickListener {
    SimpleDraweeView imageView;
    ImageButton removeImage;
    ImageButton goBack;
    int position;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_photo_review);
        imageView = findViewById(R.id.photo_review);
        position = getIntent().getIntExtra("position",0);
        uri = Uri.parse(getIntent().getStringExtra("uri"));
        assert uri!=null;
        imageView.setImageURI(uri);
        removeImage = findViewById(R.id.removeImage);
        removeImage.setOnClickListener(this);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.changeImage:
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.putExtra(ACTION_PICK,true);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                i.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Choose Image"),1);
                break;*/
            case R.id.removeImage:
                Intent i1 = new Intent();
                i1.putExtra("position",position);
                i1.putExtra("result","remove");
                setResult(RESULT_OK,i1);
                finish();
                break;
            case R.id.goBack:
                Intent i2 = new Intent();
                i2.putExtra("result","0");
                setResult(RESULT_OK,i2);
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            Intent i = new Intent();
            i.putExtra("new_uri",data.getData().toString());
            i.putExtra("position",position);
            i.putExtra("result","change");
            setResult(RESULT_OK,i);
            finish();
        }
    }

    public void CreateBuilder(View v){
        /*new StfalconImageViewer.Builder<Uri>(this, new Uri[]{uri}, new ImageLoader<Uri>() {
            @Override
            public void loadImage(ImageView imageView, Uri image) {

            }
        }).show();*/
    }
}