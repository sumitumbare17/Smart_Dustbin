package com.example.Smart_Dustbin.collector;

public class Dustbin {
    private String id;
    private double latitude;
    private double longitude;
    private String name;
    private float currentWeight;
    private float currentDistance;
    private float currentPercentage;

    public Dustbin() {
        // Default constructor required for Firebase
    }

    public Dustbin(String id, double latitude, double longitude, String name, float currentWeight, float currentDistance, float currentPercentage) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.currentWeight = currentWeight;
        this.currentDistance = currentDistance;
        this.currentPercentage = currentPercentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(float currentWeight) {
        this.currentWeight = currentWeight;
    }

    public float getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(float currentDistance) {
        this.currentDistance = currentDistance;
    }

    public float getCurrentPercentage() {
        return currentPercentage;
    }

    public void setCurrentPercentage(float currentPercentage) {
        this.currentPercentage = currentPercentage;
    }
}
