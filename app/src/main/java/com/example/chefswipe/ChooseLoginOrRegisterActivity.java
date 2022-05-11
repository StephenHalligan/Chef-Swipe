package com.example.chefswipe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.chefswipe.SavedRecipes.SavedRecipesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChooseLoginOrRegisterActivity extends AppCompatActivity {

    private Button mLogin, mRegister;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(ChooseLoginOrRegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_choose_login_or_register);
        mAuth = FirebaseAuth.getInstance();


        mLogin = (Button) findViewById(R.id.Login);
        mRegister = (Button) findViewById(R.id.Register);

        //Onclick for login button
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginOrRegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Onclick for register button
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginOrRegisterActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}