//package com.example.ohee.fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.SearchView;
//
//import com.example.ohee.R;
//import com.example.ohee.activity.VisitProfileActivity;
//import com.example.ohee.adapter.SearchAdapter;
//import com.example.ohee.helpers.RecyclerItemClickListener;
//import com.example.ohee.helpers.SetFirebase;
//import com.example.ohee.helpers.SetFirebaseUser;
//import com.example.ohee.model.User;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class ExploreFragment extends Fragment {
//    private SearchView searchView;
//    private RecyclerView recyclerSearch;
//
//    private List<User> userList = new ArrayList<>();
//
//    private DatabaseReference userRef = SetFirebase.getFirebaseDatabase().child("user");
//
//    private String idLoggedUSer = SetFirebaseUser.getUsersId();;
//
//    private SearchAdapter adapter;
//
//    public ExploreFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_explore, container, false);
//
//        searchView = view.findViewById(R.id.searchView);
//        recyclerSearch = view.findViewById(R.id.recyclerSearch);
//
//        // Set adapter
//        adapter = new SearchAdapter(userList, getActivity());
//
//        // Set recycler
//        recyclerSearch.setHasFixedSize(true);
//        recyclerSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerSearch.setAdapter(adapter);
//
//        // Set recycler click
//        recyclerSearch.addOnItemTouchListener(new RecyclerItemClickListener(
//                getActivity(),
//                recyclerSearch,
//                new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        User selectedUser = userList.get(position);
//                        Intent i = new Intent(getActivity(), VisitProfileActivity.class);
//                        i.putExtra("selectedUser", selectedUser);
//                        startActivity(i);
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//
//                    }
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    }
//                }
//        ));
//
//        // Set searchview
//        searchView.setQueryHint("Search other users");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                String typedTxt = newText.toUpperCase();
//                searchUser(typedTxt);
//                return true;
//            }
//        });
//
//        return view;
//    }
//
//    public void searchUser (String str) {
//        userList.clear();
//
//        if (!str.isEmpty()) {
//            Query query = userRef.orderByChild("searchName")
//                    .startAt(str)
//                    .endAt(str + "\uf8ff");
//
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    userList.clear();
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        User user = ds.getValue(User.class);
//                        if (!idLoggedUSer.equals(user.getIdUser())) {
//                            userList.add(user);
//                        }
//                    }
//
//                    adapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }
//}



package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.ohee.R;
import com.example.ohee.activity.MainActivity;
import com.example.ohee.activity.OhYeeActivity;
import com.example.ohee.activity.SearchActivity;
import com.example.ohee.activity.VisitProfileActivity;
import com.example.ohee.adapter.SearchAdapter;
import com.example.ohee.helpers.RecyclerItemClickListener;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {
    private TabLayout tabs;
    private ViewPager pager;
    private ImageView btSearch, btOhYee;

    public ExploreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        btSearch        = view.findViewById(R.id.btSearch);
        btOhYee         = view.findViewById(R.id.btOhYee);
        tabs            = view.findViewById(R.id.tabs);
        pager           = view.findViewById(R.id.pager);

        // Set adapter for viewpager
        pager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        tabs.setupWithViewPager(pager);

        // Open search activity
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        btOhYee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OhYeeActivity.class));
            }
        });


        return view;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private String[] tabTitles = new String[]{"explore", "universities"};

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
                return new ExploreUsersFragment();
            } else {
                return new UniversitiesExploreFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}