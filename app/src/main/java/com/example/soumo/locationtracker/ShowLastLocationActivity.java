package com.example.soumo.locationtracker;

import android.content.Intent;
import android.location.Location;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ShowLastLocationActivity extends AppCompatActivity implements LocationAdapter.LocationAdapterListener {


    LocationAdapter locationAdapter;

    List<Location> locationList;

    private DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_last_location);

        Bundle data = getIntent().getExtras();
        User  user = (User) data.getParcelable("user");
        Log.v("lalal", user.toString());

        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("locations");

        locationList = new ArrayList<>();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_location);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        locationAdapter = new LocationAdapter(this, locationList, this);

        recyclerView.setAdapter(locationAdapter);


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    CustomLocation customLocation = dataSnapshot.getValue(CustomLocation.class);
                    Log.v("Locations: ", customLocation.toString());
                    Location location = new Location("a");
                    location.setLatitude(customLocation.getLatitude());
                    location.setLongitude(customLocation.getLongitude());
                    location.setTime(customLocation.getTime());





                    locationList.add(location);
                    Log.v("Locations:", locationList.toString());
                    locationAdapter.notifyDataSetChanged();
                } else {
                    Log.v("lala", "No snapshot");

                }

            }



            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        };
        usersDatabaseReference.addChildEventListener(childEventListener);
    }

    @Override
    public void onLocationSelected(Location location) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }
}
