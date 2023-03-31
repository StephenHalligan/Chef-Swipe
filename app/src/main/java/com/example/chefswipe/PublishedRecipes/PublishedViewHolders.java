package com.example.chefswipe.PublishedRecipes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chefswipe.R;
import com.example.chefswipe.RecipeViewActivity;

import org.w3c.dom.Text;

public class PublishedViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mRecipeName;
    public TextView mRecipeID;
    public ImageView mRecipeImage;

    public PublishedViewHolders(View itemView) {

        super(itemView);
        itemView.setOnClickListener(this);

        //Views for name, id and image
        mRecipeName = (TextView) itemView.findViewById(R.id.RecipeName);
        mRecipeID = (TextView) itemView.findViewById(R.id.RecipeID);
        mRecipeImage = (ImageView) itemView.findViewById(R.id.RecipeImage);
        
    }

    @Override
    public void onClick(View view) {
        //On click on item, put id and name in bundle and start recipeviewactivity with bundle
        Intent intent = new Intent(view.getContext(), RecipeViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("recipeID", mRecipeID.getText().toString());
        bundle.putString("recipeName", mRecipeName.getText().toString());
        intent.putExtras(bundle);
        view.getContext().startActivity(intent);
    }

}
