package com.example.ohee.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ohee.R;
import com.example.ohee.activity.MessengerActivity;
import com.example.ohee.adapter.NotificationsAdapter;
import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.example.ohee.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {
    private ImageView btMessanger;
    private RecyclerView recycler;
    private SwipeRefreshLayout swipeRefresh;

    private List<Notification> notifications = new ArrayList<>();
    private NotificationsAdapter adapter;

    DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();

    private String idLoggedUser = SetFirebaseUser.getUsersId();

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);

        btMessanger  = view.findViewById(R.id.btMessanger);
        recycler     = view.findViewById(R.id.recycler);
        swipeRefresh = view.findViewById(R.id.refresh);

        btMessanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MessengerActivity.class));
            }
        });

        // Set adapter
        adapter = new NotificationsAdapter(notifications, getActivity());

        // Set recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
            }
        });

        getNotifications();

        return view;
    }

    private void getNotifications() {
        notifications.clear();
        DatabaseReference notificationsRef = databaseReference.child("notifications").child(idLoggedUser);
        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Notification notification = ds.getValue(Notification.class);
                    notifications.add(0, notification);
                }
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
