package com.clarivate.NzComplaints.models;

import java.util.List;

public class Party {
    private String name;
    private String type;
    private List<String> representatives;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getRepresentatives() {
        return representatives;
    }

    public void setRepresentatives(List<String> representatives) {
        this.representatives = representatives;
    }
}
