package com.example.hallasayara.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hallasayara.R;
import com.example.hallasayara.activity.MapsActivity;
import com.example.hallasayara.core.Journey;
import com.example.hallasayara.core.Ride;
import com.example.hallasayara.global.Database;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.ViewHolder> {
    private List<Ride> data;
    private Context context;
    private Journey journeyForThisRide = null;

    public RideListAdapter(List<Ride> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RideListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_ride_list, parent, false);
        return new RideListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RideListAdapter.ViewHolder viewHolder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String dateString = dateFormat.format(data.get(position).getJourney().getDepartureTime());
        viewHolder.dateTextView.setText(dateString);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String timeString = timeFormat.format(data.get(position).getJourney().getDepartureTime());
        viewHolder.departureTimeTextView.setText(timeString);

        if (data.get(position).getJourney().getArrivalTime() != null) {
            timeString = timeFormat.format(data.get(position).getJourney().getArrivalTime());
            viewHolder.arrivalTimeTextView.setText(timeString);
        }

        viewHolder.departureLocTextView.setText(data.get(position).getJourney().getDepartureName());
        viewHolder.arrivalLocTextView.setText(data.get(position).getJourney().getArrivalName());

        DecimalFormat costFormat = new DecimalFormat("###,###,###.##");

        double distance = data.get(position).getJourney().getDistance() * 1.0;
        String unit = "m";
        //convert to km
        if (distance >= 1000) {
            unit = "km";
            distance /= 1000;
        }
        viewHolder.distanceTextView.setText(String.format("%.2f", distance) + " " + unit);

        if (data.get(position).getJourney().getPoints() == null)
            viewHolder.mapTextView.setVisibility(View.INVISIBLE);
        viewHolder.mapTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                journeyForThisRide = ((Activity) context).getIntent().getParcelableExtra("journey");
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("journey", journeyForThisRide);
                intent.putExtra("ride", data.get(position).getJourney());
                ((Activity) context).startActivity(intent);
            }
        });

        viewHolder.costTextView.setText(costFormat.format(data.get(position).getCost()));
        viewHolder.riderTextView.setText(data.get(position).getDriverName());
        viewHolder.vehicleTextView.setText(data.get(position).getVehiclePlate());
        viewHolder.totalSeatsTextView.setText(Integer.toString(data.get(position).getTotalSeats()));
        viewHolder.availableSeatsTextView.setText(Integer.toString(data.get(position).getAvailableSeats()));

        viewHolder.selectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int journeyId = ((Activity) context).getIntent().getIntExtra("journey_id", -1);
                Ride ride = data.get(position);
                int seats = ride.getAvailableSeats();
                seats--;
                ride.setAvailableSeats(seats);
                for (Journey journey : Database.getJourneyList())
                    if (journey.getId() == journeyId) {
                        journeyForThisRide = journey;
                        journeyForThisRide.setRide(ride);
                        journeyForThisRide.setStatus(Journey.STATUS_SCHEDULED);
                        break;
                    }

                Database.scheduleRide(journeyForThisRide.getId(), ride.getId(), seats, journeyForThisRide.getStatus());

//                Log.d("RideListAdapter", "" + journeyForThisRide.toString());
                ((Activity) context).setResult(Activity.RESULT_OK);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView dateTextView, departureTimeTextView, arrivalTimeTextView, departureLocTextView, arrivalLocTextView, distanceTextView, costTextView;
        protected TextView riderTextView, vehicleTextView, totalSeatsTextView, availableSeatsTextView;
        protected TextView selectTextView, mapTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.ride_date_text_view);
            departureTimeTextView = (TextView) itemView.findViewById(R.id.ride_departure_time_text_view);
            arrivalTimeTextView = (TextView) itemView.findViewById(R.id.ride_arrival_time_text_view);
            departureLocTextView = (TextView) itemView.findViewById(R.id.ride_departure_location_text_view);
            arrivalLocTextView = (TextView) itemView.findViewById(R.id.ride_arrival_location_text_view);
            distanceTextView = (TextView) itemView.findViewById(R.id.ride_distance_value_text_view);
            riderTextView = (TextView) itemView.findViewById(R.id.ride_rider_value_text_view);
            vehicleTextView = (TextView) itemView.findViewById(R.id.ride_vehicle_value_text_view);
            totalSeatsTextView = (TextView) itemView.findViewById(R.id.ride_total_seats_value_text_view);
            availableSeatsTextView = (TextView) itemView.findViewById(R.id.ride_available_seats_value_text_view);
            costTextView = (TextView) itemView.findViewById(R.id.ride_cost_text_view);
            mapTextView = (TextView) itemView.findViewById(R.id.ride_map_button);
            selectTextView = (TextView) itemView.findViewById(R.id.ride_select_button);
        }
    }
}
