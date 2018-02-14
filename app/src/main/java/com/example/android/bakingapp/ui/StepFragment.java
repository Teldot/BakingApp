package com.example.android.bakingapp.ui;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Step;
import com.example.android.bakingapp.utils.BitmapUtility;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class StepFragment extends Fragment {

    private static final String STEP_DATA = "STEP_DATA";
    TextView tvStepDescription;
    SimpleExoPlayerView stepVideo;
    private Step stepData;

    public StepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        tvStepDescription = (TextView) rootView.findViewById(R.id.tv_step_description);
        stepVideo = (SimpleExoPlayerView) rootView.findViewById(R.id.step_video);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STEP_DATA)) {
                setStepData((Step) savedInstanceState.get(STEP_DATA));
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
        currentState.putSerializable(STEP_DATA, stepData);
        super.onSaveInstanceState(currentState);
    }

    public void loadData() {
        if (getStepData() == null) return;
        if (getStepData().Thumbnail != null && getStepData().Thumbnail.length > 0) {
            Bitmap thumbnail = BitmapUtility.getImage(getStepData().Thumbnail);
            stepVideo.setDefaultArtwork(thumbnail);
        }
        tvStepDescription.setText(getStepData().Description);

    }

    public Step getStepData() {
        return stepData;
    }

    public void setStepData(Step stepData) {
        this.stepData = stepData;
    }
}
