package com.example.hallasayara.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hallasayara.global.Constants;
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

public class Journey {

    private static final String PHP_CREATE_JOURNEY = Database.URL_SERVER + "create_journey.php";
    private static final String PHP_GET_JOURNEYS = Database.URL_SERVER + "get_journeys.php";
    public static final String TAG_JOURNEYS = "journeys";
    public static final String TAG_ID = "id";
    public static final String TAG_JOURNEY_ID = "journey_id";
    public static final String TAG_DEPARTURE_NAME = "departure_name";
    public static final String TAG_ARRIVAL_NAME = "arrival_name";
    public static final String TAG_DEPARTURE_ADDRESS = "departure_address";
    public static final String TAG_ARRIVAL_ADDRESS = "arrival_address";
    public static final String TAG_DEPARTURE_LATITUDE = "departure_lat";
    public static final String TAG_DEPARTURE_LONGITUDE = "departure_lng";
    public static final String TAG_ARRIVAL_LATITUDE = "arrival_lat";
    public static final String TAG_ARRIVAL_LONGITUDE = "arrival_lng";
    public static final String TAG_DISTANCE = "distance";
    public static final String TAG_DURATION = "duration";
    public static final String TAG_DEPARTURE_TIME = "departure_time";
    public static final String TAG_ARRIVAL_TIME = "arrival_time";
    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_STATUS = "status";
    public static final String TAG_CREATED_AT = "created_at";
    public static final String TAG_POINTS = "points";
    public static final String TAG_LATITUDE = "lat";
    public static final String TAG_LONGITUDE = "lng";

    private Context context;

    public Journey(Context context) {
        this.context = context;
    }

    public void createJourney(String departureName, String arrivalName, String departureAddress, String arrivalAddress, LatLng departurePoint, LatLng arrivalPoint, long distance, long duration, List<LatLng> points, Timestamp departureTime) {

        new AsyncTask<String, String, String>() {

            /**
             * Before starting background thread Show Progress Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            /**
             * getting all items
             */
            protected String doInBackground(String... args) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, PHP_CREATE_JOURNEY, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
//                            Log.i("tagconvertstr", "["+response+"]");
                            JSONObject json = new JSONObject(response);
                            if (json.getInt(Database.TAG_SUCCESS) == Database.SUCCESS) {
                                Toast.makeText(context, "Journey successfully created!", Toast.LENGTH_LONG).show();

                                int id = json.getInt(TAG_ID);

                                Timestamp createdAt = new Timestamp(Format.Timestamp.parse(json.getString(TAG_CREATED_AT)).getTime());
                                Timestamp arrivalTime = new Timestamp(Format.Timestamp.parse(json.getString(TAG_ARRIVAL_TIME)).getTime());

                                SharedPreferences sp = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
                                int userId = sp.getInt(TAG_ID, -1);

                                com.example.hallasayara.core.Journey journey = new com.example.hallasayara.core.Journey(id, departureName, arrivalName, departureAddress, arrivalAddress, departurePoint, arrivalPoint, distance, duration, points, departureTime, arrivalTime, userId, createdAt);
                                Database.getJourneyList().add(journey);

//                                Log.d("LatLng", points.toString());
                                ((Activity) context).setResult(Activity.RESULT_OK);
                                ((Activity) context).finish();
                            } else {
                                String message = json.getString(Database.TAG_MESSAGE);
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.i("createJourney1", e.getMessage());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("createJourney2", error.getMessage());
                    }
                }) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put(TAG_DEPARTURE_NAME, departureName);
                        params.put(TAG_ARRIVAL_NAME, arrivalName);
                        params.put(TAG_DEPARTURE_ADDRESS, departureAddress);
                        params.put(TAG_ARRIVAL_ADDRESS, arrivalAddress);
                        params.put(TAG_DEPARTURE_LATITUDE, Double.toString(departurePoint.latitude));
                        params.put(TAG_DEPARTURE_LONGITUDE, Double.toString(departurePoint.longitude));
                        params.put(TAG_ARRIVAL_LATITUDE, Double.toString(arrivalPoint.latitude));
                        params.put(TAG_ARRIVAL_LONGITUDE, Double.toString(arrivalPoint.longitude));
                        params.put(TAG_DISTANCE, Long.toString(distance));
                        params.put(TAG_DURATION, Long.toString(duration));
                        params.put(TAG_DEPARTURE_TIME, departureTime.toString());
                        Timestamp arrivalTime = new Timestamp(departureTime.getTime() + (duration * 1000)); // convert seconds to milliseconds
                        params.put(TAG_ARRIVAL_TIME, arrivalTime.toString());

