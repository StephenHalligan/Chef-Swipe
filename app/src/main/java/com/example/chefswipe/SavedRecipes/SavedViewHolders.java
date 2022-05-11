package com.example.chefswipe.SavedRecipes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.chefswipe.R;

import androidx.recyclerview.widget.RecyclerView;

public class SavedViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mRecipeName;
    public ImageView mRecipeImage;

    public SavedViewHolders(View itemView) {

        super(itemView);
        itemView.setOnClickListener(this);

        mRecipeName = (TextView) itemView.findViewById(R.id.RecipeName);

        mRecipeImage = (ImageView) itemView.findViewById(R.id.RecipeImage);

    }

    @Override
    public void onClick(View view) {

    }
}
