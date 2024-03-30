package com.example.Smart_Dustbin;
// DustbinData.java

public class DustbinData {
    private String dustbinId;
    private float totalWaste;

    public DustbinData(String dustbinId, float totalWaste) {
        this.dustbinId = dustbinId;
        this.totalWaste = totalWaste;
    }

    public String getDustbinId() {
        return dustbinId;
    }

    public float getTotalWaste() {
        return totalWaste;
    }
}
