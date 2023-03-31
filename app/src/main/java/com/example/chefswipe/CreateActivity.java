package com.example.chefswipe;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chefswipe.SavedRecipes.SavedRecipesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CreateActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference userDb;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    BottomNavigationView bottomNavigation;
    private ImageView recipeImage;

    //Global vars
    String uploadedImage;
    String authorName;
    String authorUserId;
    String userId;

    String selectedTags;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    String recipeCookbook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Set xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        //XML views
        recipeImage = (ImageView) findViewById(R.id.recipeImage);
        Button tagsButton = (Button) findViewById(R.id.tagsButton);
        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        ImageButton publishButton = (ImageButton) findViewById(R.id.publishButton);
        Button imageButton = (Button) findViewById(R.id.imageButton);
        TextView cookbookText = (TextView) findViewById(R.id.cookbookText);

        //Back button to main page
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(CreateActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        EditText recipeName = (EditText) findViewById(R.id.recipeName);
        EditText recipeIngredients = (EditText) findViewById(R.id.recipeImg);
        EditText recipeMethod = (EditText) findViewById(R.id.recipeMethod);
        EditText recipeDesc = (EditText) findViewById(R.id.recipeDesc);

        //Create database instance
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Set user id
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        authorUserId = userId;

        //Create database ref
        DatabaseReference dbRef = userDatabase.getReference().child("Users").child(userId);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                authorName = dataSnapshot.child("Name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //Publish save recipe button
        publishButton.setOnClickListener(view -> {

            //Check if user has entered all fields

            if(recipeName.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),"Please enter a recipe name!",Toast.LENGTH_SHORT).show();
            }
            else if(filePath == null) {
                Toast.makeText(getApplicationContext(),"Please select an image!",Toast.LENGTH_SHORT).show();
            }
            else if(recipeIngredients.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),"Please enter ingredients!",Toast.LENGTH_SHORT).show();
            }
            else if(recipeMethod.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),"Please enter the recipe method!",Toast.LENGTH_SHORT).show();
            }
            else if(recipeCookbook == null) {
                Toast.makeText(getApplicationContext(),"Please select a cookbook!",Toast.LENGTH_SHORT).show();
            }
            else if(recipeDesc.getText().toString().equals("")) {
                Toast.makeText(getApplicationContext(),"Please enter a description!",Toast.LENGTH_SHORT).show();
            }
            else if(selectedTags == null) {
                Toast.makeText(getApplicationContext(),"Please select some recipe tags!",Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
            }

        });

        //Button for adding recipe tags

        tagsButton.setOnClickListener(view -> {
            AlertDialog.Builder tagBuilder = new AlertDialog.Builder(CreateActivity.this);
            TextView tagTitle = new TextView(CreateActivity.this);
            tagTitle.setText("Select up to 3 dietary tags");
            tagTitle.setPadding(20, 30, 20, 30);
            tagTitle.setTextSize(20F);
            tagTitle.setBackgroundColor(Color.DKGRAY);
            tagTitle.setTextColor(Color.WHITE);
            ObjectAnimator animator = ObjectAnimator.ofInt(tagTitle, "textColor", Color.DKGRAY, Color.WHITE, Color.DKGRAY, Color.WHITE);
            animator.setDuration(400);
            animator.setEvaluator(new ArgbEvaluator());
            tagBuilder.setCustomTitle(tagTitle);
            String[] tagList = getResources().getStringArray(R.array.dietary_tags);
            boolean[] checkedItems = {false, false, false, false, false, false, false};
            TextView tag1Text = (TextView) findViewById(R.id.tag1);
            TextView tag2Text = (TextView) findViewById(R.id.tag2);
            TextView tag3Text = (TextView) findViewById(R.id.tag3);
            tagBuilder.setMultiChoiceItems(tagList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    int tagCount = 0;
                    tag1Text.setVisibility(View.GONE);
                    tag2Text.setVisibility(View.GONE);
                    tag3Text.setVisibility(View.GONE);
                    for (int j = 0; j < checkedItems.length; j++) {
                        if(checkedItems[j]) {
                            tagCount++;
                        }
                        if(tagCount > 3) {
                            checkedItems[i] = false;
                            ((AlertDialog) dialogInterface).getListView().setItemChecked(i, false);
                            animator.start();
                        }
                    }
                }
            });

            //Save tags and add to an array, split at the comma

            tagBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedTags = "";
                    for (int i = 0; i < tagList.length; i++) {
                        if(checkedItems[i]) {
                            selectedTags = selectedTags + (tagList[i]) + ", ";
                        }
                    }

                    String[] tagsArr = selectedTags.split(", ");
                    if(tagsArr.length >= 1) {
                        tag1Text.setText(tagsArr[0]);
                        tag1Text.setVisibility(View.VISIBLE);
                    }
                    if(tagsArr.length >= 2) {
                        tag2Text.setText(tagsArr[1]);
                        tag2Text.setVisibility(View.VISIBLE);
                    }
                    if(tagsArr.length >= 3) {
                        tag3Text.setText(tagsArr[2]);
                        tag3Text.setVisibility(View.VISIBLE);
                    }
                    tagsButton.setVisibility(View.GONE);
                }
            });

            //Cancel tagbuilder
            tagBuilder.setNegativeButton("Cancel", null);
            AlertDialog dialog = tagBuilder.create();
            dialog.show();
        });


        Button cookbookButton = (Button) findViewById(R.id.cookbookButton);

        //Button for selecting cookbook
        cookbookButton.setOnClickListener(view -> {
            AlertDialog.Builder tagBuilder = new AlertDialog.Builder(CreateActivity.this);
            TextView tagTitle = new TextView(CreateActivity.this);
            tagTitle.setText("Select a cookbook");
            tagTitle.setPadding(20, 30, 20, 30);
            tagTitle.setTextSize(20F);
            tagTitle.setBackgroundColor(Color.DKGRAY);
            tagTitle.setTextColor(Color.WHITE);
            tagBuilder.setCustomTitle(tagTitle);
            String[] tagList = getResources().getStringArray(R.array.cookbook_list);
            boolean[] checkedItems = {false, false, false, false, false, false};
            tagBuilder.setMultiChoiceItems(tagList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                    //Ensure only 1 cookbook can be selected
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

            //Save cookbook selection
            tagBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (int i = 0; i < tagList.length; i++) {
                        if (checkedItems[i]) {
                            recipeCookbook = tagList[i];
                            cookbookText.setText(recipeCookbook);
                            cookbookButton.setVisibility(View.GONE);
                        }
                    }
                }
            });
            //Cancel cookbook selection
            tagBuilder.setNegativeButton("Cancel", null);
            AlertDialog dialog = tagBuilder.create();
            dialog.show();
        });

        //Choose image for recipe
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

    }

    //Chooseimage function
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                recipeImage.setImageBitmap(bitmap);
                Button imageButton = (Button) findViewById(R.id.imageButton);
                imageButton.setVisibility(View.GONE);
            }
      catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    //Init storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();

    //Upload image to firebase
    private void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadedImage = uri.toString();
                                    if(filePath != null) {


                                        EditText recipeName = (EditText) findViewById(R.id.recipeName);
                                        EditText recipeIngredients = (EditText) findViewById(R.id.recipeImg);
                                        EditText recipeMethod = (EditText) findViewById(R.id.recipeMethod);
                                        EditText recipeDesc = (EditText) findViewById(R.id.recipeDesc);
                                        TextView cookbookText = (TextView) findViewById(R.id.cookbookText);

                                        String editRecipeName = recipeName.getText().toString().replaceAll("\\n", ";@");
                                        String editRecipeIngredients = recipeIngredients.getText().toString().replaceAll("\\n", ";@");
                                        String editRecipeMethod = recipeMethod.getText().toString().replaceAll("\\n", ";@");
                                        String editRecipeDesc = recipeDesc.getText().toString().replaceAll("\\n", ";@");

                                        //Create map of the inputted recipe info
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("Name", editRecipeName);
                                        data.put("Ingredients", editRecipeIngredients);
                                        data.put("Method", editRecipeMethod);
                                        data.put("Desc", editRecipeDesc);
                                        data.put("Likes", 0);
                                        data.put("Approved", false);
                                        data.put("Author", authorName);
                                        data.put("URL", uploadedImage);
                                        data.put("Tags", selectedTags);
                                        data.put("Cookbook", cookbookText.getText().toString());
                                        data.put("AuthorID", authorUserId);
                                        data.put("Comments", null);

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        //Write a document with this info map

                                        userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Published Recipes");
                                        db.collection("Cookbook").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        userDb.child(documentReference.getId()).setValue(true);
                                                        System.out.println(("DocumentSnapshot written with ID: " + documentReference.getId()));

                                                    }
                                                })
                                                //If doc write fails
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println(("Error adding document"));
                                                    }
                                                });

                                        //Launch back into main activity with fade animation
                                        Intent intent;
                                        intent = new Intent(CreateActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();

                                    }
                                }
                            });
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Published "+(int)progress+"%");
                        }
                    });
        }
    }


    //bottom nav bar
    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(CreateActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
            case R.id.saved:
                intent = new Intent(CreateActivity.this, SavedRecipesActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;

            case R.id.create:
                return true;

            case R.id.profile:
                intent = new Intent(CreateActivity.this, ProfileViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return true;
        }

        return true;
    }


}

