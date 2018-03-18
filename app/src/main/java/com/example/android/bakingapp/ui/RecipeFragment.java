package com.example.android.bakingapp.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeFragment extends Fragment implements RecipeStepListAdapter.RecipeStepListAdapterOnClickHandler {

    private static final String RECIPE_DATA = "RECIPE_DATA";
    private RecipeStepListAdapter stepListAdapter;
    private RecipeIngredientListAdapter ingredientListAdapter;
    private Recipe recipeData;
    private RecipeStepListAdapter.RecipeStepListAdapterOnClickHandler parentListenerHandler;

    private TextView tvRecipeName;

    public RecipeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentListenerHandler = (RecipeStepListAdapter.RecipeStepListAdapterOnClickHandler) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context mContext = getContext();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        RecyclerView rvRecipeStepsList = rootView.findViewById(R.id.rv_recipe_steps_list);
        RecyclerView rvRecipeIngredientsList = rootView.findViewById(R.id.rv_recipe_ingredients_list);
        tvRecipeName = rootView.findViewById(R.id.tv_recipe_name);

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

    private void loadData() {
        if (getRecipeData() == null) return;
        stepListAdapter.swapData(getRecipeData().Steps);
        ingredientListAdapter.swapData(getRecipeData().Ingredients);

        tvRecipeName.setText(getRecipeData().Name);
    }

    @Override
    public void OnClickedSelectedStep(Step[] step, int selStep) {
        parentListenerHandler.OnClickedSelectedStep(step, selStep);
    }

    private Recipe getRecipeData() {
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
        super.onSaveInstanceState(currentState);
    }

}
