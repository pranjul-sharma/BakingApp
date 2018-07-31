package in.p2psystem.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import in.p2psystem.bakingapp.model.Recipe;
import in.p2psystem.bakingapp.model.RecipeStep;

import static in.p2psystem.bakingapp.StepDetailActivity.STEP_KEY;
import static in.p2psystem.bakingapp.StepDetailActivity.STEP_KEY_SELECTED;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnRecipeStepClickListener {
    public static final String RECIPE_KEY = "recipe";
    boolean twoPane;
    Recipe recipe;
    Menu favoriteMenu;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recipe = getIntent().getParcelableExtra(RECIPE_KEY);

        sharedPreferences = getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);

        if (sharedPreferences.getString(getString(R.string.prefs_key), null) == null) {
            updateFavoriteAndWidget();
        }

        if (recipe == null)
            Toast.makeText(this, R.string.unable_to_load, Toast.LENGTH_SHORT).show();


        if (findViewById(R.id.recipe_detail_container) != null) {
            twoPane = true;
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .add(R.id.recipe_detail_container, RecipeDetailFragment.newInstance(recipe))
                    .commit();

            manager.beginTransaction()
                    .add(R.id.fragment_container, StepDetailFragment.newInstance(recipe.getRecipeSteps().get(0)))
                    .commit();
        } else {
            twoPane = false;
            if (savedInstanceState == null) {
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .add(R.id.fragment_container, RecipeDetailFragment.newInstance(recipe))
                        .commit();
            }
        }

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        favoriteMenu = menu;
        updateMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateMenu() {
        if (sharedPreferences != null) {
            String recipeInPrefsJson = sharedPreferences.getString(getString(R.string.prefs_key), null);
            Recipe recipeInPrefs = new Gson().fromJson(recipeInPrefsJson, Recipe.class);
            if (recipeInPrefs.getRecipeName().equals(recipe.getRecipeName())) {
                favoriteMenu.findItem(R.id.action_un_favorite).setVisible(true);
                favoriteMenu.findItem(R.id.action_add_favorite).setVisible(false);
            } else {
                favoriteMenu.findItem(R.id.action_un_favorite).setVisible(false);
                favoriteMenu.findItem(R.id.action_add_favorite).setVisible(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (id == R.id.action_add_favorite) {
            updateFavoriteAndWidget();
            updateMenu();
            Snackbar.make(getWindow().getDecorView(), R.string.added_to_favorite, Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_un_favorite) {
            Snackbar.make(getWindow().getDecorView(), R.string.unfavorite_current, Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateFavoriteAndWidget() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String jsonStr = new Gson().toJson(recipe);
        Log.v(RECIPE_KEY, jsonStr);
        editor.putString(getString(R.string.prefs_key), jsonStr);
        editor.apply();

        Intent intent = new Intent(this, IngredientWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), IngredientWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return true;
    }

    @Override
    public void onRecipeClicked(int position) {
        if (position >= 0) {
            if (twoPane) {
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, StepDetailFragment.newInstance(recipe.getRecipeSteps().get(position)))
                        .commit();
            } else {
                Intent intent = new Intent(this, StepDetailActivity.class);
                intent.putParcelableArrayListExtra(STEP_KEY, (ArrayList<RecipeStep>) recipe.getRecipeSteps());
                intent.putExtra(STEP_KEY_SELECTED, position);
                startActivity(intent);
            }
        }
    }
}
