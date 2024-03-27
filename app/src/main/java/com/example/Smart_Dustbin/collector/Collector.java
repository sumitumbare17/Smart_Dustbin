package com.example.Smart_Dustbin.collector;

// Collector.java
public class Collector {
    private String collectorId;
    private String name;
    private String aadhar;
    private String phoneNumber; // Added phone number field
    private String password; // Added password field

    public Collector() {
        // Default constructor required for Firebase
    }

    public Collector(String collectorId, String name, String aadhar, String phoneNumber, String password) {
        this.collectorId = collectorId;
        this.name = name;
        this.aadhar = aadhar;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
