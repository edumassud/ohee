package com.example.ohee.model;

//import com.google.gson.annotations.SerializedName;

import android.widget.Toast;

import com.example.ohee.activity.SignUpActivity;
import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class University {
    private String name;
    private String city;
    private String state;
    private String domain;
    private List<User> students;

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
    }

    public University() {
    }

    public void save() {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        firebase.child("universities")
                .child(this.getDomain())
                .setValue(this);
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

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }
}
