package com.utar.assignment.Model;

import java.util.List;

public class Friend {
    private String userid;
    private List<String> friendEmail;

    public Friend(){};

    public String getId() {
        return userid;
    }

    public void setId(String id) {
        this.userid = id;
    }

    public List<String> getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(List<String> friendEmail) {
        this.friendEmail = friendEmail;
    }
}
