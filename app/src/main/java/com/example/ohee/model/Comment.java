package com.example.ohee.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private String idUser, comment;

    public Comment(String idUser, String comment) {
        this.idUser = idUser;
        this.comment = comment;
    }

    public Comment() {
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
}
