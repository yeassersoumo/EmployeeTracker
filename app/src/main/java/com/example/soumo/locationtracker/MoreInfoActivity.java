package com.example.soumo.locationtracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MoreInfoActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button button;
    private String selectedOrganizationId;
    private String selectedOrganizationName;
    ArrayList<Organization> organizationList = new ArrayList<>();
    ArrayList<String> organizations = new ArrayList<>();
    ArrayAdapter spinnerAdapter;

    private DatabaseReference mDatabaseReference;
    private DatabaseReference mOrgDatabaseReference;
    private DatabaseReference usersDatabaseReference;


    private String selectedOrganization;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mOrgDatabaseReference = mDatabaseReference.child("organizations");

        usersDatabaseReference = mDatabaseReference.child("users").child(currentFirebaseUser.getUid());


        //organizationList.add(new Organization("State University of Bangladesh", "sub"));
        //organizationList.add(new Organization("Lab Aid Group", "labAid"));

        spinner = (Spinner) findViewById(R.id.spinner);
        button = (Button) findViewById(R.id.button);
        button.setEnabled(false);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedOrganizationId != null && selectedOrganizationName != null){
                    usersDatabaseReference.child("organization").setValue(selectedOrganizationId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            usersDatabaseReference.child("organizationName").setValue(selectedOrganizationName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    updateUI();
                                }
                            });
                        }
                    });

                }
            }
        });



        populatePostSpinner();

            ValueEventListener organizationListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            //Organization organization = snapshot.getValue(Organization.class);
                            //organizationList.add(organization.getOrganizationName());

                            Organization organization = new Organization();
                            organization.setOrgId(snapshot.getKey());
                            organization.setOrganizationName((String) snapshot.getValue());
                            organizationList.add(organization);

                            organizations.add((String) snapshot.getValue());

                            Log.v("lala",snapshot.getKey().toString());
                            Log.v("lala",snapshot.getValue().toString());
                        }

                        spinnerAdapter.notifyDataSetChanged();
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
        mOrgDatabaseReference.addListenerForSingleValueEvent(organizationListener);
    }

    private void populatePostSpinner() {
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, organizations);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //selectedPost = postList.get(i);
                selectedOrganizationId = organizationList.get(i).getOrgId();
                selectedOrganizationName = organizationList.get(i).getOrganizationName();
               // Log.v("org", organization.toString());

                button.setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void updateUI() {
        Intent intent = new Intent(this, NavigationDrawerActivity.class);
        startActivity(intent);
        finish();
    }
}
