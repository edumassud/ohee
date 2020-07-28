package com.example.ohee.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.ohee.R;
import com.example.ohee.fragment.ExploreFragment;
import com.example.ohee.fragment.HSHomeFragment;
import com.example.ohee.fragment.HSProfileFragment;
import com.example.ohee.fragment.HomeFragment;
import com.example.ohee.fragment.NotificationsFragment;
import com.example.ohee.fragment.ProfileFragment;
import com.example.ohee.fragment.QAndAFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainHSActivity extends AppCompatActivity {
    private ImageView navHome, navExplore, navNotifications, navProfile;
    private FloatingActionButton fabPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_h_s);

        navHome = findViewById(R.id.homeIcon);
        navExplore = findViewById(R.id.chatIcon);
        navNotifications = findViewById(R.id.notificationsIcon);
        navProfile = findViewById(R.id.profileIcon);
        fabPost = findViewById(R.id.fabPost);

        navigateBar();
    }

    public void navigateBar() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new HSHomeFragment()).commit();
        navHome.setImageResource(R.drawable.ic_home_hs_selected);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new HSHomeFragment()).commit();
                navHome.setImageResource(R.drawable.ic_home_hs_selected);
                navExplore.setImageResource(R.drawable.ic_chat_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_unselected);
                navProfile.setImageResource(R.drawable.ic_profile_unselected);
            }
        });

        navExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new QAndAFragment()).commit();
                navExplore.setImageResource(R.drawable.ic_chat_selected);
                navHome.setImageResource(R.drawable.ic_home_hs_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_unselected);
                navProfile.setImageResource(R.drawable.ic_profile_unselected);
            }
        });

        navNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new NotificationsFragment()).commit();
                navExplore.setImageResource(R.drawable.ic_chat_unselected);
                navHome.setImageResource(R.drawable.ic_home_hs_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_selected);
                navProfile.setImageResource(R.drawable.ic_profile_unselected);
            }
        });

        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewPager, new HSProfileFragment()).commit();
                navExplore.setImageResource(R.drawable.ic_chat_unselected);
                navHome.setImageResource(R.drawable.ic_home_hs_unselected);
                navNotifications.setImageResource(R.drawable.ic_notifications_unselected);
                navProfile.setImageResource(R.drawable.ic_profile_selected);
            }
        });

        fabPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MakeQuestionActivity.class));
            }
        });
    }
}