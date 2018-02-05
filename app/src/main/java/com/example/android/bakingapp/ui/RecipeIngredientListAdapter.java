package com.example.android.bakingapp.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mauricio Torres on 04/02/2018.
 */

public class RecipeIngredientListAdapter extends RecyclerView.Adapter<RecipeIngredientListAdapter.RecipeIngredientLisAdapterViewHolder> {


    @Override
    public RecipeIngredientLisAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecipeIngredientLisAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecipeIngredientLisAdapterViewHolder extends RecyclerView.ViewHolder{

        public RecipeIngredientLisAdapterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
