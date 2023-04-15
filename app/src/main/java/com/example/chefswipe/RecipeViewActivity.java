package com.example.chefswipe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.chefswipe.Comments.CommentsActivity;
import com.example.chefswipe.SavedRecipes.SavedRecipesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;

public class RecipeViewActivity extends AppCompatActivity {

    private DatabaseReference userDb;

    //User id
    String recipeAuthorID;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set xml
        setContentView(R.layout.activity_recipeview);
        //get bundle
        Bundle bundle = getIntent().getExtras();

        //create instances and auth instances
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId).child("Saved Recipes");

        //Delete recipe button
        ImageButton mDelete = (ImageButton) findViewById(R.id.deleteButton);
        mDelete.setOnClickListener(view -> {
            ref.child(bundle.getString("recipeID")).removeValue();
            Intent intent;
            intent = new Intent(RecipeViewActivity.this, SavedRecipesActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        //Back button
        ImageButton mBack = (ImageButton) findViewById(R.id.backButton);
        mBack.setOnClickListener(view -> {
            finish();
        });

        //Comments activity button
        ImageButton mComments = (ImageButton) findViewById(R.id.commentsButton);
        mComments.setOnClickListener(view -> {
            Intent intent = new Intent(RecipeViewActivity.this, CommentsActivity.class);
            Bundle bundleProfile = new Bundle();
            //put recipe id in intent bundle
            bundleProfile.putString("RecipeID", bundle.getString("recipeID"));
            intent.putExtras(bundleProfile);
            //launch comments activity
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        //Recipe author's profile button
        Button recipeAuthor = (Button) findViewById(R.id.recipeAuthor);
        recipeAuthor.setOnClickListener(view -> {
            if(!Objects.equals(recipeAuthorID, "null")) {
                Intent intent = new Intent(view.getContext(), ViewUserProfileActivity.class);
                Bundle bundleProfile = new Bundle();
                //put author id in intent bundle
                bundleProfile.putString("AuthorID", recipeAuthorID);
                intent.putExtras(bundleProfile);
                //launch profile view activity
                view.getContext().startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });




        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Collection reference to cookbook
                CollectionReference colRef = db.collection("Cookbook");
                //get document where recipe id equals bundle intent recipe id
                colRef.whereEqualTo(FieldPath.documentId().toString(), bundle.getString("recipeID")).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {

                            //set recipe name field text
                            TextView recipeName = (TextView) findViewById(R.id.recipeName);
                            recipeName.setText(document.getString("Name"));

                            //set recipe author field text
                            recipeAuthorID = document.getString("AuthorID");
                            recipeAuthor.setText("By:" + document.getString("Author"));
                            recipeAuthor.setVisibility(View.VISIBLE);

                            //set recipe image field using glide
                            ImageView recipeImage = (ImageView) findViewById(R.id.recipeImage);
                            Glide.with(getApplicationContext()).load(document.getString("URL")).into(recipeImage);

                            //set recipe likes field int
                            TextView recipeLikes = (TextView) findViewById(R.id.recipeLikes);
                            recipeLikes.setText((document.getLong("Likes") + " ♥").toString());

                            //set recipe cookbook field text
                            TextView cookbookText = (TextView) findViewById(R.id.cookbookText);
                            cookbookText.setText(document.getString("Cookbook"));

                            //set tag field xml
                            TextView tag1Text = (TextView) findViewById(R.id.tag1);
                            TextView tag2Text = (TextView) findViewById(R.id.tag2);
                            TextView tag3Text = (TextView) findViewById(R.id.tag3);

                            //set array from tags document string, split at comma
                            String[] tagsArr = Objects.requireNonNull(document.getString("Tags")).split(", ");
                            //set tag xml text if tag exists for each
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

                            //Set ingredients text, split at ;@ with bullet points to separate each step
                            String ingredients = document.getString("Ingredients");
                            String[] ingredientsArr = Objects.requireNonNull(ingredients).split(";@", 15);
                            TextView ingredientsView = (TextView) findViewById(R.id.ingredientsView);
                            ingredientsView.setSingleLine(false);
                            for (String s : ingredientsArr) {
                                ingredientsView.append("• " + s + "\n\n");
                            }

                            //Set method text, split at ;@ with bullet points to separate each step
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
