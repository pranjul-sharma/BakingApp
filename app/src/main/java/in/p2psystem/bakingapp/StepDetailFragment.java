package in.p2psystem.bakingapp;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    private static final String TAG = StepDetailFragment.class.getSimpleName();
    private static final String STEP_ARG = "step";

    @Nullable
    @BindView(R.id.video_view)
    PlayerView playerView;
    @Nullable
    @BindView(R.id.step_short_desc)
    TextView shortDescTv;
    @Nullable
    @BindView(R.id.step_desc)
    TextView descTv;
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


        String uriStr = null;
        if (step.getThumbnailURL() != null && !step.getThumbnailURL().equals(""))
            uriStr = step.getThumbnailURL();
        if (step.getVideoURL() != null && !step.getVideoURL().equals(""))
            uriStr = step.getVideoURL();
        if (uriStr != null && !uriStr.equals("")) {
            Uri uri = Uri.parse(uriStr);
            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource.Factory(
                    new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);

            player.prepare(mediaSource);
            player.setPlayWhenReady(true);

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
        initializePlayer();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null)
            releasePlayer();
    }

}
