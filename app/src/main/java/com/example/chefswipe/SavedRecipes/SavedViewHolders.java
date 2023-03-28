package com.example.chefswipe.SavedRecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.chefswipe.R;
import com.example.chefswipe.RecipeViewActivity;
import androidx.recyclerview.widget.RecyclerView;

public class SavedViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mRecipeName;
    public TextView mRecipeID;
    public ImageView mRecipeImage;

    public SavedViewHolders(View itemView) {

        super(itemView);
        itemView.setOnClickListener(this);

        mRecipeName = (TextView) itemView.findViewById(R.id.RecipeName);
        mRecipeID = (TextView) itemView.findViewById(R.id.RecipeID);
        mRecipeImage = (ImageView) itemView.findViewById(R.id.RecipeImage);
        
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), RecipeViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("recipeID", mRecipeID.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }

}
