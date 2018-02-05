package com.example.android.bakingapp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.utils.AsyncTaskCompleteListener;
import com.example.android.bakingapp.utils.FetchDataTask;

public class MainListActivity extends AppCompatActivity implements RecipeListAdapter.RecipeListAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private RecipeListAdapter mRecipeListAdapter;

    private final String K_RECYCLEDVIEW_STATE = "recycledview_state";
    private Parcelable listState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipe_list);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            layoutManager = new GridLayoutManager(this, 1);
        else
            layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecipeListAdapter = new RecipeListAdapter(this, this);
        mRecyclerView.setAdapter(mRecipeListAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = layoutManager.onSaveInstanceState();

        outState.putParcelable(K_RECYCLEDVIEW_STATE, listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            listState = savedInstanceState.getParcelable(K_RECYCLEDVIEW_STATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipesInfo();
    }

    private void loadRecipesInfo() {
        new FetchDataTask(this,
                new FetchDataTaskCompleteListener())
                .execute();

    }

    @Override
    public void onClick(Recipe recipe) {

    }

    public class FetchDataTaskCompleteListener implements AsyncTaskCompleteListener<Object> {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onTaskComplete(Object result) {
            Recipe[] recipeData = (Recipe[]) result;
            if (recipeData != null) {
                mRecipeListAdapter.swapData(recipeData);
                if (listState != null) {
                    layoutManager.onRestoreInstanceState(listState);
                }
            }
        }
    }
}