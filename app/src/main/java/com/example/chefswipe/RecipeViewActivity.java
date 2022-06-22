package com.example.chefswipe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RecipeViewActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userCookbook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipeview);
        Bundle bundle = getIntent().getExtras();

        TextView recipeName = (TextView) findViewById(R.id.recipeName);
        recipeName.setText(bundle.getString("recipeName"));


        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId);

        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userCookbook = dataSnapshot.child("Cookbook").getValue(String.class);

                assert userCookbook != null;
                CollectionReference colRef = db.collection(userCookbook);
                colRef.whereEqualTo("Name", bundle.getString("recipeName")).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {

                            ImageView recipeImage = (ImageView) findViewById(R.id.recipeImage);
                            Glide.with(getApplicationContext()).load(document.getString("URL")).into(recipeImage);

                            TextView veganTag = (TextView) findViewById(R.id.veganTag);
                            TextView vegetarianTag = (TextView) findViewById(R.id.vegetarianTag);
                            TextView glutenFreeTag = (TextView) findViewById(R.id.glutenFreeTag);

                            if (Objects.requireNonNull(document.getString("Tags")).contains("Vegetarian")) {
                                vegetarianTag.setVisibility(View.VISIBLE);
                            }
                            if (Objects.requireNonNull(document.getString("Tags")).contains("Vegan")) {
                                veganTag.setVisibility(View.VISIBLE);
                            }
                            if (Objects.requireNonNull(document.getString("Tags")).contains("Gluten Free")) {
                                glutenFreeTag.setVisibility(View.VISIBLE);
                            }

                            String ingredients = document.getString("Ingredients");
                            String[] ingredientsArr = Objects.requireNonNull(ingredients).split(";@", 15);
                            TextView ingredientsView = (TextView) findViewById(R.id.ingredientsView);
                            ingredientsView.setSingleLine(false);
                            for (String s : ingredientsArr) {
                                ingredientsView.append("• " + s + "\n\n");
                            }

                            String method = document.getString("Method");
                            String[] methodArr = Objects.requireNonNull(method).split(";@", 15);
                            TextView methodView = (TextView) findViewById(R.id.methodView);
                            methodView.setSingleLine(false);
                            for (String s : methodArr) {
                                methodView.append("• " + s + "\n\n");
                            }

                        }

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Recipe read failed: " + databaseError.getCode());
            }

        });
    }
}
