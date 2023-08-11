package com.example.hallasayara.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.hallasayara.R;
import com.example.hallasayara.core.Journey;
import com.example.hallasayara.core.PolylineData;
import com.example.hallasayara.core.Ride;
import com.example.hallasayara.fragment.DatePickerFragment;
import com.example.hallasayara.fragment.TimePickerFragment;
import com.example.hallasayara.global.Database;
import com.example.hallasayara.global.Format;
import com.example.hallasayara.global.Validation;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        GoogleMap.OnPolylineClickListener {

    private static final String TAG = "MapsActivity";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String DEPARTURE_TITLE = "Departure Point";
    private static final String ARRIVAL_TITLE = "Arrival Point";
    private static final int REQUEST_LOCATION = 1;

    private GoogleMap map;
    private MapView mapView;
    private TextView dateTextView, timeTextView;
    private GeoApiContext geoApiContext = null;
    private LatLng departurePoint, arrivalPoint;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
    private AutocompleteSupportFragment departureFragment, arrivalFragment;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private List<PolylineData> polyLines;
    private List<Marker> markers;
    private Marker departureMarker, arrivalMarker;
    private String departureName, arrivalName, departureAddress, arrivalAddress;
    private Button createButton;
    private long duration;
    private long distance;
    private List<LatLng> points;
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Database.initialize(this);

        mapView = (MapView) findViewById(R.id.map_view);
        polyLines = new ArrayList<>();
        markers = new ArrayList<>();

        dateTextView = (TextView) findViewById(R.id.date_text_view);
        timeTextView = (TextView) findViewById(R.id.time_text_view);
        createButton = (Button) findViewById(R.id.create_journey_button);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validation checks
                if (departureMarker == null)
                    Toast.makeText(getApplicationContext(), "Select departure point", Toast.LENGTH_SHORT).show();
                else if (arrivalMarker == null)
                    Toast.makeText(getApplicationContext(), "Select arrival point", Toast.LENGTH_SHORT).show();
                else if (Validation.matchesExact(dateTextView.getText().toString(), "Select Date"))
                    Toast.makeText(getApplicationContext(), "Select departure date", Toast.LENGTH_SHORT).show();
                else if (Validation.matchesExact(timeTextView.getText().toString(), "Select Time"))
                    Toast.makeText(getApplicationContext(), "Select departure time", Toast.LENGTH_SHORT).show();
                else {
                    // insert into database
                    String dateString = dateTextView.getText().toString();
                    dateString = dateString.substring(dateString.indexOf(", ") + 2);
                    String timeString = timeTextView.getText().toString();

//                    Log.d(TAG, "timestampString=" + dateString + " " + timeString);
                    Timestamp departureTime = null;
                    try {
                        departureTime = new Timestamp(timestampFormat.parse(dateString + " " + timeString).getTime());
//                        Log.d(TAG, "Timestamp=" + departureTime.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Database.createJourney(departureName, arrivalName, departureAddress, arrivalAddress, departurePoint, arrivalPoint, distance, duration, points, departureTime);
                }
            }
        });

        // Map feature
        Bundle mapViewBundle = null;
        if (savedInstanceState != null)
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }

        // Autocompletion feature
        Places.initialize(this, getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(this);

        departureFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.departure_autocomplete_fragment);
        departureFragment.setPlaceFields(placeFields);
        departureFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (departureMarker != null)
                    departureMarker.remove();
                departurePoint = place.getLatLng();
                departureMarker = map.addMarker(new MarkerOptions()
                        .position(departurePoint)
                        .title(DEPARTURE_TITLE));
                departureMarker.showInfoWindow();

//                Log.d(TAG, "onPlaceSelected: Name=" + place.getName() + " Address=" + place.getAddress());
                departureName = place.getName();
                departureAddress = place.getAddress();

                if (arrivalMarker != null)
                    calculateRoutes();
                else
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(departurePoint, 18f));
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d(TAG, "setOnPlaceSelectedListener:" + status.getStatusMessage());
            }
        });

        arrivalFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.arrival_autocomplete_fragment);
        arrivalFragment.setPlaceFields(placeFields);
        arrivalFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                if (arrivalMarker != null)
                    arrivalMarker.remove();
                arrivalPoint = place.getLatLng();
                arrivalMarker = map.addMarker(new MarkerOptions()
                        .position(arrivalPoint)
                        .title(ARRIVAL_TITLE));
                arrivalMarker.showInfoWindow();

//                Log.d(TAG, "onPlaceSelected: Name=" + place.getName() + " Address=" + place.getAddress());
                arrivalName = place.getName();
                arrivalAddress = place.getAddress();

                if (departureMarker != null)
                    calculateRoutes();
                else
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(arrivalPoint, 18f));
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.d(TAG, "setOnPlaceSelectedListener:" + status.getStatusMessage());
            }
        });

        // If activity is started only for viewing map routes then disable all features
        if (getIntent().hasExtra("journey")) {
            Journey journey = getIntent().getParcelableExtra("journey");
            Log.i("MapsActivity: Extras", Integer.toString(journey.getId()));

            departureFragment.setText(journey.getDepartureName());
            arrivalFragment.setText(journey.getArrivalName());
            departureFragment.getView().setEnabled(false);
            arrivalFragment.getView().setEnabled(false);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(journey.getDepartureTime().getTime()));
            String dayString = Format.Day.format(calendar.getTime()).substring(0, 3);
            String dateString = dayString + ", " + Format.Date.format(calendar.getTime());
            dateTextView.setText(dateString);
            dateTextView.setEnabled(false);

            String timeString = Format.Time.format(calendar.getTime());
            timeTextView.setText(timeString);
            timeTextView.setEnabled(false);

            createButton.setVisibility(View.INVISIBLE);

            departurePoint = journey.getDeparturePoint();
            arrivalPoint = journey.getArrivalPoint();
            points = journey.getPoints();
            duration = journey.getDuration();

            Log.i("Journey", journey.toString());
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date_text_view:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;
            case R.id.time_text_view:
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String dayString = Format.Day.format(calendar.getTime()).substring(0, 3);
        String dateString = dayString + ", " + Format.Date.format(calendar.getTime());
        dateTextView.setText(dateString);
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        String timeString = Format.Time.format(calendar.getTime());
        timeTextView.setText(timeString);
    }

    private void calculateRoutes() {
//        Log.d(TAG, "calculateRoutes: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                arrivalPoint.latitude,
                arrivalPoint.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        departurePoint.latitude,
                        departurePoint.longitude
                )
        );
