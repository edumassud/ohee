package com.example.ohee.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.ohee.R;
import com.example.ohee.fragment.AnswerQuestionsFragment;
import com.example.ohee.fragment.FollowingFeedFragment;
import com.example.ohee.fragment.HomeFragment;
import com.example.ohee.fragment.YourUniversityFeedFragment;
import com.google.android.material.tabs.TabLayout;

public class BeAmbassadorActivity extends AppCompatActivity {
    private TabLayout tabs;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        tabs = findViewById(R.id.tabsHome);
        pager = findViewById(R.id.pagerHome);

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
                return new AnswerQuestionsFragment(true);
            } else {
                return new AnswerQuestionsFragment(false);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}