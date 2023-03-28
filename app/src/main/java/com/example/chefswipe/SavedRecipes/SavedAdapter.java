package com.example.chefswipe.SavedRecipes;

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

public class SavedAdapter extends RecyclerView.Adapter<SavedViewHolders> {

    private List<SavedObject> savedList;
    private Context context;

    public SavedAdapter(List<SavedObject> savedList, Context context) {
        this.savedList = savedList;
        this.context = context;

    }

    @NonNull
    @Override
    public SavedViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_savedrecipes, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        SavedViewHolders rcv = new SavedViewHolders(layoutView);

        return rcv;

    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewHolders holder, int position) {

        holder.mRecipeName.setText(savedList.get(position).getRecipeName());
        holder.mRecipeID.setText(savedList.get(position).getRecipeID());
        Glide.with(context).load(savedList.get(position).getRecipeURL()).into(holder.mRecipeImage);


    }

    @Override
    public int getItemCount() {
        return savedList.size();
    }
}
