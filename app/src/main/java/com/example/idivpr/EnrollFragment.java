package com.example.idivpr;

import static com.example.idivpr.MainActivity.android_id;
import static com.example.idivpr.MainActivity.database;
import static com.example.idivpr.MainActivity.f;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.facebook.imagepipeline.producers.LocalFileFetchProducer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EnrollFragment extends AppCompatDialogFragment {

    private final int fromMyOffers = 1;

    private int start, end, unit, fromWhere;
    private Context context;
    boolean[] checkedItemsArray;
    String reference;
    DataSnapshot snap;
    FragmentManager manager;
    ArrayList<String> idis;

    public EnrollFragment(int start, int end, int unit, String  reference, Context context, int fromWhere, FragmentManager manager) {
        this.reference = reference;
        this.start=start;
        this.end=end;
        this.unit=unit;
        this.context = context;
        this.fromWhere = fromWhere;
        this.manager = manager;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_enroll, container, true);
    }

    @NonNull
    @SuppressLint("ResourceType")
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //DatePicker datePicker = new DatePicker(getActivity());
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_enroll,null);
        DatePicker datePicker = v.findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        HashSet<Integer> blocked = new HashSet<>();
        ArrayList<String> timetable=new ArrayList<>();
        //ArrayList<Boolean> checkedItemsArray = new ArrayList<>();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        final int[] cntOfChecked = {0};
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
        HashSet<Integer> offset = new HashSet<>();
        final boolean[] ifIcan = {false};
        final boolean[] NeutBut = {false};
        builder1.setTitle("Выберите дату")
                .setNeutralButton("Далее", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("reference",reference);
                        database.child(reference).child(datePicker.getYear() +"/"+datePicker.getMonth()+"/"+datePicker.getDayOfMonth()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snap = snapshot;
                                Log.i("snap2",snapshot.toString());

                                if (fromWhere==fromMyOffers){
                                    idis = new ArrayList<>();
                                    for (int i=0;i<(end-start)/unit;i++)
                                        idis.add(null);
                                }

                                for (DataSnapshot t : snapshot.getChildren()) {
                                    if (t == null) continue;
                                    Log.i("vaer", t.getKey());
                                    if (fromWhere==fromMyOffers){
                                        offset.add(Integer.parseInt(Objects.requireNonNull(t.getKey())));
                                        int i = Integer.parseInt(Objects.requireNonNull(t.getKey()));
                                        idis.set(i,t.getValue(String.class));
                                        Log.i("wefwefwf",i+" "+t.getValue(String.class));
                                        if (!idis.get(i).equals(android_id) && !ifIcan[0]) {
                                            ifIcan[0] = true;
                                        }
                                    }
                                    else {
                                        if (!Objects.equals(t.getValue(String.class), android_id)) {
                                            int i = Integer.parseInt(Objects.requireNonNull(t.getKey()));
                                            blocked.add(i);
                                        } else
                                            offset.add(Integer.parseInt(Objects.requireNonNull(t.getKey())));
                                    }
                                }

                                if (fromWhere==fromMyOffers){
                                    checkedItemsArray = new boolean[(int) (end-start)/unit];
                                }
                                else
                                    checkedItemsArray = new boolean[(int) ((end-start)/unit - snapshot.getChildrenCount())+offset.size()];
                                Log.i("fromwhere", String.valueOf(fromWhere==fromMyOffers));
                                if (fromWhere==fromMyOffers && ifIcan[0]) {
                                    Log.i("nearBuilder","FEWFWEFWEFWF");
                                    builder2.setNeutralButton("О пользователе", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.i("fwfwefwefwef", "fwefwefwf");
                                            InfoAboutUser myDialogFragment = new InfoAboutUser(snap, start, end, unit, context);
                                            myDialogFragment.show(manager, "wefwef");
                                        }
                                    });
                                }

                                int i = 0;
                                int st = start;
                                while (st+unit<=end){
                                    if (!blocked.contains(i)){
                                        String s="";
                                        if (st/60<10)
                                            s+="0";
                                        s+=st/60+":";
                                        if (st%60<10)
                                            s+="0";
                                        s+=st%60+" - ";
                                        if ((st+unit)/60<10)
                                            s+="0";
                                        s+=(st+unit)/60+":";
                                        if ((st+unit)%60<10)
                                            s+="0";
                                        s+=(st+unit)%60;
                                        timetable.add(s);
                                        checkedItemsArray[timetable.size()-1]= offset.contains(i);
                                        if (checkedItemsArray[timetable.size()-1])
                                            cntOfChecked[0]++;
                                    }
                                    st+=unit;
                                    i++;
                                }
                                Log.i("vaer",String.valueOf(timetable.size()));
                                builder2.setMultiChoiceItems((CharSequence[]) timetable.toArray(new CharSequence[0]), checkedItemsArray,
                                        new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which, boolean isChecked) {
                                                Log.i("onClick","fweokfwef");
                                                checkedItemsArray[which]=isChecked;
                                                if (isChecked) {
                                                    cntOfChecked[0]++;
                                                    if (cntOfChecked[0]>checkedItemsArray.length/2 && checkedItemsArray.length>=10 && fromWhere!=fromMyOffers && !NeutBut[0]){
                                                        Toast.makeText(context,"Укажите, что вы не робот",Toast.LENGTH_LONG).show();
                                                        NeutBut[0] =true;
                                                        builder2.setNeutralButton("Я не робот", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                NeutBut[0]=false;
                                                                builder2.setNeutralButton("", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                                    }
                                                                }).show();
                                                            }
                                                        }).create().show();
                                                        dialog.cancel();

                                                    }
                                                }
                                                else cntOfChecked[0]--;
                                            }

                                        });
                                builder2.show();
                                dialogInterface.cancel();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                })
                .setView(v);
        String s = "Выберите время встречи";
        if (fromWhere==fromMyOffers){
            s="Ваше расписание";
        }
        builder2.setTitle(s)
                .setPositiveButton("Готово",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                if (NeutBut[0]){
                                    Toast.makeText(context,"Вы робот!",Toast.LENGTH_LONG).show();
                                }
                                else
                                database.child(reference).child(datePicker.getYear()+"/"+datePicker.getMonth()+"/"+datePicker.getDayOfMonth()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        HashSet<Integer> blockedAfter = new HashSet<>();
                                        for (DataSnapshot t:snapshot.getChildren()){
                                            if (!Objects.equals(t.getValue(String.class), android_id))
                                                blockedAfter.add(Integer.parseInt(Objects.requireNonNull(t.getKey())));
                                        }
                                        final int[] cnt = {0};
                                        if (cntOfChecked[0]!=0)
                                        for (int i=0;i<checkedItemsArray.length;i++){
                                            if (checkedItemsArray[i]){
                                                if (fromWhere==fromMyOffers && idis.get(i)==null)
                                                    idis.set(i,android_id);
                                                String s = timetable.get(i);
                                                int min = ((s.charAt(0)-'0')*10+(s.charAt(1)-'0'))*60+(s.charAt(3)-'0')*10+(s.charAt(4)-'0');
                                                int which2 = (min-start)/unit;
                                                if (blockedAfter.contains(which2) && fromWhere!=fromMyOffers){
                                                    Toast.makeText(context,"Это время стало занятым",Toast.LENGTH_LONG).show();
                                                    dialog.cancel();
                                                    onDestroy();
                                                    return;
                                                }
                                                else {
                                                    offset.remove(which2);
                                                    Log.i("sho", "sdfsdf");
                                                    if (fromWhere==fromMyOffers) {
                                                        database.child(reference).child(datePicker.getYear() + "/" + datePicker.getMonth() + "/" + datePicker.getDayOfMonth()).child(String.valueOf(which2)).setValue(idis.get(i)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                cnt[0]++;
                                                                Log.i("cnt", String.valueOf(cnt[0]));
                                                                if (cnt[0] == cntOfChecked[0]) {
                                                                    for (int t : offset) {
                                                                        database.child(reference).child(datePicker.getYear() + "/" + datePicker.getMonth() + "/" + datePicker.getDayOfMonth()).child(String.valueOf(t)).setValue(null);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                    else {
                                                        database.child(reference).child(datePicker.getYear() + "/" + datePicker.getMonth() + "/" + datePicker.getDayOfMonth()).child(String.valueOf(which2)).setValue(android_id).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                cnt[0]++;
                                                                Log.i("cnt", String.valueOf(cnt[0]));
                                                                if (cnt[0] == cntOfChecked[0]) {
                                                                    for (int t : offset) {
                                                                        database.child(reference).child(datePicker.getYear() + "/" + datePicker.getMonth() + "/" + datePicker.getDayOfMonth()).child(String.valueOf(t)).setValue(null);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                        }
                                        else{
                                            for (int t : offset) {
                                                database.child(reference).child(datePicker.getYear() + "/" + datePicker.getMonth() + "/" + datePicker.getDayOfMonth()).child(String.valueOf(t)).setValue(null);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })

                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });

                /*.setNeutralButton("Далее", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        View v2 = LayoutInflater.from(getActivity()).inflate(R.layout.enroll_lay2,null);
                        builder.setView(v2);
                        String[] timetable=new String[(end-start)/unit];
                        boolean[] checkedItemsArray = new boolean[(end-start)/unit];
                        int st = start;
                        while (st+unit<=end){
                            String s="";
                            if (st/60<10)
                                s+="0";
                            s+=st/60+":";
                            if (st%60<10)
                                s+="0";
                            s+=st%60+" - ";
                            if ((st+unit)/60<10)
                                s+="0";
                            s+=(st+unit)/60+":";
                            if ((st+unit)%60<10)
                                s+="0";
                            s+=(st+unit)%60;
                            timetable[(st-start)/unit]=s;
                            checkedItemsArray[(st-start)/unit]=false;
                            st+=unit;
                        }

                        builder.setTitle("Выберите время встречи")
                                .setMultiChoiceItems(timetable, checkedItemsArray,
                                        new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which, boolean isChecked) {
                                                checkedItemsArray[which] = isChecked;
                                            }
                                        })
                                .setPositiveButton("Готово",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                StringBuilder state = new StringBuilder();
                                                for (int i = 0; i < timetable.length; i++) {
                                                    state.append(timetable[i]);
                                                    if (checkedItemsArray[i])
                                                        state.append(" выбран\n");
                                                    else
                                                        state.append(" не выбран\n");
                                                }
                                                Toast.makeText(getActivity(),
                                                        state.toString(), Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        })

                                .setNegativeButton("Отмена",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        })
                                .setNeutralButton("Назад", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_enroll,null);

                                    }
                                });
                        builder.show();
                        Toast.makeText(getActivity(),"fwefwe",Toast.LENGTH_SHORT).show();
                    }
                });*/
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker1, year, month, dayOfMonth) -> {});
        /*String[] timetable=new String[(end-start)/unit];
        boolean[] checkedItemsArray = new boolean[(end-start)/unit];
        int st = start;
        while (st+unit<=end){
            String s="";
            if (st/60<10)
                s+="0";
            s+=st/60+":";
            if (st%60<10)
                s+="0";
            s+=st%60+" - ";
            if ((st+unit)/60<10)
                s+="0";
            s+=(st+unit)/60+":";
            if ((st+unit)%60<10)
                s+="0";
            s+=(st+unit)%60;
            timetable[(st-start)/unit]=s;
            checkedItemsArray[(st-start)/unit]=false;
            st+=unit;
        }

        builder.setTitle("Выберите время встречи")
                .setMultiChoiceItems(timetable, checkedItemsArray,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                checkedItemsArray[which] = isChecked;
                            }
                        })
                .setPositiveButton("Готово",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                StringBuilder state = new StringBuilder();
                                for (int i = 0; i < timetable.length; i++) {
                                    state.append(timetable[i]);
                                    if (checkedItemsArray[i])
                                        state.append(" выбран\n");
                                    else
                                        state.append(" не выбран\n");
                                }
                                Toast.makeText(getActivity(),
                                        state.toString(), Toast.LENGTH_LONG)
                                        .show();
                            }
                        })

                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });*/
        return builder1.create();
    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        Log.i("show","1");
        return super.show(transaction, tag);
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        Log.i("show","2");
        super.show(manager, tag);
    }
}