package com.example.idivpr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.ALLOW;
import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.database;
import static com.example.idivpr.MainActivity.f;
import static com.example.idivpr.MainActivity.mStorageRef;

public class Search_offer extends Fragment {
    private ArrayList<MyCategories> AllWantedCategories = new ArrayList<>();
    private SearchAdapter adapter;
    private String prodCat="";
    private String subProdCat = "";
    private String query = "";
    int countCat;
    RecyclerView recyclerView;
    int maxValue = 100;
    int MyWidth;
    SwipeRefreshLayout refreshLayout;
    Handler handler;


    public static Search_offer newInstance() {
        Bundle args = new Bundle();
        Search_offer fragment = new Search_offer();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("Search_offer","onCreateView");
        View view = inflater.inflate(R.layout.activity_search_offer,container,false);
        refreshLayout = view.findViewById(R.id.swipeRecycler);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    findCategories(prodCat+"/"+subProdCat);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        //получение размеров экрана
        recyclerView = view.findViewById(R.id.search_recycler);
        adapter = new SearchAdapter(view.getContext());
        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext());
        manager.setFlexWrap(FlexWrap.WRAP);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setStateRestorationPolicy(ALLOW);
        adapter.notifyDataSetChanged();
        ImageButton filterButton = view.findViewById(R.id.search_filter);
        filterButton.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(),FilterActivity.class);
            i.putExtra("prodCat",prodCat);
            i.putExtra("subProdCat",subProdCat);
            startActivityForResult(i,1);
        });
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                query = q;
                Log.i("OnQuery","wefijewoifjewf");
                findCategories(prodCat+"/"+subProdCat);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                query = newText;
                return false;
            }
        });
        Log.i("Search_offer","onCreateView");
        if (savedInstanceState==null){
            Log.i("Search_offer","savedInstanceState==null");
            Fresco.initialize(requireActivity());
            setHasOptionsMenu(true);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float density = displayMetrics.density;
            MyWidth = (int) Math.min((displayMetrics.widthPixels-21*density)/2.0,200*density);
            handler = new Handler();
            try {
                Log.i("findCategories","findCategories");
                findCategories("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onStart(){
        super.onStart();
        Log.i("Search_offer","onStart");
    }

    /*private void searchCategories(String query){
        AllWantedCategories = new ArrayList<>();
        adapter.notifyDataSetChanged();
        database.child("productCategory").child(productCategory).child("sell").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot pref : snapshot.getChildren()) {
                    assert pref!=null;
                    if (pref.getKey().equals(android_id)) continue;
                    for (DataSnapshot pref2 : pref.getChildren()) {
                        if ((returnProductCategory.equals("") || pref2.child("want").getValue(String.class).equals(returnProductCategory.replace("/","!")))
                                && (query.equals("") || pref2.child("ProductName").getValue().equals(query))) {
                            String FReference = productCategory+"/sell/"+pref.getKey()+"/"+pref2.getKey();
                            ReadyCategory.add(0, FReference);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void findCategories(String cat){
        countCat = 0;
        database.child("productCategory").child(cat).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lparams.gravity = Gravity.CENTER;
                    ProgressBar bar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
                    LinearLayout llMain = requireActivity().findViewById(R.id.mainLineSearchLay);
                    AllWantedCategories = new ArrayList<>();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                refreshLayout.setRefreshing(true);
                                recyclerView.setVisibility(View.GONE);
                                llMain.addView(bar, lparams);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    Log.i("RRCC2",cat);
                    RecSearchCategories(snapshot, cat);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                llMain.removeView(bar);
                            } catch (Exception e) {
                                Log.e("ProgressBar", "ProgressBar has not deleted");
                                e.printStackTrace();
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            if (refreshLayout.isRefreshing())
                                refreshLayout.setRefreshing(false);
                            Log.i("adapter","notifyDataSetChanged");
                            Collections.sort(AllWantedCategories, new Comparator<MyCategories>() {
                                @Override
                                public int compare(MyCategories o1, MyCategories o2) {
                                    return Integer.compare(o2.getCount(), o1.getCount());
                                }
                            });
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void RecSearchCategories(DataSnapshot sn,String cat){//call with productCategory/
        if (countCat>=maxValue) return;
        //Log.i("RecSearchCategories","cat");
        //Log.i("RecSearchCategories","Recursion");
        if (sn.child("sell").getValue()!=null){
            Log.i("RecSearchCategories",cat);
            searchCategories(sn,cat);
        }
        else{
            for (DataSnapshot snap:sn.getChildren()){
                //Log.i("RecSearchCategories",cat+"/"+snap.getKey());
                if (countCat>=maxValue) return;
                RecSearchCategories(snap,cat+"/"+snap.getKey());
            }
        }
    }


    private void searchCategories(DataSnapshot sn,String cat){
        if (countCat>=maxValue) return;
        for (DataSnapshot dataSnapshot:sn.child("sell").getChildren()){
            if (dataSnapshot.getKey().equals(android_id)) continue;
            Log.i("searchCategories","dataSnapshot");
            for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){//все предложения id
                Log.i("searchCategories","dataSnapshot1");
                int count=0;
                if (!query.equals("")){
                    Log.i("searchCategories","!query.equals(\"\")");
                    String st = dataSnapshot1.child("ProductName").getValue(String.class);
                    if (st==null) continue;
                    int y = prefix_func(query.toLowerCase().replace(" ","").replace("#","")
                            .replace("/","").replace(",","").replace(".","")+"#"+st
                            .toLowerCase().replace(" ","").replace("#","")
                            .replace("/","").replace(",","").replace(".",""));
                    if (st.length()>query.length())
                        count += (int)((y / (float)query.length()) * 100);
                    else count += (int)((y / (float)st.length()) * 100);
                    Log.i("searchCategories",st + " "+ count+ " "+ y);
                    Log.i("searchCategories",query.toLowerCase().replace(" ","").replace("#","")
                            .replace("/","").replace(",","").replace(".",""));
                    Log.i("searchCategories",st.toLowerCase().replace(" ","").replace("#","")
                            .replace("/","").replace(",","").replace(".",""));
                    if (count<50) break;
                }
                Log.i("searchCategories2",cat + " "+ count);
                MyCategories mC = new MyCategories(cat+"/sell/"+dataSnapshot.getKey()+"/"
                        +dataSnapshot1.getKey(),count,dataSnapshot1.child("ProductName").getValue(String.class),null);
                Log.i("searchCategories","productCategory/"+cat+"/sell/"+dataSnapshot.getKey()+"/"+dataSnapshot1.getKey());
                try {
                    mStorageRef.child("productCategory/" + cat + "/sell/" + dataSnapshot.getKey() + "/" + dataSnapshot1.getKey()).child("photos").listAll().addOnSuccessListener(
                            new OnSuccessListener<ListResult>() {
                                @Override
                                public void onSuccess(ListResult listResult) {
                                    Log.i("searchCategories", "productCategory" + cat + "/sell/" + dataSnapshot.getKey() + "/" + dataSnapshot1.getKey());
                                    listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            AllWantedCategories.add(mC);
                                            countCat++;
                                            mC.setUri(uri);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }



    private int prefix_func(String s){
        int n = s.length();
        int [] pi = new int[n];
        int max = 0;
        for (int i=1;i<n;i++){
            int j = pi[i-1];
            while (j>0 && s.charAt(j)!=s.charAt(i))
                j = pi[j-1];
            if (s.charAt(i)==s.charAt(j)) j++;
            pi[i]=j;
            if (pi[i]>max){
                max=pi[i];
            }
        }
        return max;
    }



    /*private void findCategories(){
        database.child("users").child(android_id).child("users_categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    String[] arr = new String[2];
                    arr[0] = dataSnapshot.getKey();
                    assert arr[0] != null;
                    arr[0] = arr[0].replace("!","/");
                    arr[1] = dataSnapshot.getValue(String.class);
                    assert arr[1] != null;
                    arr[1] = arr[1].replace("!","/");
                    AllWantedCategories.add(arr);
                }
                getCategory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getCategory (){
        for (int i=0;i<AllWantedCategories.size() && i<50;i++){
            //заходим в категорию
            int finalI1 = i;
            database.child("productCategories").child(AllWantedCategories.get(i)[0]).child("sell").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getKey().equals(android_id)) continue;
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.child("want").getValue(String.class).replace("!","/").equals(AllWantedCategories.get(finalI1)[1])) {
                                ReadyCategory.add(AllWantedCategories.get(finalI1)[0]+"/sell/"+dataSnapshot.getKey()+"/"+dataSnapshot1.getKey()+"/");
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }*/

    public  class SearchAdapter extends RecyclerView.Adapter<SearchHolder>{
        LayoutInflater layoutInflater;

        public SearchAdapter(Context context) {
            layoutInflater=LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.search_item,parent,false);
            view.getLayoutParams().width=MyWidth;
            view.getLayoutParams().height= (int) (MyWidth+18*getResources().getDisplayMetrics().density);
            View im = view.findViewById(R.id.imageView);
            im.getLayoutParams().height=MyWidth;
            View name = view.findViewById(R.id.textView2);

            //im.setLayoutParams(new LinearLayout.LayoutParams(MyWidth,MyWidth+18));
            //name.setLayoutParams(new LinearLayout.LayoutParams(MyWidth,300));
            return new SearchHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
            //Log.i("RecSearchCategories","onBindViewHolder");
            //Log.i("RecSearchCategories",AllWantedCategories.get(position).getRef());
            holder.view.setImageURI(AllWantedCategories.get(position).getUri());
            holder.view.setOnClickListener(v -> {
                Intent i = new Intent(getActivity(),Offer_activity.class);
                i.putExtra("reference","productCategory/"+AllWantedCategories.get(position).getRef());
                startActivity(i);
            });
            holder.information.setVisibility(View.INVISIBLE);
            holder.name.setText(AllWantedCategories.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return AllWantedCategories.size();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("OnCreated","onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && data!=null) {
            AllWantedCategories = new ArrayList<>();
            adapter.notifyDataSetChanged();
            prodCat = data.getStringExtra("prodCat");
            subProdCat = data.getStringExtra("subProdCat");
            try {
                findCategories(prodCat+"/"+subProdCat);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i("onSaveInstanceState","onSaveInstanceState");
        outState.putInt("ScrollState",recyclerView.getScrollState());
        super.onSaveInstanceState(outState);
    }
}