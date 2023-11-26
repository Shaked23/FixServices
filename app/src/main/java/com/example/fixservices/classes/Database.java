package com.example.fixservices.classes;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fixservices.adapters.OpenRequestProfAdapter;
import com.example.fixservices.adapters.OpenRequestsUserAdapter;
import com.example.fixservices.adapters.PriceAdapter;
import com.example.fixservices.adapters.ProfListViewAdapter;
import com.example.fixservices.adapters.UserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** class with functions on Firebase Database **/
public class Database {
    /** print price list on Price Fragment from Firebase realtime database **/
    public static void setupRecyclerViewPrice(Context context, RecyclerView recyclerView) {
        ArrayList<Item> listPrice = new ArrayList<>();
        PriceAdapter adapter = new PriceAdapter(listPrice);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference("Prices").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPrice.clear();
                listPrice.add(new Item("Name", "Description", "Price"));
                if (snapshot.exists()) {
                    for (DataSnapshot snapDomain : snapshot.getChildren()) {
                        Item item = new Item("", "", snapDomain.getKey());
                        listPrice.add(item);
                        for (DataSnapshot snapName : snapDomain.getChildren()) {
                            if (snapName.getChildrenCount() > 1) {
                                if(snapName.hasChild("איכות-היקף") && snapName.hasChild("מחיר")){
                                    item = new Item(snapName.getKey(), snapName.child("איכות-היקף").getValue(String.class),
                                            snapName.child("מחיר").getValue(String.class));
                                    listPrice.add(item);
                                    adapter.setDataSet(listPrice);
                                }
                            } else {
                                    item = new Item(snapName.getKey(), "", snapName.getValue(String.class));
                                    listPrice.add(item);
                                    adapter.setDataSet(listPrice);
                                }
                            }
                        }
                    }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "problem read data from Prices");
            }
        });

    }

    /** print details of the user on Details Fragment from Firebase realtime database **/
    public static void setupListViewUser(Context context, ListView listView, DatabaseReference databaseReference) {
        ArrayList<User> users = new ArrayList<>();
        UserAdapter adapter = new UserAdapter(context, users);
        listView.setAdapter(adapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        user.setUidUser(snapshot.getKey());
                        List<User> userList = new ArrayList<>();
                        userList.add(user); // Add the user to the list
                        adapter.setData(userList); // Set the data in the adapter
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** print details of the professional on Details Fragment from Firebase realtime database **/
    public static void setupListViewProfessional(Context context, ListView listView, DatabaseReference databaseReference) {
        ArrayList<Professional> professionals = new ArrayList<>();
        ProfListViewAdapter adapter = new ProfListViewAdapter(context, professionals);
        listView.setAdapter(adapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Professional professional = snapshot.getValue(Professional.class);
                    if (professional != null) {
                        professional.setUID(snapshot.getKey());
                        ArrayList<Professional> professionalList = new ArrayList<>();
                        professionalList.add(professional); // Add the professional to the list
                        adapter.setDataSet(professionalList); // Set the data in the adapter
                    }
                }
                adapter.notifyDataSetChanged(); // Notify the adapter of data changes
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** print requests on My Requests Fragment from Firebase realtime database **/
    public static void setupRecyclerViewRequests(Context context, RecyclerView recyclerView, String uid, boolean isProfessional, OpenRequestProfAdapter.OnItemClickListener listener) {
        ArrayList<Request> openRequestsList = new ArrayList<>();
        ArrayList<Request> treatedRequestsList = new ArrayList<>();

        if (isProfessional) {
            OpenRequestProfAdapter adapter = new OpenRequestProfAdapter(treatedRequestsList);
            adapter.setListener(listener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
            FirebaseDatabase.getInstance().getReference("TreatedRequests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    treatedRequestsList.clear();
                    if (snapshot.exists()) {
                        for (DataSnapshot snapUser : snapshot.getChildren()) {
                            for (DataSnapshot snapDesc : snapUser.getChildren()) {
                                if (Objects.equals(snapDesc.child("uidProfessional").getValue(String.class), uid)) {
                                    Request request = snapDesc.getValue(Request.class);
                                    if (request != null) {
                                        request.setDescription(snapDesc.getKey());
                                        request.setUidUser(snapUser.getKey());
                                        request.setProfessional(true);
                                        treatedRequestsList.add(request);
                                    }
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ERROR", "problem read data from TreatedRequests");
                }
            });
        } else {
            ArrayList<Request> combinedList = new ArrayList<>();
            OpenRequestsUserAdapter adapter = new OpenRequestsUserAdapter(combinedList);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
            FirebaseDatabase.getInstance().getReference("OpenRequests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    openRequestsList.clear();
                    if (snapshot.exists() && snapshot.hasChild(uid)) {
                        for (DataSnapshot snapDesc : snapshot.child(uid).getChildren()) {
                            Request request = snapDesc.getValue(Request.class);
                            if (request != null) {
                                request.setDescription(snapDesc.getKey());
                                request.setUidUser(uid);
                                openRequestsList.add(request);
                            }
                        }
                        updateAdapterData(openRequestsList, treatedRequestsList, adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ERROR", "problem read data from OpenRequests");
                }
            });

            FirebaseDatabase.getInstance().getReference("TreatedRequests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    treatedRequestsList.clear();
                    if (snapshot.exists() && snapshot.hasChild(uid)) {
                        for (DataSnapshot snapDesc : snapshot.child(uid).getChildren()) {
                            Request request = snapDesc.getValue(Request.class);
                            if (request != null) {
                                request.setDescription(snapDesc.getKey());
                                request.setUidUser(uid);
                                treatedRequestsList.add(request);
                            }
                        }
                        updateAdapterData(openRequestsList, treatedRequestsList, adapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ERROR", "problem read data from TreatedRequests");
                }
            });
        }
    }

    private static void updateAdapterData(ArrayList<Request> openRequestsList, ArrayList<Request> treatedRequestsList, OpenRequestsUserAdapter adapter) {
        // Combine data from both lists and update the adapter
        if (adapter != null) {
            ArrayList<Request> combinedList = new ArrayList<>();
            combinedList.addAll(openRequestsList);
            combinedList.addAll(treatedRequestsList);
            adapter.updateData(combinedList);
        } else {
            Log.e("RequestsManager", "Adapter is null");
        }
    }
}
