package com.example.fixservices.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fixservices.classes.MyLocation;
import com.example.fixservices.R;
import com.example.fixservices.classes.Request;

import java.util.ArrayList;

import android.location.Location;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder>{
    private ArrayList<Request> dataSet;
    private MyLocation currentLocation;
    private OnItemClickListener listener;


    public RequestAdapter(ArrayList<Request> dataSet, MyLocation currentLocation) {
        this.dataSet = dataSet;
        this.currentLocation = currentLocation;
    }

    @NonNull
    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.requests_list_card, parent ,false);
        return new RequestAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.MyViewHolder holder, int position) {
        TextView textViewLocation = holder.textLocation;
        TextView textViewDistance = holder.textDistance;
        TextView textViewDescription = holder.textDescription;

        textViewLocation.setText("Location: \n" + dataSet.get(position).getLocation());
        float[] distance = new float[1];
        Location.distanceBetween(Double.parseDouble(currentLocation.get_Latitude()), Double.parseDouble(currentLocation.get_Longitude()), dataSet.get(position).getLatitude(), dataSet.get(position).getLongitude(), distance);
//            Log.d("TAG", currentLocation.get_Latitude());
//            Log.d("TAG", currentLocation.get_Longitude());
//            Log.d("TAG", dataSet.get(position).getLatitude().toString());
//            Log.d("TAG", dataSet.get(position).getLongitude().toString());
        textViewDistance.setText("Distance: " + String.format("%.3f", (distance[0] * 2)/1000) + " km");
        textViewDescription.setText("Description: \n" + dataSet.get(position).getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public Request getItem(int position) {
        return dataSet.get(position);
    }

    public void setListener (OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textLocation, textDistance, textDescription;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textLocation = itemView.findViewById(R.id.textViewLocation);
            textDistance = itemView.findViewById(R.id.textViewName);
            textDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }
}
