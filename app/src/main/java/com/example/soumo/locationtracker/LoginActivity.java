package com.example.soumo.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;

    EditText editTextName;
    EditText editTextPassword;
    Button button;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    private static final int RC_SIGN_IN = 9001;
    private String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private User user;
    private DatabaseReference mDatabase;
    private DatabaseReference usersDatabaseReference;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("userPrefs",MODE_PRIVATE);

        editTextName = findViewById(R.id.textName);
        editTextPassword = findViewById(R.id.textPassword);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);
        linearLayout = findViewById(R.id.container);

        // Set the dimensions of the sign-in button.
        //  SignInButton signInButton = findViewById(R.id.button);
        // signInButton.setSize(SignInButton.SIZE_STANDARD);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersDatabaseReference = mDatabase.child("users");

        user = new User();

        editTextName.setText("");
        editTextPassword.setText("");



        // Configure Google Sign In
     /*   GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.sign_in_button).setOnClickListener(this);*/


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    signIn();
                }
            }
        });

    }
    



  /*  private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.d("MyTAG", "onComplete: " + (isNew ? "new user" : "old user"));
                            if (isNew) {
                                String name = firebaseUser.getDisplayName();
                                String email = firebaseUser.getEmail();
                                Log.v("userInfo",email+name);
                                String uid = firebaseUser.getUid();
                                user.setUid(uid);
                                user.setname(name);
                                user.setEmail(email);
                                usersDatabaseReference.child(user.getUid()).setValue(user);

                                updateUI(firebaseUser, true);

                            } else {
                                updateUI(firebaseUser, false);
                            }


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                            updateUI(null,false);
                        }

                        // ...
                    }
                });
    } */

    private void signIn() {
       // Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent, RC_SIGN_IN);
        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        final String email= editTextName.getText().toString();
        final String password = editTextPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences.edit().putString("loginEmail",email).apply();
                            sharedPreferences.edit().putString("loginPassword",password).apply();
                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {


            Intent intent = new Intent(this, NavigationDrawerActivity.class);
            startActivity(intent);
            finish();

            } else {
            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            Toast.makeText(this, "user Invalid", Toast.LENGTH_SHORT).show();


            }

        }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                signIn();
                break;
            default:
                break;
        }
    }

    private boolean validateInput() {
        String name = editTextName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (name.isEmpty()) {
            editTextName.setError("empty field");
            return false;
        } else {
            if (name.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`" +
                    "{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|" +
                    "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*" +
                    "[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|" +
                    "[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*" +
                    "[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\" +
                    "[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {


                if (password.isEmpty()) {
                    editTextPassword.setError("empty");
                    return false;


                }else{
                    if(password.length()>=6){
                        return true;

                    }else {
                        editTextPassword.setError("password too short");
                        return false;
                    }


                }

            }else
            {
                editTextName.setError("invalid text");
            }

        }
        return false;
    }

}
