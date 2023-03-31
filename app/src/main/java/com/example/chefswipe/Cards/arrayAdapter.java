package com.example.chefswipe.Cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.chefswipe.R;
import java.util.List;

public class arrayAdapter extends ArrayAdapter<Cards> {

    //Create context
    public arrayAdapter(Context context, int resourceId, List<Cards> items) {
        super(context, resourceId, items);
    }

    //Getview for remotely editing XML views
    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {

        //Get current carditem
        Cards card_item = getItem(position);

        //If convertView hasn't been inflated, inflate it now
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        // Import xml views
        TextView recipeAuthor = (TextView) convertView.findViewById(R.id.recipeAuthor);
        TextView recipeName = (TextView) convertView.findViewById(R.id.recipeName);
        TextView recipeLikes = (TextView) convertView.findViewById(R.id.recipeLikes);
        ImageView recipeImage = (ImageView) convertView.findViewById(R.id.recipeImage);
        TextView tag1Text = (TextView) convertView.findViewById(R.id.tag1);
        TextView tag2Text = (TextView) convertView.findViewById(R.id.tag2);
        TextView tag3Text = (TextView) convertView.findViewById(R.id.tag3);

        //Split tag array at the comma and display tags if they exist
        String[] tagsArr = card_item.getRecipeTags().split(", ");
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

        //Set description
        TextView recipeDesc = (TextView) convertView.findViewById(R.id.recipeDesc);
        recipeDesc.setText(card_item.getRecipeDesc());

        //Set likes & author
        recipeLikes.setText(card_item.getRecipeLikes() + " ♥");
        recipeAuthor.setText("By: " + card_item.getRecipeAuthor());
        recipeName.setText(card_item.getRecipeName());

        //Set image with glide
        Glide.with(getContext()).load(card_item.getRecipeURL()).into(recipeImage);

        return convertView;

    }

}
