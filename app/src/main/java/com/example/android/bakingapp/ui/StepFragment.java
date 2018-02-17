package com.example.android.bakingapp.ui;


import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Step;
import com.example.android.bakingapp.utils.BitmapUtility;
import com.example.android.bakingapp.utils.StepsUtility;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class StepFragment extends Fragment implements View.OnClickListener, ExoPlayer.EventListener {

    private static final String TAG = StepFragment.class.getName();
    private static final String K_STEPS_DATA = "K_STEPS_DATA";
    private static final String K_STEP_IDX = "K_STEP_IDX";

    private TextView tvStepDescription, tvStepShortDesc;
    private Button btnNextStep, btnPvsStep;
    private ImageButton btnPlayerControlNext, btnPlayerControlPrev;
    private Step[] mSteps;
    private int mStepIdx;
    private Step mStepData;
    private String mNextStep, mPreviousStep;
    private Context mContext;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView stepPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;

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
        stepPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.step_video);
        btnNextStep = (Button) rootView.findViewById(R.id.btn_next_step);
        btnNextStep.setOnClickListener(this);
        btnPvsStep = (Button) rootView.findViewById(R.id.btn_previous_step);
        btnPvsStep.setOnClickListener(this);
        btnPlayerControlNext = (ImageButton) stepPlayerView.findViewById(R.id.button_player_control_next);
        btnPlayerControlNext.setOnClickListener(this);
        btnPlayerControlPrev = (ImageButton) stepPlayerView.findViewById(R.id.button_player_control_previous);
        btnPlayerControlPrev.setOnClickListener(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(K_STEPS_DATA)) {
                setStepData((Step[]) savedInstanceState.get(K_STEPS_DATA),
                        savedInstanceState.getInt(K_STEP_IDX), mContext);
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
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
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
            stepPlayerView.setDefaultArtwork(thumbnail);
        }
        int stpNbr = getStepData().Id;
        String srtDesc = (stpNbr == 0) ? getStepData().ShortDescription : stpNbr + ". " + getStepData().ShortDescription;
        tvStepDescription.setText(getStepData().Description);
        tvStepShortDesc.setText(srtDesc);
        btnPvsStep.setVisibility(mPreviousStep == null ? View.GONE : View.VISIBLE);
        btnPlayerControlPrev.setVisibility(mPreviousStep == null ? View.GONE : View.VISIBLE);
        btnNextStep.setVisibility(mNextStep == null ? View.GONE : View.VISIBLE);
        btnPlayerControlNext.setVisibility(mNextStep == null ? View.GONE : View.VISIBLE);
        btnPvsStep.setText(mPreviousStep);
        btnNextStep.setText(mNextStep);
        Uri videoUri = null;
        releasePlayer();
        initializeMediaSession();
        if (getStepData().VideoURL != null && getStepData().VideoURL.length() > 0) {
            stepPlayerView.setVisibility(View.VISIBLE);
            videoUri = Uri.parse(getStepData().VideoURL);
        } else {
            stepPlayerView.setVisibility(View.GONE);
        }
        initializePlayer(videoUri);
    }

    public Step getStepData() {
        return mStepData;
    }

    public void setStepData(Step[] steps, int stepId, Context context) {
        mContext = context;
        int maxLength = mContext.getResources().getInteger(R.integer.step_desc_max_length);
        mSteps = steps;
        mStepIdx = stepId;
        mStepData = steps[stepId];
        mNextStep = StepsUtility.getNextStepDesc(steps, stepId, maxLength);
        mPreviousStep = StepsUtility.getPreviousStepDesc(steps, stepId, maxLength);
    }

    @Override
    public void onClick(View v) {
        int selStep = 0;
        switch (v.getId()) {
            case R.id.button_player_control_previous:
            case R.id.btn_previous_step:
                selStep = mStepIdx - 1;
                break;
            case R.id.button_player_control_next:
            case R.id.btn_next_step:
                selStep = mStepIdx + 1;
                break;
            default:
                break;
        }
        setStepData(mSteps, selStep, mContext);
        loadData();
    }

    private void initializeMediaSession() {

        mMediaSession = new MediaSessionCompat(mContext, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_NEXT);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // SessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new SessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null && mediaUri != null) {
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
            mExoPlayer.setPlayWhenReady(true);
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
//            mExoPlayer.seekTo(0);
            btnPvsStep.performClick();
        }

        @Override
        public void onSkipToNext() {
//            mExoPlayer.seekTo(0);
            btnNextStep.performClick();
        }
    }

    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.exo_controls_pause_description);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.exo_controls_play_description);
        }


        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.exo_controls_previous_description),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (mContext, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        NotificationCompat.Action nextPauseAction = new NotificationCompat.Action(
                R.drawable.exo_controls_next, getString(R.string.exo_controls_next_description),
                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT));

//        PendingIntent contentPendingIntent = PendingIntent.getActivity
//                (this, 0, new Intent(this, QuizActivity.class), 0);

        builder.setContentTitle(getStepData().ShortDescription)
                .setContentText(getString(R.string.notification_text))
                //.setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_recipe)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .addAction(nextPauseAction)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));


        mNotificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }
}
