package com.example.chefswipe;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chefswipe.PublishedRecipes.PublishedAdapter;
import com.example.chefswipe.PublishedRecipes.PublishedObject;
import com.example.chefswipe.SavedRecipes.SavedRecipesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewUserProfileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private RecyclerView.Adapter mPublishedAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String currentUserID;

    int currentFriendsCount;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    BottomNavigationView bottomNavigation;


    private final ArrayList<PublishedObject> resultsPublishedRecipes = new ArrayList<>();
    private List<PublishedObject> getDataSetPublishedRecipes() {
        return resultsPublishedRecipes;
    }



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle publishedInstanceState) {
        super.onCreate(publishedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_view_user_profile);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.profile);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        };

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        String userId = bundle.getString("AuthorID");

        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId);

        Button mFollowButton = (Button) findViewById(R.id.followButton);

        String userID = bundle.getString("AuthorID");

        ImageView mProfileImage = (ImageView) findViewById(R.id.profileImage);
        ref.child("ProfileImage").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    if(ref.child("ProfileImage") != null) {
                        Glide.with(ViewUserProfileActivity.this).load(String.valueOf(task.getResult().getValue())).into(mProfileImage);
                    }
                }
            }
        });

        ref.child("FriendList").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    mFollowButton.setBackgroundResource(R.color.verylightgrey);
                    mFollowButton.setText("Unfollow");
                }
            }
        });

        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users");

        TextView mUserFriendsCount = (TextView) findViewById(R.id.friendCount);

        mFollowButton.setOnClickListener(view -> {
            switch (mFollowButton.getText().toString()) {
                case "Follow":
                    currentUserDb.child(userId).child("FriendList").child(bundle.getString("AuthorID")).setValue(true);
                    currentFriendsCount = currentFriendsCount + 1;
                    currentUserDb.child(userID).child("Friends").setValue(currentFriendsCount);
                    mFollowButton.setBackgroundResource(R.color.verylightgrey);
                    mFollowButton.setText("Unfollow");
                    mUserFriendsCount.setText(String.valueOf(currentFriendsCount));
                    break;
                case "Unfollow":
                    currentUserDb.child(userId).child("FriendList").child(bundle.getString("AuthorID")).removeValue();
                    currentFriendsCount = currentFriendsCount - 1;
                    currentUserDb.child(userID).child("Friends").setValue(currentFriendsCount);
                    mFollowButton.setBackgroundResource(R.color.green1);
                    mFollowButton.setText("Follow");
                    mUserFriendsCount.setText(String.valueOf(currentFriendsCount));
                    break;
            }
        });

        TextView mUserBio = (TextView) findViewById(R.id.bioView);

        ref.child("Name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    TextView mUsername = (TextView) findViewById(R.id.username);
                    mUsername.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        ref.child("Bio").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    mUserBio.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        ref.child("Friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    TextView mUserFriendsCount = (TextView) findViewById(R.id.friendCount);
                    mUserFriendsCount.setText(String.valueOf(task.getResult().getValue()));
                    currentFriendsCount = (task.getResult().getValue(Integer.class));
                }
            }
        });

        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.publishedRecyclerView);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mPublishedLayoutManager = new LinearLayoutManager(ViewUserProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mPublishedLayoutManager);
        mPublishedAdapter = new PublishedAdapter(getDataSetPublishedRecipes(), ViewUserProfileActivity.this);
        mRecyclerView.setAdapter(mPublishedAdapter);

        getPublishedRecipeName();

    }

    private void getPublishedRecipeName() {
        DatabaseReference publishedDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Published Recipes");
        publishedDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot publishedRecipe : dataSnapshot.getChildren()) {
                        FetchPublishedRecipeInformation(publishedRecipe.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void FetchPublishedRecipeInformation(String key) {

        DatabaseReference publishedDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Published Recipes").child(key);
        publishedDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String recipeID = snapshot.getKey();

                    CollectionReference colRef = db.collection("Cookbook");
                    colRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (Objects.equals(document.getId(), recipeID)) {
                                    String recipeURL = document.getString("URL");
                                    String recipeName = document.getString("Name");

                                    PublishedObject obj = new PublishedObject(recipeName, recipeURL, recipeID);
                                    resultsPublishedRecipes.add(obj);

                                    mPublishedAdapter.notifyDataSetChanged();
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

    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(ViewUserProfileActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            case R.id.saved:
                intent = new Intent(ViewUserProfileActivity.this, SavedRecipesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            case R.id.create:
                intent = new Intent(ViewUserProfileActivity.this, CreateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;


            case R.id.profile:
                intent = new Intent(ViewUserProfileActivity.this, ProfileViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}

