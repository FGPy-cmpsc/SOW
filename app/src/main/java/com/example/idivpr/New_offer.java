package com.example.idivpr;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.content.Intent.ACTION_PICK;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.database;
import static com.example.idivpr.MainActivity.f;
import static com.example.idivpr.MainActivity.mStorageRef;
import static java.lang.Math.min;

public class New_offer extends Activity implements View.OnClickListener {
    private RecyclerView photos;
    private final int MAX_PHOTO_COUNT = 10;
    private New_offer_adapter adapter;
    private int position;
    private EditText productName;
    private EditText productDescription;
    private String category;
    private String category2;
    private ImageButton cancel11;
    private ImageButton cancel22;
    private TextView productCategory;
    private EditText serviceCost;
    private TextView startClock;
    private TextView endClock;
    private TextView unitClock;
    private ImageButton deleteOffer;
    private int main_count=-1;
    private int c;
    private int min_size=0;
    private int start_size = 0;
    private final ArrayList<Uri> photos_uri = new ArrayList<>();

    Calendar dateAndTime1=Calendar.getInstance();
    Calendar dateAndTime2=Calendar.getInstance();
    Calendar dateAndTime3=Calendar.getInstance();

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://messanger-e14be.appspot.com/");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getIntent().getStringExtra("name")!=null){
            Intent i = getIntent();

            String ref = i.getStringExtra("reference");
            String[] t = ref.substring(16).split("/");//productCategory = 16 letters
            Log.i("test",i.getStringExtra("reference"));
            category = ref.substring(0,16)+t[0]+"/"+t[1]+"/";
            Log.i("test",category);
            main_count = Integer.parseInt(t[t.length-1]);
            category2 = i.getStringExtra("ref2");
            Log.i("test",category2);


