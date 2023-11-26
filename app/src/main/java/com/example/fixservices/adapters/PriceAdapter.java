package com.example.fixservices.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixservices.classes.Item;
import com.example.fixservices.R;

import java.util.ArrayList;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.MyViewHolder>{

    private ArrayList<Item> dataSet;

    public PriceAdapter(ArrayList<Item> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prices_card, parent ,false);
        return new PriceAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView textName = holder.textName;
        TextView textDescription = holder.textDescription;
        TextView textPrice = holder.textPrice;

        textName.setText(dataSet.get(position).getName());
        textDescription.setText(dataSet.get(position).getDescription());
        textPrice.setText(dataSet.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public ArrayList<Item> getDataSet() {
        return dataSet;
    }

    public void setDataSet(ArrayList<Item> dataSet) {
        this.dataSet = dataSet;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textName, textDescription, textPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textViewName);
            textDescription = itemView.findViewById(R.id.textViewDescription);
            textPrice = itemView.findViewById(R.id.textViewPrice);
        }
    }
}
