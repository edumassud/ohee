package com.example.ohee.model;

public class Feed {
    private String id, path, caption, userName, userPic;

    public Feed() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getuserPic() {
        return userPic;
    }

    public void setuserPic(String userPic) {
        this.userPic = userPic;
    }
}
