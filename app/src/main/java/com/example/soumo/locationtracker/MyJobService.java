package com.example.soumo.locationtracker;

import android.Manifest;
import android.app.Activity;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends JobService {
    private static final String TAG = "ExampleJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            private FusedLocationProviderClient mFusedLocationProviderClient;
            public boolean mLocationPermissionGranted;
            private Location mLastKnownLocation;
            Context context;

            @Override
            public void run() {
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                context = getApplicationContext();
                try {
                    getLocationPermission();
                    if (mLocationPermissionGranted) {
                        Task locationResult = mFusedLocationProviderClient.getLastLocation();
                        locationResult.addOnCompleteListener((Activity) context, new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    // Set the map's camera position to the current location of the device.
                                    mLastKnownLocation = (Location) task.getResult();
                                     Log.v("LastLocation", mLastKnownLocation.toString());
                                    //usersLocationDatabaseReference.push().setValue(mLastKnownLocation);
                                } else {
                                    Log.v("lslsl","Unsuccess");
                                    Log.d(TAG, "Current location is null. Using defaults.");
                                    Log.e(TAG, "Exception: %s", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "No permission");
                    }
                } catch(SecurityException e)  {
                    Log.e("Exception: %s", e.getMessage());
                }

                Log.d(TAG, "Doing Task");
                if (jobCancelled) {
                    return;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }

            private boolean getLocationPermission() {
                /*
                 * Request location permission, so that we can get the location of the
                 * device. The result of the permission request is handled by a callback,
                 * onRequestPermissionsResult.
                 */
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    return true;
                } else {
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
                return false;
            }

        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = false;
        return true;
    }
}
