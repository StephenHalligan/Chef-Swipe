package com.example.chefswipe;

import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.chefswipe.SavedRecipes.SavedRecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chefswipe.Cards.Cards;
import com.example.chefswipe.Cards.arrayAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Chef Swipe";
    private Cards Item;

    private DatabaseReference userDb;

    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    //Creating array adapter from arrayAdapter class
    private com.example.chefswipe.Cards.arrayAdapter arrayAdapter;

    //Need to initialize index on registration **IMPORTANT**
    String userCookbook;

    String dbRecipeName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Cards> rowItems;

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.home);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        //Create arraylist from Cards
        rowItems = new ArrayList<Cards>();

        //Choose adapter
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        //set the listener and the adapter
        flingContainer.setAdapter(arrayAdapter);

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild("Diet Filter")) userDb.child("Diet Filter").setValue("None");

                userCookbook = dataSnapshot.child("Cookbook").getValue(String.class);

                TextView cookbookTitle = (TextView) findViewById(R.id.cookbookTitle);
                cookbookTitle.setText(userCookbook);

                getRecipe();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Recipe read failed: " + databaseError.getCode());
            }

        });

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {
                //Delete an object from the Adapter (/AdapterView)
                rowItems.remove(0);

                Log.d("LIST", "removed object!");

            }

            //Called when adapter is almost empty
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                updateArrayAdapter();
                Log.d("Items in adapter", valueOf(itemsInAdapter));
            }

            //Called when card is swiped to left
            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                dbRecipeName = obj.getRecipeName();
                userDb.child("Saved Recipes").child(dbRecipeName).removeValue();
                updateArrayAdapter();
            }

            //Called when card is swiped to right
            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                dbRecipeName = obj.getRecipeName();
                userDb.child("Saved Recipes").child(obj.getRecipeName()).setValue(true);
                updateArrayAdapter();
            }

            @Override
            public void onScroll(float v) {
            }

        });

        //OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {

        });

    }


    //Updates array adapter & changes visibility of tags
    public void updateArrayAdapter() {

        TextView veganTag = (TextView) findViewById(R.id.veganTag);
        veganTag.setVisibility(View.GONE);
        TextView vegetarianTag = (TextView) findViewById(R.id.vegetarianTag);
        vegetarianTag.setVisibility(View.GONE);
        TextView glutenFreeTag = (TextView) findViewById(R.id.glutenFreeTag);
        glutenFreeTag.setVisibility(View.GONE);

        arrayAdapter.notifyDataSetChanged();
        if (!rowItems.isEmpty()) {
            if (rowItems.get(0).getRecipeTags().contains("Vegan")) {
                veganTag.setVisibility(View.VISIBLE);
            }
            if (rowItems.get(0).getRecipeTags().contains("Vegetarian")) {
                vegetarianTag.setVisibility(View.VISIBLE);
            }
            if (rowItems.get(0).getRecipeTags().contains("Gluten Free")) {
                glutenFreeTag.setVisibility(View.VISIBLE);
            }
        }

    }

    public void getRecipe() {
        Query colRef = db.collection(userCookbook);

        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Item = new Cards(document.getString("Name"), document.getString("Name"), document.getString("URL"), document.getString("Tags"));
                    rowItems.add(Item);
                    updateArrayAdapter();
                }
            }

            else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                return true;

            case R.id.saved:
                intent = new Intent(MainActivity.this, SavedRecipesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                return true;

            case R.id.profile:
                intent = new Intent(MainActivity.this, ProfileViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                return true;
        }

        return true;
    }
}



