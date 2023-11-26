package com.example.fixservices.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fixservices.R;
import com.example.fixservices.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/** Register a new user to application **/
public class RegUserFragment extends Fragment {
    private FirebaseAuth mAuth;
    private String name, phone, password, confirm_password, email;
    private User user;
    private DatabaseReference myRefReq;
    private EditText edName, edPhone, edEmail, edPassword, edConfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reg_user, container, false);
        edName = view.findViewById(R.id.editTextName);
        edPhone = view.findViewById(R.id.editTextPhone);
        edEmail = view.findViewById(R.id.editTextEmail);
        edPassword = view.findViewById(R.id.editTextPassword);
        edConfirm = view.findViewById(R.id.editTextConfirm);
        Button singInBtn = view.findViewById(R.id.singInProf);
        Button profBtn = view.findViewById(R.id.buttonProfessional);
        Button loginBtn = view.findViewById(R.id.login);

        singInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edName.getText().toString();
                phone = edPhone.getText().toString();
                email = edEmail.getText().toString();
                password = edPassword.getText().toString();
                confirm_password = edConfirm.getText().toString();
                if (name.length() == 0 || password.length() == 0 || email.length() == 0 || confirm_password.length() == 0
                        || phone.length() == 0) {
                    Toast.makeText(getContext(), "Please add all the details", Toast.LENGTH_SHORT).show();
                } else {
                    if(password.length() < 6){
                        Toast.makeText(requireContext(), "Please enter password with least 6 chars", Toast.LENGTH_SHORT).show();
                    } else {
                        if (password.compareTo(confirm_password) == 0) {
                            regFunc(email, password, name, phone);
                            Navigation.findNavController(v).navigate(R.id.action_regFragmentUser_to_loginFragment);
                        } else {
                            Toast.makeText(getContext(), "The password does not match to confirm", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        profBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_regFragmentUser_to_regProfessionalFragment);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_regFragmentUser_to_loginFragment);
            }
        });

        return view;
    }


    public void regFunc(String email, String password, String name, String phone) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRefReq = database.getReference("Users");
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getUid();
                            user = new User(name, phone);
                            assert uid != null;
                            myRefReq.child(uid).setValue(user);
                            Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_LONG).show();
                        }
                    }
            });
    }
}
