package com.example.ohee.model;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DatabaseReference;

public class Post {
    private String idUser, id, caption, path, type;

    public Post() {
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference postRef = databaseReference.child("posts");

        String idPost = postRef.push().getKey();

        setId(idPost);
    }

    public void save() {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        DatabaseReference postsRef = firebase.child("posts")
                .child(getIdUser())
                .child(getId());
        postsRef.setValue(this);
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
}