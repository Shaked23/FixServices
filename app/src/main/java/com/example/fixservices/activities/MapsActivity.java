package com.example.fixservices.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.location.Location;
import android.Manifest;


import com.example.fixservices.classes.MyLocation;
import com.example.fixservices.R;
import com.example.fixservices.databinding.ActivityMapsBinding;
import com.example.fixservices.fragments.RequestsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Context activityContext;

    private Bundle bundle;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final double MIN_DISTANCE_THRESHOLD = 100; // Set your desired threshold in meters
    private ArrayList<Marker> markerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getIntent().getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityContext = this;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_container);

        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                // Initialize GoogleMap
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // Move the camera to the device's current location
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    if (lastKnownLocation != null && isValidLocation(lastKnownLocation)) {
                        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    }
                } else {
                    // Request location permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            });
        }
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        getDeviceLocation();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        BitmapDescriptor greenMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        markerList.clear();
        FirebaseDatabase.getInstance().getReference("OpenRequests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapUser : snapshot.getChildren()) {
                        for (DataSnapshot snapDesc : snapUser.getChildren()) {
                            MyLocation location = new MyLocation(Objects.requireNonNull(snapDesc.child("latitude").getValue(Double.class)).toString(),
                                    Objects.requireNonNull(snapDesc.child("longitude").getValue(Double.class)).toString());
                            LatLng request = new LatLng(Double.parseDouble(location.get_Latitude()), Double.parseDouble(location.get_Longitude()));
                            boolean farEnough = isFarEnoughFromOtherMarkers(request);
                            if(markerList.isEmpty()){
                                Marker marker = mMap.addMarker(new MarkerOptions().position(request).title(snapUser.getKey()).icon(greenMarker));
                                markerList.add(marker);
                            } else {
                                if (farEnough) {
                                    Marker marker = mMap.addMarker(new MarkerOptions().position(request).title(snapUser.getKey()).icon(greenMarker));
                                    markerList.add(marker);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(request));
                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                            RequestsFragment fragment = new RequestsFragment();
                                            bundle.putString("latitude", String.valueOf(marker.getPosition().latitude));
                                            bundle.putString("longitude", String.valueOf(marker.getPosition().longitude));
                                            fragment.setArguments(bundle);
                                            transaction.replace(R.id.map_container, fragment);
                                            transaction.commit();
                                            return true;
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activityContext, "problem read data from OpenRequest", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Checks if a new marker (request) is far enough from existing markers on the map.
    private boolean isFarEnoughFromOtherMarkers(LatLng newRequest) {
        for (Marker existingMarker : markerList) {
            float[] distance = new float[1];
            Location.distanceBetween(
                    existingMarker.getPosition().latitude,
                    existingMarker.getPosition().longitude,
                    newRequest.latitude,
                    newRequest.longitude,
                    distance
            );
            Log.d("TAG", String.valueOf(distance[0]));

            if (distance[0] < MIN_DISTANCE_THRESHOLD) {
                // The new request is too close to an existing marker
                return false;
            }
        }

        // The new request is far enough from all existing markers
        return true;
    }

     // Retrieves the device's last-known location using the `FusedLocationProviderClient`.
     // If the location is valid, it moves the camera to that location.
    private void getDeviceLocation() {
        if (mMap == null) {
            return;
        }

        // Move the camera to the last-known location (if available)
        if (lastKnownLocation != null && isValidLocation(lastKnownLocation)) {
            LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }

        // Now, check for the current location
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null && isValidLocation(lastKnownLocation) && mMap != null) {
                                LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                                bundle.putString("currentLatitude", String.valueOf(latLng.latitude));
                                bundle.putString("currentLongitude", String.valueOf(latLng.longitude));
//                                Log.d("MapsActivity", "Last-known location retrieved: " + latLng.toString());
                            } else {
                                Log.e("MapsActivity", "Invalid or null location.");
                            }
                        } else {
                            Log.e("MapsActivity", "Location task not successful.");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    // Updates the UI elements based on the location permission status.
    // If permission is granted, it enables the "My Location" feature on the map; otherwise, it disables it.
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Checks if a location is valid based on its timestamp.
    // It ensures that the location data is not too old.
    private boolean isValidLocation(Location location) {
        long currentTime = System.currentTimeMillis();
        long locationTime = location.getTime();
        long maxLocationAge = 5 * 60 * 1000; // 5 minutes

        return (currentTime - locationTime) <= maxLocationAge;
    }
}