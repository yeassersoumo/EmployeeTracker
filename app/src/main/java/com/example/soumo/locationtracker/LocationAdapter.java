package com.example.soumo.locationtracker;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationAdapterViewHolder> implements Filterable {

    private Context context;
    private List<Location> locationList;
    private List<Location> filteredLocationList;
    private LocationAdapter.LocationAdapterListener locationAdapterListener;

    public LocationAdapter(Context context, List<Location> locations, LocationAdapter.LocationAdapterListener locationAdapterListener) {
        this.context = context;
        this.locationAdapterListener = locationAdapterListener;
        this.locationList = locations;
        this.filteredLocationList = locationList;
    }

    public void setLocationData(List<Location> locationData){
        this.locationList = locationData;
        this.filteredLocationList = locationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationAdapter.LocationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.list_item_location;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediate = false;

        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediate);
        return new LocationAdapter.LocationAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationAdapterViewHolder locationAdapterViewHolder, int i) {
        final Location location = locationList.get(i);
        locationAdapterViewHolder.latTextView.setText(String.valueOf(location.getLatitude()));
        locationAdapterViewHolder.longTextView.setText(String.valueOf(location.getLongitude()));
        locationAdapterViewHolder.timeTextView.setText(String.valueOf(location.getTime()));
    }




    @Override
    public int getItemCount() {
        if (null == filteredLocationList) return 0;
        return filteredLocationList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()){
                    filteredLocationList = locationList;
                } else {
                    List<User> userList = new ArrayList<>();
                    for (User user: userList) {
                        if (user.getEmail().toLowerCase().contains(charString.toLowerCase())
                                || user.getname().toLowerCase().contains(charString.toLowerCase())) {
                            userList.add(user);
                        }
                        filteredLocationList = locationList;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredLocationList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredLocationList = (ArrayList<Location>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class LocationAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView latTextView;
        public final TextView longTextView;
        public final TextView timeTextView;

        public LocationAdapterViewHolder(View view) {
            super(view);
            latTextView = (TextView) view.findViewById(R.id.lat);
            longTextView = (TextView) view.findViewById(R.id.lon);
            timeTextView = (TextView) view.findViewById(R.id.time);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationAdapterListener.onLocationSelected(filteredLocationList.get(getAdapterPosition()));
                }
            });
        }
    }



    public interface LocationAdapterListener {
        void onLocationSelected(Location location);
    }
}
