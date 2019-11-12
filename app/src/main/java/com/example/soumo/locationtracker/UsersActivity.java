package com.example.soumo.locationtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.soumo.locationtracker.EditUserActivity.showMessege;

public class UsersActivity extends AppCompatActivity implements UserAdapter.UserAdapterListener {

    private String uid;

    UserAdapter userAdapter;

    List<User> userList;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference usersDatabaseReference;

    SwipeRefreshLayout swipeRefreshLayout;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddUser();
            }
        });

        userList = new ArrayList<>();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        usersDatabaseReference = mDatabaseReference.child("users");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_users);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        userAdapter = new UserAdapter(this, userList, this);

        recyclerView.setAdapter(userAdapter);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
             uid = firebaseUser.getUid();
            // User is signed in
        } else {
            // No firebaseUser is signed in
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Log.v("lalllal: ", dataSnapshot.getValue().toString());
                    String setting = sharedPreferences.getString("orgId", "");
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getOrgId().equals(setting)  && !user.getUid().equals(uid)){
                        userList.add(user);

                    } else if (user.getRole().equals("admin") && !user.getUid().equals(uid)
                    && sharedPreferences.getString("role", "").equals("superAdmin")) {
                        userList.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                } else {
                    Log.v("lala", "No snapshot");

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                userList.clear();
                User user = dataSnapshot.getValue(User.class);
                Log.d("ChangeUser", user.toString());
                userList.add(user);
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User removedUser = dataSnapshot.getValue(User.class);
                for (User user: userList) {
                    if (user.getUid().equals(removedUser.getUid())) {
                        userList.remove(user);
                        userAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                Log.d("UserList", userList.toString());
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


     /**   ValueEventListener userListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        //organizationList.add(organization.getOrganizationName());

                        userList.add(user);

                       // Log.v("lala",user.getname());
                    }

                    userAdapter.notifyDataSetChanged();
                } else {
                    Log.v("lala", "No snapshot");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        usersDatabaseReference.addValueEventListener(userListener); **/
    }

    private void openAddUser() {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
    }


    @Override
    public void onUserSelected(final User user) {
        //creating a popup menu
        PopupMenu popup = new PopupMenu(this, findViewById(R.id.list_item_user));
        //inflating menu from xml resource
        popup.inflate(R.menu.popup_menu);
        //adding click listener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ShowLastLocation: updateUI(1, user);
                        break;
                  case R.id.edit: updateUI(2, user);
                        //handle menu2 click
                     break;
                   case R.id.delete:
                      //handle menu3 click
                       usersDatabaseReference.child(user.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){
                                   showMessege(UsersActivity.this);
                               }
                           }
                       });
                       break;


                }
                return false;
            }
        });
        //displaying the popup
        popup.show();

    }

    private void updateUI(int id, User user) {
        if (id == 1) {
            Intent intent = new Intent(this, ShowLastLocationActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
        if (id == 2) {
            Intent intent = new Intent(this, EditUserActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }
}
