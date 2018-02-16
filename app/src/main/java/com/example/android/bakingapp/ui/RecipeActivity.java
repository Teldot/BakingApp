package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;

public class RecipeActivity extends AppCompatActivity implements RecipeStepListAdapter.RecipeStepListAdapterOnClickHandler {

    private ViewGroup recipeContainer, stepsContainer;
    private static final String K_SELECTED_RECIPE = "K_SELECTED_RECIPE";
    private static final String K_SELECTED_STEP_IDX = "K_SELECTED_STEP_IDX";
    private static final String K_STEPS = "K_STEPS";
    private static final String K_IS_BIG_SCREEN = "K_IS_BIG_SCREEN";
    private boolean IS_BIG_SCREEN;
    private Recipe mRecipe;
    StepFragment stepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Bundle recipeBundle = getIntent().getExtras();
        if (recipeBundle != null && recipeBundle.containsKey(K_SELECTED_RECIPE))
            mRecipe = (Recipe) recipeBundle.getSerializable(K_SELECTED_RECIPE);

        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {
            //RECIPE
            recipeContainer = (ViewGroup) findViewById(R.id.recipe_fragment_container);
            RecipeFragment recipeFragment = new RecipeFragment();
            recipeFragment.setRecipeData(mRecipe);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(recipeContainer.getId(), recipeFragment, recipeFragment.getClass().getName())
                    .commit();


            //STEPS
            stepsContainer = (ViewGroup) findViewById(R.id.recipe_steps_fragment_container);
            IS_BIG_SCREEN = stepsContainer != null;
            if (IS_BIG_SCREEN) {
                stepFragment = new StepFragment();
                if (mRecipe.Steps != null && mRecipe.Steps.length > 0) {
                    int cntStep = 0;
                    stepFragment.setStepData(mRecipe.Steps, cntStep, getResources().getInteger(R.integer.step_desc_max_length));
                }
                fragmentManager.beginTransaction()
                        .replace(stepsContainer.getId(), stepFragment, stepFragment.getClass().getName())
                        .commit();
            }
        } else {
            IS_BIG_SCREEN = savedInstanceState.getBoolean(K_IS_BIG_SCREEN);
            mRecipe = (Recipe) savedInstanceState.getSerializable(K_SELECTED_RECIPE);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        currentState.putBoolean(K_IS_BIG_SCREEN, IS_BIG_SCREEN);
        currentState.putSerializable(K_SELECTED_RECIPE, mRecipe);
        super.onSaveInstanceState(currentState);
    }


    @Override
    public void OnClickedSelectedStep(Step[] steps, int selStep) {
        int mxLgt = getResources().getInteger(R.integer.step_desc_max_length);
        if (IS_BIG_SCREEN && stepFragment != null) {
            stepFragment.setStepData(steps, selStep, getResources().getInteger(R.integer.step_desc_max_length));
            stepFragment.loadData();
        } else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(K_STEPS, steps);
            intent.putExtra(K_SELECTED_STEP_IDX, selStep);
            startActivity(intent);
        }
    }
}
