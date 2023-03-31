package com.example.chefswipe;

import static android.graphics.Color.WHITE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.chefswipe.PublishedRecipes.PublishedAdapter;
import com.example.chefswipe.PublishedRecipes.PublishedObject;
import com.example.chefswipe.SavedRecipes.SavedRecipesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ProfileViewActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter mPublishedAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String currentUserID;

    String recipeURL;
    String uploadedImage;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    BottomNavigationView bottomNavigation;

    private final ArrayList<PublishedObject> resultsPublishedRecipes = new ArrayList<>();
    private List<PublishedObject> getDataSetPublishedRecipes() {
        return resultsPublishedRecipes;
    }

    @Override
    protected void onCreate(Bundle publishedInstanceState) {
        super.onCreate(publishedInstanceState);
        setContentView(R.layout.activity_profile_view);

        //Button to edit and save photo
        Button mEditPhoto = (Button) findViewById(R.id.editPhoto);
        Button mSavePhoto = (Button) findViewById(R.id.savePhoto);

        //Bottom nav xml
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        bottomNavigation.setSelectedItemId(R.id.profile);

        //Get authentication user
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        };

        //Get instance of auth
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Set user id
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //Create database reference
        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId);

        EditText mUserBio = (EditText) findViewById(R.id.bioEdit);

        ImageView mProfileImage = (ImageView) findViewById(R.id.profileImage);

        //If the profile image is set, display it using glide
        ref.child("ProfileImage").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Glide.with(ProfileViewActivity.this).load(String.valueOf(task.getResult().getValue())).into(mProfileImage);
                    mProfileImage.setBackgroundColor(WHITE);
                }
            }
        });

        //If the name is set, display it in username field
        ref.child("Name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    TextView mUsername = (TextView) findViewById(R.id.username);
                    mUsername.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        //If the bio is set, display it in bio field
        ref.child("Bio").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    mUserBio.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        //If the friends num are set, display it in friends field as int
        ref.child("Friends").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    TextView mUserFriendsCount = (TextView) findViewById(R.id.friendCount);
                    mUserFriendsCount.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });

        //Set user id
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //Init recyclerview
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.publishedRecyclerView);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mPublishedLayoutManager = new LinearLayoutManager(com.example.chefswipe.ProfileViewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mPublishedLayoutManager);
        mPublishedAdapter = new PublishedAdapter(getDataSetPublishedRecipes(), com.example.chefswipe.ProfileViewActivity.this);
        mRecyclerView.setAdapter(mPublishedAdapter);

        //Get recipe name
        getPublishedRecipeName();

        //Button for choosing image
        mEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //Button for uploading chosen image
        mSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

    }

    //Choose image option using intent
    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){

            //Set imageview xml
            ImageView mProfileImage = (ImageView) findViewById(R.id.profileImage);
            filePath = data.getData();
            try {
                //If photo has been edited, remove the view so it can be saved
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mProfileImage.setImageBitmap(bitmap);
                Button mEditPhoto = (Button) findViewById(R.id.editPhoto);
                mEditPhoto.setVisibility(View.GONE);
                Button mSavePhoto = (Button) findViewById(R.id.savePhoto);
                mSavePhoto.setVisibility(View.VISIBLE);
                mProfileImage.setBackgroundColor(WHITE);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    //Init firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    //Upload image function
    private void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Set listener for success of upload
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //If successful, set url of image to uploaded image
                                    uploadedImage = uri.toString();
                                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                    DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("ProfileImage");
                                    //Set image to profile image in database
                                    currentUserDb.setValue(uploadedImage);
                                    ImageView mProfileImage = (ImageView) findViewById(R.id.profileImage);
                                    Glide.with(ProfileViewActivity.this).load(uploadedImage).into(mProfileImage);
                                    //Remove save button
                                    Button mSavePhoto = (Button) findViewById(R.id.savePhoto);
                                    mSavePhoto.setVisibility(View.GONE);
                                    //Make edit button visible
                                    Button mEditPhoto = (Button) findViewById(R.id.editPhoto);
                                    mEditPhoto.setVisibility(View.VISIBLE);

                                }

                        });
                            progressDialog.dismiss();
                            Toast.makeText(ProfileViewActivity.this, "Published!", Toast.LENGTH_SHORT).show();
                    }
        })
                    //If image upload fails
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfileViewActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Published "+(int)progress+"%");
                        }
                    });
        }
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

                    //Collection reference for cookbook
                    CollectionReference colRef = db.collection("Cookbook");
                    //add collection listener
                    colRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (Objects.equals(document.getId(), recipeID)) {
                                    //Set url to the documents url
                                    recipeURL = document.getString("URL");
                                    //set recipe name to documents recipe name
                                    String recipeName = document.getString("Name");

                                    //add object as new publishedobject with recipe name, url and id
                                    PublishedObject obj = new PublishedObject(recipeName, recipeURL, recipeID);
                                    resultsPublishedRecipes.add(obj);

                                    //Notify published adapter changes
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

    //Bottom nav bar
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(ProfileViewActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;

            case R.id.saved:
                intent = new Intent(ProfileViewActivity.this, SavedRecipesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;

            case R.id.create:
                intent = new Intent(ProfileViewActivity.this, CreateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;

            case R.id.profile:
                return true;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    //If activity stops, save changed information stored in edittext (bio)
    @Override
    protected void onStop() {
        super.onStop();
        EditText mUserBio = (EditText) findViewById(R.id.bioEdit);
        final String userBio = mUserBio.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        };

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId).child("Bio");
        ref.setValue(userBio);
    }

}

