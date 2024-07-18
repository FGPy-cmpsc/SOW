package com.example.idivpr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.renderscript.Matrix2f;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import static com.example.idivpr.MainActivity.mStorageRef;

public class ShowPictFragment extends Fragment {

    String reference;
    int position;

    public ShowPictFragment(String pref,int position) {
        reference = pref;
        this.position = position;
    }

    public static ShowPictFragment newInstance(String pref,int position) {
        ShowPictFragment fragment = new ShowPictFragment(pref,position);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_show_pict, container, false);
        SimpleDraweeView imageView = view.findViewById(R.id.pict);
        mStorageRef.child(reference).child("photos").listAll().addOnSuccessListener(listResult -> listResult.getItems().get(position).getDownloadUrl().addOnSuccessListener(URI -> {
            imageView.setImageURI(URI);
            Log.i("TEST10000","wefpokwpfkw");
        }));
        return view;
    }
}