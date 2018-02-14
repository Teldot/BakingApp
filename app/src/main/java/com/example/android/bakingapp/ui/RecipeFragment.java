package com.example.android.bakingapp.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment implements RecipeStepListAdapter.RecipeStepListAdapterOnClickHandler {

    private static final String RECIPE_DATA = "RECIPE_DATA";
    Context mContext;
    RecyclerView rvRecipeStepsList;
    RecyclerView rvRecipeIngredientsList;
    RecipeStepListAdapter stepListAdapter;
    RecipeIngredientListAdapter ingredientListAdapter;
    private Recipe recipeData;

    TextView tvRecipeName;
    Button btnShare;


    public RecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        rvRecipeStepsList = (RecyclerView) rootView.findViewById(R.id.rv_recipe_steps_list);
        rvRecipeIngredientsList = (RecyclerView) rootView.findViewById(R.id.rv_recipe_ingredients_list);
        tvRecipeName = (TextView) rootView.findViewById(R.id.tv_recipe_name);
        btnShare = (Button) rootView.findViewById(R.id.btn_share);

        GridLayoutManager stepLayoutManager = new GridLayoutManager(mContext, 1);
        GridLayoutManager ingredLayoutManager = new GridLayoutManager(mContext, 1);
        rvRecipeStepsList.setLayoutManager(stepLayoutManager);
        rvRecipeStepsList.setHasFixedSize(true);
        rvRecipeIngredientsList.setLayoutManager(ingredLayoutManager);
        rvRecipeIngredientsList.setHasFixedSize(true);

        stepListAdapter = new RecipeStepListAdapter(this, mContext);
        ingredientListAdapter = new RecipeIngredientListAdapter(mContext);

        rvRecipeStepsList.setAdapter(stepListAdapter);
        rvRecipeIngredientsList.setAdapter(ingredientListAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_DATA))
            setRecipeData((Recipe) savedInstanceState.get(RECIPE_DATA));

        loadData();

        return rootView;
    }

    public void loadData() {
        if (getRecipeData() == null) return;
        stepListAdapter.swapData(getRecipeData().Steps);
        ingredientListAdapter.swapData(getRecipeData().Ingredients);

        tvRecipeName.setText(getRecipeData().Name);
    }

    @Override
    public void OnClick(Step step) {

    }

    public Recipe getRecipeData() {
        return recipeData;
    }

    public void setRecipeData(Recipe recipeData) {
        this.recipeData = recipeData;
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putSerializable(RECIPE_DATA, recipeData);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
