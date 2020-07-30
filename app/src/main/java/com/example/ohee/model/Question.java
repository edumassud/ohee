package com.example.ohee.model;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question implements Serializable {
    private String idUser, id, question, type, datePosted;
    private University specificUniversity;
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
                .child(this.id)
                .setValue(this);
    }

    public void upDateLikes() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference postRef = firebaseRef
                .child("questions")
                .child(getId());

        Map<String, Object> postLikes = convertLikesToMap();

        postRef.updateChildren(postLikes);
    }

    public Map<String, Object> convertLikesToMap() {
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("likedBy", getLikedBy());

        return postMap;
    }

    public void upDateAnswers() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference postRef = firebaseRef
                .child("questions")
                .child(getId());

        Map<String, Object> postComments = convertAnswersToMap();

        postRef.updateChildren(postComments);
    }

    public Map<String, Object> convertAnswersToMap() {
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("answers", getAnswers());

        return postMap;
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

    public University getSpecificUniversity() {
        return specificUniversity;
    }

    public void setSpecificUniversity(University specificUniversity) {
        this.specificUniversity = specificUniversity;
    }
}
