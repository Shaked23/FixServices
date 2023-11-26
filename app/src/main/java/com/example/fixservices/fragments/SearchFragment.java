package com.example.fixservices.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixservices.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** Search details on specific professional (by uid of the professional) **/
public class SearchFragment extends Fragment {
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        EditText EDuid = view.findViewById(R.id.editTextUID);
        TextView name = view.findViewById(R.id.textViewName);
        TextView phone = view.findViewById(R.id.textViewPhone);
        TextView experience = view.findViewById(R.id.textViewExperience);
        TextView score = view.findViewById(R.id.textViewScore);
        TextView raters = view.findViewById(R.id.textViewRaters);
        TextView domain =  view.findViewById(R.id.textViewDomain);
        TextView date =  view.findViewById(R.id.textViewDate);
        Button searchBtn = view.findViewById(R.id.buttonSearch);
        Button backBtn = view.findViewById(R.id.buttonBack);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference("Professionals").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String uid = EDuid.getText().toString();
                        if (!uid.equals("")) {
                            if (snapshot.hasChild(uid)) {
                                name.setText(snapshot.child(uid + "/name").getValue(String.class));
                                phone.setText(snapshot.child(uid + "/phone").getValue(String.class));
                                experience.setText("Experience: " + snapshot.child(uid + "/experience").getValue(Integer.class));
                                score.setText("Score: " + snapshot.child(uid + "/score").getValue(Double.class));
                                raters.setText("Raters: " + snapshot.child(uid + "/raters").getValue(Integer.class));
                                domain.setText("Domain: " + snapshot.child(uid + "/domain").getValue(String.class));
                                date.setText("Register Date: "+ snapshot.child(uid + "/dateCreate").getValue(String.class));
                            }
                        } else {
                            Toast.makeText(requireContext(), "wrong UID, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG", "problem read data from Professionals");
                    }
                });
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phone.getText().toString();
                // Create an Intent to initiate a phone call
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));
                // Start the phone dialer with the phone number
                startActivity(dialIntent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_searchFragment_to_homeFragment, bundle);
            }
        });

        return view;
    }
}