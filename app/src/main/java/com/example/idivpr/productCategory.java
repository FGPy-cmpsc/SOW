package com.example.idivpr;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.example.idivpr.MainActivity.f;
import static com.example.idivpr.MainActivity.mStorageRef;

public class productCategory extends AppCompatActivity {
    ImageButton button;
    RecyclerView recyclerView;
    TextView textView;
    ProductAdapter adapter;
    String category="productCategory/";
    HashMap<String,ArrayList<String>> categories = new HashMap<>();
    ArrayList<String> firstCat= new ArrayList<>();
    ArrayList<String> currentCat = new ArrayList<>();
    Thread createCat;
    Handler backgroundHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);
        button = findViewById(R.id.comeback);
        int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
        int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(wrapContent,wrapContent);
        lparams.gravity = Gravity.CENTER;
        ProgressBar bar  = new ProgressBar(this,null,android.R.attr.progressBarStyle);
        bar.setTag("BarId");
        LinearLayout llMain = findViewById(R.id.llMain);
        llMain.addView(bar,lparams);
        Log.i("TestCat","added");
        backgroundHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                llMain.removeView(llMain.findViewWithTag("BarId"));
                Log.i("TestCat","removed");
                textView = findViewById(R.id.category_text);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(matchParent,matchParent);
                recyclerView = new RecyclerView(getApplicationContext(),null);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                llMain.addView(recyclerView,layoutParams);
                adapter = new ProductAdapter(getApplicationContext());
                recyclerView.setAdapter(adapter);
                changeCat(category);
                Log.i("TestCat","Handler");
                return true;
            }
        });
        Runnable r = () -> {
            try {
                if (f==null || !f.exists()){
                    f = File.createTempFile("FileCategory","txt",getApplicationContext().getCacheDir());
                    mStorageRef.child("FileCategory.txt").getFile(f).addOnSuccessListener(taskSnapshot -> {
                        if (f == null) {
                            Log.i("TestCat", "f==null");
                        }
                        getData();

                    });
                }
                else{
                    getData();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        createCat = new Thread(r);
        createCat.start();
        button.setOnClickListener(v -> {
            onPressed();
        });

        /*database.child("productCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("Snapshot", String.valueOf(snapshot.getChildrenCount()));
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Log.i("Snapshot",dataSnapshot.getKey());
                    categories.add(dataSnapshot.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    public void getData(){
        FileReader fr;
        try {
            fr = new FileReader(f);
            BufferedReader reader = new BufferedReader(fr);
            String s = reader.readLine();
            int co = Integer.parseInt(s);
            for (int i=0;i<co;i++){
                String line = reader.readLine();
                String cat = line.split("/")[0];
                Log.i("LINECAT",line);
                int co2 = Integer.parseInt(line.split("/")[1]);
                categories.put(cat, new ArrayList<>());
                firstCat.add(cat);
                for (int j=0;j<co2;j++){
                    String l = reader.readLine();
                    Objects.requireNonNull(categories.get(cat)).add(l);
                }
            }
            Log.i("TestCat","Runnable");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        backgroundHandler.sendEmptyMessage(0);
    }

    public boolean changeCat(String t){
        try {
            recyclerView.scrollToPosition(0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        currentCat = new ArrayList<>();
        Log.i("TestCat2",t);
        if (t.equals("productCategory/")){
            Log.i("TestCat", String.valueOf(categories.keySet().size()));
            currentCat = firstCat;
            adapter.notifyDataSetChanged();
            return false;
        }
        if (categories.get(t)!=null && categories.get(t).size()>0) {
            currentCat = categories.get(t);
            Log.i("TestCat","notifyDataSetChanged");
            adapter.notifyDataSetChanged();
            return false;
        }
        return true;
    }

    public void onPressed(){
        int index = category.substring(0,category.length()-2).lastIndexOf("/");
        Log.i("onPressed",category+" "+index);
        if (index==-1){
            Log.i("onPressed","index==-1");
            setResult(RESULT_OK, null);
            finish();
        }
        else {
            category = category.substring(0, index) + "/";
            textView.setText("Выберите категорию");
            if (changeCat(category)) {
                setResult(RESULT_OK, null);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        onPressed();
    }

    class ProductAdapter extends RecyclerView.Adapter<productHolder>{
        LayoutInflater layoutInflater;
        public ProductAdapter(Context context) {
            layoutInflater= LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public productHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.category_item,parent,false);
            return new productHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull productHolder holder, int position) {
            holder.textView.setText(currentCat.get(position));
            holder.textView.setOnClickListener(v -> {
                String name = holder.textView.getText().toString();
                category += name+"/";
                textView.setText(name);
                if (changeCat(currentCat.get(position))){
                    Intent i = new Intent();
                    i.putExtra("ProductCategory", category);
                    setResult(RESULT_OK, i);
                    finish();
                }
                /*categories = new ArrayList<>();


                database.child(category).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.i("Snapshot", String.valueOf(snapshot.getChildrenCount()));
                        if (snapshot.getChildrenCount() <= 1) {
                            Intent i = new Intent();
                            i.putExtra("ProductCategory", category);
                            setResult(RESULT_OK, i);
                            finish();
                        } else {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Log.i("Snapshot",snapshot1.getKey());
                                categories.add(snapshot1.getKey());
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/

            });
        }

        @Override
        public int getItemCount() {
            return currentCat.size();
        }
    }



    @Override
    protected void onDestroy() {
        if (f.exists()) {
            if (!f.delete()) Log.e("FileNotDeleted","FileNotDeleted!!!");
        }
        else{
            Log.e("FileNotCreated","FileNotCreated!!!");
        }
        super.onDestroy();
    }
}