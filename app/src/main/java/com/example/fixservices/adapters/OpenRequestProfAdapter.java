package com.example.fixservices.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixservices.R;
import com.example.fixservices.classes.Request;

import java.util.ArrayList;

public class OpenRequestProfAdapter extends RecyclerView.Adapter<OpenRequestProfAdapter.MyViewHolder> {

    private ArrayList<Request> dataSet;
    private int flag = 0;
    private OnItemClickListener listener;

    public OpenRequestProfAdapter(ArrayList<Request> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public OpenRequestProfAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_request_prof_card, parent, false);
        return new OpenRequestProfAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenRequestProfAdapter.MyViewHolder holder, int position) {
        TextView textViewLocation = holder.textLocation;
        TextView textViewName = holder.textName;
        TextView textViewPhone = holder.textPhone;
        TextView textViewDescription = holder.textDescription;
        Button doneBtn = holder.doneBtn;

//        doneBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("TAG", "done clicked");
//                FirebaseDatabase.getInstance().getReference().child("TreatedRequests").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            for (DataSnapshot snapUser : snapshot.getChildren()) {
//                                if (Objects.equals(snapUser.getKey(), dataSet.get(holder.getAdapterPosition()).getUidUser())) {
//                                    for (DataSnapshot snapDesc : snapUser.getChildren()) {
//                                        if (Objects.equals(snapDesc.getKey(), dataSet.get(holder.getAdapterPosition()).getDescription())) {
//                                            Request request = snapDesc.getValue(Request.class);
//                                            FirebaseDatabase.getInstance().getReference().child("CloseRequests/" +
//                                                    dataSet.get(holder.getAdapterPosition()).getUidUser() + "/" +
//                                                    dataSet.get(holder.getAdapterPosition()).getDescription()).setValue(request);
//                                            FirebaseDatabase.getInstance().getReference().child("CloseRequests/" +
//                                                    dataSet.get(holder.getAdapterPosition()).getUidUser() + "/" +
//                                                    dataSet.get(holder.getAdapterPosition()).getDescription() + "/professional").removeValue();
//                                            flag = 1;
//                                        } else {
//                                            Toast.makeText(holder.itemView.getContext(), "There is not this request", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//                if (flag == 1) {
//                    Log.d("TAG", "successful");
////                    FirebaseDatabase.getInstance().getReference("CloseRequests/"+
////                            dataSet.get(holder.getAdapterPosition()).getUidUser() + "/" +
////                            dataSet.get(holder.getAdapterPosition()).getDescription()).removeValue();
//                }
//            }
//        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDoneClick(dataSet.get(holder.getAdapterPosition()));
                }
            }
        });

        Button presentBtn = holder.presentBtn;
        presentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onPresentClick(dataSet.get(holder.getAdapterPosition()));
                }
            }
        });
//        presentBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("uidRequest", dataSet.get(holder.getAdapterPosition()).getUidUser());
//                bundle.putString("descriptionRequest", dataSet.get(holder.getAdapterPosition()).getDescription());
//                Navigation.findNavController(v).navigate(R.id.action_myRequestFragment_to_presentRequestFragment, bundle);
//            }
//        });

        textViewLocation.setText("Location: " + dataSet.get(holder.getAdapterPosition()).getLocation());
        textViewDescription.setText("Description: " + dataSet.get(holder.getAdapterPosition()).getDescription());
        String description = dataSet.get(holder.getAdapterPosition()).getDescription();
        textViewName.setText("Name User: " + dataSet.get(holder.getAdapterPosition()).getNameUser());
        textViewPhone.setText("Phone User: " + dataSet.get(holder.getAdapterPosition()).getPhoneUser());
    }

    public OnItemClickListener getListener() {
        return listener;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public interface OnItemClickListener {
        void onDoneClick(Request request);
        void onPresentClick(Request request);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textLocation, textName, textPhone, textDescription;
        Button doneBtn, presentBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textLocation = itemView.findViewById(R.id.textViewLocation);
            textName = itemView.findViewById(R.id.textViewName);
            textPhone = itemView.findViewById(R.id.textViewPhone);
            textDescription = itemView.findViewById(R.id.textViewDescription);
            doneBtn = itemView.findViewById(R.id.doneBtn);
            presentBtn = itemView.findViewById(R.id.presentBtn);
        }
    }
}