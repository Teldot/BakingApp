package com.example.android.bakingapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Step;

public class StepActivity extends AppCompatActivity {
    private ViewGroup stepContainer;
    private static final String K_SELECTED_STEP_IDX = "K_SELECTED_STEP_IDX";
    private static final String K_STEPS = "K_STEPS";
    private Step[] mSteps;
    private int mStepIndex;
    private StepFragment stepFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Bundle stepBundle = getIntent().getExtras();
        if (stepBundle != null && stepBundle.containsKey(K_STEPS)) {
            mSteps = (Step[]) stepBundle.getSerializable(K_STEPS);
            mStepIndex = stepBundle.getInt(K_SELECTED_STEP_IDX);
        }

        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {
            //STEPS

            stepContainer = (ViewGroup) findViewById(R.id.step_fragment_container);
            stepFragment = new StepFragment();
            stepFragment.setStepData(mSteps, mStepIndex, this);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(stepContainer.getId(), stepFragment, stepFragment.getClass().getName())
                    .commit();

        } else {
            mSteps = (Step[]) savedInstanceState.getSerializable(K_STEPS);
            mStepIndex = savedInstanceState.getInt(K_SELECTED_STEP_IDX);

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        currentState.putSerializable(K_STEPS, mSteps);
        currentState.putInt(K_SELECTED_STEP_IDX, mStepIndex);
        super.onSaveInstanceState(currentState);
    }

}
