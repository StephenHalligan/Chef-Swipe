package com.example.chefswipe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewUserProfileActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {


    private RecyclerView.Adapter mPublishedAdapter;

    //Database ref
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //user id
    private String currentUserID;

    //user friends count
    int currentFriendsCount;

    //firebase auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    //bottom nav
    BottomNavigationView bottomNavigation;

    //array for published recipes
    private final ArrayList<PublishedObject> resultsPublishedRecipes = new ArrayList<>();
    private List<PublishedObject> getDataSetPublishedRecipes() {
        return resultsPublishedRecipes;
    }



    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle publishedInstanceState) {
        //set xml and get bundle info
        super.onCreate(publishedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_view_user_profile);

        //bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.profile);

        //get authentication
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        };

        //get instance of database
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //get user id from bundle
        String userId = bundle.getString("AuthorID");

        //get user id from database
        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId);

        Button mFollowButton = (Button) findViewById(R.id.followButton);

        String userID = bundle.getString("AuthorID");

        //load profile image of user using glide
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

        //follow button when clicked
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

        //onclick listener for follow button
        mFollowButton.setOnClickListener(view -> {
            switch (mFollowButton.getText().toString()) {
                //if user isn't following, when they click make them follow
                case "Follow":
                    //add them to database as friends
                    currentUserDb.child(userId).child("FriendList").child(bundle.getString("AuthorID")).setValue(true);
                    //add 1 to friends count
                    currentFriendsCount = currentFriendsCount + 1;
                    currentUserDb.child(userID).child("Friends").setValue(currentFriendsCount);
                    mFollowButton.setBackgroundResource(R.color.verylightgrey);
                    //set text
                    mFollowButton.setText("Unfollow");
                    mUserFriendsCount.setText(String.valueOf(currentFriendsCount));
                    break;
                //if user is following, when they click make them unfollow
                case "Unfollow":
                    //remove from database as friends
                    currentUserDb.child(userId).child("FriendList").child(bundle.getString("AuthorID")).removeValue();
                    //remove 1 from friends count
                    currentFriendsCount = currentFriendsCount - 1;
                    currentUserDb.child(userID).child("Friends").setValue(currentFriendsCount);
                    mFollowButton.setBackgroundResource(R.color.green1);
                    //set text
                    mFollowButton.setText("Follow");
                    mUserFriendsCount.setText(String.valueOf(currentFriendsCount));
                    break;
            }
        });

        TextView mUserBio = (TextView) findViewById(R.id.bioView);

        //set username text
        ref.child("Name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    TextView mUsername = (TextView) findViewById(R.id.username);
                    mUsername.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        //set bio text
        ref.child("Bio").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    mUserBio.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        //set friends count as an integer
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

        //init recyclerview for published recipes
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

    //get published recipes
    private void FetchPublishedRecipeInformation(String key) {

        DatabaseReference publishedDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Published Recipes").child(key);
        publishedDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String recipeID = snapshot.getKey();

                    //collection ref for cookbook & cycle through documents
                    CollectionReference colRef = db.collection("Cookbook");
                    colRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //if doc is retrieved and id equals the user's published recipe id
                                if (Objects.equals(document.getId(), recipeID)) {
                                    String recipeURL = document.getString("URL");
                                    String recipeName = document.getString("Name");

                                    //add object with recipe name, url, and id as a new object
                                    PublishedObject obj = new PublishedObject(recipeName, recipeURL, recipeID);
                                    //add object to published recipes
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

    //bottom nav bar
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

    //start auth state listener
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

