package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ohee.R;
import com.example.ohee.activity.SearchActivity;
import com.google.android.material.tabs.TabLayout;


public class QAndAFragment extends Fragment {
    private TabLayout tabs;
    private ViewPager pager;
    private ImageView btSearch;

    public QAndAFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_q_and_a, container, false);

        btSearch        = view.findViewById(R.id.btSearch);
        tabs            = view.findViewById(R.id.tabs);
        pager           = view.findViewById(R.id.pager);

        // Set adapter for viewpager
        pager.setAdapter(new QAndAFragment.PagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pager);

        // Open search activity
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        return view;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitles = new String[]{"my list", "public"};

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
                return new HSQandAFragment(false);
            } else {
                return new HSQandAFragment(true);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}