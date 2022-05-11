package com.example.chefswipe;

import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import org.w3c.dom.Text;

//Implement onFlingListener
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Chef Swipe";

    private Cards cards_data[];

    private Cards Item;

    private DatabaseReference userDb;

    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    //Creating array adapter from arrayAdapter class
    private com.example.chefswipe.Cards.arrayAdapter arrayAdapter;

    //Need to initialize index on registration **IMPORTANT**
    int recipeIndex = 0;
    String userCookbook;

    String dbRecipeName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ListView listView;
    List<Cards> rowItems;

    ArrayList<Boolean> veganArray = new ArrayList<>();

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

        int maxRecipeIndex = 13;

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

                recipeIndex = Objects.requireNonNull(dataSnapshot).child("Recipe Index").getValue(Integer.class);
                userCookbook = dataSnapshot.child("Cookbook").getValue(String.class);

                TextView cookbookTitle = (TextView) findViewById(R.id.cookbookTitle);
                cookbookTitle.setText(userCookbook);

                if(recipeIndex > maxRecipeIndex-1) {
                    rowItems.clear();
                    recipeIndex = 0;
                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Recipe Index");
                    currentUserDb.setValue(recipeIndex);
                }

                assert userCookbook != null;

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

                if(recipeIndex > (maxRecipeIndex-1)) {
                    recipeIndex = 0;
                    rowItems.clear();
                }
                else {
                    recipeIndex++;
                }

                Log.d("LIST", "removed object!");

                DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Recipe Index");
                currentUserDb.setValue(recipeIndex);

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
            }

            //Called when card is swiped to right
            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                dbRecipeName = obj.getRecipeName();
                userDb.child("Saved Recipes").child(dbRecipeName).setValue(true);
            }

            @Override
            public void onScroll(float v) {
            }

        });

        //OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {

        });

    }

    public void updateArrayAdapter() {
        arrayAdapter.notifyDataSetChanged();
    }

    public void getRecipe() {
        CollectionReference colRef = db.collection(userCookbook);
        colRef.whereGreaterThan("Recipe ID", recipeIndex).orderBy("Recipe ID").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {

                    Item = new Cards(document.getString("Name"), document.getString("Name"), document.getString("Image URL"));
                    rowItems.add(Item);
                    updateArrayAdapter();


                    veganArray.add(document.getBoolean("Vegan"));
                    TextView veganTag = (TextView) findViewById(R.id.veganTag);
                    veganTag.setVisibility(View.VISIBLE);
                    System.out.println(veganArray.get(0));
                    if (!veganArray.get(0)) {
                        veganTag.setVisibility(View.GONE);
                    }


                }
                veganArray.clear();

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
                System.out.println("profile");
                return true;
        }

        return true;
    }
}



