package com.example.fixservices.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.example.fixservices.R;
import com.example.fixservices.classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private List<User> userList; // Use a list to hold multiple users
    private int num = 0;


    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size(); // Return the number of users in the list
    }

    @Override
    public User getItem(int position) {
        return userList.get(position); // Get the user at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position; // Return the position as the item ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.user_card_layout, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.textViewName);
        TextView phoneTextView = convertView.findViewById(R.id.textViewPhone);
        TextView numberTextView = convertView.findViewById(R.id.textViewRequests);
        User user = getItem(position); // Get the user at the current position
        if (user != null) {
            nameTextView.setText("Name: " + user.getName());
            phoneTextView.setText("Phone: " + user.getPhone());
            FirebaseDatabase.getInstance().getReference("OpenRequests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(user.getUidUser())) {
                        Log.d("TAG", user.getUidUser());
                        num = num + (int) snapshot.child(user.getUidUser()).getChildrenCount();
                        Log.d("TAG", String.valueOf(num));
                    }
                    numberTextView.setText("Open Requests: " + num);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ERROR", "problem read data from OpenRequests");
                }
            });

            FirebaseDatabase.getInstance().getReference("TreatedRequests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(user.getUidUser())) {
                        num = num + (int) snapshot.child(user.getUidUser()).getChildrenCount();
                        Log.d("TAG", String.valueOf(num));
                    }
                    numberTextView.setText("Open Requests: " + num);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ERROR", "problem read data from TreatedRequests");
                }
            });
        }
        return convertView;
    }

    // Add a method to set the data in the adapter
    public void setData(List<User> userList) {
        this.userList = userList;
    }
}
