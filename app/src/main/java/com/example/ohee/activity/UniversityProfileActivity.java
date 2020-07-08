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
import com.example.ohee.fragment.ContactsFragment;
import com.example.ohee.fragment.FollowingFeedFragment;
import com.example.ohee.fragment.HomeFragment;
import com.example.ohee.fragment.RatingsFragment;
import com.example.ohee.fragment.StatutsDistFragment;
import com.example.ohee.fragment.UniversityDataFragment;
import com.example.ohee.fragment.UniversityGridFragment;
import com.example.ohee.fragment.UniversityProfileMainFragment;
import com.example.ohee.fragment.UniversityStudentsFragment;
import com.example.ohee.fragment.YourUniversityFeedFragment;
import com.example.ohee.model.University;
import com.google.android.material.tabs.TabLayout;

public class UniversityProfileActivity extends AppCompatActivity {
    private TabLayout tabs;
    private ViewPager pager;
    private TabLayout tabsContent;
    private ViewPager pagerContent;

    private University university;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_profile);

        tabs            = findViewById(R.id.tabs);
        pager           = findViewById(R.id.pager);
        tabsContent     = findViewById(R.id.tabContent);
        pagerContent    = findViewById(R.id.pagerContent);


        pager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(pager);

        pagerContent.setAdapter(new PagerAdapterContent(getSupportFragmentManager()));
        tabsContent.setupWithViewPager(pagerContent);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            university = (University) bundle.getSerializable("selectedUniversity");
        }
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new UniversityProfileMainFragment(university);
            } else if (position == 1) {
                return new UniversityDataFragment(university);
            } else if (position == 2){
                return new StatutsDistFragment(university);
            } else {
                return new RatingsFragment(university);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private class PagerAdapterContent extends FragmentStatePagerAdapter {

        public PagerAdapterContent(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new UniversityGridFragment(university);
            } else {
                return new UniversityStudentsFragment(university);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