//        Log.d(TAG, "calculateRoutes: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
//                Log.d(TAG, "calculateRoutes: routes: " + result.routes[0].toString());
//                Log.d(TAG, "calculateRoutes: duration: " + result.routes[0].legs[0].duration);
//                Log.d(TAG, "calculateRoutes: distance: " + result.routes[0].legs[0].distance);
//                Log.d(TAG, "calculateRoutes: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolyLinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateRoutes: Failed to get directions: " + e.getMessage());
            }
        });
    }

    public void drawRoutes(List<LatLng> points, int color) {
        if (points == null) {
            Log.e("Draw Line", "Got null as parameters");
            return;
        }

        Polyline line = map.addPolyline(new PolylineOptions().color(color));
        line.setPoints(points);

        MarkerOptions departureOptions = new MarkerOptions()
                .position(departurePoint)
                .title(DEPARTURE_TITLE);
        if (color == Color.RED)
            departureOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        else if (color == Color.GREEN)
            departureOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        departureMarker = map.addMarker(departureOptions);
        departureMarker.showInfoWindow();


        MarkerOptions arrivalOptions = new MarkerOptions()
                .position(arrivalPoint)
                .title(ARRIVAL_TITLE)
                .snippet("Duration: " + duration);
        if (color == Color.RED)
            arrivalOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        else if (color == Color.GREEN)
            arrivalOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        arrivalMarker = map.addMarker(arrivalOptions);
        arrivalMarker.showInfoWindow();
    }

    private void addPolyLinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
//                Log.d(TAG, "run: result routes: " + result.routes.length);
                if (polyLines.size() > 0) {
                    for (PolylineData polylineData : polyLines) {
                        polylineData.getPolyline().remove();
                    }
                    polyLines.clear();
                    polyLines = new ArrayList<>();
                }

                double duration = 999999999;
                for (DirectionsRoute route : result.routes) {
//                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    // Uncomment the log for a demonstration
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.quantum_grey));
                    polyline.setClickable(true);
                    polyLines.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if (tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }
                }
            }
        });
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (map == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
//        for (LatLng latLngPoint : lstLatLngRoute)
//            boundsBuilder.include(latLngPoint);
        boundsBuilder.include(departurePoint);
        boundsBuilder.include(arrivalPoint);

        int routePadding = 200;
        LatLngBounds latLngBounds = boundsBuilder.build();

        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (!checkLocationPermission()) {
            finish();
        }
        map.setMyLocationEnabled(true);
        // set boundaries for Karachi only
        map.setLatLngBoundsForCameraTarget(new LatLngBounds(new LatLng(24.753944, 66.649923), new LatLng(25.656015, 67.644186)));
        map.setOnPolylineClickListener(this);
        this.map = map;
//        Log.d(TAG, "onMapReady: " + (this.map != null));
        if (getIntent().hasExtra("journey")){
            drawRoutes(points, Color.RED);
            if (getIntent().hasExtra("ride")){
                Journey journey = getIntent().getParcelableExtra("ride");
                departurePoint = journey.getDeparturePoint();
                arrivalPoint = journey.getArrivalPoint();
                duration = journey.getDuration();
                points = journey.getPoints();
                drawRoutes(points, Color.GREEN);
            }
            zoomRoute(points);
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                permissions[0])
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, permissions[1]) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions[0]) && ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1])) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("This app requires location permissions to work.")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        permissions,
                                        REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        permissions,
                        REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    Toast.makeText(this, "You can now create journeys.", Toast.LENGTH_LONG).show();

                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "You cannot create a journey without location permissions.", Toast.LENGTH_LONG).show();
                    finish();

                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
//        getLastKnownLocation();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        for (PolylineData polylineData : polyLines) {
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(ContextCompat.getColor(this, R.color.colorRed));
                polylineData.getPolyline().setZIndex(1);

                arrivalMarker.remove();
                arrivalMarker = map.addMarker(new MarkerOptions()
                        .position(arrivalPoint)
                        .title(ARRIVAL_TITLE)
                        .snippet("Duration: " + polylineData.getLeg().duration));
                arrivalMarker.showInfoWindow();

                duration = polylineData.getLeg().duration.inSeconds;
                distance = polylineData.getLeg().distance.inMeters;
                points = polylineData.getPolyline().getPoints();
//                Log.d(TAG, "TRUE: " + polylineData.getPolyline().getId());
            } else {
                polylineData.getPolyline().setColor(ContextCompat.getColor(this, R.color.quantum_grey));
                polylineData.getPolyline().setZIndex(0);
//                Log.d(TAG, "FALSE: " + polylineData.getPolyline().getId());
            }
        }
//        Log.d(TAG, "onPolylineClick: " + polyline.getId());
//        for (LatLng point : polyline.getPoints())
//            Log.d(TAG, "Point: " + point);
    }
}
