package com.example.fixservices.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fixservices.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    EditText edEmail, edPassword;
    Button btn;
    TextView tv;

    private String uid;

    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        edEmail = view.findViewById(R.id.editTextTextEmailAddress);
        edPassword = view.findViewById(R.id.editTextContact_book);
        btn = view.findViewById(R.id.Login_bt);
        tv = view.findViewById(R.id.textNewUser);
        bundle = new Bundle();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                if (email.length() == 0 || password.length() == 0) {
                    Toast.makeText(getContext(), "Please add details", Toast.LENGTH_SHORT).show();
                } else {
                    loginFunc(email, password);
                }
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_regFragmentUser);
            }
        });

        return view;
    }

    public void loginFunc(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uid = mAuth.getUid();
                            bundle.putString("uidUser", uid);
                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Professionals/");
                            database.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
//                                        Log.d("TAG", snap.toString());
                                        if (Objects.equals(snap.getKey(), uid)) {
                                            bundle.putString("professional", "1");
                                            Log.d("TAG", "professional login");
                                        }
                                    }

//                                    FirebaseMessaging.getInstance().getToken()
//                                            .addOnCompleteListener(new OnCompleteListener<String>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<String> task) {
//                                                    if (!task.isSuccessful()) {
//                                                        Log.d("TAG", "Fetching FCM registration token failed", task.getException());
//                                                        return;
//                                                    }
//
//                                                    // Get new FCM registration token
//                                                    String token = task.getResult();
//                                                    if (bundle.getString("professional") != null) {
//                                                        FirebaseDatabase.getInstance().getReference("Professionals/" + bundle.getString("domainUser") + "/" + uid + "/token").setValue(token);
//                                                    } else {
//                                                        FirebaseDatabase.getInstance().getReference("Users/" + uid + "/token").setValue(token);
//                                                    }
//                                                }
//                                            });
                                    Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeFragment, bundle);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Error in read data from Professional", Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(requireContext(), "Successful login", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Wrong password or email", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

//@Override
//public void onMessageReceived(RemoteMessage remoteMessage) {
//    // ...
//
//    // TODO(developer): Handle FCM messages here.
//    // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//    Log.d(TAG, "From: " + remoteMessage.getFrom());
//    // Check if message contains a data payload.
//    if (remoteMessage.getData().size() > 0) {
//        Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//        if (/* Check if data needs to be processed by long running job */ true) {
//            // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//            scheduleJob();
//        } else {
//            // Handle message within 10 seconds
//            handleNow();
//        }
//    }
//
//    // Check if message contains a notification payload.
//    if (remoteMessage.getNotification() != null) {
//        Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//    }
//
//    // Also if you intend on generating your own notifications as a result of a received FCM
//    // message, here is where that should be initiated. See sendNotification method below.
//}
