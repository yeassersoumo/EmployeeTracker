package com.example.soumo.locationtracker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String TAG = "MapsActivity";
    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private DatabaseReference usersLocationDatabaseReference;
    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    public boolean mLocationPermissionGranted;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM=15;
    private final LatLng mDefaultLocation=new LatLng(24.878737, 89.372324);
    private static final String KEY_LOCATION="location";
    private static final String KEY_CAMERA_POSITION="camera_position";
    private CameraPosition mCameraPosition;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle data = getIntent().getExtras();
        Location  location = (Location) data.getParcelable("location");
        mLastKnownLocation = location;
        Log.v("LastKnownLocation: " +
                "", String.valueOf(location.getLatitude())
                + String.valueOf(location.getLongitude()));


        if(savedInstanceState != null){
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            // User is signed in
        } else {
            // No user is signed in
        }
        usersLocationDatabaseReference = mDatabase.child("users/" + uid + "/location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

        if (getLocationPermission()) {
           Toast.makeText(this, "Allowed", Toast.LENGTH_SHORT).show();
        }else {
           Toast.makeText(this, "Not Allowed", Toast.LENGTH_SHORT).show();
       }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        updateLocationUI();
        //updateLocationDatabase();
        getDeviceLocation();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.log_out:

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

   private boolean getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        return false;
    }

    private void getDeviceLocation() {
        LatLng userLocation = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Marker in UserLocation"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
    }

//    private void updateLocationDatabase() {
//        if (mMap == null) {
//            return;
//        }
//        try {
//            if (mLocationPermissionGranted) {
//                usersLocationDatabaseReference.setValue();
//            } else {
//                mLastKnownLocation = null;
//                getLocationPermission();
//            }
//        } catch (SecurityException e)  {
//            Log.e("Exception: %s", e.getMessage());
//        }
//    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
