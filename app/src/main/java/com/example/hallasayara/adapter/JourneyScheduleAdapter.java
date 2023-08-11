package com.example.hallasayara.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hallasayara.R;
import com.example.hallasayara.activity.MapsActivity;
import com.example.hallasayara.activity.RideListActivity;
import com.example.hallasayara.core.Journey;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class JourneyScheduleAdapter extends RecyclerView.Adapter<JourneyScheduleAdapter.ViewHolder> {

    private List<Journey> data;
    private Context context;

    public JourneyScheduleAdapter(List<Journey> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public JourneyScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_journey_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JourneyScheduleAdapter.ViewHolder viewHolder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = dateFormat.format(data.get(position).getDepartureTime());
        viewHolder.dateTextView.setText(dateString);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeString = timeFormat.format(data.get(position).getDepartureTime());
        viewHolder.departureTimeTextView.setText(timeString);

        if (data.get(position).getArrivalTime() != null) {
            timeString = timeFormat.format(data.get(position).getArrivalTime());
            viewHolder.arrivalTimeTextView.setText(timeString);
        }

        viewHolder.departureLocTextView.setText(data.get(position).getDepartureName());
        viewHolder.arrivalLocTextView.setText(data.get(position).getArrivalName());

        DecimalFormat idFormat = new DecimalFormat("00000");
        DecimalFormat costFormat = new DecimalFormat("###,###,###.##");

        double distance = data.get(position).getDistance() * 1.0;
        String unit = "m";
        //convert to km
        if (distance >= 1000){
            unit = "km";
            distance /= 1000;
        }
        viewHolder.distanceTextView.setText(String.format("%.2f", distance) + " " + unit);

        if (data.get(position).getPoints() == null)
            viewHolder.mapTextView.setVisibility(View.INVISIBLE);

        int status = data.get(position).getStatus();
        switch (status) {
            case Journey.STATUS_SCHEDULED:
                viewHolder.statusTextView.setText("SCHEDULED");
                viewHolder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
                viewHolder.scheduleTextView.setVisibility(View.INVISIBLE);
                viewHolder.cancelTextView.setVisibility(View.VISIBLE);
                viewHolder.rideIdTextView.setText("Ride ID: " + idFormat.format(data.get(position).getRide().getId()));
                viewHolder.costTextView.setText(costFormat.format(data.get(position).getRide().getCost()) + " Rs");
                break;
            case Journey.STATUS_UNSCHEDULED:
                viewHolder.statusTextView.setText("UNSCHEDULED");
                viewHolder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.orange));
                viewHolder.scheduleTextView.setVisibility(View.VISIBLE);
                viewHolder.cancelTextView.setVisibility(View.INVISIBLE);
                break;
            case Journey.STATUS_CANCELLED:
                viewHolder.statusTextView.setText("CANCELLED");
                viewHolder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                viewHolder.scheduleTextView.setVisibility(View.VISIBLE);
                viewHolder.cancelTextView.setVisibility(View.INVISIBLE);
                break;
        }

        viewHolder.scheduleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RideListActivity.class);
                intent.putExtra("journey", data.get(position));
                ((Activity) context).startActivityForResult(intent, RideListActivity.SCHEDULE_RIDE);
            }
        });

        viewHolder.mapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("journey", data.get(position));
                ((Activity) context).startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView dateTextView, departureTimeTextView, arrivalTimeTextView, departureLocTextView, arrivalLocTextView, rideIdTextView, statusTextView, distanceTextView, costTextView;
        protected TextView cancelTextView, scheduleTextView, mapTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.journey_date_text_view);
            departureTimeTextView = (TextView) itemView.findViewById(R.id.journey_departure_time_text_view);
            arrivalTimeTextView = (TextView) itemView.findViewById(R.id.journey_arrival_time_text_view);
            departureLocTextView = (TextView) itemView.findViewById(R.id.journey_departure_location_text_view);
            arrivalLocTextView = (TextView) itemView.findViewById(R.id.journey_arrival_location_text_view);
            rideIdTextView = (TextView) itemView.findViewById(R.id.journey_ride_id_text_view);
            statusTextView = (TextView) itemView.findViewById(R.id.journey_status_text_view);
            distanceTextView = (TextView) itemView.findViewById(R.id.journey_distance_value_text_view);
            costTextView = (TextView) itemView.findViewById(R.id.journey_cost_text_view);
            cancelTextView = (TextView) itemView.findViewById(R.id.cancel_button);
            scheduleTextView = (TextView) itemView.findViewById(R.id.schedule_button);
            mapTextView = (TextView) itemView.findViewById(R.id.ride_map_button);
        }
    }
}
