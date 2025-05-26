package com.clarivate.NzComplaints.models;

public class Right {
    private String id;
    private boolean isOpponent;
    private Classification classification;
    private String name;
    private String type;
    private String reference;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOpponent() {
        return isOpponent;
    }

    public void setOpponent(boolean opponent) {
        isOpponent = opponent;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}