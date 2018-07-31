package in.p2psystem.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import in.p2psystem.bakingapp.model.Recipe;

import static in.p2psystem.bakingapp.RecipeDetailActivity.RECIPE_KEY;

public class IngredientWidgetProvider extends AppWidgetProvider {

    static Recipe recipe;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.prefs_name), Context.MODE_PRIVATE);
        String recipeJSON = preferences.getString(context.getString(R.string.prefs_key), null);

        recipe = new Gson().fromJson(recipeJSON, Recipe.class);
        Intent intent = new Intent(context, RecipeDetailActivity.class);
        intent.putExtra(RECIPE_KEY, recipe);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredient_widget_provider);
        if (recipe != null) {
            views.setTextViewText(R.id.appwidget_text, recipe.getRecipeName());
            views.setTextViewText(R.id.ingredients_widget_text, recipe.getRecipeIngredientsList());
        } else {
            views.setTextViewText(R.id.appwidget_text, context.getString(R.string.widget_heading_placeholder));
            views.setTextViewText(R.id.ingredients_widget_text, context.getString(R.string.widget_ingredients_placeholder));
        }
        views.setOnClickPendingIntent(R.id.ingredients_widget_text, pendingIntent);


        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

