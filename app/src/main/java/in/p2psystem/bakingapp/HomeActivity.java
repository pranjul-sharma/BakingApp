package in.p2psystem.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.p2psystem.bakingapp.IdlingResources.HomeIdlingResources;
import in.p2psystem.bakingapp.adapters.RecipeListAdapter;
import in.p2psystem.bakingapp.model.Recipe;
import in.p2psystem.bakingapp.util.JsonParseUtils;

public class HomeActivity extends AppCompatActivity {

    private final static String RECIPE_DATA = "recipe_bundle_data_tag";
    List<Recipe> mRecipes;
    @BindView(R.id.recycler_home)
    RecyclerView mRecyclerView;
    @BindView(R.id.data_loading_indicator)
    ProgressBar mLoadingIndicator;
    @Nullable
    private HomeIdlingResources mIdlingResource;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipes != null)
            outState.putParcelableArrayList(RECIPE_DATA, (ArrayList<Recipe>) mRecipes);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mLoadingIndicator.setVisibility(View.VISIBLE);

        if (mIdlingResource != null)
            mIdlingResource.setIdleState(false);

        if (savedInstanceState != null) {
            mRecipes = savedInstanceState.getParcelableArrayList(RECIPE_DATA);
            if (mIdlingResource != null)
                mIdlingResource.setIdleState(true);
            processData(null);
        } else {
            loadDataFromNetwork();
        }
    }

    private void loadDataFromNetwork() {
        final String dataUrl = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, dataUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processData(response);
                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mLoadingIndicator.setVisibility(View.GONE);
                        Snackbar.make(getWindow().getDecorView(), R.string.error_network, Snackbar.LENGTH_SHORT).show();
                        if (mIdlingResource != null) {
                            mIdlingResource.setIdleState(true);
                        }
                    }
                });
        queue.add(request);
    }

    private void processData(String data) {
        if (data != null)
            mRecipes = JsonParseUtils.parseRecipeData(data);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) manager.getDefaultDisplay().getMetrics(metrics);
        GridLayoutManager gridLayoutManager;
        if (metrics.widthPixels >= 800)
            gridLayoutManager = new GridLayoutManager(this, 3);
        else
            gridLayoutManager = new GridLayoutManager(this, 1);
        RecipeListAdapter adapter = new RecipeListAdapter(this, mRecipes);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mLoadingIndicator.setVisibility(View.GONE);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new HomeIdlingResources();
        }
        return mIdlingResource;
    }
}
