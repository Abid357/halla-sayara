package com.example.hallasayara.database;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hallasayara.global.Database;
import com.example.hallasayara.global.Format;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ride {

    private static final String PHP_GET_AVAILABLE_RIDES = Database.URL_SERVER + "get_available_rides.php";
    private static final String PHP_SCHEDULE_RIDE = Database.URL_SERVER + "schedule_ride.php";
    public static final String TAG_RIDES = "rides";
    public static final String TAG_RIDE = "ride";
    public static final String TAG_RIDE_ID = "ride_id";
    public static final String TAG_DRIVER_ID = "driver_id";
    public static final String TAG_VEHICLE_PLATE = "vehicle_plate";
    public static final String TAG_TOTAL_SEATS = "total_seats";
    public static final String TAG_AVAILABLE_SEATS = "available_seats";
    public static final String TAG_COST = "cost";
    public static final String TAG_SEARCH_RADIUS = "search_radius";
    public static final String TAG_DRIVER_NAME = "driver_name";

    private Context context;

    public Ride(Context context) {
        this.context = context;
    }

    public void loadAvailableRideList(com.example.hallasayara.core.Journey journey, List<com.example.hallasayara.core.Ride> possibleRideList, RecyclerView.Adapter adapter) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, PHP_GET_AVAILABLE_RIDES, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getInt(Database.TAG_SUCCESS) == Database.SUCCESS) {
//                                Log.d("loadAvailableRideList", "Got response");
                                JSONArray jsonRides = response.getJSONArray(TAG_RIDES);
                                for (int i = 0; i < jsonRides.length(); i++) {
                                    JSONObject json = jsonRides.getJSONObject(i);
                                    int rideId = json.getInt(TAG_RIDE_ID);
                                    int driverId = json.getInt(TAG_DRIVER_ID);
                                    String vehiclePlate = json.getString(TAG_VEHICLE_PLATE);
                                    int totalSeats = json.getInt(TAG_TOTAL_SEATS);
                                    int availableSeats = json.getInt(TAG_AVAILABLE_SEATS);
                                    double cost = json.getDouble(TAG_COST);
                                    double searchRadius = json.getDouble(TAG_SEARCH_RADIUS);
                                    String driverName = json.getString(TAG_DRIVER_NAME);

                                    int journeyId = json.getInt(Journey.TAG_JOURNEY_ID);
                                    String departureName = json.getString(Journey.TAG_DEPARTURE_NAME);
                                    String arrivalName = json.getString(Journey.TAG_ARRIVAL_NAME);
                                    String departAddress = null;
                                    if (!json.isNull(Journey.TAG_DEPARTURE_ADDRESS))
                                        departAddress = json.getString(Journey.TAG_DEPARTURE_ADDRESS);
                                    String arrivalAddress = null;
                                    if (!json.isNull(Journey.TAG_ARRIVAL_ADDRESS))
                                        arrivalAddress = json.getString(Journey.TAG_ARRIVAL_ADDRESS);
                                    double departureLat = json.getDouble(Journey.TAG_DEPARTURE_LATITUDE);
                                    double departureLng = json.getDouble(Journey.TAG_DEPARTURE_LONGITUDE);
                                    LatLng departurePoint = new LatLng(departureLat, departureLng);
                                    double arrivalLat = json.getDouble(Journey.TAG_ARRIVAL_LATITUDE);
                                    double arrivalLng = json.getDouble(Journey.TAG_ARRIVAL_LONGITUDE);
                                    LatLng arrivalPoint = new LatLng(arrivalLat, arrivalLng);
                                    long distance = json.getLong(Journey.TAG_DISTANCE);
                                    long duration = json.getLong(Journey.TAG_DURATION);
                                    Timestamp departureTime = null;
                                    Timestamp arrivalTime = null;
                                    Timestamp createdAt = null;
                                    try {
                                        departureTime = new Timestamp(Format.Timestamp.parse(json.getString(Journey.TAG_DEPARTURE_TIME)).getTime());
                                        if (!json.isNull(Journey.TAG_ARRIVAL_TIME))
                                            arrivalTime = new Timestamp(Format.Timestamp.parse(json.getString(Journey.TAG_ARRIVAL_TIME)).getTime());
                                        createdAt = new Timestamp(Format.Timestamp.parse(json.getString(Journey.TAG_CREATED_AT)).getTime());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    int status = json.getInt(Journey.TAG_STATUS);

                                    List<LatLng> points = null;
                                    if (json.has(Journey.TAG_POINTS)) {
                                        points = new ArrayList<>();
                                        JSONArray jsonPoints = json.getJSONArray(Journey.TAG_POINTS);
                                        for (int j = 0; j < jsonPoints.length(); j++) {
                                            JSONObject json2 = jsonPoints.getJSONObject(j);
                                            double lat = json2.getDouble(Journey.TAG_LATITUDE);
                                            double lng = json2.getDouble(Journey.TAG_LONGITUDE);
                                            points.add(new LatLng(lat, lng));
                                        }
                                    }

                                    com.example.hallasayara.core.Ride ride = new com.example.hallasayara.core.Ride(rideId, driverName, driverId, vehiclePlate, totalSeats, availableSeats, cost, searchRadius);
                                    com.example.hallasayara.core.Journey journey = new com.example.hallasayara.core.Journey(journeyId, departureName, arrivalName, departAddress, arrivalAddress, departurePoint, arrivalPoint, distance, duration, points, departureTime, arrivalTime, driverId, ride, status, createdAt);
                                    ride.setJourney(journey);
                                    Database.getRideList().add(ride);
//                                    Log.d("loadAvailableRideList", Integer.toString(Database.getRideList().size()));
                                }
                                getPossibleRideList(journey, possibleRideList, adapter);
                            }
                            Toast.makeText(context, response.getString(Database.TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Log.i("loadAvailableRideList1", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getMessage() != null && !error.getMessage().isEmpty())
                            Log.i("loadAvailableRideList2", error.getMessage());
                    }
                });
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    // Haversine formula to find distance between two lat-long points
    private double calculateRadius(LatLng point1, LatLng point2) {
        double lat1 = point1.latitude;
        double lat2 = point2.latitude;
        double lng1 = point1.longitude;
        double lng2 = point2.longitude;
        double theta = lng1 - lng2;

        double dist = Math.sin(degreesToRadians(lat1))
                * Math.sin(degreesToRadians(lat2))
                + Math.cos(degreesToRadians(lat1))
                * Math.cos(degreesToRadians(lat2))
                * Math.cos(degreesToRadians(theta));
        dist = Math.acos(dist);
        dist = radiansToDegrees(dist);
        dist = dist * 60 * 1.1515 * 1.60934 * 1000; // distance in meters
        return (dist);
    }

    private double degreesToRadians(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double radiansToDegrees(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    // simple match-making algorithm based on a search radius fixed by the driver
    public void getPossibleRideList(com.example.hallasayara.core.Journey journey, List<com.example.hallasayara.core.Ride> possibleRideList, RecyclerView.Adapter adapter) {
        possibleRideList.clear();
        List<com.example.hallasayara.core.Ride> availableRideList = Database.getRideList();
        for (com.example.hallasayara.core.Ride ride : availableRideList) {
            boolean foundSeekerStartPoint = false;
            boolean foundSeekerEndPoint = false;
            List<LatLng> points = ride.getJourney().getPoints();
            if (points != null && !points.isEmpty()) {
                int i = 0;
                while (!foundSeekerStartPoint && i < points.size()) {
                    double radius = calculateRadius(journey.getDeparturePoint(), points.get(i));
                    if (radius <= ride.getSearchRadius())
                        foundSeekerStartPoint = true;
                    i++;

                    Log.i("ALGO", "--Start Point-- ride: " + ride.getId() + " point: " + i + " radius: " + radius + " search: " + ride.getSearchRadius());
                }
                while (!foundSeekerEndPoint && i < points.size()) {
                    double radius = calculateRadius(journey.getArrivalPoint(), points.get(i));
                    if (radius <= ride.getSearchRadius())
                        foundSeekerEndPoint = true;
                    i++;

                    Log.i("ALGO", "--End Point-- ride: " + ride.getId() + " point: " + i + " radius: " + radius + " search: " + ride.getSearchRadius());
                }
                if (foundSeekerStartPoint && foundSeekerEndPoint) {
                    possibleRideList.add(ride);
                    Log.i("ALGO", "Ride (id=" + ride.getId() + ") added!");
                }
            }

//            Log.i("POINTS", Integer.toString(points.size()));
            adapter.notifyDataSetChanged();
        }
    }

    public void scheduleRide(int journeyId, int rideId, int seats, int status) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PHP_SCHEDULE_RIDE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    if (json.getInt(Database.TAG_SUCCESS) == Database.SUCCESS) {
                        String message = json.getString(Database.TAG_MESSAGE);
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.i("scheduleRide1", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("scheduleRide2", error.getMessage());
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Journey.TAG_JOURNEY_ID, Integer.toString(journeyId));
                params.put(TAG_RIDE_ID, Integer.toString(rideId));
                params.put(TAG_AVAILABLE_SEATS, Integer.toString(seats));
                params.put(Journey.TAG_STATUS, Integer.toString(status));
                return new JSONObject(params).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        Volley.newRequestQueue(context).add(stringRequest);
    }
}
