package com.example.ohee.model;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HighSchooler {
    private String userName, name, email, password, idUser, picturePath, searchName;
    private String bio = "I'm new to OhEE!";
    private List<University> interests = new ArrayList();

    public HighSchooler() {
    }

    public HighSchooler(String userName, String name, String email, String password) {
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public HighSchooler(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void save() {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        firebase.child("highschoolers")
                .child(this.idUser)
                .setValue(this);
    }

    public void updatePersonalInfo() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference userRef = firebaseRef
                .child("highschoolers")
                .child(getIdUser());

        Map<String, Object> userValues = convertPersonalInfoToMap();

        userRef.updateChildren(userValues);
    }

    public Map<String, Object> convertPersonalInfoToMap() {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("name", getName());
        usersMap.put("bio", getBio());
        usersMap.put("searchName", getName().toUpperCase());

        return usersMap;
    }

    public void updateImg() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference userRef = firebaseRef
                .child("highschoolers")
                .child(getIdUser());

        Map<String, Object> userValues = convertImgToMap();

        userRef.updateChildren(userValues);
    }

    public Map<String, Object> convertImgToMap() {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("picturePath", getPicturePath());

        return usersMap;
    }

    public void updateInterests() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference userRef = firebaseRef
                .child("highschoolers")
                .child(getIdUser());

        Map<String, Object> userValues = convertListToMap();

        userRef.updateChildren(userValues);
    }

    public Map<String, Object> convertListToMap() {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("interests", getInterests());

        return usersMap;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public List<University> getInterests() {
        return interests;
    }

    public void setInterests(List<University> interests) {
        this.interests = interests;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
