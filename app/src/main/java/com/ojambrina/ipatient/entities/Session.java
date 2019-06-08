package com.ojambrina.ipatient.entities;

import java.io.Serializable;
import java.util.List;

public class Session implements Serializable {

    private String date;
    private long dateMillis;
    private List<String> highlightList;
    private List<String> reasonList;
    private List<String> explorationList;
    private List<String> treatmentList;

    public Session() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(long dateMillis) {
        this.dateMillis = dateMillis;
    }

    public List<String> getHighlightList() {
        return highlightList;
    }

    public void setHighlightList(List<String> highlightList) {
        this.highlightList = highlightList;
    }

    public List<String> getReasonList() {
        return reasonList;
    }

    public void setReasonList(List<String> reasonList) {
        this.reasonList = reasonList;
    }

    public List<String> getExplorationList() {
        return explorationList;
    }

    public void setExplorationList(List<String> explorationList) {
        this.explorationList = explorationList;
    }

    public List<String> getTreatmentList() {
        return treatmentList;
    }

    public void setTreatmentList(List<String> treatmentList) {
        this.treatmentList = treatmentList;
    }
}
