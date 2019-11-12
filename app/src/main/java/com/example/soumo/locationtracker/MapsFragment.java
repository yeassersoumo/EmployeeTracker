package com.example.soumo.locationtracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapsFragment extends Fragment {
    public MapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.activity_maps, container, false);
        return rootView;

    }

    @Override
    public void onStop(){
        super.onStop();
    }
}
