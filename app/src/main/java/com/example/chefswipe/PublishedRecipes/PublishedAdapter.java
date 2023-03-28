package com.example.chefswipe.PublishedRecipes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chefswipe.R;

import java.util.List;

public class PublishedAdapter extends RecyclerView.Adapter<PublishedViewHolders> {

    private List<PublishedObject> publishedList;
    private Context context;

    public PublishedAdapter(List<PublishedObject> publishedList, Context context) {
        this.publishedList = publishedList;
        this.context = context;

    }

    @NonNull
    @Override
    public PublishedViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publishedrecipes, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        PublishedViewHolders rcv = new PublishedViewHolders(layoutView);

        return rcv;

    }

    @Override
    public void onBindViewHolder(@NonNull PublishedViewHolders holder, int position) {


        holder.mRecipeID.setText(publishedList.get(position).getRecipeID());
        holder.mRecipeName.setText(publishedList.get(position).getRecipeName());
        Glide.with(context).load(publishedList.get(position).getRecipeURL()).into(holder.mRecipeImage);


    }

    @Override
    public int getItemCount() {
        return publishedList.size();
    }
}
