package com.example.idivpr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ShowPictures extends AppCompatActivity {
    int count;
    String reference;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = getIntent().getStringExtra("reference");

        setContentView(R.layout.activity_show_pictures);
        ViewPager pager = findViewById(R.id.show_pager);
        count = getIntent().getIntExtra("count",0);
        reference = getIntent().getStringExtra("reference");
        position = getIntent().getIntExtra("position",0);
        ShowPageAdapter adapter = new ShowPageAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(position);
        ImageButton imageButton = findViewById(R.id.stop_show);
        imageButton.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private class ShowPageAdapter extends FragmentPagerAdapter {

        public ShowPageAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return ShowPictFragment.newInstance(reference,position);
        }

        @Override
        public int getCount() {
            return count;
        }
    }
}