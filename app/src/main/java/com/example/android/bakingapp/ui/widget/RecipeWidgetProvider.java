package com.example.android.bakingapp.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Ingredient;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;
import com.example.android.bakingapp.data.provider.RecipeContract;
import com.example.android.bakingapp.ui.MainListActivity;
import com.example.android.bakingapp.ui.RecipeActivity;
import com.example.android.bakingapp.utils.BitmapUtility;

import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    private static final String K_SELECTED_RECIPE = "K_SELECTED_RECIPE";
    private static Recipe mRecipe;

    public RecipeWidgetProvider() {

    }

    private static void updateAppWidget(final Context context, AppWidgetManager appWidgetManager,
                                        final int appWidgetId) {
        Timber.i("Loading RecipeData...");
        loadRecipeData(context);
        Timber.i("RecipeData loaded:%s", (mRecipe == null ? "0" : "1"));

        Intent intent;
        if (mRecipe != null)
            intent = new Intent(context, RecipeActivity.class).putExtra(K_SELECTED_RECIPE, mRecipe);
        else
            intent = new Intent(context, MainListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
        Timber.i("Loading data fields...");
        views = loadData(context, views);
        Timber.i("Data fields loaded");

        Timber.i("Loading IngredientListAdapter...");
        Intent lIntent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_recipe_ingredients, lIntent);
        Timber.i("IngredientListAdapter loaded");

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void loadRecipeData(Context context) {
        Cursor cursor = context.getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            mRecipe = new Recipe();
            Timber.i("Recipe cursor length:%s", cursor.getCount());

            cursor.moveToFirst();
            int recipeIdIdx = cursor.getColumnIndex(RecipeContract.RecipeEntry._ID);
            int recipeNameIdx = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_NAME);
            int recipeServingsIdx = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_SERVINGS);
            int recipeImgIdx = cursor.getColumnIndex(RecipeContract.RecipeEntry.COLUMN_IMAGE);

            mRecipe.Name = cursor.getString(recipeNameIdx);
            mRecipe.Servings = cursor.getInt(recipeServingsIdx);
            mRecipe.Image = cursor.getBlob(recipeImgIdx);
            mRecipe.Id = cursor.getInt(recipeIdIdx);

            mRecipe.Ingredients = loadIngredientsData(context);
            mRecipe.Steps = loadStepsData(context);
            cursor.close();
        } else mRecipe = null;

    }

    private static Step[] loadStepsData(Context context) {
        Step[] steps = null;
        Cursor cursor = context.getContentResolver().query(RecipeContract.StepEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            Timber.i("Steps cursor length:%s", cursor.getCount());
            steps = new Step[cursor.getCount()];

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                int IdIdx = cursor.getColumnIndex(RecipeContract.StepEntry._ID);
                int DescriptionIdx = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_DESCRIPTION);
                int ShortDescriptionIdx = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_SHORT_DESCRIPTION);
                int VideoURLIdx = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_VIDEO_URL);
                int ThumbnailURLIdx = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_THUMBNAIL_URL);
                int ThumbnailIdx = cursor.getColumnIndex(RecipeContract.StepEntry.COLUMN_THUMBNAIL);

                steps[i] = new Step();
                steps[i].Id = cursor.getInt(IdIdx);
                steps[i].Description = cursor.getString(DescriptionIdx);
                steps[i].ShortDescription = cursor.getString(ShortDescriptionIdx);
                steps[i].VideoURL = cursor.getString(VideoURLIdx);
                steps[i].ThumbnailURL = cursor.getString(ThumbnailURLIdx);
                steps[i].Thumbnail = cursor.getBlob(ThumbnailIdx);
            }
            cursor.close();
        }
        return steps;
    }

    private static Ingredient[] loadIngredientsData(Context context) {
        Ingredient[] ingredients = null;

        Cursor cursor = context.getContentResolver().query(RecipeContract.IngredientEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            Timber.i("Ingredients cursor length:%s", cursor.getCount());
            ingredients = new Ingredient[cursor.getCount()];

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);

                int ingredientIdx = cursor.getColumnIndex(RecipeContract.IngredientEntry.COLUMN_INGREDIENT);
                int quantityIdx = cursor.getColumnIndex(RecipeContract.IngredientEntry.COLUMN_QUANTITY);
                int measureIdx = cursor.getColumnIndex(RecipeContract.IngredientEntry.COLUMN_MEASURE);

                ingredients[i] = new Ingredient();
                ingredients[i].Ingredient = cursor.getString(ingredientIdx);
                ingredients[i].Quantity = cursor.getDouble(quantityIdx);
                ingredients[i].Measure = cursor.getString(measureIdx);
            }
            cursor.close();
        }

        return ingredients;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateBakingWidgets(context, appWidgetManager, appWidgetIds);
    }

    public static void updateBakingWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Timber.i("onUpdate:appWidgetId(%s)", appWidgetId);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static RemoteViews loadData(Context context, RemoteViews views) {

        if (mRecipe != null) {
            views.setTextViewText(R.id.appwidget_recipe_name, mRecipe.Name);
            String servings = context.getString(R.string.widget_recipe_servings_text) + mRecipe.Servings;
            views.setTextViewText(R.id.appwidget_recipe_servings, servings);

            if (mRecipe.Image != null && mRecipe.Image.length > 0)
                views.setImageViewBitmap(R.id.widget_recipe_image,
                        BitmapUtility.getImage(mRecipe.Image));
        } else

        {
            views.setTextViewText(R.id.appwidget_recipe_name, context.getString(R.string.add_widget));
            views.setTextViewText(R.id.appwidget_recipe_servings, context.getString(R.string.appwidget_text));
            views.setImageViewResource(R.id.widget_recipe_image, R.drawable.ic_recipe);
        }

        return views;
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

