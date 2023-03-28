package com.example.chefswipe.Comments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chefswipe.PublishedRecipes.PublishedObject;
import com.example.chefswipe.PublishedRecipes.PublishedViewHolders;
import com.example.chefswipe.R;

import java.util.List;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private String[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final TextView usernameView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.textView);
            usernameView = (TextView) view.findViewById(R.id.usernameView);

        }

        public TextView getTextView() {
            return textView;
        }
        public TextView getUsernameView() {
            return usernameView;
        }
    }

    public CommentsAdapter(String[] dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        String username = localDataSet[position].split("&nbsp;", 2)[0];
        viewHolder.getUsernameView().setText(username);
        String comment = localDataSet[position].replaceAll(".+&nbsp;", "");
        viewHolder.getTextView().setText(comment);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}


