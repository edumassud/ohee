package com.example.ohee.model;

import androidx.annotation.NonNull;

import com.example.ohee.helpers.SetFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment implements Serializable, Comparable<Comment> {
    private String idUser, comment, idPost, idComment;
    private List<String> likedBy = new ArrayList<>();

    public Comment(String idUser, String comment) {
        this.idUser = idUser;
        this.comment = comment;
    }

    public Comment() {
    }

    public void save() {
        DatabaseReference firebase = SetFirebase.getFirebaseDatabase();
        DatabaseReference commentsRef = firebase.child("comments");

        String id = commentsRef.push().getKey();
        setIdComment(id);

        commentsRef.child(id)
                .setValue(this);
    }

    public void upDateLikes() {
        DatabaseReference firebaseRef = SetFirebase.getFirebaseDatabase();
        DatabaseReference postRef = firebaseRef
                .child("comments")
                .child(getIdComment());

        Map<String, Object> postLikes = convertLikesToMap();

        postRef.updateChildren(postLikes);
    }

    public Map<String, Object> convertLikesToMap() {
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("likedBy", getLikedBy());

        return postMap;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

//    public String getPostDomain() {
//        return postDomain;
//    }
//
//    public void setPostDomain(String postDomain) {
//        this.postDomain = postDomain;
//    }
//
    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdComment() {
        return idComment;
    }

    public void setIdComment(String idComment) {
        this.idComment = idComment;
    }

    @Override
    public int compareTo(Comment o) {
        return 0;
    }

    public static class Comparators {
        public static Comparator<Comment> LIKES = new Comparator<Comment>() {
            @Override
            public int compare(Comment o1, Comment o2) {
                return o1.getLikedBy().size() - o2.getLikedBy().size();
            }
        };
    }
}