                        SharedPreferences sp = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
                        params.put(TAG_USER_ID, Integer.toString(sp.getInt(TAG_ID, -1)));
                        params.put(TAG_STATUS, Integer.toString(com.example.hallasayara.core.Journey.STATUS_UNSCHEDULED));
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        params.put(TAG_CREATED_AT, timestamp.toString());
                        params.put(TAG_POINTS, points.toString());
                        return new JSONObject(params).toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
                return null;
            }

            /**
             * After completing background task Dismiss the progress dialog
             **/
            protected void onPostExecute(String string) {
            }

        }.execute();
    }

    public void loadJourneyList() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PHP_GET_JOURNEYS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                    Log.i("tagconvertstr", "[" + response + "]");
                    JSONObject payload = new JSONObject(response);
                    if (payload.getInt(Database.TAG_SUCCESS) == Database.SUCCESS) {
                        JSONArray journeys = payload.getJSONArray(TAG_JOURNEYS);
                        for (int i = 0; i < journeys.length(); i++) {
                            JSONObject json = journeys.getJSONObject(i);
                            int id = json.getInt(TAG_ID);
                            String departureName = json.getString(TAG_DEPARTURE_NAME);
                            String arrivalName = json.getString(TAG_ARRIVAL_NAME);
                            String departAddress = null;
                            if (!json.isNull(TAG_DEPARTURE_ADDRESS))
                                departAddress = json.getString(TAG_DEPARTURE_ADDRESS);
                            String arrivalAddress = null;
                            if (!json.isNull(TAG_ARRIVAL_ADDRESS))
                                arrivalAddress = json.getString(TAG_ARRIVAL_ADDRESS);
                            double departureLat = json.getDouble(TAG_DEPARTURE_LATITUDE);
                            double departureLng = json.getDouble(TAG_DEPARTURE_LONGITUDE);
                            LatLng departurePoint = new LatLng(departureLat, departureLng);
                            double arrivalLat = json.getDouble(TAG_ARRIVAL_LATITUDE);
                            double arrivalLng = json.getDouble(TAG_ARRIVAL_LONGITUDE);
                            LatLng arrivalPoint = new LatLng(arrivalLat, arrivalLng);
                            long distance = json.getLong(TAG_DISTANCE);
                            long duration = json.getLong(TAG_DURATION);
                            Timestamp departureTime = new Timestamp(Format.Timestamp.parse(json.getString(TAG_DEPARTURE_TIME)).getTime());
                            Timestamp arrivalTime = null;
                            if (!json.isNull(TAG_ARRIVAL_TIME))
                                arrivalTime = new Timestamp(Format.Timestamp.parse(json.getString(TAG_ARRIVAL_TIME)).getTime());

                            int status = json.getInt(TAG_STATUS);
                            Timestamp createdAt = new Timestamp(Format.Timestamp.parse(json.getString(TAG_CREATED_AT)).getTime());

                            List<LatLng> points = null;
                            if (json.has(TAG_POINTS)) {
                                points = new ArrayList<>();
                                JSONArray jsonPoints = json.getJSONArray(TAG_POINTS);
                                for (int j = 0; j < jsonPoints.length(); j++) {
                                    JSONObject json2 = jsonPoints.getJSONObject(j);
                                    double lat = json2.getDouble(TAG_LATITUDE);
                                    double lng = json2.getDouble(TAG_LONGITUDE);
                                    points.add(new LatLng(lat, lng));
                                }
                            }
//                            Log.d("loadJourneyList13", Boolean.toString(points == null));

                            SharedPreferences sp = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
                            int userId = sp.getInt(TAG_ID, -1);

                            com.example.hallasayara.core.Ride ride = null;
                            if (!json.isNull(Ride.TAG_RIDE_ID)){
                                JSONObject json3 = json.getJSONObject(Ride.TAG_RIDE);
                                int rideId = json3.getInt(Ride.TAG_RIDE_ID);
                                int driverId = json3.getInt(Ride.TAG_DRIVER_ID);
                                String driverName = json3.getString(Ride.TAG_DRIVER_NAME);
                                String vehiclePlate = json3.getString(Ride.TAG_VEHICLE_PLATE);
                                int totalSeats = json3.getInt(Ride.TAG_TOTAL_SEATS);
                                int availableSeats = json3.getInt(Ride.TAG_AVAILABLE_SEATS);
                                double cost = json3.getDouble(Ride.TAG_COST);
                                double searchRadius = json3.getDouble((Ride.TAG_SEARCH_RADIUS));
                                ride = new com.example.hallasayara.core.Ride(rideId, driverName, driverId, vehiclePlate, totalSeats, availableSeats, cost, searchRadius);
                            }

                            Database.getJourneyList().add(new com.example.hallasayara.core.Journey(id, departureName, arrivalName, departAddress, arrivalAddress, departurePoint, arrivalPoint, distance, duration, points, departureTime, arrivalTime, userId, ride, status, createdAt));
                        }
                    }
                    String message = payload.getString(Database.TAG_MESSAGE);
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                } catch (JSONException | ParseException e) {
                    Log.i("loadJourneyList1", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("loadJourneyList2", error.toString());
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                SharedPreferences sp = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<>();
                params.put(TAG_USER_ID, Integer.toString(sp.getInt(TAG_ID, -1)));
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
