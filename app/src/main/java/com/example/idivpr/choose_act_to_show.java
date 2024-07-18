package com.example.idivpr;

import static com.example.idivpr.MainActivity.android_id;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class choose_act_to_show extends FragmentActivity {
    public static BottomNavigationView bottomNavigationView;

    public choose_act_to_show() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.fragment_choose_act_to_show);
            bottomNavigationView = findViewById(R.id.navigation_View);
            bottomNavigationView.setOnNavigationItemSelectedListener(getBottomNavigationListener());
            bottomNavigationView.setSelectedItemId(R.id.search);
            Log.i("choose_act", "create");
        } catch (Exception e) {
            e.printStackTrace();
            Intent i = new Intent(choose_act_to_show.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener getBottomNavigationListener(){
        return (item) -> {

            switch (item.getItemId()){
                case R.id.search:
                    Fragment fragment1 = new Search_offer();
                    getSupportFragmentManager().beginTransaction().replace(R.id.act_to_show,fragment1).commit();
                    break;

                case  R.id.new_offer:
                    Intent i = new Intent(choose_act_to_show.this,New_offer.class);
                    startActivity(i);
                    break;
                case R.id.messages:
                    Fragment fragment = new ChatSelection();
                    getSupportFragmentManager().beginTransaction().replace(R.id.act_to_show,fragment).commit();
                    break;
                case R.id.my_offers:
                    Log.i("pressed","my_offers");
                    Fragment fragment2 = new MyOffers();
                    getSupportFragmentManager().beginTransaction().replace(R.id.act_to_show,fragment2).commit();
                    break;
            }
            return true;
        };
    }
}