package com.example.fixservices.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;

import com.example.fixservices.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/** The main fragment of the application **/
public class HomeFragment extends Fragment {
    private Bundle bundle;
    int flag = 0;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        CardView exit = view.findViewById(R.id.cardLogout);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_loginFragment);
            }
        });

        // Option to get details on a specific professional
        CardView searchForMaster = view.findViewById(R.id.cardSearch);
        searchForMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bundle.getString("professional") == null) {
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_searchFragment, bundle);
                } else {
                    Toast.makeText(requireContext(), "This is no for professional", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Option to get the details of the user
        CardView MyDetails = view.findViewById(R.id.cardDetails);
        MyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_detailsFragment, bundle);
            }
        });

        // Presentation the open and treated requests
        CardView MyRequests = view.findViewById(R.id.cardRequests);
        MyRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bundle.getString("professional") == null) {
                    FirebaseDatabase.getInstance().getReference("CloseRequests").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // If the user has closed request, in order to give him to rate the professional
                                    if (snapshot.hasChild(Objects.requireNonNull(bundle.getString("uidUser")))) {
                                        for (DataSnapshot snap : snapshot.child(Objects.requireNonNull(bundle.getString("uidUser"))).getChildren()) {
                                            bundle.putString("professionalUIDClose", snap.child("uidProfessional").getValue(String.class));
                                            bundle.putString("descriptionClose", snap.getKey());
                                        }
                                        Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_profHomeFragment, bundle);
                                        // Give to the user the option to open new request
                                    } else {
                                        Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_myRequestFragment, bundle);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("ERROR", "problem read data from CloseRequests");
                                }
                            });
                } else {
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_myRequestFragment, bundle);
                }
            }
        });

        // Presentation price list
        CardView cardPrices = view.findViewById(R.id.cardPrices);
        cardPrices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_pricesFragment, bundle);
            }
        });

        //For professional search requests
        // For user create new request
        CardView newRequest = view.findViewById(R.id.cardNewRequest);
        newRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bundle.getString("professional") != null) {
                    FirebaseDatabase.getInstance().getReference("TreatedRequests").addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot snapUser : snapshot.getChildren()){
                                        for(DataSnapshot snapDesc : snapUser.getChildren()){
                                            if(Objects.equals(snapDesc.child("uidProfessional").getValue(String.class), bundle.getString("uidUser"))){
                                                flag = 1;
                                                break;
                                            }
                                        }
                                    }
                                    if (flag == 1) {
                                        Toast.makeText(requireContext(), "You already has a request, please do it", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                        }
                                        fusedLocationClient.getLastLocation()
                                                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Location> task) {
                                                        if (task.isSuccessful() && task.getResult() != null) {
                                                            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_mapsActivity, bundle);
                                                        }
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("ERROR", "problem read data from TreatedRequests");
                                }
                            });
                } else {
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_requestFragment, bundle);
                }
            }
        });

        return view;
    }
}