            Log.i("TEST",i.getStringExtra("reference")+"photos");
            /*database.child(i.getStringExtra("reference")).child("uri").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        Log.i("FOEFWE",snapshot1.getValue(String.class));
                        photos_uri.add(Uri.parse(snapshot1.getValue(String.class)));
                    }
                    if (photos_uri.size()<=9)
                        photos_uri.add(null);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/
            /*database.child(i.getStringExtra("reference")).child("uri").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child:snapshot.getChildren()){
                        photos_uri.add(Uri.parse(child.getValue(String.class)));
                    }
                    adapter.notifyDataSetChanged();
                    if (photos_uri.size()<=9)
                        photos_uri.add(null);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/
            mStorageRef.child(ref).child("photos").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(@NonNull ListResult listResult)
                {
                    for (int i=0;i<min(listResult.getItems().size()+1,10);i++){
                        photos_uri.add(null);
                    }
                    start_size = listResult.getItems().size();
                    min_size=start_size;
                    Log.i("LOG#3", i.getStringExtra("reference"));
                    final int[] k = {0};
                    for (int i=0;i<listResult.getItems().size();i++){
                        int finalI = i;

                        listResult.getItems().get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("OnSuccess","fwefwef");

                                mStorageRef.child(ref).child("photos").child(String.valueOf(finalI)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.i("briri",uri.toString());
                                        k[0]++;
                                        photos_uri.set(finalI,uri);
                                        if (k[0]==listResult.getItems().size()) {
                                            Log.i("adapterNot",photos_uri.get(0).toString());
                                            init_comp(1);
                                        }
                                        /*FirebaseStorage.getInstance("gs://messanger-e14be.appspot.com/").getReferenceFromUrl(uri.toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });*/
                                    }
                                });
                            }
                        });
                        /*listResult.getItems().get(i).getBytes(10*1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Log.i("OnSuccess","fwefwef");
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                mStorageRef.child(ref).child("photos").child(String.valueOf(finalI)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.i("briri",uri.toString());

                                        FirebaseStorage.getInstance("gs://messanger-e14be.appspot.com/").getReferenceFromUrl(uri.toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                k[0]++;
                                                photos_uri.set(finalI,bitmapToFile(bitmap));
                                                if (k[0]==listResult.getItems().size()) {
                                                    Log.i("adapterNot",photos_uri.get(0).toString());
                                                    init_comp(1);
                                                }
                                            }
                                        });
                                    }
                                });
                                //photos_bytes.set(finalI,bytes);
                            }
                        })*/;/*.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri){
                                photos_uri.set(finalI, uri);
                                k[0]++;
                                if (k[0]==listResult.getItems().size())
                                    adapter.notifyDataSetChanged();
                            }
                        });*/
                        /*listResult.getItems().get(i).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(@NonNull Uri uri){
                                photos_uri.set(finalI, uri);
                                k[0]++;
                                if (k[0]==listResult.getItems().size())
                                    adapter.notifyDataSetChanged();
                            }
                        });*/
                    }
                }
            });
        }
        else {
            init_comp(0);
        }

    }

    private void init_comp(int a){



        setContentView(R.layout.activity_new_offer);
        productName = findViewById(R.id.product_name);
        productDescription = findViewById(R.id.product_description);
        Button placeAnAdd = findViewById(R.id.Place_an_ad);
        placeAnAdd.setOnClickListener(this);
        productCategory = findViewById(R.id.catGet_newoffer);
        serviceCost = findViewById(R.id.service_cost);
        cancel11 = findViewById(R.id.cancel11);
        cancel22 = findViewById(R.id.cancel22);
        photos = findViewById(R.id.photos);
        startClock = findViewById(R.id.start_clock);
        endClock = findViewById(R.id.end_clock);
        unitClock = findViewById(R.id.unit_clock);
        deleteOffer = findViewById(R.id.deleteOffer);
        photos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new New_offer_adapter(this);
        photos.setAdapter(adapter);

        dateAndTime3.set(0,0,0,0,0);



        if (a==0){
            cancel11.setVisibility(View.GONE);
            cancel22.setVisibility(View.GONE);
            deleteOffer.setVisibility(View.GONE);
            photos_uri.add(null);
        }
        else {
            Intent i = getIntent();
            int start=i.getIntExtra("start",0),end=i.getIntExtra("end",0),unit=i.getIntExtra("unit",0);

            Log.i("fewifw",start+ " "+end+" "+unit);

            String[] t = i.getStringExtra("reference").substring(16).split("/");//productCategory = 16 letters

            String s = i.getStringExtra("ref2");

            serviceCost.setText(s);
            productName.setText(i.getStringExtra("name"));
            productDescription.setText(i.getStringExtra("desc"));

            if (t.length==2) productCategory.setText(t[0]+"/");
            else
                productCategory.setText(t[0]+"/"+t[1]+"/");

            dateAndTime1.set(Calendar.HOUR_OF_DAY, start / 60);
            dateAndTime1.set(Calendar.MINUTE, start % 60);

            dateAndTime2.set(Calendar.HOUR_OF_DAY, end / 60);
            dateAndTime2.set(Calendar.MINUTE, end % 60);

            dateAndTime3.set(Calendar.HOUR_OF_DAY, unit / 60);
            dateAndTime3.set(Calendar.MINUTE, unit % 60);

            adapter.notifyDataSetChanged();
        }
        setInitialDateTime();


    }

    public void deletePictFromStorage(StorageReference sr){
        int k = photos_uri.size();
        if (photos_uri.get(photos_uri.size()-1)==null) k--;
        Log.i("deletePictFromStorage",String.valueOf(photos_uri.size()));
        for (int i =0;i<k;i++){
            Log.i("deletePictFromStorage",category+android_id+"/" +i);
            sr.child("sell").child(android_id).child(String.valueOf(main_count)).child("photos").child(String.valueOf(i)).delete();
        }
    }

    public void addRec(List<StorageReference> list,int p){
        list.get(p).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                photos_uri.add(uri);
                adapter.notifyDataSetChanged();
                if (p==list.size()-1){
                    if (p<=9)
                        photos_uri.add(null);
                }
                else {
                    addRec(list, p + 1);
                }
            }
        });
    }



    TimePickerDialog.OnTimeSetListener t1=new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint("SetTextI18n")
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime1.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime1.set(Calendar.MINUTE, minute);
            startClock.setText(DateUtils.formatDateTime(New_offer.this,
                    dateAndTime1.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_24HOUR));
        }
    };

    TimePickerDialog.OnTimeSetListener t2=new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint("SetTextI18n")
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime2.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime2.set(Calendar.MINUTE, minute);
            endClock.setText(DateUtils.formatDateTime(New_offer.this,
                    dateAndTime2.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_24HOUR));
        }
    };

    TimePickerDialog.OnTimeSetListener t3=new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint("SetTextI18n")
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime3.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime3.set(Calendar.MINUTE, minute);
            unitClock.setText(DateUtils.formatDateTime(New_offer.this,
                    dateAndTime3.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_24HOUR));
        }
    };

    private void setInitialDateTime() {

        startClock.setText(DateUtils.formatDateTime(this,
                dateAndTime1.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_24HOUR));
        endClock.setText(DateUtils.formatDateTime(this,
                dateAndTime2.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_24HOUR));
        unitClock.setText(DateUtils.formatDateTime(this,
                dateAndTime3.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME|DateUtils.FORMAT_24HOUR));
    }


    public void setTime(View v) {
        if (main_count==-1) {
            if (v.getId() == R.id.start_clock)
                new TimePickerDialog(New_offer.this, t1,
                        dateAndTime1.get(Calendar.HOUR_OF_DAY),
                        dateAndTime1.get(Calendar.MINUTE), true)
                        .show();
            else if (v.getId() == R.id.end_clock)
                new TimePickerDialog(New_offer.this, t2,
                        dateAndTime2.get(Calendar.HOUR_OF_DAY),
                        dateAndTime2.get(Calendar.MINUTE), true)
                        .show();
            else if (v.getId() == R.id.unit_clock)
                new TimePickerDialog(New_offer.this, t3,
                        dateAndTime3.get(Calendar.HOUR_OF_DAY),
                        dateAndTime3.get(Calendar.MINUTE), true)
                        .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.catGet_newoffer:
                if (main_count==-1){
                    Intent i = new Intent(New_offer.this,productCategory.class);
                    startActivityForResult(i,4);
                }
                else Toast.makeText(getApplicationContext(),"Вы не можете изменять категорию своего товара",Toast.LENGTH_LONG).show();
                break;
            /*case R.id.service_cost:
                Intent intent = new Intent(New_offer.this,productCategory.class);
                startActivityForResult(intent,5);
                break;*/
            case R.id.Place_an_ad:
                if (photos_uri.size()!=1 && productDescription.getText()!=null
                        && productName.getText()!=null
                        && category!=null
                        && !(database.child(category).getParent()==null)
                        && !(serviceCost.getText() ==null)
                        &&  dateAndTime1.get(Calendar.HOUR_OF_DAY)*60+dateAndTime1.get(Calendar.MINUTE)<dateAndTime2.get(Calendar.HOUR_OF_DAY)*60+dateAndTime2.get(Calendar.MINUTE)
                        && dateAndTime3.get(Calendar.HOUR_OF_DAY)+dateAndTime3.get(Calendar.MINUTE)!=0){
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    Log.i("testt", String.valueOf(dateAndTime3.get(Calendar.HOUR_OF_DAY)+dateAndTime3.get(Calendar.MINUTE)));
                    Log.i("prodname", String.valueOf(productName.getText()));
                    DatabaseReference DatabaseCategory = database.child(category);
                    //добавление информации о товаре
                    DatabaseCategory.child("sell/"+android_id+"/").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int size = 1;
                            if (main_count!=-1) size = main_count;
                            else{
                                for (DataSnapshot snapshot1:snapshot.getChildren()){
                                    size = Integer.parseInt(Objects.requireNonNull(snapshot1.getKey()))+1;
                                    break;
                                }
                            }
                            StorageReference promCat = mStorageRef.child(category).child("sell/" + android_id + "/" + size + "/"+"photos");
                            c = 0;

                            DatabaseReference databaseReference = DatabaseCategory.child("sell/"+android_id).child(String.valueOf(size));
                            int k = (photos_uri.get(photos_uri.size()-1)==null)?photos_uri.size()-1:photos_uri.size();
                            if (k>min_size) {
                                for (int i1 = min_size; i1 < k; i1++) {
                                    int finalSize = size;
                                    promCat.child(String.valueOf(i1)).putFile(photos_uri.get(i1)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if (++c == k - min_size) {
                                                final int[] d = {0};
                                                if (k < start_size)
                                                    for (int i2 = k; i2 < start_size; i2++) {
                                                        firebaseStorage.getReferenceFromUrl(photos_uri.get(i2).toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                if (++d[0] == start_size - k)
                                                                    addOffer(databaseReference, progressDialog, finalSize);
                                                            }
                                                        });
                                                    }
                                                else
                                                    addOffer(databaseReference, progressDialog, finalSize);
                                            }
                                        }
                                    });
                                }
                            }
                            else{
                                final int[] d = {0};
                                if (k<start_size)
                                    for (int i2 = k; i2 < start_size; i2++) {
                                        int finalSize1 = size;
                                        firebaseStorage.getReferenceFromUrl(photos_uri.get(i2).toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                if (++d[0] == start_size - k)
                                                    addOffer(databaseReference, progressDialog, finalSize1);
                                            }
                                        });
                                    }
                                else addOffer(databaseReference, progressDialog, size);
                            }
                                /*@Override
                                public void onSuccess(Void unused) {
                                    databaseReference.child("uri").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                        }
                                    });



                                }*/
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else Toast.makeText(this,"Заполните все поля правильно",Toast.LENGTH_LONG).show();
        }
    }

    /*public void recPhotoAdd(StorageReference sr, int i, DatabaseReference reference){
        int y = (photos_uri.get(photos_uri.size()-1)==null) ?photos_uri.size()-1:photos_uri.size();
        if (i==y) {addOffer(reference);return;}
        Log.i("recPhotoAdd","recPhotoAdd");


    }*/

    public void addOffer(DatabaseReference databaseReference, ProgressDialog progressDialog, int size){
        final int[] k = {0};
        if (main_count==-1){
            database.child("users").child(android_id).child("users_categories").child(category.replace('/','!')).setValue(size);
        }



        databaseReference.child("ProductName").setValue(productName.getText().toString()).addOnSuccessListener(unused -> {
            k[0]++;
            if (k[0]==6) {
                progressDialog.cancel();
                finish();
            }
        });
        databaseReference.child("ProductDesc").setValue(productDescription.getText().toString()).addOnSuccessListener(unused -> {
            k[0]++;
            if (k[0]==6) {
                progressDialog.cancel();
                finish();
            }
        });
        databaseReference.child("want").setValue(String.valueOf(serviceCost.getText())).addOnSuccessListener(unused -> {
            k[0]++;
            if (k[0]==6) {
                progressDialog.cancel();
                finish();
            }
        });
        databaseReference.child("start").setValue(dateAndTime1.get(Calendar.HOUR_OF_DAY)*60+dateAndTime1.get(Calendar.MINUTE)).addOnSuccessListener(unused -> {
            k[0]++;
            if (k[0]==6) {
                progressDialog.cancel();
                finish();
            }
        });
        databaseReference.child("end").setValue(dateAndTime2.get(Calendar.HOUR_OF_DAY)*60 + dateAndTime2.get(Calendar.MINUTE)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                k[0]++;
                if (k[0]==6) {
                    progressDialog.cancel();
                    finish();
                }
            }
        });
        databaseReference.child("unit").setValue(dateAndTime3.get(Calendar.HOUR_OF_DAY)*60+dateAndTime3.get(Calendar.MINUTE)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                k[0]++;
                if (k[0]==6) {
                    progressDialog.cancel();
                    finish();
                }
            }
        });
    }

    class New_offer_adapter extends RecyclerView.Adapter<New_offer_holder>{
        LayoutInflater layoutInflater;
        public New_offer_adapter(Context context){
            layoutInflater=LayoutInflater.from(context);
        }
        @NonNull
        @Override
        public New_offer_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (!Fresco.hasBeenInitialized())
                Fresco.initialize(getApplicationContext());
            View view = layoutInflater.inflate(R.layout.new_offer_item,parent,false);
            return new New_offer_holder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onBindViewHolder(@NonNull New_offer_holder holder, @SuppressLint("RecyclerView") int position) {
            if (photos_uri.get(position)==null){
                holder.itemView.setOnClickListener(v -> getPhoto(1));
                holder.imageView.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
                holder.clear.setVisibility(View.GONE);
            }
            else{
                Log.i("qwfqwfqwf", String.valueOf(photos_uri.get(position)));
                Picasso.get().load(photos_uri.get(position)).into(holder.imageView);
                holder.itemView.setOnClickListener(v -> {
                    Intent i = new Intent(New_offer.this,Photo_review.class);
                    i.putExtra("uri",photos_uri.get(position).toString());
                    i.putExtra("position",position);
                    startActivityForResult(i,2);
                });
                if (position<photos_uri.size()-2 && photos_uri.get(photos_uri.size()-1)==null || position<photos_uri.size()-1 && photos_uri.get(photos_uri.size()-1)!=null){
                    holder.clear.setVisibility(View.GONE);
                }
                else{
                    holder.clear.setVisibility(View.VISIBLE);
                    Log.i("visible", String.valueOf(position));
                    holder.clear.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onClick(View view) {
                            if (photos_uri.get(photos_uri.size()-1)!=null)
                                photos_uri.add(null);
                            if (!(photos_uri.size()-1+ Boolean.compare(true, photos_uri.get(photos_uri.size() - 1) == null)>min_size || main_count==-1)){
                                min_size--;
                            }
                            photos_uri.remove(position);
                            photos.post(() -> adapter.notifyDataSetChanged());

                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return photos_uri.size();
        }
    }

    private Uri bitmapToFile(Bitmap bitmap){
        // Get the context wrapper
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        // Initialize a new file instance to save bitmap object
        File file = wrapper.getDir("Images",Context.MODE_PRIVATE);
        file = new File(file,UUID.randomUUID()+".jpg");

        try{
            // Compress the bitmap and save in jpg format
            OutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            stream.flush();
            stream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        Log.i("qwfqwfqwf", String.valueOf(Uri.parse(file.getAbsolutePath())));
        // Return the saved bitmap uri
        return Uri.parse(file.getAbsolutePath());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK && data!=null)
            switch (requestCode){
                case 1:
                    int count = MAX_PHOTO_COUNT+1-photos_uri.size();
                    int k;
                    photos_uri.remove(photos_uri.size()-1);
                    boolean aBoolean = true;
                    if (data.getClipData()==null) {
                        Log.i("ClipData","data.getClipData()==null");
                        Uri uri = data.getData();
                        if (uri==null){
                            Log.i("ClipData","data.getData()==null");
                            return;
                        }
                        photos_uri.add(uri);

                    }
                    else{
                        k = data.getClipData().getItemCount();
                        for (int i=0;i<k && i<count;i++){
                            photos_uri.add(data.getClipData().getItemAt(i).getUri());
                        }
                    }



                    Log.i("TestUriSize", String.valueOf(photos_uri.size()));
                    Log.i("TestUriSize",String.valueOf(MAX_PHOTO_COUNT));
                    if (photos_uri.size()<MAX_PHOTO_COUNT){
                        photos_uri.add(null);
                    }
                    adapter.notifyDataSetChanged();
                    photos.smoothScrollToPosition(photos_uri.size());
                    break;
                case 2:
                    String result = data.getStringExtra("result");
                    position = data.getIntExtra("position",0);
                    Log.i("TestPosition",String.valueOf(position));
                    switch (result){
                        case "change":
                            photos_uri.set(data.getIntExtra("position",0),photos_uri.get(photos_uri.size()-1));
                            photos_uri.remove(photos_uri.size()-1);
                            adapter.notifyDataSetChanged();
                            break;
                        case "remove":
                            if (photos_uri.size()==MAX_PHOTO_COUNT && photos_uri.get(photos_uri.size()-1)!=null) photos_uri.add(null);
                            photos_uri.remove(position);
                            adapter.notifyDataSetChanged();
                            photos.smoothScrollToPosition(photos_uri.size());
                            break;
                    }
                    break;
                case 3:
                    photos_uri.set(position,data.getData());
                    adapter.notifyDataSetChanged();
                    photos.smoothScrollToPosition(photos_uri.size());
                    break;
                case 4:
                    category = data.getStringExtra("ProductCategory");
                    productCategory.setText(data.getStringExtra("ProductCategory").substring(16));
                    cancel11.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    cancel22.setVisibility(View.VISIBLE);
                    serviceCost.setText(data.getStringExtra("ProductCategory").substring(16));
                    category2 = data.getStringExtra("ProductCategory");

            }
    }

    public void getPhoto(int intent){
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        i.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        i.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i,"Choose Image"),intent);
    }

    public void onCancelClick(View v){
        if (v.getId()==R.id.cancel11){
            if (main_count==-1) {
                productCategory.setText("Выберите категорию");
                category = null;
                v.setVisibility(View.GONE);
            }
            else Toast.makeText(getApplicationContext(),"Вы не можете изменять категорию своего товара",Toast.LENGTH_LONG).show();
        }
        else if (v.getId()==R.id.cancel22){
            serviceCost.setText("");
            category2 = null;
            v.setVisibility(View.GONE);
        }
        else if (v.getId()==R.id.goBack2){
            finish();
        }
        else if (v.getId() == R.id.deleteOffer){
            Log.i("delete",category+"sell/"+android_id+"/"+main_count+"/");
            database.child(category).child("sell").child(android_id).child(String.valueOf(main_count)).setValue(null);
            database.child("users").child(android_id).child("users_categories").child(category.replace("/","!")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        int k =snapshot.getValue(Integer.class);
                        if (k==1) database.child("users").child(android_id).child("users_categories").child(category.replace("/","!")).setValue(null);
                        else database.child("users").child(android_id).child("users_categories").child(category.replace("/","!")).setValue(k-1);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            deletePictFromStorage(mStorageRef.child(category));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (main_count==-1) {
            Intent i = new Intent(New_offer.this, choose_act_to_show.class);
            startActivity(i);
        }
    }
}