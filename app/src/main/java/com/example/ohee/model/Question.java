package com.example.ohee.model;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question {
    private String idUser, id, question, type, datePosted;
    private List<String> likedBy = new ArrayList<>();
    private List<Comment> answers = new ArrayList<>();
    private List<University> universities = new ArrayList<>();

    public Question() {
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference questionRef = databaseReference.child("questions");

        String id = questionRef.push().getKey();

        setId(id);

        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        String strDate = dateFormat.format(currentTime);
        setDatePosted(strDate);
    }

    public void save() {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        firebase.child("questions")
                .child(this.idUser)
                .child(this.id)
                .setValue(this);
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public List<Comment> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Comment> answers) {
        this.answers = answers;
    }

    public List<University> getUniversities() {
        return universities;
    }

    public void setUniversities(List<University> universities) {
        this.universities = universities;
    }
}
