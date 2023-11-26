package com.example.fixservices.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.fixservices.classes.Database;
import com.example.fixservices.R;
import com.example.fixservices.classes.Request;
import com.example.fixservices.adapters.OpenRequestProfAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Presentation of the open and treated requests of the user/professional **/
public class MyRequestsFragment extends Fragment implements OpenRequestProfAdapter.OnItemClickListener {
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_requests, container, false);
        bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        RecyclerView requestView = view.findViewById(R.id.listRequests);
        Database.setupRecyclerViewRequests(requireContext(), requestView, bundle.getString("uidUser"),
                bundle.getString("professional") != null, this);

        Button backBtn = view.findViewById(R.id.buttonBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_MyRequestFragment_to_homeFragment, bundle);
            }
        });

        return view;
    }

    // When the professional clicks on the button Done of specific request, this request closing
    // (in database, the request will pass from "TreatedRequests" to "CloseRequests").
    @Override
    public void onDoneClick(Request request) {
        DatabaseReference treatedRequests = FirebaseDatabase.getInstance().getReference("TreatedRequests/" + request.getUidUser() + '/' + request.getDescription());
        treatedRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Request requestFire = snapshot.getValue(Request.class);
                    if (requestFire != null) {
                        FirebaseDatabase.getInstance().getReference().child("CloseRequests/" +
                                request.getUidUser() + "/" +
                                request.getDescription()).setValue(requestFire);
                        FirebaseDatabase.getInstance().getReference().child("CloseRequests/" +
                                request.getUidUser() + "/" +
                                request.getDescription() + "/professional").removeValue();
                        treatedRequests.removeValue().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "There is not this request", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "This request is completed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "problem read data from TreatedRequests");
            }
        });
    }

    // When the professional clicks on the present button, he will pass to PresentRequestsFragment,
    // and there he can call the person who opened the request and to navigate to him
    @Override
    public void onPresentClick(Request request) {
        bundle.putString("uidRequest", request.getUidUser());
        bundle.putString("descriptionRequest", request.getDescription());
        Navigation.findNavController(requireView()).navigate(R.id.action_myRequestFragment_to_presentRequestFragment, bundle);
    }
}