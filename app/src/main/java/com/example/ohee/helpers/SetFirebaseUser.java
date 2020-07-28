package com.example.ohee.helpers;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ohee.model.HighSchooler;
import com.example.ohee.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SetFirebaseUser {

    public static FirebaseUser getUser() {
        FirebaseAuth user = SetFirebase.getFirebaseAuth();
        return user.getCurrentUser();
    }

    public static void updateUsersName(String name) {
        try {
            // Logged user
            FirebaseUser loggedUser = getUser();

            // Object to update profile
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(name)
                    .build();

            loggedUser.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Profile", "Error name");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUsersPhoto(Uri url) {
        try {
            // Logged user
            FirebaseUser loggedUser = getUser();

            // Object to update profile
            UserProfileChangeRequest profile = new UserProfileChangeRequest
                    .Builder()
                    .setPhotoUri(url)
                    .build();

            loggedUser.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Profile", "Error Photo");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static User getUserData() {
        FirebaseUser firebaseUser = getUser();

        User user = new User();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        user.setIdUser(firebaseUser.getUid());

        if (firebaseUser.getPhotoUrl() == null) {
            user.setPicturePath("");
        } else {
            user.setPicturePath(firebaseUser.getPhotoUrl().toString());
        }

        return user;
    }

    public static HighSchooler getHighSchoolerData() {
        FirebaseUser firebaseUser = getUser();

        HighSchooler user = new HighSchooler();
        user.setEmail(firebaseUser.getEmail());
        user.setName(firebaseUser.getDisplayName());
        user.setIdUser(firebaseUser.getUid());

        if (firebaseUser.getPhotoUrl() == null) {
            user.setPicturePath("");
        } else {
            user.setPicturePath(firebaseUser.getPhotoUrl().toString());
        }

        return user;
    }

    public static String getUsersId() {
        return getUser().getUid();
    }

}
