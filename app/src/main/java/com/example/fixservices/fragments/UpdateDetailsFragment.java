package com.example.fixservices.fragments;

import static android.view.View.VISIBLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fixservices.classes.Professional;
import com.example.fixservices.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateDetailsFragment extends Fragment {
    private EditText name, phone;
    private Spinner domain;
    private Bundle bundle;
    private Button update, back;
    private DatabaseReference myProf, myUser;
    private Professional professional;

    public UpdateDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_details, container, false);
        bundle = getArguments();
        if(bundle == null){
            bundle = new Bundle();
        }
        name = view.findViewById(R.id.editTextName);
        phone = view.findViewById(R.id.editTextPhone);
        domain = view.findViewById(R.id.spinnerDomain);
        update = view.findViewById(R.id.updateBtn);
        back = view.findViewById(R.id.backBtn);
        if(bundle.getString("professional") != null) {
            domain.setVisibility(VISIBLE);
            String[] domains = {"Select Domain", "Installation", "Electric", "Clean", "Delivery", "Garage"};
            ArrayAdapter<String> adapterDomain = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, domains);
            adapterDomain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            domain.setAdapter(adapterDomain);
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bundle.getString("professional") != null) {
                    myProf = FirebaseDatabase.getInstance().getReference("Professionals/" + bundle.getString("uidUser"));
                    if (!name.getText().toString().equals("Name") || !name.getText().toString().equals("")) {
                        myProf.child("name").setValue(name.getText().toString());
                        Toast.makeText(getContext(), "Name is changed", Toast.LENGTH_SHORT).show();
                    }
                    if (!phone.getText().toString().equals("Phone") || !phone.getText().toString().equals("")) {
                        myProf.child("phone").setValue(phone.getText().toString());
                        Toast.makeText(getContext(), "Phone is changed", Toast.LENGTH_SHORT).show();
                    }
                    if(!domain.getSelectedItem().equals("Select Domain")){
                        myProf.child("domain").setValue(domain.getSelectedItem());
                        myProf.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    professional = snapshot.getValue(Professional.class);
                                    if (professional != null) {
                                        FirebaseDatabase.getInstance().getReference("Professionals/" +
                                                '/' + bundle.getString("uidUser")).setValue(professional);
                                        FirebaseDatabase.getInstance().getReference("Professionals/" +
                                                '/' + bundle.getString("uidUser")).removeValue();
                                        Toast.makeText(getContext(), "Domain is changed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "problem in read data from database", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else{
                    myUser = FirebaseDatabase.getInstance().getReference("Users/" + bundle.getString("uidUser"));
                    if (!name.getText().toString().equals("Name")) {
                        myUser.child("name").setValue(name.getText().toString());
                    }
                    if (!phone.getText().toString().equals("Phone")) {
                        myUser.child("phone").setValue(phone.getText().toString());
                    }
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_updateDetailsFragment_to_detailsFragment, bundle);
            }
        });
        return view;
    }
}