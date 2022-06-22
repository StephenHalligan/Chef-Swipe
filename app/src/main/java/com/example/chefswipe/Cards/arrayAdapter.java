package com.example.chefswipe.Cards;

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

    public arrayAdapter(Context context, int resourceId, List<Cards> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Cards card_item = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView recipeName = (TextView) convertView.findViewById(R.id.recipeName);
        ImageView recipeImage = (ImageView) convertView.findViewById(R.id.recipeImage);

        recipeName.setText(card_item.getRecipeName());
        Glide.with(getContext()).load(card_item.getRecipeURL()).into(recipeImage);

        return convertView;

    }

}
