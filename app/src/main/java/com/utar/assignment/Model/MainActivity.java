package com.utar.assignment.Model;

import java.util.List;

public class MainActivity implements Comparable<MainActivity> {
    private String Id;
    private String name;
    private double billAmount;
    private String createdDate;
    private List<SubActivity> subActivityList;

    public MainActivity() {
    }

    public List<SubActivity> getSubActivityList() {
        return subActivityList;
    }

    public void setSubActivityList(List<SubActivity> subActivityList) {
        this.subActivityList = subActivityList;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int compareTo(MainActivity o) {

        return getCreatedDate().compareTo(o.getCreatedDate());
    }
}
