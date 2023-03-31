package com.example.chefswipe.Comments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chefswipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;


public class CommentsActivity extends AppCompatActivity {

    //Implement recycler view
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManagerRecyclerView;
    private CommentsAdapter recyclerViewCommentsAdapter;

    // Create variables
    String[] commentsArray;
    String username;
    String recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Get bundle variables
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();

        //Set xml layout
        setContentView(R.layout.activity_comments);

        //Set recipe ID from bundle
        recipeID = bundle.getString("RecipeID");

        //Initialize firebase & authentication to get user ID
        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //Create reference to user ID and create listener
        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId);

        ref.child("Name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    username = String.valueOf(task.getResult().getValue());
                }
            }
        });

        //Create reference to specified recipe and get that recipe
        DocumentReference docRef = db.collection("Cookbook").document(recipeID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //If retrieval of document was successful, get result
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //Set comment list to current document comments field
                    List<String> commentsList = (List<String>) document.get("Comments");
                    if(commentsList != null) {
                        // Add comments to array for display on recyclerview
                        commentsArray = Objects.requireNonNull(commentsList).toArray(new String[0]);
                        recyclerViewCommentsAdapter = new CommentsAdapter(commentsArray);
                        recyclerView.setAdapter(recyclerViewCommentsAdapter);
                    }

                }
            }
        });

        //Initialize XML layout items
        TextView mCommentText = (TextView) findViewById(R.id.commentText);
        Button mPostComment = (Button) findViewById(R.id.postComment);
        ImageButton mBackButton = (ImageButton) findViewById(R.id.backButton);

        //Back button click listener
        mBackButton.setOnClickListener(view -> {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        //Post button click listener
        mPostComment.setOnClickListener(view -> {

            //Split string into username and message
            String comment = username;
            comment = comment + ":&nbsp;" + mCommentText.getText();
            mCommentText.setText("");
            docRef.update("Comments", FieldValue.arrayUnion(comment));

            //Get recipe document to add comment
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        //Set comment list to current document comments field
                        List<String> commentsList = (List<String>) document.get("Comments");
                        //If the recipe comments aren't empty, add comment to array
                        if(commentsList != null) {
                            commentsArray = Objects.requireNonNull(commentsList).toArray(new String[0]);
                            recyclerViewCommentsAdapter = new CommentsAdapter(commentsArray);
                            recyclerView.setAdapter(recyclerViewCommentsAdapter);
                        }

                    }
                }
            });


        });

        // Initialize recyclerView and update
        recyclerView = findViewById(R.id.recyclerView);
        layoutManagerRecyclerView = new LinearLayoutManager(CommentsActivity.this);
        recyclerView.setLayoutManager(layoutManagerRecyclerView);

    }

    // RecyclerViewList function
    public void buttonRecyclerViewList(View view){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Cookbook").document(recipeID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    //Set comment list to current document comments field
                    List<String> commentsList = (List<String>) document.get("Comments");
                    // If the recipe comments aren't empty, add comment to array
                    if(commentsList != null) {

                        // If the recipe comments aren't empty, add comment to array
                        commentsArray = Objects.requireNonNull(commentsList).toArray(new String[0]);
                        recyclerViewCommentsAdapter = new CommentsAdapter(commentsArray);
                        recyclerView.setAdapter(recyclerViewCommentsAdapter);
                    }

                }
            }
        });
    }


}


