package com.example.android.bakingapp.ui;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Step;
import com.example.android.bakingapp.utils.BitmapUtility;
import com.example.android.bakingapp.utils.StepsUtility;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class StepFragment extends Fragment implements View.OnClickListener {

    private static final String K_STEPS_DATA = "K_STEPS_DATA";
    private static final String K_STEP_IDX = "K_STEP_IDX";
    private static final String K_MAX_LENGHT = "K_MAX_LENGHT";

    TextView tvStepDescription, tvStepShortDesc;
    Button btnNextStep, btnPvsStep;
    SimpleExoPlayerView stepVideo;
    private Step[] mSteps;
    private int mStepIdx, mMaxLenght;
    private Step mStepData;
    private String mNextStep, mPreviousStep;

    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        tvStepDescription = (TextView) rootView.findViewById(R.id.tv_step_description);
        tvStepShortDesc = (TextView) rootView.findViewById(R.id.tv_step_short_description);
        stepVideo = (SimpleExoPlayerView) rootView.findViewById(R.id.step_video);
        btnNextStep = (Button) rootView.findViewById(R.id.btn_next_step);
        btnNextStep.setOnClickListener(this);
        btnPvsStep = (Button) rootView.findViewById(R.id.btn_previous_step);
        btnPvsStep.setOnClickListener(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(K_STEPS_DATA)) {
                setStepData((Step[]) savedInstanceState.get(K_STEPS_DATA),
                        savedInstanceState.getInt(K_STEP_IDX),
                        savedInstanceState.getInt(K_MAX_LENGHT));
            }
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putSerializable(K_STEPS_DATA, mSteps);
        currentState.putInt(K_STEP_IDX, mStepIdx);

        super.onSaveInstanceState(currentState);
    }

    public void loadData() {
        if (getStepData() == null) return;
        if (getStepData().Thumbnail != null && getStepData().Thumbnail.length > 0) {
            Bitmap thumbnail = BitmapUtility.getImage(getStepData().Thumbnail);
            stepVideo.setDefaultArtwork(thumbnail);
        }
        int stpNbr = getStepData().Id;
        String srtDesc = (stpNbr == 0) ? getStepData().ShortDescription : stpNbr + ". " + getStepData().ShortDescription;
        tvStepDescription.setText(getStepData().Description);
        tvStepShortDesc.setText(srtDesc);
        btnPvsStep.setEnabled(mPreviousStep != null);
        btnNextStep.setEnabled(mNextStep != null);
        btnPvsStep.setText(mPreviousStep);
        btnNextStep.setText(mNextStep);
    }

    public Step getStepData() {
        return mStepData;
    }

    public void setStepData(Step[] steps, int stepId, int maxLength) {
        mSteps = steps;
        mStepIdx = stepId;
        mMaxLenght = maxLength;
        mStepData = steps[stepId];
        mNextStep = StepsUtility.getNextStepDesc(steps, stepId, maxLength);
        mPreviousStep = StepsUtility.getPreviousStepDesc(steps, stepId, maxLength);
    }

    @Override
    public void onClick(View v) {
        int selStep = 0;
        switch (v.getId()) {
            case R.id.btn_previous_step:
                selStep = mStepIdx - 1;
                break;
            case R.id.btn_next_step:
                selStep = mStepIdx + 1;
                break;
            default:
                break;
        }
        setStepData(mSteps, selStep, mMaxLenght);
        loadData();
    }
}
