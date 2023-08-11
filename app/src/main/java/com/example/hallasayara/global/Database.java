package com.example.hallasayara.global;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;

import com.example.hallasayara.adapter.RideListAdapter;
import com.example.hallasayara.core.University;
import com.example.hallasayara.database.Auth;
import com.example.hallasayara.database.Journey;
import com.example.hallasayara.database.Ride;
import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static final String IP_ADDRESS = "192.168.0.161";
    public static final String URL_SERVER = "http://" + IP_ADDRESS + "/phpmyadmin/";

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MESSAGE = "message";
    public static final int SUCCESS = 1;

    private static List<University> universityList;
    private static List<com.example.hallasayara.core.Journey> journeyList;
    private static List<com.example.hallasayara.core.Ride> rideList;

    private static Auth auth;
    private static Journey journey;
    private static Ride ride;

    public static void initialize(Context context) {
        auth = new Auth(context);
        journey = new Journey(context);
        ride = new Ride(context);
    }

    public static void createUser(String name, String email, String phone, University university, int regNo) {
        auth.createUser(name, email, phone, university, regNo);
    }

    public static void verifyUser(int userId, boolean emailVerified, boolean phoneVerified) {
        auth.verifyUser(userId, emailVerified, phoneVerified);
    }

    public static void loadUniversityList() {
        universityList = new ArrayList<>();
        auth.loadUniversityList();
    }

    public static void loginUser(String email, String phone) {
        auth.loginUser(email, phone);
    }

    public static List<University> getUniversityList() {
        return universityList;
    }

    public static void sendEmailVerification(String email, int code, String name) {
        auth.sendEmailVerification(email, code, name);
    }

    public static void sendPhoneVerification(String phone, int code, String name) {
        auth.sendPhoneVerification(phone, code, name);
    }

    public static void loadJourneyList() {
        journeyList = new ArrayList<>();
        journey.loadJourneyList();
    }

    public static List<com.example.hallasayara.core.Journey> getJourneyList() {
        return journeyList;
    }

    public static void createJourney(String departureName, String arrivalName, String departureAddress, String arrivalAddress, LatLng departurePoint, LatLng arrivalPoint, long distance, long duration, List<LatLng> points, Timestamp departureTime){
        journey.createJourney(departureName, arrivalName, departureAddress, arrivalAddress, departurePoint, arrivalPoint, distance, duration, points, departureTime);
    }

    public static void loadAvailableRideList(com.example.hallasayara.core.Journey journey, List<com.example.hallasayara.core.Ride> possibleRideList, RecyclerView.Adapter adapter){
        rideList = new ArrayList<>();
        ride.loadAvailableRideList(journey, possibleRideList, adapter);
//        Log.d("loadAvailableRideList3", rideList.size() + "");
    }

    public static List<com.example.hallasayara.core.Ride> getRideList() {
        return rideList;
    }

    public static void scheduleRide(int journeyId, int rideId, int seats, int status) {
        ride.scheduleRide(journeyId, rideId, seats, status);
    }
}
