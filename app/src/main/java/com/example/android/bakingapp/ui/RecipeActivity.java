package com.example.android.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.android.bakingapp.BuildConfig;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;

import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity implements RecipeStepListAdapter.RecipeStepListAdapterOnClickHandler {

    private ViewGroup recipeContainer, stepsContainer;
    private static final String K_SELECTED_RECIPE = "K_SELECTED_RECIPE";
    private static final String K_SELECTED_STEP_IDX = "K_SELECTED_STEP_IDX";
    private static final String K_IS_BIG_SCREEN = "K_IS_BIG_SCREEN";
    private static final String K_IS_CALLED_FROM_NOTIF = "K_IS_CALLED_FROM_NOTIF";

    private boolean CALLED_FROM_NOTIF;
    private boolean IS_BIG_SCREEN;
    private Recipe mRecipe;
    private int mStepIndx;
    StepFragment stepFragment;

//    @Override
//    public void onAttachFragment(Fragment fragment) {
//        super.onAttachFragment(fragment);
//        Timber.d("onAttachFragment");
//        if (CALLED_FROM_NOTIF) {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable(K_SELECTED_RECIPE, mRecipe);
//            bundle.putInt(K_SELECTED_STEP_IDX, mStepIndx);
//            bundle.putBoolean(K_IS_BIG_SCREEN, IS_BIG_SCREEN);
//            fragment.onCreateView(getLayoutInflater(), stepsContainer, bundle);
//            selectStep(mStepIndx);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        setContentView(R.layout.activity_recipe);
        Bundle recipeBundle = getIntent().getExtras();
        mStepIndx = 0;
        if (recipeBundle != null) {
            if (recipeBundle.containsKey(K_SELECTED_RECIPE))
                mRecipe = (Recipe) recipeBundle.getSerializable(K_SELECTED_RECIPE);
            if (recipeBundle.containsKey(K_SELECTED_STEP_IDX))
                mStepIndx = recipeBundle.getInt(K_SELECTED_STEP_IDX);
            if (recipeBundle.containsKey(K_IS_CALLED_FROM_NOTIF))
                CALLED_FROM_NOTIF = recipeBundle.getBoolean(K_IS_CALLED_FROM_NOTIF);
        }
        stepsContainer = (ViewGroup) findViewById(R.id.recipe_steps_fragment_container);
        IS_BIG_SCREEN = stepsContainer != null;
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
            if (IS_BIG_SCREEN) {
                stepFragment = new StepFragment();
//                if (fragmentManager.findFragmentByTag(StepFragment.class.getName()) == null)
//                    fragmentManager.beginTransaction().add(stepFragment, StepFragment.class.getName()).commit();
                fragmentManager.beginTransaction()
                        .replace(stepsContainer.getId(), stepFragment, stepFragment.getClass().getName())
                        .commit();
                if (mRecipe != null)
                    stepFragment.setStepData(mRecipe, mStepIndx, IS_BIG_SCREEN, this);
            }
        } else {
            IS_BIG_SCREEN = savedInstanceState.getBoolean(K_IS_BIG_SCREEN);
            mRecipe = (Recipe) savedInstanceState.getSerializable(K_SELECTED_RECIPE);
            mStepIndx = savedInstanceState.getInt(K_SELECTED_STEP_IDX);
        }
//        if (CALLED_FROM_NOTIF) {
//            boolean isAdded = stepFragment.isAdded();
//            //stepFragment.onCreateView(getLayoutInflater(), stepsContainer, recipeBundle);
//            selectStep(mStepIndx);
//        }
    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        currentState.putBoolean(K_IS_BIG_SCREEN, IS_BIG_SCREEN);
        currentState.putSerializable(K_SELECTED_RECIPE, mRecipe);
        currentState.putInt(K_SELECTED_STEP_IDX, mStepIndx);
        super.onSaveInstanceState(currentState);
    }


    @Override
    public void OnClickedSelectedStep(Step[] steps, int selStep) {
        selectStep(selStep);
    }

    private void selectStep(int selStep) {
        if (IS_BIG_SCREEN && stepFragment != null) {
            stepFragment.setStepData(mRecipe, selStep, IS_BIG_SCREEN, this);
            stepFragment.loadData();
        } else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(K_SELECTED_RECIPE, mRecipe);
            intent.putExtra(K_SELECTED_STEP_IDX, selStep);
            intent.putExtra(K_IS_BIG_SCREEN, IS_BIG_SCREEN);
            startActivity(intent);
        }
    }
}
