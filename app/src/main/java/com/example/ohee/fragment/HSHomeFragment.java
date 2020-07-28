package com.example.ohee.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ohee.R;
import com.example.ohee.model.FeedExplore;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class HSHomeFragment extends Fragment {
    private TabLayout tabs;
    private ViewPager pager;

    public HSHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home_hs, container, false);

        tabs = view.findViewById(R.id.tabsHome);
        pager = view.findViewById(R.id.pagerHome);


        pager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pager);

        return view;

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitles = new String[]{"posts", "universities"};

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
                return new HSFeedFragment();
            } else {
                return new HSUniversitiesExploreFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
