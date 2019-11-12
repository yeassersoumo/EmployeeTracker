package com.example.soumo.locationtracker;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddUserActivity extends AppCompatActivity {

    String mail;
    String name;
    String phone;
    String password;

    private Spinner rolesSpinner;

    EditText userName;
    EditText userMail;
    EditText phoneNumber;
    EditText setPassword;
    private Spinner spinner;
    private Button button;
    private String selectedOrganizationId;
    private String selectedOrganizationName;
    ArrayList<Organization> organizationList = new ArrayList<>();
    ArrayList<String> organizations = new ArrayList<>();
    ArrayAdapter spinnerAdapter;




    private FirebaseAuth mAuth;
    private User user;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mOrgDatabaseReference;
    private DatabaseReference usersDatabaseReference;


    private String selectedOrganization;

    private SharedPreferences sharedPreferences;
    private String loginPassword;
    private String loginEmail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);




        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        loginEmail = sharedPreferences.getString("loginEmail",null);
        loginPassword = sharedPreferences.getString("loginPassword",null);


        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mOrgDatabaseReference = mDatabaseReference.child("organizations");

        usersDatabaseReference = mDatabaseReference.child("users");
        mAuth = FirebaseAuth.getInstance();




        //organizationList.add(new Organization("State University of Bangladesh", "sub"));
        //organizationList.add(new Organization("Lab Aid Group", "labAid"));

        userName = findViewById(R.id.userName);
        userMail = findViewById(R.id.userMail);
        phoneNumber = findViewById(R.id.phoneNumber);
        setPassword = findViewById(R.id.userPassword);

        user = new User();

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.save);

        rolesSpinner = (Spinner) findViewById(R.id.user_roles_spinner);
        rolesSpinner.setVisibility(View.GONE);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        rolesSpinner.setAdapter(adapter);







       // button.setEnabled(false);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
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

        if (sharedPreferences.getString("role","").equals("superAdmin")){
            spinner.setVisibility(View.VISIBLE);
            rolesSpinner.setVisibility(View.VISIBLE );
        }
    }

    private void singIn(){
        mAuth.signInWithEmailAndPassword(loginEmail, loginPassword);
    }

    private void addUser() {
        Log.v("SelectedRole", rolesSpinner.getSelectedItem().toString());
        if(validateInput()){
            mAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                               // Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                String uid = firebaseUser.getUid();
                                user.setUid(uid);
                                user.setEmail(mail);
                                user.setname(name);
                                user.setPhoneNumber(phone);

                                user.setRole("user");
                                user.setOrgId(sharedPreferences.getString("orgId",null));
                                user.setOrganizationName(sharedPreferences.getString("organizationName",null));
                                if (sharedPreferences.getString("role", "").equals("superAdmin")) {
                                    user.setRole(rolesSpinner.getSelectedItem().toString());
                                    user.setOrgId(selectedOrganizationId);
                                    user.setOrganizationName(selectedOrganizationName);
                                }



                                usersDatabaseReference.child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mAuth.signOut();
                                        singIn();
                                        Toast.makeText(AddUserActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                               // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(AddUserActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }


    }



    private void updateUI(FirebaseUser user) {

        if (user != null) {


            Intent intent = new Intent(this, NavigationDrawerActivity.class);
            startActivity(intent);
            finish();

        } else {


            Toast.makeText(this, "user Invalid", Toast.LENGTH_SHORT).show();


        }

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

                //button.setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }






    private boolean validateInput() {
        if (!validPassword() || !validPhone() || !validName() || !validMail()){
            return false;
        } else {
            if(validMail()){
                mail = userMail.getText().toString().trim();
            }
            if(validName()){
               name = userName.getText().toString().trim();
            }
            if(validPhone()){
                phone = phoneNumber.getText().toString().trim();
            }
            if(validPassword()){
                password = setPassword.getText().toString().trim();
            }
            return true;
        }
    }
    private boolean validName(){
        if (!userName.getText().toString().trim().isEmpty()){
            return true;
        } else {
            userName.setError("set Name");
            return false;
        }
    }
    private boolean validMail(){
        if (userMail.getText().toString().trim().matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`" +
                "{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|" +
                "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*" +
                "[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|" +
                "[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*" +
                "[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\" +
                "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")){

            return true;

        }else {
            userMail.setError("Error");
            return false;
        }


    }

    private boolean validPhone() {
        if(!phoneNumber.getText().toString().trim().isEmpty()){
            return true;
        }else {
            phoneNumber.setError("error");
            return false;
        }
    }
    private boolean validPassword(){

       if(!setPassword.getText().toString().trim().isEmpty()){
           return true;
       }else{
           if(setPassword.length()>=6){
               return true;

           }else {
               setPassword.setError("password too short");
               return false;
           }
       }
    }


    }


