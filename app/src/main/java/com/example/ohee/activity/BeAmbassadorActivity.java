package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ohee.R;
import com.example.ohee.fragment.AnswerQuestionsFragment;
import com.example.ohee.fragment.FollowingFeedFragment;
import com.example.ohee.fragment.HomeFragment;
import com.example.ohee.fragment.YourUniversityFeedFragment;
import com.google.android.material.tabs.TabLayout;

public class BeAmbassadorActivity extends AppCompatActivity {
    private TabLayout tabs;
    private ViewPager pager;
    private Button btMode;
    private String mode = "recent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        tabs    = findViewById(R.id.tabsHome);
        pager   = findViewById(R.id.pagerHome);
        btMode  = findViewById(R.id.btMode);

        btMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("recent")) {
                    btMode.setBackgroundResource(R.drawable.ic_top_selected);
                    mode = "top";
                    pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                    tabs.setupWithViewPager(pager);
                } else if (mode.equals("top")) {
                    btMode.setBackgroundResource(R.drawable.ic_recent_selected);
                    mode = "recent";
                    pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
                    tabs.setupWithViewPager(pager);
                }
            }
        });

        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitles = new String[]{"home", "public"};

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new AnswerQuestionsFragment(true, mode);
            } else {
                return new AnswerQuestionsFragment(false, mode);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}