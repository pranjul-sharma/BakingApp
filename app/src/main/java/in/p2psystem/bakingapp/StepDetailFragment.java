package in.p2psystem.bakingapp;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.p2psystem.bakingapp.model.RecipeStep;

public class StepDetailFragment extends Fragment {
    private static final String STEP_ARG = "step";
    private static final String PLAYER_PLAY_WHEN_READY = "player_play_state";
    private static final String PLAYER_PLAY_POSITION = "video_position";
    @Nullable
    @BindView(R.id.video_view)
    PlayerView playerView;
    @Nullable
    @BindView(R.id.step_short_desc)
    TextView shortDescTv;
    @Nullable
    @BindView(R.id.step_desc)
    TextView descTv;
    private boolean playWhenReady = true;
    private long seekPos = 0;
    private SimpleExoPlayer player;
    private RecipeStep step;


    public StepDetailFragment() {
        // Required empty public constructor
    }

    public static StepDetailFragment newInstance(RecipeStep step) {
        Bundle args = new Bundle();
        args.putParcelable(STEP_ARG, step);
        StepDetailFragment fragment = new StepDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            step = getArguments().getParcelable(STEP_ARG);
        }
    }

    public void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(), new DefaultLoadControl());

        if (playerView != null) playerView.setPlayer(player);


        //Checking if video URL is misplaced in thmubnail URL
        //and loading the video from appropriate link.
        String uriStr = null;
        if (!TextUtils.isEmpty(step.getThumbnailURL()))
            uriStr = step.getThumbnailURL();
        if (!TextUtils.isEmpty(step.getVideoURL()))
            uriStr = step.getVideoURL();
        if (!TextUtils.isEmpty(uriStr)) {
            Uri uri = Uri.parse(uriStr);
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource.Factory(
                    new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);

            player.prepare(mediaSource);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(seekPos);
        } else {
            playerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(), R.drawable.ic_placeholder));
            playerView.hideController();
            Toast.makeText(getContext(), R.string.no_video, Toast.LENGTH_SHORT).show();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        ButterKnife.bind(this, view);
        if (shortDescTv != null) shortDescTv.setText(step.getShortDescription());
        if (descTv != null) descTv.setText(step.getDescription());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null)
            initializePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null)
            releasePlayer();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (player != null) {
            //storing state in member variables in case if app is pushed to background
            //using home button and again comes into foreground
            playWhenReady = player.getPlayWhenReady();
            seekPos = player.getContentPosition();
            outState.putBoolean(PLAYER_PLAY_WHEN_READY, playWhenReady);
            outState.putLong(PLAYER_PLAY_POSITION, seekPos);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            playWhenReady = savedInstanceState.getBoolean(PLAYER_PLAY_WHEN_READY);
            seekPos = savedInstanceState.getLong(PLAYER_PLAY_POSITION);
        }
    }
}
