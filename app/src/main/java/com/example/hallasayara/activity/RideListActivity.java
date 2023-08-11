package com.example.hallasayara.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.hallasayara.R;
import com.example.hallasayara.adapter.JourneyScheduleAdapter;
import com.example.hallasayara.adapter.RideListAdapter;
import com.example.hallasayara.core.Journey;
import com.example.hallasayara.core.Ride;
import com.example.hallasayara.global.Database;

import java.util.ArrayList;
import java.util.List;

public class RideListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    public static final int SCHEDULE_RIDE = 1;
    private static final String TAG = "RideListActivity";
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Ride> possibleRideList;
    private Journey journey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_list);

        recyclerView = (RecyclerView) findViewById(R.id.ride_list_recycler_view);
        manager = new LinearLayoutManager(this);

        possibleRideList = new ArrayList<>();

        adapter = new RideListAdapter(possibleRideList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        if (getIntent().hasExtra("journey"))
            journey = getIntent().getParcelableExtra("journey");

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ride_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                Database.initialize(getApplicationContext());
                Database.loadAvailableRideList(journey, possibleRideList, adapter);

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @Override
    public void onRefresh() {
        Database.initialize(this);
        Database.loadAvailableRideList(journey, possibleRideList, adapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
