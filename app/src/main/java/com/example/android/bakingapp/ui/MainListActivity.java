package com.example.android.bakingapp.ui;

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
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.utils.AsyncTaskCompleteListener;
import com.example.android.bakingapp.utils.FetchDataTask;

import timber.log.Timber;

public class MainListActivity extends AppCompatActivity implements RecipeListAdapter.RecipeListAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private RecipeListAdapter mRecipeListAdapter;
    private Recipe[] recipeData;

    private static final String K_SELECTED_RECIPE = "K_SELECTED_RECIPE";

    private final String K_RECYCLED_VIEW_STATE = "recycled_view_state";
    private Parcelable listState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        setContentView(R.layout.activity_main_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipe_list);
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
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(K_SELECTED_RECIPE, recipe);
        startActivity(intent);
    }


    public class FetchDataTaskCompleteListener implements AsyncTaskCompleteListener<Object> {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onTaskComplete(Object result) {
            Timber.d("Recipes Fetching completed");
            recipeData = (Recipe[]) result;
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