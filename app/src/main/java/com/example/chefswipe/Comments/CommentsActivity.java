package com.example.chefswipe.Comments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chefswipe.R;
import com.example.chefswipe.ViewUserProfileActivity;
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

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManagerRecyclerView;
    private CommentsAdapter recyclerViewCommentsAdapter;

    String[] commentsArray;
    String username;
    String profileImage;
    String recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();

        setContentView(R.layout.activity_comments);

        recipeID = bundle.getString("RecipeID");

        FirebaseDatabase userDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference ref = userDatabase.getReference().child("Users").child(userId);

        ref.child("Name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    username = String.valueOf(task.getResult().getValue());
                }
            }
        });

        DocumentReference docRef = db.collection("Cookbook").document(recipeID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    List<String> commentsList = (List<String>) document.get("Comments");
                    if(commentsList != null) {
                        commentsArray = Objects.requireNonNull(commentsList).toArray(new String[0]);

                        recyclerViewCommentsAdapter = new CommentsAdapter(commentsArray);
                        recyclerView.setAdapter(recyclerViewCommentsAdapter);
                    }

                }
            }
        });

        TextView mCommentText = (TextView) findViewById(R.id.commentText);
        Button mPostComment = (Button) findViewById(R.id.postComment);
        ImageButton mBackButton = (ImageButton) findViewById(R.id.backButton);

        mBackButton.setOnClickListener(view -> {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        mPostComment.setOnClickListener(view -> {

            String comment = username;
            comment = comment + ":&nbsp;" + mCommentText.getText();
            mCommentText.setText("");
            docRef.update("Comments", FieldValue.arrayUnion(comment));

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        DocumentSnapshot document = task.getResult();
                        List<String> commentsList = (List<String>) document.get("Comments");

                        if(commentsList != null) {
                            commentsArray = Objects.requireNonNull(commentsList).toArray(new String[0]);
                            recyclerViewCommentsAdapter = new CommentsAdapter(commentsArray);
                            recyclerView.setAdapter(recyclerViewCommentsAdapter);
                        }

                    }
                }
            });


        });

        recyclerView = findViewById(R.id.recyclerView);

        layoutManagerRecyclerView = new LinearLayoutManager(CommentsActivity.this);

        recyclerView.setLayoutManager(layoutManagerRecyclerView);

    }

    public void buttonRecyclerViewList(View view){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Cookbook").document(recipeID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    List<String> commentsList = (List<String>) document.get("Comments");
                    if(commentsList != null) {

                        commentsArray = Objects.requireNonNull(commentsList).toArray(new String[0]);
                        recyclerViewCommentsAdapter = new CommentsAdapter(commentsArray);
                        recyclerView.setAdapter(recyclerViewCommentsAdapter);
                    }

                }
            }
        });
    }


}


