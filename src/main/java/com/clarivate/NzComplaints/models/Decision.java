package com.clarivate.NzComplaints.models;

import java.time.LocalDate;

public class Decision {
    private String id;
    private String reference;
    private LocalDate judgmentDate;
    private String level; // e.g., "First Instance"
    private String nature; // e.g., "Complaints & Hearings"
    private String robotSource;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getJudgmentDate() {
        return judgmentDate;
    }

    public void setJudgmentDate(LocalDate judgmentDate) {
        this.judgmentDate = judgmentDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getRobotSource() {
        return robotSource;
    }

    public void setRobotSource(String robotSource) {
        this.robotSource = robotSource;
    }
}
