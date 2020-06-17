package com.example.ohee.model;

//import com.google.gson.annotations.SerializedName;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class University {
    private String name;
    private String city;
    private String state;
    private List<User> students;

    public University(JSONObject json) throws JSONException {
        JSONObject whois = json.getJSONObject("WhoisRecord");
        JSONObject registryData = whois.getJSONObject("registryData");
        JSONObject registrant = registryData.getJSONObject("registrant");

        this.name = registrant.getString("name");
        this.city = registrant.optString("city");
        this.state = registrant.optString("state");
    }

    public void save() {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        firebase.child("universities")
                .child(this.getName())
                .setValue(this);
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

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }
}
