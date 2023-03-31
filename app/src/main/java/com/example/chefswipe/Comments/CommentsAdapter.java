package com.example.chefswipe.Comments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chefswipe.R;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private String[] localDataSet;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Textviews for comment and username
        private final TextView textView;
        private final TextView usernameView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textView = (TextView) view.findViewById(R.id.textView);
            usernameView = (TextView) view.findViewById(R.id.usernameView);

        }

        //Getters for message and username
        public TextView getTextView() {
            return textView;
        }
        public TextView getUsernameView() {
            return usernameView;
        }
    }

    //Set dataset to local dataset
    public CommentsAdapter(String[] dataSet) {
        localDataSet = dataSet;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_row_item, viewGroup, false);

        //Return current viewholder
        return new ViewHolder(view);
    }

    //Replace the contents of view
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        //Get element from your dataset at this position and replace the contents of the view with that element
        String username = localDataSet[position].split("&nbsp;", 2)[0];
        viewHolder.getUsernameView().setText(username);
        String comment = localDataSet[position].replaceAll(".+&nbsp;", "");
        viewHolder.getTextView().setText(comment);
    }

    //Return the size of dataset
    @Override
    public int getItemCount() {
        return localDataSet.length;
    }
}


