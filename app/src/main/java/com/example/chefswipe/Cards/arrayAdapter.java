package com.example.chefswipe.Cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chefswipe.R;


import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class arrayAdapter extends ArrayAdapter<Cards> {

    public arrayAdapter(Context context, int resourceId, List<Cards> items) {
        super(context, resourceId, items);
    }

    @SuppressLint("SetTextI18n")
    public View getView(int position, View convertView, ViewGroup parent) {

        Cards card_item = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        // Import xml
        TextView recipeAuthor = (TextView) convertView.findViewById(R.id.recipeAuthor);
        TextView recipeName = (TextView) convertView.findViewById(R.id.recipeName);
        TextView recipeLikes = (TextView) convertView.findViewById(R.id.recipeLikes);
        ImageView recipeImage = (ImageView) convertView.findViewById(R.id.recipeImage);
        TextView tag1Text = (TextView) convertView.findViewById(R.id.tag1);
        TextView tag2Text = (TextView) convertView.findViewById(R.id.tag2);
        TextView tag3Text = (TextView) convertView.findViewById(R.id.tag3);

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

        TextView recipeDesc = (TextView) convertView.findViewById(R.id.recipeDesc);
        recipeDesc.setText(card_item.getRecipeDesc());

        recipeLikes.setText(card_item.getRecipeLikes() + " ♥");
        recipeAuthor.setText("By: " + card_item.getRecipeAuthor());
        recipeName.setText(card_item.getRecipeName());
        Glide.with(getContext()).load(card_item.getRecipeURL()).into(recipeImage);

        return convertView;

    }

}
