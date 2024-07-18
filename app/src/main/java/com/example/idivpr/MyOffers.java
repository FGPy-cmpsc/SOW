package com.example.idivpr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.database;
import static com.example.idivpr.MainActivity.mStorageRef;

public class MyOffers extends Fragment {

    int MyWidth;
    ArrayList<SecondCategories> myOffers;
    DatabaseReference databaseRef = database.child("users").child(android_id).child("users_categories");
    MyOffersAdapter adapter;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    int c;
    int k;
    public MyOffers() {
    }

    public static MyOffers newInstance() {
        Bundle args = new Bundle();
        MyOffers fragment = new MyOffers();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.i("OnStartMyOffers","OnStart");
        recyclerView = requireActivity().findViewById(R.id.my_offers_recycler);
        myOffers = new ArrayList<>();
        ImageButton ib = requireActivity().findViewById(R.id.me);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),Profile.class);
                startActivityForResult(i,1);
            }
        });
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float density = displayMetrics.density;
        MyWidth = (int) Math.min((displayMetrics.widthPixels-21*density)/2.0,200*density);
        adapter = new MyOffersAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext());
        manager.setFlexWrap(FlexWrap.WRAP);
        recyclerView.setLayoutManager(manager);
        refreshLayout = requireActivity().findViewById(R.id.SwipeRefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findMyOffers();
            }
        });
        findMyOffers();

    }

    public void findMyOffers(){
        refreshLayout.setRefreshing(true);
        c = 0;
        k = 0;
        myOffers = new ArrayList<>();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()==0) {refreshLayout.setRefreshing(false); return;}
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String v = Objects.requireNonNull(dataSnapshot.getKey()).replace("!","/");
                    c+=dataSnapshot.getValue(Integer.class);
                    DatabaseReference ref = database.child(v).child("sell").child(android_id);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1:snapshot.getChildren()){
                                String t=snapshot1.child("want").getValue(String.class);
                                String m = v+"sell"+"/"+android_id +"/"+ snapshot1.getKey()+"/";
                                Log.i("MyOffersCheckRef",v+"sell"+"/"+android_id+"/"+snapshot1.getKey()+"/");
                                Log.i("MyOffersCheckRef",t);
                                /*database.child(i.getStringExtra("reference")).child("uri").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                                            Log.i("FOEFWE",snapshot1.getValue(String.class));
                                            myOffers.add(new SecondCategories(m, finalT,snapshot1.child("ProductName").getValue(String.class),snapshot1.child("ProductDesc").getValue(String.class),Uri.parse(snapshot1.getValue(String.class))));
                                        }
                                        if (photos_uri.size()<=9)
                                            photos_uri.add(null);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });*/
                                /*database.child(m).child("uri").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snap) {
                                        for (DataSnapshot snapshot2:snap.getChildren()){
                                            if (++k==c) refreshLayout.setRefreshing(false);
                                            myOffers.add(new SecondCategories(m, t,snapshot1.child("ProductName").getValue(String.class),snapshot1.child("ProductDesc").getValue(String.class),Uri.parse(snapshot2.getValue(String.class))));
                                            adapter.notifyDataSetChanged();
                                            break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });*/
                                mStorageRef.child(m).child("photos").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Log.i("findMyOffers","c= "+c+" "+ "k= "+ k);
                                                if (++k==c) refreshLayout.setRefreshing(false);
                                                myOffers.add(new SecondCategories(m, t,snapshot1.child("ProductName").getValue(String.class),snapshot1.child("ProductDesc").getValue(String.class),snapshot1.child("start").getValue(Integer.class),snapshot1.child("end").getValue(Integer.class),snapshot1.child("unit").getValue(Integer.class),uri));
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("fragment","onCreateFragment MyOffers");
        return inflater.inflate(R.layout.fragment_my_offers, container, false);
    }

    private class MyOffersAdapter extends RecyclerView.Adapter<SearchHolder>{

        LayoutInflater layoutInflater;

        public MyOffersAdapter(Context context) {
            layoutInflater=LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (!Fresco.hasBeenInitialized())
                Fresco.initialize(requireContext());
            View view = layoutInflater.inflate(R.layout.search_item,parent,false);
            view.getLayoutParams().width=MyWidth;
            view.getLayoutParams().height= (int) (MyWidth+18*getResources().getDisplayMetrics().density);
            View im = view.findViewById(R.id.imageView);
            im.getLayoutParams().height=MyWidth;
            return new SearchHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, @SuppressLint("RecyclerView") int position) {
            Picasso.get().load(myOffers.get(position).getUri()).into(holder.view);
            holder.name.setText(myOffers.get(position).getName());
            holder.view.setOnClickListener(v -> {
                if (!refreshLayout.isRefreshing()) {
                    Intent i = new Intent(getActivity(), New_offer.class);
                    i.putExtra("reference", myOffers.get(position).getRef());
                    i.putExtra("ref2", myOffers.get(position).getRef2());
                    i.putExtra("name", myOffers.get(position).getName());
                    i.putExtra("desc", myOffers.get(position).getDesc());
                    i.putExtra("uri", myOffers.get(position).getUri());
                    i.putExtra("start", myOffers.get(position).getStart());
                    i.putExtra("end", myOffers.get(position).getEnd());
                    i.putExtra("unit", myOffers.get(position).getUnit());
                    startActivity(i);
                }
            });
            holder.information.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager manager = getFragmentManager();
                    EnrollFragment myDialogFragment = new EnrollFragment(myOffers.get(position).getStart(),myOffers.get(position).getEnd(),
                            myOffers.get(position).getUnit(),myOffers.get(position).getRef(),getContext(),1,manager);
                    assert manager != null;
                    myDialogFragment.show(manager,"wefwef");
                }
            });
        }

        @Override
        public int getItemCount() {
            return myOffers.size();
        }
    }

}