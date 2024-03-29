package com.example.chefswipe.SavedRecipes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.chefswipe.CreateActivity;
import com.example.chefswipe.ProfileViewActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.chefswipe.MainActivity;
import com.example.chefswipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SavedRecipesActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter mSavedAdapter;

    //Init db
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String currentUserID;

    String userCookbook;
    String recipeURL;
    String recipeName;

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set xml layout
        setContentView(R.layout.activity_saved_recipes);

        //Create bottom nav
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.saved);

        //Init firebase auth and get user id
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //Init recyclerview
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mSavedLayoutManager = new LinearLayoutManager(SavedRecipesActivity.this);
        mRecyclerView.setLayoutManager(mSavedLayoutManager);
        mSavedAdapter = new SavedAdapter(getDataSetSavedRecipes(), SavedRecipesActivity.this);
        mRecyclerView.setAdapter(mSavedAdapter);

        getSavedRecipeID();

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        //Create listener on ref for database
        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCookbook = dataSnapshot.child("Cookbook").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getSavedRecipeID() {
        //Get saved recipes
        DatabaseReference savedDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Saved Recipes");
        savedDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Cycle through saved recipes list and get key
                    for (DataSnapshot savedRecipe : dataSnapshot.getChildren()) {
                        FetchSavedRecipeInformation(savedRecipe.getKey());
                    }
                }
                //If saved recipes dont exist, display textview
                else if (!dataSnapshot.exists()) {
                    TextView noRecipes = (TextView) findViewById(R.id.noRecipes);
                    noRecipes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void FetchSavedRecipeInformation(String key) {

        DatabaseReference savedDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Saved Recipes").child(key);
        savedDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String recipeID = snapshot.getKey();
                    //If saved recipe exists, look for it in firestore documents
                    CollectionReference colRef = db.collection("Cookbook");
                    colRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //If saved recipe id matches document id, set vars and display it
                                if (document.getId().equals(recipeID)) {
                                    recipeURL = document.getString("URL");
                                    recipeName = document.getString("Name");

                                    SavedObject obj = new SavedObject(recipeID, recipeURL, recipeName);
                                    //Add recipe to list
                                    resultsSavedRecipes.add(obj);

                                    //Tell saved adapter to update
                                    mSavedAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private final ArrayList<SavedObject> resultsSavedRecipes = new ArrayList<>();
    private List<SavedObject> getDataSetSavedRecipes() {
        return resultsSavedRecipes;
    }

    //Bottom nav bar
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(SavedRecipesActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;

            case R.id.saved:
                return true;

            case R.id.create:
                intent = new Intent(SavedRecipesActivity.this, CreateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;

            case R.id.profile:
                intent = new Intent(SavedRecipesActivity.this, ProfileViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
        }

        return false;
    }

}
