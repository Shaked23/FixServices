package com.example.fixservices.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fixservices.R;
import com.example.fixservices.classes.Request;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewRequestFragment extends Fragment {
    private Bundle bundle;
    private FusedLocationProviderClient fusedLocationClient;
    private Spinner spinnerDescription;
    private ArrayAdapter<String> adapterDescription;
    private TextView locationXY;
    double latitude = 0, longitude = 0;

    public NewRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_request, container, false);
        bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        Spinner spinnerDomain = view.findViewById(R.id.spinnerDomain);
        spinnerDescription = view.findViewById(R.id.spinnerDescription);
        Button backBtn = view.findViewById(R.id.backBtn);
        Button sendBtn = view.findViewById(R.id.sendBtn);
        Button locationBtn = view.findViewById(R.id.buttonLocation);
        locationXY = view.findViewById(R.id.textViewLocation);

        ArrayList<String> descriptionArrDomain = new ArrayList<>();
        descriptionArrDomain.add("Select Domain");
        FirebaseDatabase.getInstance().getReference("Prices").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapDomain : snapshot.getChildren()) {
                    descriptionArrDomain.add(snapDomain.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "problem read data from Prices");
            }
        });

        ArrayAdapter<String> adapterDomain = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, descriptionArrDomain);
        adapterDomain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDomain.setAdapter(adapterDomain);

        ArrayAdapter<String> adapterDescription = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, new ArrayList<String>());
        adapterDescription.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDescription.setAdapter(adapterDescription);

        spinnerDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = descriptionArrDomain.get(position); // Get the selected item from spinnerDomain
                ArrayList<String> descriptionArr = new ArrayList<>();
                descriptionArr.add("Select Description");
                FirebaseDatabase.getInstance().getReference("Prices/" + selectedItem).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            descriptionArr.add(snap.getKey());
                        }
                        updateSpinnerDescriptionAdapter(descriptionArr);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ERROR", "problem read data from Price/" + selectedItem);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(requireContext(), "please select domain", Toast.LENGTH_SHORT).show();
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_requestFragment_to_homeFragment, bundle);
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all fields is full
                if (!spinnerDomain.getSelectedItem().toString().equals("Select Domain")
                        && !spinnerDescription.getSelectedItem().toString().equals("Select Description")
                        && latitude != 0 && longitude != 0) {
                    Request request = new Request();
                    // Add listener to "Users" in order to
                    FirebaseDatabase.getInstance().getReference("Users/" + bundle.getString("uidUser")).addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                                request.setUidUser(snapshot.getKey());
                                                request.setNameUser(snapshot.child("name").getValue(String.class));
                                                request.setPhoneUser(snapshot.child("phone").getValue(String.class));
                                                request.setDescription(spinnerDescription.getSelectedItem().toString());
                                                request.setLatitude(latitude);
                                                request.setLongitude(longitude);
                                                try {
                                                    request.setLocation(get_full_address(latitude, longitude));
                                                } catch (IOException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                // Check if the user doesn't has request like this in "OpenRequests"
                                                FirebaseDatabase.getInstance().getReference("OpenRequests").addListenerForSingleValueEvent(
                                                        new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.hasChild(Objects.requireNonNull(bundle.getString("uidUser")))) {
                                                                    if (snapshot.child(Objects.requireNonNull(bundle.getString("uidUser"))).
                                                                            hasChild(spinnerDescription.getSelectedItem().toString())) {
                                                                        Toast.makeText(requireContext(), "you already have request like this", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        // If not, check at the "TreatedRequests"
                                                                        FirebaseDatabase.getInstance().getReference("TreatedRequests").addValueEventListener(
                                                                                new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        if (snapshot.hasChild(Objects.requireNonNull(bundle.getString("uidUser")))) {
                                                                                            // Check if the user has requests, if there is, check that there is not one like the new request
                                                                                            if (snapshot.child(Objects.requireNonNull(bundle.getString("uidUser"))).
                                                                                                    hasChild(spinnerDescription.getSelectedItem().toString())) {
                                                                                                Toast.makeText(requireContext(), "you already have request like this", Toast.LENGTH_SHORT).show();
                                                                                            } else {
                                                                                                // If there is not, save the new request at "OpenRequests"
                                                                                                FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).setValue(request);
                                                                                                FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("description").removeValue();
                                                                                                FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("professional").removeValue();
                                                                                                FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("uidUser").removeValue();
                                                                                                Toast.makeText(requireContext(), "Your request accepted", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        } else {
                                                                                            // If the user there is not any request, save the new request at "OpenRequests"
                                                                                            FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).setValue(request);
                                                                                            FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("description").removeValue();
                                                                                            FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("professional").removeValue();
                                                                                            FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("uidUser").removeValue();
                                                                                            Toast.makeText(requireContext(), "Your request accepted", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                                        Log.e("ERROR", "problem read data from TreatedRequests");
                                                                                    }
                                                                                });
                                                                    }
                                                                } else {
                                                                    // If the user there is not request at "OpenRequests", check at "TreatedRequests"
                                                                    FirebaseDatabase.getInstance().getReference("TreatedRequests").addValueEventListener(
                                                                            new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                    if (snapshot.hasChild(Objects.requireNonNull(bundle.getString("uidUser")))) {
                                                                                        if (snapshot.child(Objects.requireNonNull(bundle.getString("uidUser"))).
                                                                                                hasChild(spinnerDescription.getSelectedItem().toString())) {
                                                                                            Toast.makeText(requireContext(), "you already have request like this", Toast.LENGTH_SHORT).show();
                                                                                        } else {
                                                                                            FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).setValue(request);
                                                                                            FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("description").removeValue();
                                                                                            FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("professional").removeValue();
                                                                                            FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("uidUser").removeValue();
                                                                                            Toast.makeText(requireContext(), "Your request accepted", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    } else {
                                                                                        FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).setValue(request);
                                                                                        FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("description").removeValue();
                                                                                        FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("professional").removeValue();
                                                                                        FirebaseDatabase.getInstance().getReference("OpenRequests/" + bundle.getString("uidUser") + "/" + spinnerDescription.getSelectedItem()).child("uidUser").removeValue();
                                                                                        Toast.makeText(requireContext(), "Your request accepted", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                                    Log.e("ERROR", "problem read data from TreatedRequests");
                                                                                }
                                                                            });
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Log.e("ERROR", "problem read data from OpenRequests");
                                                            }
                                                        });
                                            }
                                        }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("ERROR", "problem read data from Users");
                                }
                            }
                    );
                } else {
                    Toast.makeText(requireContext(), "please fill all fields and click on location button", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

    private void updateSpinnerDescriptionAdapter(ArrayList<String> items) {
        if (adapterDescription == null) {
            adapterDescription = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
            spinnerDescription.setAdapter(adapterDescription);
        } else {
            adapterDescription.clear();
            adapterDescription.addAll(items);
            adapterDescription.notifyDataSetChanged();
        }
    }

    // Function that transform location by (latitude, longitude) to location by name
    private String get_full_address(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(requireContext(), Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        if (addresses != null) {
            return addresses.get(0).getAddressLine(0);
        }
        return "error";
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            // Now, you have the latitude and longitude of the current location.
//                            locationXY.setText("Latitude: " + latitude + ", Longitude: " + longitude);
                            try {
                                locationXY.setText(get_full_address(latitude, longitude));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
                        } else {
                            // Handle location retrieval error, if any.
                            Log.e("Location", "Failed to get location: " + task.getException());
                        }
                    }
                });
    }

    // Location Permission
    public final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        // PERMISSION GRANTED
                        getLocation();
                    } else {
                        // PERMISSION NOT GRANTED
                        Toast.makeText(requireContext(), "For send new request, you must to allow location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
}