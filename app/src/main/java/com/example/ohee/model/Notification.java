package com.example.ohee.model;

import androidx.annotation.NonNull;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String idReceiver;
    private String idSender;
    private String idNotification;
    private String action;
    private String idPost;
    private String comment;

    public Notification() {

    }


    public void save() {
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference notificationRef = databaseReference
                .child("notifications")
                .child(this.idReceiver);

        String idNotify = notificationRef.push().getKey();
        setIdNotification(idNotify);


        databaseReference
                .child("notifications")
                .child(this.idReceiver)
                .child(idNotify)
                .setValue(this);

    }

    public void deleteNotification() {
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference notificationsRef = databaseReference
                .child("notifications").child(getIdReceiver());

        notificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Notification notification = ds.getValue(Notification.class);
                    if (notification.getIdPost().equals(getIdPost()) && notification.getIdSender().equals(getIdSender())) {
                        DatabaseReference notificationRef = notificationsRef.child(notification.getIdNotification());
                        notificationRef.removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Exclude
    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }


    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
