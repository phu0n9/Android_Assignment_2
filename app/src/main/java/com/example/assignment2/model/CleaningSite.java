package com.example.assignment2.model;

import java.util.List;

public class CleaningSite {
    private String name;
    private String address;
    private double lat;
    private double lon;
    private String owner;
    private List<String> userEmail;

    public List<String> getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(List<String> userEmail) {
        this.userEmail = userEmail;
    }

    public CleaningSite(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public CleaningSite(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
