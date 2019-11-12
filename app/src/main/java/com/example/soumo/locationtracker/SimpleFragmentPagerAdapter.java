package com.example.soumo.locationtracker;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SimpleFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm){
        super(fm);
        mContext = context;

    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return new MapsFragment();
        }else {
            return new MapsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
    @Override
    public CharSequence getPageTitle(int i){

        switch (i){
            case 0:
            return "Map";
            default:
                return null;

        }

    }
}
