package com.example.android.bakingapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Ingredient;

/**
 * Created by Mauricio Torres
 * on 04/02/2018.
 */

public class RecipeIngredientListAdapter extends RecyclerView.Adapter<RecipeIngredientListAdapter.RecipeIngredientLisAdapterViewHolder> {
    private final Context mContext;
    private Ingredient[] ingredientsData;

    public RecipeIngredientListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecipeIngredientLisAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recipe_ingredient_list_item, parent, false);

        return new RecipeIngredientLisAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeIngredientLisAdapterViewHolder holder, int position) {
        if (getIngredientsData() == null) return;
        Ingredient ingredient = getIngredientsData()[position];
        holder.tv_ingred_name.setText(ingredient.Ingredient);
        holder.tv_ingred_qty.setText(String.valueOf(ingredient.Quantity));
        holder.tv_ingred_measure.setText(ingredient.Measure);
    }

    @Override
    public int getItemCount() {
        if (ingredientsData == null) return 0;
        else return ingredientsData.length;
    }

    private Ingredient[] getIngredientsData() {
        return ingredientsData;
    }

    public void swapData(Ingredient[] ingredients) {
        if (ingredients == null || ingredients.length == 0) {
            ingredientsData = null;
            this.notifyDataSetChanged();
            return;
        }

        ingredientsData = ingredients;
        this.notifyDataSetChanged();
    }

    public class RecipeIngredientLisAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView tv_ingred_name, tv_ingred_qty, tv_ingred_measure;

        public RecipeIngredientLisAdapterViewHolder(View itemView) {
            super(itemView);
            tv_ingred_name = itemView.findViewById(R.id.tv_ingredient_name);
            tv_ingred_qty = itemView.findViewById(R.id.tv_ingredient_qty);
            tv_ingred_measure = itemView.findViewById(R.id.tv_ingredient_measure);
        }
    }
}
