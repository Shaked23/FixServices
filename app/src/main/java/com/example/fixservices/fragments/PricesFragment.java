package com.example.fixservices.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fixservices.classes.Database;
import com.example.fixservices.R;


public class PricesFragment extends Fragment{

    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prices, container, false);
        RecyclerView rView = view.findViewById(R.id.recyclerViewPrices);
        Button backBtn = view.findViewById(R.id.buttonBack);
        bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }

        Database.setupRecyclerViewPrice(requireContext(), rView);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_pricesFragment_to_homeFragment, bundle);
            }
        });

        return view;
    }
}