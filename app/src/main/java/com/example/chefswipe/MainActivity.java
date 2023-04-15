package com.example.chefswipe;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.brouding.doubletaplikeview.DoubleTapLikeView;
import com.example.chefswipe.Comments.CommentsActivity;
import com.example.chefswipe.SavedRecipes.SavedRecipesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chefswipe.Cards.Cards;
import com.example.chefswipe.Cards.arrayAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Chef Swipe";
    private Cards Item;

    String recipeID;

    String userCookbook;

    String userTagFilter = " ";

    private DatabaseReference userDb;

    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    //Creating array adapter from arrayAdapter class
    private com.example.chefswipe.Cards.arrayAdapter arrayAdapter;

    String dbRecipeName;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<Cards> rowItems;

    int stackIndex;

    BottomNavigationView bottomNavigation;

    private DoubleTapLikeView mDoubleTapLikeView;

    @SuppressLint("InflateParams")
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

        ImageButton filterButton = (ImageButton) findViewById(R.id.filterButton);
        filterButton.setOnClickListener(view -> {
            AlertDialog.Builder tagBuilder = new AlertDialog.Builder(MainActivity.this);
            TextView tagTitle = new TextView(MainActivity.this);
            tagTitle.setText("Select a dietary tag filter");
            tagTitle.setPadding(20, 30, 20, 30);
            tagTitle.setTextSize(20F);
            tagTitle.setBackgroundColor(getResources().getColor(R.color.green1));
            tagTitle.setTextColor(Color.BLACK);
            ObjectAnimator animator = ObjectAnimator.ofInt(tagTitle, "textColor", Color.RED, Color.WHITE);
            animator.setDuration(200);
            animator.setEvaluator(new ArgbEvaluator());
            animator.setRepeatCount(Animation.ABSOLUTE);
            tagBuilder.setCustomTitle(tagTitle);
            String[] tagList = getResources().getStringArray(R.array.dietary_tags);
            boolean[] checkedItems = {false, false, false, false, false, false, false};
            userTagFilter =  " ";
            tagBuilder.setMultiChoiceItems(tagList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    for (int j = 0; j < checkedItems.length; j++) {
                        if (j == i) {
                            checkedItems[j] = true;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(j, true);
                        }
                        else {
                            checkedItems[j] = false;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(j, false);
                        }
                    }
                }
            });

            tagBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = 0; i < tagList.length; i++) {
                        if (checkedItems[i]) {
                            userTagFilter = tagList[i];
                        }
                    }
                    rowItems.clear();
                    updateArrayAdapter();
                    getRecipe();
                }
            });
            tagBuilder.setNegativeButton("Cancel", null);
            AlertDialog dialog = tagBuilder.create();
            dialog.show();
        });

        PowerSpinnerView cookbookSpinner = (PowerSpinnerView) findViewById(R.id.cookbookSpinner);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild("Cookbook")) userDb.child("Cookbook").setValue("All Cookbooks");
                userCookbook = dataSnapshot.child("Cookbook").getValue(String.class);

                cookbookSpinner.setHint(userCookbook);

                getRecipe();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Recipe read failed: " + databaseError.getCode());
            }

        });

        cookbookSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override public void onItemSelected(int oldIndex, @Nullable String oldItem, int newIndex, String newItem) {
                ref.child("Cookbook").setValue(newItem);
                rowItems.clear();
                updateArrayAdapter();
                getRecipe();
            }
        });


        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {
                //Delete an object from the Adapter (/AdapterView)
                rowItems.remove(0);
                if (rowItems.isEmpty()) {
                    TextView noRecipes = (TextView) findViewById(R.id.noRecipes);
                }
            }

            //Called when adapter is almost empty
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                updateArrayAdapter();
                stackIndex = itemsInAdapter;
            }

            //Called when card is swiped to left
            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                dbRecipeName = obj.getRecipeName();
                updateArrayAdapter();
            }

            //Called when card is swiped to right
            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                dbRecipeName = obj.getRecipeName();

                Integer recipeLikes = obj.getRecipeLikes();
                recipeLikes += 1;

                DocumentReference docRef = db.collection("Cookbook").document(obj.getRecipeId());
                docRef.update("Likes", recipeLikes);
                userDb.child("Saved Recipes").child(obj.getRecipeId()).setValue(true);
                updateArrayAdapter();
            }

            @Override
            public void onScroll(float v) {
            }

        });

        //OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> {
            getRecipeID(dataObject);
        });

    }


    public void getRecipeID(Object dataObject) {
        Cards obj = (Cards) dataObject;
        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
        Bundle bundleProfile = new Bundle();
        bundleProfile.putString("RecipeID", obj.getRecipeId());
        System.out.println(obj.getRecipeId());
        intent.putExtras(bundleProfile);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //Updates array adapter & changes visibility of tags
    public void updateArrayAdapter() {
        arrayAdapter.notifyDataSetChanged();
    }

    public void getRecipe() {
        Query colRef = db.collection("Cookbook");
        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    DatabaseReference dbRef = userDatabase.getReference().child("Users").child(userId);
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(Objects.equals(userCookbook, "All Cookbooks")) {
                                //If author id is null, set it
                                if(document.getString("AuthorID") == null) {
                                    DocumentReference authorID = db.collection("Cookbook").document(document.getId());
                                    authorID.update("AuthorID", "");
                                }
                                //If author is null, set it
                                if(document.getString("Author") == null) {
                                    DocumentReference author = db.collection("Cookbook").document(document.getId());
                                    author.update("Author", "ChefSwipe team");
                                }
                                //If approved is null, set it
                                if(document.getBoolean("Approved") == null) {
                                    DocumentReference author = db.collection("Cookbook").document(document.getId());
                                    author.update("Approved", true);
                                }
                                //If likes is null, set it
                                if(document.getLong("Likes") == null) {
                                    DocumentReference author = db.collection("Cookbook").document(document.getId());
                                    author.update("Likes", 0);
                                }
                                //Get recipes which contain user tag filters if they exist
                                if (!dataSnapshot.child("Saved Recipes").hasChild(document.getId()) && Boolean.TRUE.equals(document.getBoolean("Approved"))  && Objects.requireNonNull(document.getString("Tags")).contains(userTagFilter)) {
                                    //Create new item with the retrieved document id, name etc.
                                    Item = new Cards(document.getId(), document.getString("Name"), document.getString("URL"), document.getString("Tags"), document.getString("Author"), Objects.requireNonNull(document.getLong("Likes")).intValue(), document.getString("Desc"));
                                    //Add this item to rowItems
                                    rowItems.add(Item);
                                    //Update adapter
                                    updateArrayAdapter();
                                }
                            }
                            else {
                                //Get recipes which contain user tag filters and cookbook
                                if (!dataSnapshot.child("Saved Recipes").hasChild(document.getId()) && Boolean.TRUE.equals(document.getBoolean("Approved")) && (Objects.equals(document.getString("Cookbook"), userCookbook)) && Objects.requireNonNull(document.getString("Tags")).contains(userTagFilter)) {
                                    //Create new item with the retrieved document id, name etc.
                                    Item = new Cards(document.getId(), document.getString("Name"), document.getString("URL"), document.getString("Tags"), document.getString("Author"), Objects.requireNonNull(document.getLong("Likes")).intValue(), document.getString("Desc"));
                                    //Add this item to rowItems
                                    rowItems.add(Item);
                                    //Update adapter
                                    updateArrayAdapter();
                                }
                            }

                        }
                        //If recipe read fails
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            System.out.println("Recipe read failed: " + databaseError.getCode());
                        }

                    });
                }
            }
            else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    //Bottom nav bar
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                return true;

            case R.id.create:
                intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;

            case R.id.saved:
                intent = new Intent(MainActivity.this, SavedRecipesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;

            case R.id.profile:
                intent = new Intent(MainActivity.this, ProfileViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
        }

        return true;
    }
}



