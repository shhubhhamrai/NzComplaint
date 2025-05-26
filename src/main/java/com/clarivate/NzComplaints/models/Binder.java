package com.clarivate.NzComplaints.models;

import java.time.LocalDate;
import java.util.List;

public class Binder {
    private String id;
    private List<String> domains; // TM, CR, DM, PT
    private String firstAction;
    private LocalDate firstActionDate;
    private List<Docket> dockets;
    private List<Party> parties;
    private List<Right> rights;
    private List<Decision> decisions; // <-- Added field

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public String getFirstAction() {
        return firstAction;
    }

    public void setFirstAction(String firstAction) {
        this.firstAction = firstAction;
    }

    public LocalDate getFirstActionDate() {
        return firstActionDate;
    }

    public void setFirstActionDate(LocalDate firstActionDate) {
        this.firstActionDate = firstActionDate;
    }

    public List<Docket> getDockets() {
        return dockets;
    }

    public void setDockets(List<Docket> dockets) {
        this.dockets = dockets;
    }

    public List<Party> getParties() {
        return parties;
    }

    public void setParties(List<Party> parties) {
        this.parties = parties;
    }

    public List<Right> getRights() {
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    public List<Decision> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<Decision> decisions) {
        this.decisions = decisions;
    }
}
