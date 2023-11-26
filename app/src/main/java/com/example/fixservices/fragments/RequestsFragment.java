package com.example.fixservices.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fixservices.activities.MapsActivity;
import com.example.fixservices.classes.MyLocation;
import com.example.fixservices.R;
import com.example.fixservices.classes.Request;
import com.example.fixservices.adapters.RequestAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class RequestsFragment extends Fragment {

    private Bundle bundle;
    private RequestAdapter.OnItemClickListener listener;
    private RecyclerView requestsRecyclerView;

    public RequestsFragment() {
        // Required empty public constructor
    }

//    public static RequestsFragment newInstance(String param1, String param2) {
//        RequestsFragment fragment = new RequestsFragment();
//        Bundle args = new Bundle();
//
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        requestsRecyclerView = view.findViewById(R.id.recyclerViewRequests);
        Button backBtn = view.findViewById(R.id.buttonBack);

        ArrayList<Request> listRequests = new ArrayList<>();
        RequestAdapter adapter = new RequestAdapter(listRequests, new MyLocation(bundle.getString("currentLatitude"), bundle.getString("currentLongitude")));
        adapter.setListener(new RequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("TAG", "the item clicked");
                Request clickedRequest = ((RequestAdapter) Objects.requireNonNull(requestsRecyclerView.getAdapter())).getItem(position);
                bundle.putString("latitude", clickedRequest.getLatitude().toString());
                bundle.putString("longitude", clickedRequest.getLongitude().toString());
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                PresentRequestFragment fragment = new PresentRequestFragment();
                fragment.setArguments(bundle);
                transaction.replace(R.id.map_container, fragment);
                transaction.commit();
            }
        });
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        requestsRecyclerView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference("OpenRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listRequests.clear();
                for (DataSnapshot user : snapshot.getChildren()) {
                    for (DataSnapshot description : user.getChildren()) {
                        float[] distance = new float[1];
                        Location.distanceBetween(Objects.requireNonNull(description.child("latitude").getValue(Double.class)),
                                Objects.requireNonNull(description.child("longitude").getValue(Double.class)),
                                Double.parseDouble(Objects.requireNonNull(bundle.getString("latitude"))),
                                Double.parseDouble(Objects.requireNonNull(bundle.getString("longitude"))), distance);
                        if (distance[0] <= 50) {
                            Request request = description.getValue(Request.class);
                            if (request != null) {
                                request.setUidUser(user.getKey());
                                request.setDescription(description.getKey());
                                request.setProfessional(bundle.getString("professional") != null);
                                listRequests.add(request);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "problem read data from OpenRequests");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.action_requests_mapsActivity);
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return view;
    }


}

//    @Override
//    public void onItemClick(int position) {
//        Log.d("TAG", "the item clicked");
//        Request clickedRequest = ((RequestAdapter) Objects.requireNonNull(requestsRecyclerView.getAdapter())).getItem(position);
//        bundle.putString("latitude", clickedRequest.getLatitude().toString());
//        bundle.putString("longitude", clickedRequest.getLongitude().toString());
//        Navigation.findNavController(requireView()).navigate(R.id.action_requestsFragment_to_presentRequestFragment, bundle);
//
////        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
////        PresentRequestFragment fragment = new PresentRequestFragment();
////        fragment.setArguments(bundle);
////        transaction.replace(R.id.request_container, fragment);
////        transaction.addToBackStack(null);
////        transaction.commit();
//    }
//}