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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User implements Serializable {
    private String userName, name, email, password, idUser, picturePath, searchName, universityName, universityDomain, status, sex;
    private String bio = "Im new to OhEE!";
    private int rotation = 0;
    private int postCount = 0;
    private String isPrivate, isAmbassador;

    public User(String userName, String name, String email, String password, String universityName, String universityDomain) {
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.password = password;
        this.universityName = universityName;
        this.universityDomain = universityDomain;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User() {
    }

    public void save() {
        setIsPrivate("false");
        setIsAmbassador("false");
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

    public void updatePersonalInfo() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference userRef = firebaseRef
                .child("user")
                .child(getIdUser());

        Map<String, Object> userValues = convertPersonalInfoToMap();

        userRef.updateChildren(userValues);
    }

//    public void changeFollower(String user, String act) {
//        List<String> followers = this.getFollowers();
//        if (act.equals("add")) {
//            followers.add(user);
//        } else if (act.equals("remove")) {
//            followers.remove(user);
//        }
//        setFollowers(followers);
//        updateLists();
//    }
//
//    public void changeFollowing(String user, String act) {
//        List<String> following = this.getFollowing();
//        if (act.equals("add")) {
//            following.add(user);
//        } else if (act.equals("remove")) {
//            following.remove(user);
//        }
//        setFollowing(following);
//        updateLists();
//    }

    public void updateLists() {
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
//        usersMap.put("followingCount", getFollowingCount());
//        usersMap.put("followerCount", getFollowerCount());
        usersMap.put("status", getStatus());
        usersMap.put("sex", getSex());
//        usersMap.put("followers", getFollowers());
//        usersMap.put("following", getFollowing());

        return usersMap;
    }

    public Map<String, Object> convertPersonalInfoToMap() {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("name", getName());
        usersMap.put("bio", getBio());
        usersMap.put("searchName", getName().toUpperCase());
        usersMap.put("status", getStatus());
        usersMap.put("sex", getSex());
        usersMap.put("isPrivate", getIsPrivate());
        usersMap.put("isAmbassador", getIsAmbassador());
        usersMap.put("rotation", getRotation());

        return usersMap;
    }

    public Map<String, Object> convertImgToMap() {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("picturePath", getPicturePath());

        return usersMap;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
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

    public String getUniversityDomain() {
        return universityDomain;
    }

    public void setUniversityDomain(String universityDomain) {
        this.universityDomain = universityDomain;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getIsAmbassador() {
        return isAmbassador;
    }

    public void setIsAmbassador(String isAmbassador) {
        this.isAmbassador = isAmbassador;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}
