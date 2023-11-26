package com.example.fixservices.fragments;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegProfessionalFragment extends Fragment {
    private Spinner spinnerDomain;
    private EditText nameET, phoneET, experienceET, emailET, passwordET, confirm_passwordET;
    private FirebaseAuth mAuth;
    private Professional professional;
    private DatabaseReference myRefPro;
    private String name, password, email, phone, domain, confirm_password;
    private int experience;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reg_professional, container, false);
        // Assuming you have an array of suggestions
        String[] domains = {"Select Domain", "Installation", "Electric", "Sealing", "Flooring", "Dyeing", "Renovation"};

        spinnerDomain = view.findViewById(R.id.spinnerDomain);
        nameET = view.findViewById(R.id.editTextName);
        phoneET = view.findViewById(R.id.editTextPhone);
        experienceET = view.findViewById(R.id.editTextExperience);
        emailET = view.findViewById(R.id.editTextEmail);
        passwordET = view.findViewById(R.id.editTextPassword);
        confirm_passwordET = view.findViewById(R.id.editTextConfirm);
        Button singInBtn = view.findViewById(R.id.singInProf);
        Button userBtn = view.findViewById(R.id.userBtn);
        Button loginBtn = view.findViewById(R.id.login);

        ArrayAdapter<String> adapterDomain = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, domains);
        adapterDomain.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDomain.setAdapter(adapterDomain);
        singInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameET.getText().toString();
                phone = phoneET.getText().toString();
                domain = spinnerDomain.getSelectedItem().toString();
                email = emailET.getText().toString();
                password = passwordET.getText().toString();
                confirm_password = confirm_passwordET.getText().toString();
                if (name.length() == 0 || password.length() == 0 || email.length() == 0 || confirm_password.length() == 0
                        || phone.length() == 0 || domain.equals("Select Domain") || experienceET.getText().toString().length()==0) {
                    Toast.makeText(getContext(), "Please add details", Toast.LENGTH_SHORT).show();
                } else {
                    if(password.length() < 6){
                        Toast.makeText(requireContext(), "Please enter password with least 6 chars", Toast.LENGTH_SHORT).show();
                    } else {
                        if (password.compareTo(confirm_password) == 0) {
                            regFunc(email, password, name, phone, domain, experience);
                            Navigation.findNavController(v).navigate(R.id.action_regProfessionalFragment_to_loginFragment);
                        } else {
                            Toast.makeText(getContext(), "Please check your details", Toast.LENGTH_SHORT).show();
                        }
                    }
                    experience = Integer.parseInt(experienceET.getText().toString());
                }
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_regProfessionalFragment_to_regFragmentUser);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_regProfessionalFragment_to_loginFragment);
            }
        });

        return view;
    }

    public void regFunc(String email, String password, String name, String phone, String domain, int experience) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRefPro = database.getReference("Professionals");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getUid();
                            assert uid != null;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String currentDate = sdf.format(new Date());
                            professional = new Professional(name, phone, domain, experience, 0);
                            professional.setDateCreate(currentDate);
                            myRefPro.child(uid).setValue(professional);
                            professional.setUID(uid);
                            Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}