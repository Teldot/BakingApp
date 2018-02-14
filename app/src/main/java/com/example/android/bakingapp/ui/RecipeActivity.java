package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;

public class RecipeActivity extends AppCompatActivity implements OnRecipeStepSelectedListener {

    private ViewGroup recipeContainer, stepsContainer;
    private static final String K_SELECTED_RECIPE = "K_SELECTED_RECIPE";
    private static final String K_SELECTED_STEP = "K_SELECTED_STEP";
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
                    stepFragment.setStepData(mRecipe.Steps[0]);
                    //stepFragment.loadData();
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
    public void OnRecipeStepSelected(Step step) {
        if (IS_BIG_SCREEN && stepFragment != null) {
            stepFragment.setStepData(step);
            stepFragment.loadData();
        } else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(K_SELECTED_STEP, step);
            startActivity(intent);
        }
    }
}
