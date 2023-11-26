package com.example.fixservices.fragments;

import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fixservices.classes.Professional;
import com.example.fixservices.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfHomeFragment extends Fragment {

    private RatingBar ratingBar;
    private TextView nameText, experienceText, phoneText, scoreText, ratersText;
    private Button backBtn, sendBtn;
    private Professional professional;
    private Bundle bundle;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_professional, container, false);
        bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        sendBtn = view.findViewById(R.id.send);
        ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setStepSize(1);
        nameText = view.findViewById(R.id.textViewName);
        experienceText = view.findViewById(R.id.textViewExperience);
        phoneText = view.findViewById(R.id.textViewPhone);
        scoreText = view.findViewById(R.id.textViewScore);
        ratersText = view.findViewById(R.id.textViewRaters);
        DatabaseReference myRefProf = FirebaseDatabase.getInstance().getReference("Professionals/" + bundle.get("professionalUIDClose"));
        myRefProf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameText.setText("Name: " + snapshot.child("name").getValue(String.class));
                    experienceText.setText("Experience(years): " + snapshot.child("experience").getValue(Integer.class));
                    phoneText.setText("Phone: " + snapshot.child("phone").getValue(String.class));
                    scoreText.setText("Score: " + snapshot.child("score").getValue(Double.class));
                    ratersText.setText("Raters: " + snapshot.child("raters").getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error in read the professional", Toast.LENGTH_SHORT).show();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float score = ratingBar.getRating();
                if (score != 0) {
                    FirebaseDatabase.getInstance().getReference("Professionals/" +
                            bundle.getString("professionalUIDClose")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            professional = snapshot.getValue(Professional.class);
                            if(professional != null ) {
                                professional.setUID(bundle.getString("professionalUIDClose"));
                                Log.d("TAG", professional.getName());
                                Log.d("TAG", professional.getPhone());
                                professional.updateScore(score);
                                FirebaseDatabase.getInstance().getReference("CloseRequests/" + bundle.getString("uidUser") +
                                        '/' + bundle.getString("descriptionClose")).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Navigation.findNavController(requireView()).navigate(R.id.action_profHomeFragment_to_myRequestFragment, bundle);
                                            }
                                        });
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("ERROR", "problem read data from Professionals");
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "please choose score", Toast.LENGTH_SHORT).show();
                }
//                                Navigation.findNavController(v).navigate(R.id.action_profHomeFragment_to_myRequestFragment, bundle);
            }
        });

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(v).navigate(R.id.action_profHomeFragment2_to_installationFragment, bundle);
//            }
//        });

        return view;
    }
}