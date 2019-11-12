package com.example.soumo.locationtracker;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class EditUserActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    Spinner spinner;

    private DatabaseReference mDatabase;
    private DatabaseReference usersDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Bundle data = getIntent().getExtras();
        User  user = (User) data.getParcelable("user");

        Log.v("user: ", user.toString());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersDatabaseReference = mDatabase.child("users").child(user.getUid());


        spinner = (Spinner) findViewById(R.id.spinner_role);


        editText = findViewById(R.id.edit_name);
        button = findViewById(R.id.buttonSave);

        editText.setText(user.getname());




// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.role_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editUser();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            usersDatabaseReference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        showMessege(EditUserActivity.this);
                        finish();
                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void showMessege(Context context) {

        Toast.makeText(context, "Delete Successful", Toast.LENGTH_SHORT).show();


    }

    private void editUser() {
        String name = editText.getText().toString().trim();
        String role = spinner.getSelectedItem().toString();

        if(name!=null && role!=null){
            Toast.makeText(this, "name: "+ name + "role: " + role, Toast.LENGTH_SHORT).show();

            // Create a HashMap object called capitalCities
            HashMap<String, Object> userData = new HashMap<>();

            // Add keys and values (Country, City)
            userData.put("name", name);
            userData.put("role", role);
            usersDatabaseReference.updateChildren(userData);
        }

    }
}
