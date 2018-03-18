package com.example.android.bakingapp.ui;


import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;
import com.example.android.bakingapp.utils.BitmapUtility;
import com.example.android.bakingapp.utils.RecipeUtility;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static android.content.Context.NOTIFICATION_SERVICE;


public class StepFragment extends Fragment implements View.OnClickListener, ExoPlayer.EventListener {

    private static final String TAG = StepFragment.class.getName();
    private static final String K_SELECTED_RECIPE = "K_SELECTED_RECIPE";
    private static final String K_SELECTED_STEP_IDX = "K_SELECTED_STEP_IDX";
    private static final String K_IS_BIG_SCREEN = "K_IS_BIG_SCREEN";
    private static final String K_PLAYER_POSITION = "K_PLAYER_POSITION";

    private static final int NOTIFICATION_ID = 906;
    private boolean IS_BIG_SCREEN;
    private long playerPosition = 0;


    private TextView tvStepDescription, tvStepShortDesc;
    private Button btnNextStep, btnPvsStep;
    private Recipe mRecipe;
    private int mStepIdx;
    private Step mStepData;
    private String mNextStep, mPreviousStep;
    private Context mContext;
    private View rootView;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView stepPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen = false;

    public StepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_step, container, false);

        initViews();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(K_SELECTED_RECIPE)) {
                setStepData((Recipe) savedInstanceState.get(K_SELECTED_RECIPE),
                        savedInstanceState.getInt(K_SELECTED_STEP_IDX),
                        savedInstanceState.getBoolean(K_IS_BIG_SCREEN),
                        mContext,
                        savedInstanceState.getLong(K_PLAYER_POSITION));
            }
        }
        return rootView;
    }

    private void initViews() {
        tvStepDescription = rootView.findViewById(R.id.tv_step_description);
        tvStepShortDesc = rootView.findViewById(R.id.tv_step_short_description);
        stepPlayerView = rootView.findViewById(R.id.step_video);
        btnNextStep = rootView.findViewById(R.id.btn_next_step);
        btnNextStep.setOnClickListener(this);
        btnPvsStep = rootView.findViewById(R.id.btn_previous_step);
        btnPvsStep.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //initViews();
        loadData();
        initFullscreenDialog();
        Configuration newConfig = mContext.getResources().getConfiguration();
        if (!IS_BIG_SCREEN) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                openFullscreenDialog();
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                closeFullscreenDialog();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if (mMediaSession != null)
            mMediaSession.setActive(false);
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        if (mExoPlayer != null)
            playerPosition = mExoPlayer.getCurrentPosition();
        currentState.putSerializable(K_SELECTED_RECIPE, mRecipe);
        currentState.putInt(K_SELECTED_STEP_IDX, mStepIdx);
        currentState.putBoolean(K_IS_BIG_SCREEN, IS_BIG_SCREEN);
        currentState.putLong(K_PLAYER_POSITION, playerPosition);
        super.onSaveInstanceState(currentState);
    }

    public void loadData() {
        if (getStepData() == null) return;
        if (getStepData().Thumbnail != null && getStepData().Thumbnail.length > 0) {
            Bitmap thumbnail = BitmapUtility.getImage(getStepData().Thumbnail);
            stepPlayerView.setDefaultArtwork(thumbnail);
        }
        int stpNbr = getStepData().Id;
        String srtDesc = (stpNbr == 0) ? getStepData().ShortDescription : stpNbr + ". " + getStepData().ShortDescription;
        tvStepDescription.setText(getStepData().Description);
        tvStepShortDesc.setText(srtDesc);

        boolean IS_ANY_PREV_STEP = mPreviousStep != null;
        boolean IS_ANY_NXT_STEP = mNextStep != null;

        btnPvsStep.setVisibility(IS_ANY_PREV_STEP ? View.VISIBLE : View.GONE);
        btnNextStep.setVisibility(IS_ANY_NXT_STEP ? View.VISIBLE : View.GONE);
        btnPvsStep.setText(mPreviousStep);
        btnNextStep.setText(mNextStep);
        Uri videoUri = null;

        releasePlayer();
        initializeMediaSession();
        if (getStepData().VideoURL != null && getStepData().VideoURL.length() > 0) {
            rootView.findViewById(R.id.player_frame).setVisibility(View.VISIBLE);
            stepPlayerView.setVisibility(View.VISIBLE);
            videoUri = Uri.parse(getStepData().VideoURL);
        } else {
            playerPosition = 0;
            rootView.findViewById(R.id.player_frame).setVisibility(View.GONE);
            stepPlayerView.setVisibility(View.GONE);
        }
        initializePlayer(videoUri);
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) stepPlayerView.getParent()).removeView(stepPlayerView);
        mFullScreenDialog.addContentView(stepPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {

        ((ViewGroup) stepPlayerView.getParent()).removeView(stepPlayerView);
        ((FrameLayout) rootView.findViewById(R.id.player_frame)).addView(stepPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
    }

    private Step getStepData() {
        return mStepData;
    }

    public void setStepData(Recipe recipe, int stepId, boolean isBigScreen, Context context, long playerPos) {
        mContext = context;
        int maxLength = mContext.getResources().getInteger(R.integer.step_desc_max_length);
        IS_BIG_SCREEN = isBigScreen;
        mRecipe = recipe;
        mStepIdx = stepId;
        mStepData = recipe.Steps[stepId];
        mNextStep = RecipeUtility.getNextStepDesc(recipe.Steps, stepId, maxLength);
        mPreviousStep = RecipeUtility.getPreviousStepDesc(recipe.Steps, stepId, maxLength);
        playerPosition = playerPos;
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
        setStepData(mRecipe, selStep, IS_BIG_SCREEN, mContext, playerPosition);
        loadData();
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(mContext, TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_REWIND |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new SessionCallback());
        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri mediaUri) {
        if (mediaUri != null) {
            if (mExoPlayer == null) {
                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
                stepPlayerView.setPlayer(mExoPlayer);

                // Set the ExoPlayer.EventListener to this activity.
                mExoPlayer.addListener(this);

                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(mContext, mContext.getApplicationInfo().name);
                MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                        mContext, userAgent), new DefaultExtractorsFactory(), null, null);
                mExoPlayer.prepare(mediaSource);
                mExoPlayer.seekTo(playerPosition);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    private void releasePlayer() {
        if (mNotificationManager != null) mNotificationManager.cancelAll();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    //EXOPLAYER EVENTS
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
        showNotification(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private class SessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }

        @Override
        public void onRewind() {
            mExoPlayer.seekTo(0);
        }
    }

    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        int icon;
        String play_pause;
        long action;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.exo_controls_pause_description);
            action = PlaybackStateCompat.ACTION_PAUSE;
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.exo_controls_play_description);
            action = PlaybackStateCompat.ACTION_PLAY;
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext, action));

        NotificationCompat.Action restartAction = new NotificationCompat.Action(
                R.drawable.exo_controls_previous, getString(R.string.exo_controls_rewind_description),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (mContext, PlaybackStateCompat.ACTION_REWIND));


        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (mContext, 0, new Intent(mContext, StepFragment.class), 0);

        builder.setContentTitle(mRecipe.Name)
                .setContentText(getStepData().ShortDescription)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_recipe)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));


        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
