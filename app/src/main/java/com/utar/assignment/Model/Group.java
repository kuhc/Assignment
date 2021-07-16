package com.utar.assignment.Model;

import java.util.Date;

public class Group {
    private String Id;
    private String groupName;
    private Date createdDate;
    private String ownerId;

    public Group() {
    }

    public String getGroupId() {
        return Id;
    }

    public void setGroupId(String groupId) {
        this.Id = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
