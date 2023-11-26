package com.example.fixservices.adapters;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixservices.R;
import com.example.fixservices.classes.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OpenRequestsUserAdapter extends RecyclerView.Adapter<OpenRequestsUserAdapter.MyViewHolder> {
    private ArrayList<Request> dataSet;

    public OpenRequestsUserAdapter(ArrayList<Request> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_requests_user_card, parent, false);
        return new OpenRequestsUserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView textViewLocation = holder.textLocation;
        TextView textViewName = holder.textName;
        TextView textViewPhone = holder.textPhone;
        TextView textViewDescription = holder.textDescription;
        Button chooseBtn = holder.chooseBtn;
        EditText uidProfessional = holder.uidProfessional;


        textViewLocation.setText("Location: " + dataSet.get(position).getLocation());
        textViewDescription.setText("Description: " + dataSet.get(position).getDescription());
//        String description = dataSet.get(holder.getAdapterPosition()).getDescription();
        textViewName.setText("Name Professional: " + dataSet.get(position).getNameProfessional());
        textViewPhone.setText("Phone Professional: " + dataSet.get(position).getPhoneProfessional());
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataSet.get(holder.getAdapterPosition()).isProfessional()) {
                    if (uidProfessional != null) {
                        int visibility = uidProfessional.getVisibility();
                        if (visibility == INVISIBLE) {
                            uidProfessional.setVisibility(VISIBLE);
                        } else {
                            if (uidProfessional.getVisibility() == VISIBLE) {
                                if (!uidProfessional.getText().toString().equals("uidProfessional") && !uidProfessional.getText().toString().equals("")) {
                                    FirebaseDatabase.getInstance().getReference("Professionals").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChild(uidProfessional.getText().toString())) {
                                                DatabaseReference openRequests = FirebaseDatabase.getInstance().getReference("OpenRequests/" +
                                                        dataSet.get(holder.getAdapterPosition()).getUidUser() + '/' +
                                                        dataSet.get(holder.getAdapterPosition()).getDescription());
                                                DatabaseReference treatedRequests = FirebaseDatabase.getInstance().getReference("TreatedRequests/" +
                                                        dataSet.get(holder.getAdapterPosition()).getUidUser() + '/' +
                                                        dataSet.get(holder.getAdapterPosition()).getDescription());
                                                if (dataSet.get(holder.getAdapterPosition()).getNameProfessional() == null
                                                        && dataSet.get(holder.getAdapterPosition()).getPhoneProfessional() == null) {
                                                    openRequests.child("uidProfessional").setValue(uidProfessional.getText().toString());
                                                    openRequests.child("nameProfessional").setValue(snapshot.child(uidProfessional.getText().toString() + "/name").getValue(String.class));
                                                    openRequests.child("phoneProfessional").setValue(snapshot.child(uidProfessional.getText().toString() + "/phone").getValue(String.class));

                                                    openRequests.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            treatedRequests.setValue(snapshot.getValue(Request.class));
                                                            treatedRequests.child("professional").removeValue();
                                                            openRequests.removeValue();
//                                                            dataSet.remove(holder.getAdapterPosition());
                                                            Toast.makeText(holder.itemView.getContext(), "your request updated", Toast.LENGTH_SHORT).show();
                                                            uidProfessional.setVisibility(INVISIBLE);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                } else {
                                                    treatedRequests.child("uidProfessional").setValue(uidProfessional.getText().toString());
                                                    treatedRequests.child("nameProfessional").setValue(snapshot.child(uidProfessional.getText().toString() + "/name").getValue(String.class));
                                                    treatedRequests.child("phoneProfessional").setValue(snapshot.child(uidProfessional.getText().toString() + "/phone").getValue(String.class));
                                                    Toast.makeText(holder.itemView.getContext(), "your request updated", Toast.LENGTH_SHORT).show();
                                                    uidProfessional.setVisibility(INVISIBLE);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
//
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void updateData(ArrayList<Request> newData) {
        dataSet.clear();
        dataSet.addAll(newData);
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textLocation, textName, textPhone, textDescription;
        Button chooseBtn;
        EditText uidProfessional;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textLocation = itemView.findViewById(R.id.textViewLocation);
            textName = itemView.findViewById(R.id.textViewName);
            textPhone = itemView.findViewById(R.id.textViewPhone);
            textDescription = itemView.findViewById(R.id.textViewDescription);
            chooseBtn = itemView.findViewById(R.id.chooseBtn);
            uidProfessional = itemView.findViewById(R.id.editTextUidProfessional);
        }
    }
}