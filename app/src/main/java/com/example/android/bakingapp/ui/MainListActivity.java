package com.example.android.bakingapp.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;

import com.example.android.bakingapp.BuildConfig;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.entities.Ingredient;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;
import com.example.android.bakingapp.data.provider.RecipeContract;
import com.example.android.bakingapp.ui.widget.BakingService;
import com.example.android.bakingapp.utils.AsyncTaskCompleteListener;
import com.example.android.bakingapp.utils.FetchDataTask;

import timber.log.Timber;

public class MainListActivity extends AppCompatActivity implements RecipeListAdapter.RecipeListAdapterOnClickHandler {

    private LinearLayoutManager layoutManager;
    private RecipeListAdapter mRecipeListAdapter;

    private static final String K_SELECTED_RECIPE = "K_SELECTED_RECIPE";

    private final String K_RECYCLED_VIEW_STATE = "recycled_view_state";
    private Parcelable listState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        setContentView(R.layout.activity_main_list);

        RecyclerView mRecyclerView = findViewById(R.id.rv_recipe_list);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 1);
            Timber.i("Orientation: ORIENTATION_PORTRAIT");
        } else {
            layoutManager = new GridLayoutManager(this, getColNumber());
            Timber.i("Orientation: ORIENTATION_LANDSCAPE");

        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecipeListAdapter = new RecipeListAdapter(this, this);
        mRecyclerView.setAdapter(mRecipeListAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = layoutManager.onSaveInstanceState();

        outState.putParcelable(K_RECYCLED_VIEW_STATE, listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            listState = savedInstanceState.getParcelable(K_RECYCLED_VIEW_STATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipesInfo();
        deleteSelectedRecipe();
        BakingService.startActionWBacking(this);
    }

    private void loadRecipesInfo() {
        Timber.d("Fetching Recipes");
        new FetchDataTask(this,
                new FetchDataTaskCompleteListener())
                .execute();

    }

    private int getColNumber() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        return Math.round(dpWidth / 300);
    }

    @Override
    public void onClick(Recipe recipe) {
        Timber.d("Recipe Selected: %s", recipe.Name);

        insertSelectedRecipe(recipe);
        BakingService.startActionWBacking(this);

        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(K_SELECTED_RECIPE, recipe);
        startActivity(intent);
    }

    private void insertSelectedRecipe(Recipe recipe) {
        deleteSelectedRecipe();

        ContentValues recipeValues = new ContentValues();
        recipeValues.put(RecipeContract.RecipeEntry._ID, recipe.Id);
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_NAME, recipe.Name);
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_SERVINGS, recipe.Servings);
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_IMAGE_URL, recipe.ImageUrl);
        recipeValues.put(RecipeContract.RecipeEntry.COLUMN_IMAGE, recipe.Image);

        getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, recipeValues);

        ContentValues ingredientsValues;
        for (Ingredient ingredient : recipe.Ingredients) {
            ingredientsValues = new ContentValues();
            ingredientsValues.put(RecipeContract.IngredientEntry.COLUMN_INGREDIENT, ingredient.Ingredient);
            ingredientsValues.put(RecipeContract.IngredientEntry.COLUMN_MEASURE, ingredient.Measure);
            ingredientsValues.put(RecipeContract.IngredientEntry.COLUMN_QUANTITY, ingredient.Quantity);
            ingredientsValues.put(RecipeContract.IngredientEntry.COLUMN_RECIPE_ID, recipe.Id);

            getContentResolver().insert(RecipeContract.IngredientEntry.CONTENT_URI, ingredientsValues);
        }

        ContentValues stepsValues;
        for (Step step : recipe.Steps) {
            stepsValues = new ContentValues();
            stepsValues.put(RecipeContract.StepEntry.COLUMN_RECIPE_ID, step.Id);
            stepsValues.put(RecipeContract.StepEntry.COLUMN_DESCRIPTION, step.Description);
            stepsValues.put(RecipeContract.StepEntry.COLUMN_SHORT_DESCRIPTION, step.ShortDescription);
            stepsValues.put(RecipeContract.StepEntry.COLUMN_THUMBNAIL, step.Thumbnail);
            stepsValues.put(RecipeContract.StepEntry.COLUMN_THUMBNAIL_URL, step.ThumbnailURL);
            stepsValues.put(RecipeContract.StepEntry.COLUMN_VIDEO_URL, step.VideoURL);

            getContentResolver().insert(RecipeContract.StepEntry.CONTENT_URI, stepsValues);
        }
    }

    private void deleteSelectedRecipe() {
        getContentResolver().delete(RecipeContract.RecipeEntry.CONTENT_URI, null, null);
        getContentResolver().delete(RecipeContract.IngredientEntry.CONTENT_URI, null, null);
    }

    public class FetchDataTaskCompleteListener implements AsyncTaskCompleteListener<Object> {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onTaskComplete(Object result) {
            Timber.d("Recipes Fetching completed");
            Recipe[] recipeData = (Recipe[]) result;
            if (recipeData != null) {
                Timber.d("Total Recipes: %d", recipeData.length);
                mRecipeListAdapter.swapData(recipeData);
                if (listState != null) {
                    layoutManager.onRestoreInstanceState(listState);
                }
            }
        }
    }
}