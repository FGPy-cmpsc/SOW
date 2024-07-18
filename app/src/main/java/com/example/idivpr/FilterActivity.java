package com.example.idivpr;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.f;
import static com.example.idivpr.MainActivity.get_android_id;
import static com.example.idivpr.MainActivity.mStorageRef;

public class FilterActivity extends Activity implements View.OnClickListener {
    ConstraintLayout llMain;
    ConstraintLayout l1;
    ConstraintLayout l2;
    ConstraintLayout l3;
    ConstraintLayout l4;
    ImageButton button1;
    ImageButton button2;
    ImageButton button3;
    ImageButton button4;
    Button buttonOk;
    Handler handler;
    HashMap<String,ArrayList<String>> categories = new HashMap<>();
    ArrayList<String> firstCat= new ArrayList<>();
    TextView catGet;
    TextView catTrade;
    TextView subcatGet;
    TextView subcatTrage;
    int checked1 = -1;
    int checked2 = -1;
    int checked3 = -1;
    int checked4 = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
        int matchParent = LinearLayout.LayoutParams.MATCH_PARENT;
        llMain = findViewById(R.id.mainConstrLay);
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(matchParent,wrapContent);
        lparams.gravity = Gravity.CENTER;
        ProgressBar bar  = new ProgressBar(this,null,android.R.attr.progressBarStyle);
        bar.setTag("barId");
        bar.setId(ViewCompat.generateViewId());
        ConstraintSet set = new ConstraintSet();
        llMain.addView(bar,lparams);
        set.clone(llMain);
        LinearLayout layout = findViewById(R.id.linear1);
        set.connect(bar.getId(),ConstraintSet.TOP,layout.getId(),ConstraintSet.BOTTOM);
        set.applyTo(llMain);
        button1 = findViewById(R.id.cancel1);
        button1.setVisibility(View.GONE);
        //button2 = findViewById(R.id.cancel2);
        //button2.setVisibility(View.GONE);
        button3 = findViewById(R.id.subcancel1);
        button3.setVisibility(View.GONE);
        //button4 = findViewById(R.id.subcancel2);
        //button4.setVisibility(View.GONE);
        buttonOk = findViewById(R.id.button_OK);
        buttonOk.setVisibility(View.GONE);
        l1 = findViewById(R.id.constr1);
        //l2 = findViewById(R.id.constr2);
        l3 = findViewById(R.id.subconstr1);
        //l4 = findViewById(R.id.subconstr2);
        l1.setVisibility(View.GONE);
        //l2.setVisibility(View.GONE);
        l3.setVisibility(View.GONE);
        //l4.setVisibility(View.GONE);
        subcatGet = findViewById(R.id.subcatGet);
        //subcatTrage = findViewById(R.id.subcatTrade);
        catGet = findViewById(R.id.catGet);
        //catTrade = findViewById(R.id.catTrade);
        handler = new Handler();
        Runnable r = () -> {
            if (!f.exists()) {
                try {
                    f = File.createTempFile("FileCategory", "txt", this.getCacheDir());
                    mStorageRef.child("FileCategory.txt").getFile(f).addOnSuccessListener(taskSnapshot -> {
                        if (f == null) {
                            Log.i("TestCat", "f==null");
                        }
                        getData();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                getData();
            }
        };
        Thread createCat = new Thread(r);
        createCat.start();

    }



    @Override
    public void onClick(View v) {
        int ch;
        ArrayAdapter<String> adapter;
        String s="";
        if (v.getId() == R.id.catGet) {
            if (v.getId()==R.id.catGet) ch = checked1;
            else ch = checked2;
            adapter = new ArrayAdapter<>(this,
                    android.R.layout.select_dialog_singlechoice, firstCat);

        }
        else{
            if (v.getId()==R.id.subcatGet) {
                ch = checked3;
                s = (String) catGet.getText();
            }
            else {
                ch = checked4;
                s = (String) catTrade.getText();
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice,categories.get(s));
            Log.i("TestCategories",s);
            Log.i("TestCategories",categories.get(s).toString());
        }
        Log.i("TestCategories",categories.toString());
        String finalS = s;
        new MaterialAlertDialogBuilder(this).setTitle("Выберите категорию")
                .setNegativeButton("cancel", null)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ListView view = ((AlertDialog) dialog).getListView();
                        int checked = view.getCheckedItemPosition();
                        Log.i("FilterAct",String.valueOf(view.getCheckedItemPosition()));
                        if (checked>=0) {
                            Log.i("FilterAct",">=0");
                            switch (v.getId()) {
                                case R.id.catGet:
                                    if (checked1==-1){
                                        button1.setVisibility(View.VISIBLE);
                                        l3.setVisibility(View.VISIBLE);
                                        ConstraintSet set = new ConstraintSet();
                                        set.clone(llMain);
                                        set.connect(l3.getId(),ConstraintSet.TOP,l1.getId(),ConstraintSet.BOTTOM);
                                        //set.connect(R.id.textViewTrade,ConstraintSet.TOP,l3.getId(),ConstraintSet.BOTTOM);
                                        //set.connect(l2.getId(),ConstraintSet.TOP,R.id.textViewTrade,ConstraintSet.BOTTOM);
                                        set.applyTo(llMain);
                                    }
                                    else{
                                        subcatGet.setText("Подкатегория товара");
                                    }
                                    checked1 = checked;
                                    catGet.setText(firstCat.get(checked1));
                                    Log.i("FilterAct","catGet");
                                    break;
                                /*case R.id.catTrade:
                                    if (checked2==-1){
                                        button2.setVisibility(View.VISIBLE);
                                        ConstraintSet set = new ConstraintSet();
                                        l4.setVisibility(View.VISIBLE);
                                        set.clone(llMain);
                                        set.connect(l4.getId(),ConstraintSet.TOP,l2.getId(),ConstraintSet.BOTTOM);
                                        set.applyTo(llMain);
                                    }
                                    checked2 = checked;
                                    catTrade.setText(firstCat.get(checked2));
                                    Log.i("FilterAct","catTrade");
                                    break;*/
                                case R.id.subcatGet:
                                    if (checked3==-1) button3.setVisibility(View.VISIBLE);
                                    checked3 = checked;
                                    subcatGet.setText(categories.get(finalS).get(checked3));
                                    break;
                                /*case R.id.subcatTrade:
                                    if (checked4==-1) button4.setVisibility(View.VISIBLE);
                                    checked4 = checked;
                                    subcatTrage.setText(categories.get(finalS).get(checked4));*/
                            }
                        }
                    }
                })
                .setSingleChoiceItems(adapter, ch, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("FilterAct","setSingleChoiceItems");
                    }
                })
                .show();
    }


    public void onCancelClick(View view){
        switch (view.getId()){
            case R.id.cancel1:
                catGet.setText("Категория товара");
                checked1=-1;
                if (checked3!=-1){
                    button3.setVisibility(View.GONE);
                    subcatGet.setText("Подкатегория товара");
                }
                checked3 = -1;
                view.setVisibility(View.GONE);
                l3.setVisibility(View.GONE);
                ConstraintSet set = new ConstraintSet();
                set.clone(llMain);
                //set.connect(R.id.textViewTrade,ConstraintSet.TOP,l1.getId(),ConstraintSet.BOTTOM);
                set.applyTo(llMain);
                break;
            /*case R.id.cancel2:
                catTrade.setText("Категория обмениваемого товара");
                checked2 = -1;
                if (checked4!=-1){
                    button4.setVisibility(View.GONE);
                    subcatTrage.setText("Подкатегория товара для обмена");
                }
                checked4 = -1;
                view.setVisibility(View.GONE);
                l4.setVisibility(View.GONE);
                break;*/
            case R.id.subcancel1:
                subcatGet.setText("Подкатегория товара");
                checked3=-1;
                view.setVisibility(View.GONE);
                break;
            /*case R.id.subcancel2:
                subcatTrage.setText("Подкатегория товара для обмена");
                checked4=-1;
                view.setVisibility(View.GONE);*/
        }
    }


    void getData(){
        FileReader fr;
        try {
            fr = new FileReader(f);
            BufferedReader reader = new BufferedReader(fr);
            String s = reader.readLine();
            int co = Integer.parseInt(s);
            for (int i=0;i<co;i++){
                String line = reader.readLine();
                String cat = line.split("/")[0];
                int co2 = Integer.parseInt(line.split("/")[1]);
                categories.put(cat, new ArrayList<>());
                firstCat.add(cat);
                for (int j=0;j<co2;j++){
                    String l = reader.readLine();
                    Objects.requireNonNull(categories.get(cat)).add(l);
                }
            }
            Log.i("TestCat","Runnable");
            String prodCat= getIntent().getStringExtra("prodCat");
            String subProdCat = getIntent().getStringExtra("subProdCat");
            //String retCat = getIntent().getStringExtra("retCat");
            //String subRetCat = getIntent().getStringExtra("subRetCat");
            //Log.i("testCat",prodCat+"  "+ retCat);
            ConstraintSet set = new ConstraintSet();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    l1.setVisibility(View.VISIBLE);
                    //l2.setVisibility(View.VISIBLE);
                    if (!prodCat.equals("")){
                        checked1 = firstCat.indexOf(prodCat);
                        catGet.setText(prodCat);
                        l3.setVisibility(View.VISIBLE);
                        button1.setVisibility(View.VISIBLE);
                        set.clone(llMain);
                        set.connect(l3.getId(),ConstraintSet.TOP,l1.getId(),ConstraintSet.BOTTOM);
                        //set.connect(R.id.textViewTrade,ConstraintSet.TOP,l3.getId(),ConstraintSet.BOTTOM);
                        //set.connect(l2.getId(),ConstraintSet.TOP,R.id.textViewTrade,ConstraintSet.BOTTOM);
                        if (!subProdCat.equals("")) {
                            checked3 = Objects.requireNonNull(categories.get(prodCat)).indexOf(subProdCat);
                            subcatGet.setText(subProdCat);
                            button3.setVisibility(View.VISIBLE);
                        }
                        set.applyTo(llMain);
                    }
                    /*if (!retCat.equals("")){
                        Log.i("retCat",retCat);
                        checked2 = firstCat.indexOf(retCat);
                        catTrade.setText(retCat);
                        l4.setVisibility(View.VISIBLE);
                        button2.setVisibility(View.VISIBLE);
                        set.clone(llMain);
                        set.connect(l4.getId(),ConstraintSet.TOP,l2.getId(),ConstraintSet.BOTTOM);
                        if (!subRetCat.equals("")) {
                            checked4 = Objects.requireNonNull(categories.get(retCat)).indexOf(subRetCat);
                            subcatTrage.setText(subRetCat);
                            button4.setVisibility(View.VISIBLE);
                        }
                        set.applyTo(llMain);
                    }*/
                    llMain.removeView(llMain.findViewWithTag("barId"));
                    buttonOk.setVisibility(View.VISIBLE);
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"Что-то пошло не так",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this,"Что-то пошло не так",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void onButOkClick (View view){
        Intent i = new Intent();
        i.putExtra("prodCat", "");
        i.putExtra("retProdCat", "");
        i.putExtra("subProdCat","");
        i.putExtra("subRetProdCat","");
        if (view.getId()==R.id.button_OK) {
            if (checked1!=-1){
                i.putExtra("prodCat", catGet.getText());
                if (checked3 != -1) i.putExtra("subProdCat",subcatGet.getText());
            }
            if (checked2!=-1) {
                i.putExtra("retProdCat", catTrade.getText());
                if (checked4 != -1) i.putExtra("subRetProdCat",subcatTrage.getText());
            }
            Log.i("testCat","checked3 "+String.valueOf(checked3));
            Log.i("testCat","checked4 "+String.valueOf(checked4));


        }
        setResult(RESULT_OK,i);
        finish();
    }
}