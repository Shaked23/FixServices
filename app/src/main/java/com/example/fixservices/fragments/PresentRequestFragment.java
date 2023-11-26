package com.example.fixservices.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixservices.R;
import com.example.fixservices.classes.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Presentation the full request and give the option to call who create the request
 * and to navigate him */
public class PresentRequestFragment extends Fragment {
    private Bundle bundle;
    private PackageManager packageManager;
    private Double latitude, longitude;

    public PresentRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_present_request, container, false);
        bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        TextView location = view.findViewById(R.id.textViewLocation);
        TextView description = view.findViewById(R.id.textViewDescription);
        TextView nameUser = view.findViewById(R.id.textViewNameUser);
        TextView PhoneUser = view.findViewById(R.id.textViewPhone);
        Button backBtn = view.findViewById(R.id.backBtn);
        Button navigateBtn = view.findViewById(R.id.navigateBtn);
        packageManager = requireActivity().getPackageManager();

        // If the professional pass it from the map
        if (bundle.getString("latitude") != null && bundle.getString("longitude") != null) {
            FirebaseDatabase.getInstance().getReference("OpenRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snapUser : snapshot.getChildren()) {
                            for (DataSnapshot snapDesc : snapUser.getChildren()) {
                                if (Objects.requireNonNull(snapDesc.child("latitude").getValue(Double.class)) == Double.parseDouble(Objects.requireNonNull(bundle.getString("latitude"))) &&
                                        Objects.requireNonNull(snapDesc.child("longitude").getValue(Double.class)) == Double.parseDouble(Objects.requireNonNull(bundle.getString("longitude")))) {
                                    Request request = snapDesc.getValue(Request.class);
                                    String place;
                                    try {
                                        place = get_full(Objects.requireNonNull(snapDesc.child("latitude").getValue(Double.class)), Objects.requireNonNull(snapDesc.child("longitude").getValue(Double.class)));
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if (place != null && !place.isEmpty()) {
                                        location.setText("Location: " + place);
                                    } else {
                                        Toast.makeText(requireContext(), "There isn't a request", Toast.LENGTH_SHORT).show();
                                    }
                                    assert request != null;
                                    description.setText("Description: " + snapDesc.getKey());
                                    nameUser.setText("Name of user: " + request.getNameUser());
                                    PhoneUser.setText("Phone of user: " + request.getPhoneUser());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "problem read data from OpenRequest", Toast.LENGTH_SHORT).show();
                }
            });


            navigateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateToDestination(Double.parseDouble(Objects.requireNonNull(bundle.getString("latitude"))), Double.parseDouble(Objects.requireNonNull(bundle.getString("longitude"))));
                }
            });

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    RequestsFragment fragment = new RequestsFragment();
                    fragment.setArguments(bundle);
                    transaction.replace(R.id.map_container, fragment);
                    transaction.commit();
                }
            });
        } else {
            // If the professional pass it from MyRequests
            if (bundle.getString("uidRequest") != null && bundle.getString("descriptionRequest") != null) {
                FirebaseDatabase.getInstance().getReference("TreatedRequests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snapUser : snapshot.getChildren()) {
                                if (Objects.equals(snapUser.getKey(), bundle.getString("uidRequest"))) {
                                    for (DataSnapshot snapDesc : snapUser.getChildren()) {
                                        if (Objects.equals(snapDesc.getKey(), bundle.getString("descriptionRequest"))) {
                                            Request request = snapDesc.getValue(Request.class);
                                            String place;
                                            try {
                                                place = get_full(Objects.requireNonNull(snapDesc.child("latitude").getValue(Double.class)), Objects.requireNonNull(snapDesc.child("longitude").getValue(Double.class)));
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            }
                                            if (place != null && !place.isEmpty()) {
                                                location.setText("Location: " + place);
                                            } else {
                                                Toast.makeText(requireContext(), "There isn't a request", Toast.LENGTH_SHORT).show();
                                            }
                                            assert request != null;
                                            description.setText("Description: " + snapDesc.getKey());
                                            nameUser.setText("Name of user: " + request.getNameUser());
                                            PhoneUser.setText("Phone of user: " + request.getPhoneUser());
                                            latitude = request.getLatitude();
                                            longitude = request.getLongitude();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "problem read data from OpenRequest", Toast.LENGTH_SHORT).show();
                    }
                });

                navigateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateToDestination(latitude, longitude);
                    }
                });

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(v).navigate(R.id.action_presentRequestFragment_to_myRequestFragment, bundle);
                    }
                });
            }
        }

        PhoneUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Extract the phone number from the text of the TextView
                String phoneNumberWithPrefix = PhoneUser.getText().toString();

                // Remove the "Phone:" prefix to get just the phone number
                String phoneNumber = phoneNumberWithPrefix.replace("Phone of user: ", "");

                // Create an Intent to initiate a phone call
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));

                // Start the phone dialer with the phone number
                startActivity(dialIntent);
            }
        });

        return view;
    }

    private String get_full(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(requireContext(), Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        assert addresses != null;
        return addresses.get(0).getAddressLine(0);
    }

    public void navigateToDestination(Double latitude, Double longitude) {
        double destLatitude = latitude;
        double destLongitude = longitude;

        // Create a Uri with the destination coordinates
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destLatitude + "," + destLongitude);

        // Create an Intent with the action ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        // Set the package to Google Maps (com.google.android.apps.maps)
        mapIntent.setPackage("com.google.android.apps.maps");

        // Check if the Google Maps app is installed
        if (mapIntent.resolveActivity(packageManager) != null) {
            // Start the navigation using Google Maps
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireContext(), "Google Maps app is not installed.", Toast.LENGTH_SHORT).show();
        }
    }
}


