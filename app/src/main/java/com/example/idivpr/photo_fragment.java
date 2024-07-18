package com.example.idivpr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.ListResult;

import java.util.Objects;

import static com.example.idivpr.MainActivity.mStorageRef;

public class photo_fragment extends Fragment {

    String reference;
    int number;
    int count;
    public photo_fragment() {
        // Required empty public constructor
    }
    public static photo_fragment newInstance(String reference,int number,int count) {
        photo_fragment fragment = new photo_fragment();
        Bundle bundle = new Bundle();
        bundle.putString("reference",reference);
        bundle.putInt("number", number);
        bundle.putInt("count",count);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getArguments() != null;
        Fresco.initialize(
                requireActivity(),
                ImagePipelineConfig.newBuilder(getActivity())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
        reference = getArguments().getString("reference");
        number = getArguments().getInt("number");
        count = getArguments().getInt("count");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_foto_fragment, container, false);
        SimpleDraweeView image = view.findViewById(R.id.fragment_pict);

        view.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(),ShowPictures.class);
            i.putExtra("reference",reference);
            i.putExtra("position",number);
            i.putExtra("count",count);
            startActivityForResult(i,1);
        });
        mStorageRef.child(reference).child("photos").listAll().addOnSuccessListener(listResult -> {
            Log.i("TEST1000", String.valueOf(listResult.getItems().get(0).getDownloadUrl()));
            listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    image.setImageURI(uri);
                }
            });
        });
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}