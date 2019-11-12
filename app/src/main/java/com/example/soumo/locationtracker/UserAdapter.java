package com.example.soumo.locationtracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder> implements Filterable {

    private Context context;
    private List<User> userList;
    private List<User> filteredUserList;
    private UserAdapterListener userAdapterListener;

    public UserAdapter(Context context, List<User> users, UserAdapterListener userAdapterListener) {
        this.context = context;
        this.userAdapterListener = userAdapterListener;
        this.userList = users;
        this.filteredUserList = userList;
    }

    public void setUserData(List<User> userData){
        this.userList = userData;
        this.filteredUserList = userList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutIdForListItem = R.layout.list_item_user;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediate = false;

        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediate);
        return new UserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapterViewHolder userAdapterViewHolder, int i) {
        final User user = userList.get(i);
        userAdapterViewHolder.userNameTextView.setText(user.getname());
        userAdapterViewHolder.userOrganizationTextView.setText(user.getEmail());


    }

    @Override
    public int getItemCount() {
        if (null == filteredUserList) return 0;
        return filteredUserList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()){
                    filteredUserList = userList;
                } else {
                    List<User> userList = new ArrayList<>();
                    for (User user: userList) {
                        if (user.getEmail().toLowerCase().contains(charString.toLowerCase())
                        || user.getname().toLowerCase().contains(charString.toLowerCase())) {
                            userList.add(user);
                        }
                        filteredUserList = userList;
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredUserList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredUserList = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class UserAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView userNameTextView;
        public final TextView userOrganizationTextView;

        public UserAdapterViewHolder(View view) {
            super(view);
            userNameTextView = (TextView) view.findViewById(R.id.user_name);
            userOrganizationTextView = (TextView) view.findViewById(R.id.user_organization);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userAdapterListener.onUserSelected(filteredUserList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface UserAdapterListener {
        void onUserSelected(User user);
    }
}
