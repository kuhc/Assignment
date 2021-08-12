package com.utar.assignment.Model;

import java.util.List;
import java.util.UUID;

public class User {
    private String Uid;
    private String email;
    private String username;
    private String pushToken;
    private List<String> groupList;
    private List<Amount> amountList;
    private List<String> friendList;

    public User() {
        this.Uid = UUID.randomUUID().toString();
    }

    public User(String Uid, String username, String email) {
        this.Uid = Uid;
        this.username = username;
        this.email = email;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public List<Amount> getAmountList() {
        return amountList;
    }

    public void setAmountList(List<Amount> amountList) {
        this.amountList = amountList;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }
}
