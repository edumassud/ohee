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
import com.example.ohee.fragment.FollowingFeedFragment;
import com.example.ohee.fragment.HomeFragment;
import com.example.ohee.fragment.StatutsDistFragment;
import com.example.ohee.fragment.UniversityDataFragment;
import com.example.ohee.fragment.UniversityProfileMainFragment;
import com.example.ohee.fragment.YourUniversityFeedFragment;
import com.example.ohee.model.University;
import com.google.android.material.tabs.TabLayout;

public class UniversityProfileActivity extends AppCompatActivity {
    private TabLayout tabs;
    private ViewPager pager;

    private University university;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_profile);

        tabs    = findViewById(R.id.tabs);
        pager   = findViewById(R.id.pager);


        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            university = (University) bundle.getSerializable("selectedUniversity");
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

//        private String[] tabTitles = new String[]{"Following", "Your University"};

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return tabTitles[position];
//        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new UniversityProfileMainFragment(university);
            } else if (position == 1) {
                return new UniversityDataFragment(university);
            } else {
                return new StatutsDistFragment(university);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
