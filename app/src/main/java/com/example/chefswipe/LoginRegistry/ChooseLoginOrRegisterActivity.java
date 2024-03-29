package com.example.chefswipe.LoginRegistry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.chefswipe.MainActivity;
import com.example.chefswipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChooseLoginOrRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //If user is already logged in, launch the main activity
        if (user != null) {
            Intent intent = new Intent(ChooseLoginOrRegisterActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
            return;
        }

        //Set xml layout
        setContentView(R.layout.activity_choose_login_or_register);

        //Create login and register buttons
        Button mLogin = (Button) findViewById(R.id.Login);
        Button mRegister = (Button) findViewById(R.id.Register);

        //Onclick for login button
        mLogin.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseLoginOrRegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        //Onclick for register button
        mRegister.setOnClickListener(view -> {
            Intent intent = new Intent(ChooseLoginOrRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });
    }

}