package com.example.hallasayara.fragment.Journeys;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hallasayara.R;
import com.example.hallasayara.activity.RideListActivity;
import com.example.hallasayara.adapter.JourneyScheduleAdapter;
import com.example.hallasayara.global.Database;

/**
 * A simple {@link Fragment} subclass.
 */
public class JourneyScheduleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_journey_schedule, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.journey_schedule_recycler_view);
        manager = new LinearLayoutManager(getContext());

        adapter = new JourneyScheduleAdapter(Database.getJourneyList(), getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.journey_swipe_container);
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
                adapter.notifyDataSetChanged();

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("JourneyScheduleFragment", "ResultCode actual=" + resultCode);
        Log.d("JourneyScheduleFragment", "ResultCode expected=" + getParentFragment().getActivity().RESULT_OK);
        if (requestCode == JourneysFragment.CREATE_JOURNEY && resultCode == getParentFragment().getActivity().RESULT_OK) {
            adapter.notifyDataSetChanged();
        }else if (requestCode == RideListActivity.SCHEDULE_RIDE && resultCode == getParentFragment().getActivity().RESULT_OK){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        Database.initialize(getActivity());
        Database.loadJourneyList();
        adapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
