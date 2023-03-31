package com.example.chefswipe.LoginRegistry;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chefswipe.MainActivity;
import com.example.chefswipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText mName, mEmail, mPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get auth
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            //Check if user is logged in, and launches MainActivity
            if (user != null) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        };

        Button mRegister = (Button) findViewById(R.id.Register);

        mName = (EditText) findViewById(R.id.Name);
        mEmail = (EditText) findViewById(R.id.Email);
        mPassword = (EditText) findViewById(R.id.Password);

        //Register user
        mRegister.setOnClickListener(view -> {
            final String name = mName.getText().toString();
            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {

                //Check is registration fails
                if(!task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Sign-up Error", Toast.LENGTH_SHORT).show();
                }

                //If registration is successful
                else {
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Name");
                    currentUserDb.setValue(name);
                    currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Bio");
                    currentUserDb.setValue("Default bio");
                    currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Friends");
                    currentUserDb.setValue(0);
                }

            });
        });

    }


    //Kills & creates authstatelistener

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

}