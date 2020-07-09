package com.example.ohee.model;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post implements Serializable {
    private String idUser, id, caption, path, type, universityDomain, datePosted;
    private List<String> likedBy = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    public Post() {
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference postRef = databaseReference.child("posts");

        String idPost = postRef.push().getKey();

        setId(idPost);

        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        String strDate = dateFormat.format(currentTime);
        setDatePosted(strDate);

    }

    public void save(DataSnapshot ds) {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        Map object = new HashMap<>();

        firebase.child("posts").child(getUniversityDomain());

        String fullId = "/" + getUniversityDomain() + "/" + getId();
        object.put("/posts" + fullId, this);

        firebase.updateChildren(object);
    }

    public void upDateLikes() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference postRef = firebaseRef
                .child("posts")
                .child(getUniversityDomain())
                .child(getId());

        Map<String, Object> postLikes = convertLikesToMap();

        postRef.updateChildren(postLikes);
    }

    public Map<String, Object> convertLikesToMap() {
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("likedBy", getLikedBy());

        return postMap;
    }

    public void upDateComments() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference postRef = firebaseRef
                .child("posts")
                .child(getUniversityDomain())
                .child(getId());

        Map<String, Object> postComments = convertCommentsToMap();

        postRef.updateChildren(postComments);
    }

    public Map<String, Object> convertCommentsToMap() {
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("comments", getComments());

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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUniversityDomain() {
        return universityDomain;
    }

    public void setUniversityDomain(String universityDomain) {
        this.universityDomain = universityDomain;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }
}
