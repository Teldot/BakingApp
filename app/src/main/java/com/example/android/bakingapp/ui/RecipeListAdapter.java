package com.example.android.bakingapp.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.utils.BitmapUtility;

/**
 * Created by Mauricio Torres
 * on 04/02/2018.
 */

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeListAdapterViewHolder> {
    private Recipe[] recipesData;

    private final Context mContext;

    private final RecipeListAdapterOnClickHandler mClickHandler;

    public interface RecipeListAdapterOnClickHandler {
        void onClick(Recipe recipe);
    }

    RecipeListAdapter(RecipeListAdapterOnClickHandler onClickHandler, Context context) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    @Override
    public RecipeListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeListAdapterViewHolder holder, int position) {
        Recipe currentRecipe = getRecipesData()[position];
        if (currentRecipe == null) return;

        holder.mItemRecipeName.setText(currentRecipe.Name);
        String servings = mContext.getString(R.string.item_recipe_servings_text) + currentRecipe.Servings;
        holder.mItemServings.setText(servings);

        if (currentRecipe.Image != null && currentRecipe.Image.length > 0)
            holder.mItemRecipeImg.setImageBitmap(BitmapUtility.getImage(currentRecipe.Image));
    }

    @Override
    public int getItemCount() {
        if (recipesData == null) return 0;
        return recipesData.length;
    }

    void swapData(Recipe[] mData) {
        if (mData == null || mData.length == 0)
            recipesData = null;
        else
            recipesData = mData;
        this.notifyDataSetChanged();
    }

    private Recipe[] getRecipesData() {
        return recipesData;
    }

    public class RecipeListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView mItemRecipeName;
        final TextView mItemServings;
        final ImageView mItemRecipeImg;

        RecipeListAdapterViewHolder(View view) {
            super(view);
            mItemRecipeName = view.findViewById(R.id.item_recipe_name);
            mItemServings = view.findViewById(R.id.item_servings);
            mItemRecipeImg = view.findViewById(R.id.item_recipe_img);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Recipe recipe = getRecipesData()[adapterPosition];

            mClickHandler.onClick(recipe);
        }
    }
}
