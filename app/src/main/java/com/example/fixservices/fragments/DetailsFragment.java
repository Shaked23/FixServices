package com.example.fixservices.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fixservices.classes.Database;
import com.example.fixservices.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/** Presentation the details of the user **/
public class DetailsFragment extends Fragment {
    private Bundle bundle;
    private FirebaseDatabase database;
    private DatabaseReference professional;
    private ListView details;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        bundle = getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }

        database = FirebaseDatabase.getInstance();
        details = view.findViewById(R.id.listRequests);

        if (bundle.getString("professional") != null){
            database.getReference("Professionals").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            if (Objects.equals(snap.getKey(), bundle.getString("uidUser"))) {
                                professional = database.getReference("Professionals/" + '/' + bundle.getString("uidUser"));
                                Database.setupListViewProfessional(getContext(),details,professional);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Error read data from Professionals", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            DatabaseReference user = database.getReference("Users/" + bundle.getString("uidUser"));
            Database.setupListViewUser(getContext(),details, user);
        }

        Button backBtn = view.findViewById(R.id.buttonBackOrderDet);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_detailsFragment_to_homeFragment, bundle);
            }
        });

        Button updateBtn = view.findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_detailsFragment_to_updateDetailsFragment, bundle);
            }
        });

        return view;
    }
}