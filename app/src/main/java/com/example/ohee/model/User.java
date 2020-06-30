package com.example.ohee.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String name, email, password, idUser, picturePath, searchName, universityName, status, sex;
    private String bio = "Im new to OhEE!";
    private int postCount = 0;
    private int followerCount = 0;
    private int followingCount = 0;

    public User(String name, String email, String password, String universityName) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.universityName = universityName;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public void save() {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        firebase.child("user")
                .child(this.idUser)
                .setValue(this);
    }

    public void updateInfo() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference userRef = firebaseRef
                .child("user")
                .child(getIdUser());

        Map<String, Object> userValues = convertInfoToMap();

        userRef.updateChildren(userValues);
    }

    public void updateImg() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference userRef = firebaseRef
                .child("user")
                .child(getIdUser());

        Map<String, Object> userValues = convertImgToMap();

        userRef.updateChildren(userValues);
    }

    public Map<String, Object> convertInfoToMap() {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("name", getName());
        usersMap.put("bio", getBio());
        usersMap.put("searchName", getName().toUpperCase());
        usersMap.put("postCount", getPostCount());
        usersMap.put("followingCount", getFollowingCount());
        usersMap.put("followerCount", getFollowerCount());
        usersMap.put("status", getStatus());
        usersMap.put("sex", getSex());

        return usersMap;
    }

    public Map<String, Object> convertImgToMap() {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("picturePath", getPicturePath());

        return usersMap;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUniversityName() {
        return universityName;
    }

    public void setUniversityName(String university) {
        this.universityName = university;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
