package com.example.chefswipe.LoginRegistry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chefswipe.MainActivity;
import com.example.chefswipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //Check if user is logged in
            if (user != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        };

        //Buttons for login and register
        Button mLogin = (Button) findViewById(R.id.Login);
        Button mRegister = (Button) findViewById(R.id.Register);

        //Edittext for email and pass
        mEmail = (EditText) findViewById(R.id.Email);
        mPassword = (EditText) findViewById(R.id.Password);

        //Login user
        mLogin.setOnClickListener(view -> {
            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                //Check is registration fails
                if(!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Sign-in Error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        //Register button
        mRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

    }

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
