package com.example.hallasayara.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class Journey implements Parcelable {

    public static final int STATUS_UNSCHEDULED = -1;
    public static final int STATUS_SCHEDULED = 1;
    public static final int STATUS_CANCELLED = 0;
    public static final int STATUS_MISSED = 2;

    private int id;
    private String departureName;
    private String arrivalName;
    private String departureAddress;
    private String arrivalAddress;
    private LatLng departurePoint;
    private LatLng arrivalPoint;
    private long distance;
    private long duration;
    private List<LatLng> points;
    private Timestamp departureTime;
    private Timestamp arrivalTime;
    private int userId;
    private Ride ride;
    private int status;
    private Timestamp createdAt;

    public Journey(int id, String departureName, String arrivalName, String departureAddress, String arrivalAddress, LatLng departurePoint, LatLng arrivalPoint, long distance, long duration, List<LatLng> points, Timestamp departureTime, Timestamp arrivalTime, int userId, Timestamp createdAt) {
        this.id = id;
        this.departureName = departureName;
        this.arrivalName = arrivalName;
        this.departureAddress = departureAddress;
        this.arrivalAddress = arrivalAddress;
        this.departurePoint = departurePoint;
        this.arrivalPoint = arrivalPoint;
        this.distance = distance;
        this.duration = duration;
        this.points = points;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.userId = userId;
        this.ride = null;
        this.status = STATUS_UNSCHEDULED;
        this.createdAt = createdAt;
    }

    public Journey(int id, String departureName, String arrivalName, String departureAddress, String arrivalAddress, LatLng departurePoint, LatLng arrivalPoint, long distance, long duration, List<LatLng> points, Timestamp departureTime, Timestamp arrivalTime, int userId, Ride ride, int status, Timestamp createdAt) {
        this.id = id;
        this.departureName = departureName;
        this.arrivalName = arrivalName;
        this.departureAddress = departureAddress;
        this.arrivalAddress = arrivalAddress;
        this.departurePoint = departurePoint;
        this.arrivalPoint = arrivalPoint;
        this.distance = distance;
        this.duration = duration;
        this.points = points;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.userId = userId;
        this.ride = ride;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getDepartureAddress() {
        return departureAddress;
    }

    public void setDepartureAddress(String departureAddress) {
        this.departureAddress = departureAddress;
    }

    public String getArrivalAddress() {
        return arrivalAddress;
    }

    public void setArrivalAddress(String arrivalAddress) {
        this.arrivalAddress = arrivalAddress;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public String getDepartureName() {
        return departureName;
    }

    public void setDepartureName(String departureName) {
        this.departureName = departureName;
    }

    public String getArrivalName() {
        return arrivalName;
    }

    public void setArrivalName(String arrivalName) {
        this.arrivalName = arrivalName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LatLng getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(LatLng departurePoint) {
        this.departurePoint = departurePoint;
    }

    public LatLng getArrivalPoint() {
        return arrivalPoint;
    }

    public void setArrivalPoint(LatLng arrivalPoint) {
        this.arrivalPoint = arrivalPoint;
    }

    public Timestamp getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Timestamp departureTime) {
        this.departureTime = departureTime;
    }

    public Timestamp getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Timestamp arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Journey{" +
                "id=" + id +
                ", departureName='" + departureName + '\'' +
                ", arrivalName='" + arrivalName + '\'' +
                ", departureAddress='" + departureAddress + '\'' +
                ", arrivalAddress='" + arrivalAddress + '\'' +
                ", departurePoint=" + departurePoint +
                ", arrivalPoint=" + arrivalPoint +
                ", distance=" + distance +
                ", duration=" + duration +
                ", points=" + points +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", userId=" + userId +
                ", ride=" + ride +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Journey>(){

        @Override
        public Journey createFromParcel(Parcel parcel) {
            int id= parcel.readInt();
            String departureName = parcel.readString();
            String arrivalName = parcel.readString();
            String departureAddress = parcel.readString();
            String arrivalAddress = parcel.readString();
            LatLng departurePoint = parcel.readParcelable(LatLng.class.getClassLoader());
            LatLng arrivalPoint = parcel.readParcelable(LatLng.class.getClassLoader());
            long distance = parcel.readLong();
            long duration = parcel.readLong();
            List<LatLng> points = new ArrayList<LatLng>();
            parcel.readTypedList(points, LatLng.CREATOR);
            Timestamp departureTime = Timestamp.valueOf(parcel.readString());
            Timestamp arrivalTime = Timestamp.valueOf(parcel.readString());
            int userId = parcel.readInt();
            int status = parcel.readInt();
            Timestamp createdAt = Timestamp.valueOf(parcel.readString());
            return new Journey(id, departureName, arrivalName, departureAddress, arrivalAddress, departurePoint, arrivalPoint, distance, duration, points, departureTime, arrivalTime, userId, createdAt);
        }

        @Override
        public Journey[] newArray(int i) {
            return new Journey[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(departureName);
        parcel.writeString(arrivalName);
        parcel.writeString(departureAddress);
        parcel.writeString(arrivalAddress);
        parcel.writeParcelable(departurePoint, flags);
        parcel.writeParcelable(arrivalPoint, flags);
        parcel.writeLong(distance);
        parcel.writeLong(duration);
        parcel.writeTypedList(points);
        parcel.writeString(departureTime.toString());
        parcel.writeString(arrivalTime.toString());
        parcel.writeInt(userId);
        parcel.writeInt(status);
        parcel.writeString(createdAt.toString());
    }
}
