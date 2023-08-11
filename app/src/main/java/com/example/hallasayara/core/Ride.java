package com.example.hallasayara.core;

public class Ride {
    private int id;
    private String driverName;
    private int driverId;
    private String vehiclePlate;
    private int totalSeats;
    private int availableSeats;
    private double cost;
    private double searchRadius; // in meters
    private Journey journey;

    public Ride(int driverId, String vehiclePlate, int totalSeats, double cost, double searchRadius) {
        this.id = -1;
        this.driverId = driverId;
        this.vehiclePlate = vehiclePlate;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.cost = cost;
        this.searchRadius = searchRadius;
        this.journey = null;
    }

    public Ride(int id, String driverName, int driverId, String vehiclePlate, int totalSeats, int availableSeats, double cost, double searchRadius) {
        this.id = id;
        this.driverName = driverName;
        this.driverId = driverId;
        this.vehiclePlate = vehiclePlate;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.searchRadius = searchRadius;
        this.cost = cost;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public String getDriverName() {
        return driverName;
    }

    public double getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(double searchRadius) {
        this.searchRadius = searchRadius;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
