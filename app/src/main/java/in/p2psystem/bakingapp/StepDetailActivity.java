package in.p2psystem.bakingapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.p2psystem.bakingapp.model.RecipeStep;

public class StepDetailActivity extends AppCompatActivity {

    public static final String STEP_KEY = "step_to_display";
    public static final String STEP_KEY_SELECTED = "selected_step";
    private static final String BUNDLE_CURRENT_STEP = "current_step_being_displayed";
    @BindView(R.id.btn_prev)
    Button prevBtn;
    @BindView(R.id.btn_next)
    Button nextBtn;
    @BindView(R.id.gap_bottom)
    View gapView;
    List<RecipeStep> steps;
    private List<Integer> onStackList = new ArrayList<>();
    private int position;
    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean orientationLand = false;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientationLand = true;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            if (getSupportActionBar() != null) getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!orientationLand) {
            prevBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
            gapView.setVisibility(View.VISIBLE);
            prevBtn.setClickable(false);
            nextBtn.setClickable(false);
        }
        steps = getIntent().getParcelableArrayListExtra(STEP_KEY);
        position = getIntent().getIntExtra(STEP_KEY_SELECTED, 0);
        manager = getSupportFragmentManager();
        //Checking savedInstanceState so that fragment is not initialized twice during configuration changes since
        // android recreates fragment automatically from previous state in case of configuration changes i.e. device rotations.
        if (savedInstanceState == null) {
            manager.beginTransaction()
                    .add(R.id.fragment_container, StepDetailFragment.newInstance(steps.get(position)))
                    .commit();
        }

        if (position != 0)
            prevBtn.setClickable(true);

        if (position != steps.size() - 1)
            nextBtn.setClickable(true);
    }

    @OnClick(R.id.btn_prev)
    public void loadPrevStep(View view) {

        if (position == 0) {
            prevBtn.setClickable(false);
            nextBtn.setClickable(true);
        } else {
            RecipeStep step = steps.get(--position);
            prevBtn.setClickable(true);
            manager.beginTransaction()
                    .replace(R.id.fragment_container, StepDetailFragment.newInstance(step))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick(R.id.btn_next)
    public void loadNextStep(View view) {
        if (position == steps.size() - 1) {
            nextBtn.setClickable(false);
            prevBtn.setClickable(true);
        } else {
            position += 1;
            nextBtn.setClickable(true);
            if (!onStackList.contains(position)) {
                onStackList.add(position);
                manager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, StepDetailFragment.newInstance(steps.get(position)))
                        .commit();
            } else manager.beginTransaction()
                    .replace(R.id.fragment_container, StepDetailFragment.newInstance(steps.get(position)))
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_CURRENT_STEP, steps.get(position));

    }
}
