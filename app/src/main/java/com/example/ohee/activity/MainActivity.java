package com.example.ohee.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ohee.R;
import com.example.ohee.fragment.ExploreFragment;
import com.example.ohee.fragment.HomeFragment;
import com.example.ohee.fragment.NotificationsFragment;
import com.example.ohee.fragment.PostFragment;
import com.example.ohee.fragment.ProfileFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {
    private ImageView navHome, navExplore, navNotifications, navProfile;
    private FloatingActionButton fabPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHome = findViewById(R.id.homeIcon);
        navExplore = findViewById(R.id.explorIcon);
        navNotifications = findViewById(R.id.notificationsIcon);
        navProfile = findViewById(R.id.profileIcon);
        fabPost = findViewById(R.id.fabPost);

        navigateBar();
    }

    public void navigateBar() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new HomeFragment()).commit();
        navHome.setImageResource(R.drawable.ic_home_selected);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new HomeFragment()).commit();
                navHome.setImageResource(R.drawable.ic_home_selected);
                navExplore.setImageResource(R.drawable.ic_explore_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_unselected);
                navProfile.setImageResource(R.drawable.ic_profile_unselected);
            }
        });

        navExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new ExploreFragment()).commit();
                navExplore.setImageResource(R.drawable.ic_explore_selected);
                navHome.setImageResource(R.drawable.ic_home_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_unselected);
                navProfile.setImageResource(R.drawable.ic_profile_unselected);
            }
        });

        navNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new NotificationsFragment()).commit();
                navExplore.setImageResource(R.drawable.ic_explore_unselected);
                navHome.setImageResource(R.drawable.ic_home_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_selected);
                navProfile.setImageResource(R.drawable.ic_profile_unselected);
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new ProfileFragment()).commit();
                navExplore.setImageResource(R.drawable.ic_explore_unselected);
                navHome.setImageResource(R.drawable.ic_home_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_unselected);
                navProfile.setImageResource(R.drawable.ic_profile_selected);
            }
        });

        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new PostFragment()).commit();
                navExplore.setImageResource(R.drawable.ic_explore_unselected);
                navHome.setImageResource(R.drawable.ic_home_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_unselected);
                navProfile.setImageResource(R.drawable.ic_profile_unselected);
            }
        });
    }
}
