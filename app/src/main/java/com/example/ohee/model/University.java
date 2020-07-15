package com.example.ohee.model;

//import com.google.gson.annotations.SerializedName;

import android.widget.Toast;

import com.example.ohee.activity.SignUpActivity;
import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class University implements Serializable {
    private String name;
    private String city;
    private String state;
    private String domain;
    private List<String> students = new ArrayList<>();
    private int count;

    public University(JSONObject json) throws JSONException {
        JSONObject whois = json.getJSONObject("WhoisRecord");
        JSONObject registryData = whois.getJSONObject("registryData");
        JSONObject registrant = registryData.getJSONObject("registrant");

        String txtName = registrant.getString("name");
        if (txtName.contains("\n")) {
            int endAt = txtName.indexOf("\n");
            this.name = txtName.substring(0, endAt);
        } else {
            this.name = txtName;
        }
        this.city = registrant.optString("city");
        this.state = registrant.optString("state");
        this.count = 0;
    }

    public University() {
    }

    public void update() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference universityRef = firebaseRef
                .child("universities")
                .child(getDomain());

        Map<String, Object> userValues = convertInfoToMap();

        universityRef.updateChildren(userValues);
    }

//    public void updateUser(User user, int i) {
//        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
//        DatabaseReference universityRef = firebaseRef
//                .child("universities")
//                .child(getDomain())
//                .child("students")
//                .child(i + "");
//
//        Map<String, Object> userValues = mapUser(user);
//
//        universityRef.updateChildren(userValues);
//    }

    public void save() {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        firebase.child("universities")
                .child(this.getDomain())
                .setValue(this);
    }

    public void addUser(String user) {
        List<String> users = this.getStudents();
        users.add(user);
        setStudents(users);
        update();
    }

    public Map<String, Object> convertInfoToMap() {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("count", getCount());
        usersMap.put("students", getStudents());

        return usersMap;
    }

    public Map<String, Object> mapUser(User user) {
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("name", user.getName());
        usersMap.put("bio", user.getBio());
        usersMap.put("searchName", user.getName().toUpperCase());
        usersMap.put("postCount", user.getPostCount());
//        usersMap.put("followingCount", user.getFollowingCount());
//        usersMap.put("followerCount", user.getFollowerCount());
        usersMap.put("status", user.getStatus());
        usersMap.put("sex", user.getSex());

        return usersMap;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }
}
