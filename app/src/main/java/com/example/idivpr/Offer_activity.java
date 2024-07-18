package com.example.idivpr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.piasy.biv.view.BigImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.database;
import static com.example.idivpr.MainActivity.mStorageRef;

public class Offer_activity extends AppCompatActivity {

    int count;
    String reference;
    ViewPager pager;
    ImageAdapter adapter;
    List<StorageReference> l;
    Uri [] uri;
    int start,end,unit;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_activity);
        reference = getIntent().getStringExtra("reference");
        Log.i("OfferAct",reference);
        Fresco.initialize(this);
        context = this;
        mStorageRef.child(reference).child("photos").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                count = listResult.getItems().size();
                uri = new Uri [count];
                pager = findViewById(R.id.pager);
                adapter = new ImageAdapter(context);
                pager.setAdapter(adapter);
                l = listResult.getItems();
                for (int i=0;i<l.size();i++){
                    int finalI = i;
                    l.get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri u) {
                            uri[finalI] = u;
                            try {
                                SimpleDraweeView v = pager.findViewWithTag(finalI).findViewById(R.id.fragment_pict);
                                v.setImageURI(u);
                            }
                            catch (Exception e){
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
        TextView OfferName = findViewById(R.id.Offer_name);
        TextView OfferDesc = findViewById(R.id.Offer_desc);
        TextView Price = findViewById(R.id.Price);
        ExtendedFloatingActionButton toMessenger = findViewById(R.id.ToMessenger);
        ExtendedFloatingActionButton toEnroll = findViewById(R.id.toEnroll);
        toMessenger.setOnClickListener(v -> {
            String [] a = reference.split("/");
            String friend_id = a[a.length-2];
            database.child("users").child(android_id).child("user_id_chats").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Intent i = new Intent(Offer_activity.this,Message_Activity.class);
                    i.putExtra("friend_id",friend_id);
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String chat_name = dataSnapshot.getValue(String.class);
                        assert chat_name != null;
                        if (chat_name.equals(android_id +" "+friend_id)||chat_name.equals(friend_id+" "+android_id)){
                            i.putExtra("chat_name",chat_name);
                            i.putExtra("if_chat_has_got_created",true);
                            startActivity(i);
                            return;
                        }
                    }
                    i.putExtra("chat_name",android_id+" "+friend_id);
                    startActivity(i);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        toEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                EnrollFragment myDialogFragment = new EnrollFragment(start,end,unit,reference,getBaseContext(),0,manager);
                myDialogFragment.show(manager,"wefwef");
            }
        });
        database.child(reference).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    OfferName.setText(Objects.requireNonNull(snapshot.child("ProductName").getValue()).toString());
                    OfferDesc.setText(Objects.requireNonNull(snapshot.child("ProductDesc").getValue()).toString());
                    start= Integer.parseInt(Objects.requireNonNull(snapshot.child("start").getValue()).toString());
                    end= Integer.parseInt(Objects.requireNonNull(snapshot.child("end").getValue()).toString());
                    unit= Integer.parseInt(Objects.requireNonNull(snapshot.child("unit").getValue()).toString());
                    Log.i("fowef",snapshot.child("want").getValue().toString());
                    String pr = Objects.requireNonNull(snapshot.child("want").getValue()).toString();
                    Price.setText(pr);
                    //if (pr.length == 1)
                    //    ProductToSwap.setText(pr[pr.length - 1]);
                    //else ProductToSwap.setText(pr[pr.length - 2] + "/" + pr[pr.length - 1]);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Объявление было удалено. Обновите страницу",Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void goToSearch(View v){
        finish();
    }


    private class ImageAdapter extends PagerAdapter
    {
        private LayoutInflater layoutInflater;

        public ImageAdapter(Context context){
            layoutInflater = LayoutInflater.from(context);
        }


        @Override
        public int getCount() {
            return count;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View pagerView = layoutInflater.inflate(R.layout.fragment_foto_fragment,container,false);
            SimpleDraweeView view = pagerView.findViewById(R.id.fragment_pict);
            view.setImageURI(uri[position]);
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageViewer.OnImageChangeListener listener = new ImageViewer.OnImageChangeListener() {
                        @Override
                        public void onImageChange(int position) {
                            pager.setCurrentItem(position);
                        }
                    };
                    Button b = new Button(context);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("TestButton","Cliked");
                        }
                    });
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    b.setLayoutParams(params);
                    new BigImageView(context).showImage(Uri.parse(thumbnail)uri);
                    StfalconImageViewer.Builder<Image>(context, images) { view, image ->
                            Picasso.get().load(image.url).into(view)
                    }.show()

                    new ImageViewer.Builder(context,uri)
                            .setStartPosition(position)
                            .show();
                }
            });*/
            container.addView(pagerView);
            pagerView.setTag(position);
            return pagerView;
        }
    }

}
