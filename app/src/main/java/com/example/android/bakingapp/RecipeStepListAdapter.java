package com.example.android.bakingapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by US on 04/02/2018.
 */

public class RecipeStepListAdapter extends RecyclerView.Adapter<RecipeStepListAdapter.RecipeStepListAdapterViewHolder> {

    @Override
    public RecipeStepListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecipeStepListAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecipeStepListAdapterViewHolder extends RecyclerView.ViewHolder{

        public RecipeStepListAdapterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
