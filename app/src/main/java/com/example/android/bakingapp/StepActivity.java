package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.android.bakingapp.data.entities.Step;
import com.example.android.bakingapp.ui.StepFragment;

public class StepActivity extends AppCompatActivity {
    private ViewGroup stepContainer;
    private static final String K_SELECTED_STEP = "K_SELECTED_STEP";
    private Step mStep;
    StepFragment stepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Bundle stepBundle = getIntent().getExtras();
        if (stepBundle != null && stepBundle.containsKey(K_SELECTED_STEP))
            mStep = (Step) stepBundle.getSerializable(K_SELECTED_STEP);

        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {
            //STEPS
            stepContainer = (ViewGroup) findViewById(R.id.step_fragment_container);
            stepFragment = new StepFragment();
            stepFragment.setStepData(mStep);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(stepContainer.getId(), stepFragment, stepFragment.getClass().getName())
                    .commit();

        } else {
            mStep = (Step) savedInstanceState.getSerializable(K_SELECTED_STEP);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle currentState) {
        currentState.putSerializable(K_SELECTED_STEP, mStep);
        super.onSaveInstanceState(currentState);
    }
}
