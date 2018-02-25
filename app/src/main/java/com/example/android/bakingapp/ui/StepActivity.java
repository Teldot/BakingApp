package com.example.android.bakingapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Recipe;

public class StepActivity extends AppCompatActivity {
    private ViewGroup stepContainer;
    private static final String K_SELECTED_RECIPE = "K_SELECTED_RECIPE";
    private static final String K_SELECTED_STEP_IDX = "K_SELECTED_STEP_IDX";
    private static final String K_IS_BIG_SCREEN = "K_IS_BIG_SCREEN";
    private Recipe mRecipe;
    private int mStepIndex;
    private boolean IS_BIG_SCREEN;
    private StepFragment stepFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Bundle stepBundle = getIntent().getExtras();
        if (stepBundle != null && stepBundle.containsKey(K_SELECTED_RECIPE)) {
            mRecipe = (Recipe) stepBundle.getSerializable(K_SELECTED_RECIPE);
            mStepIndex = stepBundle.getInt(K_SELECTED_STEP_IDX);
            IS_BIG_SCREEN = stepBundle.getBoolean(K_IS_BIG_SCREEN);
        }

        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {
            //STEPS

            stepContainer = (ViewGroup) findViewById(R.id.step_fragment_container);
            stepFragment = new StepFragment();
            stepFragment.setStepData(mRecipe, mStepIndex, IS_BIG_SCREEN, this);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(stepContainer.getId(), stepFragment, stepFragment.getClass().getName())
                    .commit();

        } else {
            mRecipe = (Recipe) savedInstanceState.getSerializable(K_SELECTED_RECIPE);
            mStepIndex = savedInstanceState.getInt(K_SELECTED_STEP_IDX);
            IS_BIG_SCREEN = savedInstanceState.getBoolean(K_IS_BIG_SCREEN);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        currentState.putSerializable(K_SELECTED_RECIPE, mRecipe);
        currentState.putInt(K_SELECTED_STEP_IDX, mStepIndex);
        currentState.putBoolean(K_IS_BIG_SCREEN, IS_BIG_SCREEN);
        super.onSaveInstanceState(currentState);
    }

}
