package com.example.Smart_Dustbin.collector;

public class Collector {
    private String collectorId;
    private String name;
    private String aadharNumber;
    private String mobileNumber;

    public Collector() {
        // Default constructor required for Firebase
    }

    public Collector(String collectorId, String name, String aadharNumber, String mobileNumber) {
        this.collectorId = collectorId;
        this.name = name;
        this.aadharNumber = aadharNumber;
        this.mobileNumber = mobileNumber;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
