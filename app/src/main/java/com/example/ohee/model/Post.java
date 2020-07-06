package com.example.ohee.model;

import com.example.ohee.helpers.SetFirebase;
import com.example.ohee.helpers.SetFirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Post implements Serializable {
    private String idUser, id, caption, path, type, universityDomain;

    public Post() {
        DatabaseReference databaseReference = SetFirebase.getFirebaseDatabase();
        DatabaseReference postRef = databaseReference.child("posts");

        String idPost = postRef.push().getKey();

        setId(idPost);
    }

    public void save(DataSnapshot ds) {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        Map object = new HashMap<>();
        User loggedUser = SetFirebaseUser.getUserData();

        firebase.child("posts").child(getUniversityDomain());

        String fullId = "/" + getUniversityDomain() + "/" + getId();
        object.put("/posts" + fullId, this);

        for (DataSnapshot followers : ds.getChildren()) {
            HashMap<String, Object> dataFollowers = new HashMap<>();
            dataFollowers.put("path", getPath());
            dataFollowers.put("caption", getCaption());
            dataFollowers.put("id", getId());
            dataFollowers.put("userName", loggedUser.getName());
            dataFollowers.put("userPic", loggedUser.getPicturePath());

            String id = followers.getKey() + "/" + getId();
            object.put("/feed/" + id, dataFollowers);

        }

firebase.updateChildren(object);
//        DatabaseReference postsRef = firebase.child("posts")
//                .child(universityDomain)
//                .child(getId());
//
//
//        postsRef.setValue(this);
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
}
