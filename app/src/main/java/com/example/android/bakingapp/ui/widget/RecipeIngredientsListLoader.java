package com.example.android.bakingapp.ui.widget;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.provider.RecipeContract;

/**
 * Created by Mauricio Torres on 28/02/2018.
 */

public class RecipeIngredientsListLoader extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    SimpleCursorAdapter mAdapter;
    ListView listView;
    private static final int LOADER_ID = 599;
    static final String[] RECIPE_PROJECTION = new String[]{
            RecipeContract.RecipeEntry._ID,
            RecipeContract.RecipeEntry.COLUMN_IMAGE,
            RecipeContract.RecipeEntry.COLUMN_NAME,
            RecipeContract.RecipeEntry.COLUMN_SERVINGS
    };

    static final String[] INGREDIENTS_PREJECTION = new String[]{
            RecipeContract.IngredientEntry.COLUMN_INGREDIENT,
            RecipeContract.IngredientEntry.COLUMN_QUANTITY,
            RecipeContract.IngredientEntry.COLUMN_MEASURE
    };

    static int[] INGREDIENTS_VIEWS = {
            R.id.tv_ingredient_name,
            R.id.tv_ingredient_qty,
            R.id.tv_ingredient_measure
    };

    static final String SELECTION = RecipeContract.RecipeEntry._ID + " = ?";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_widget);
        listView = (ListView) findViewById(R.id.widget_recipe_ingredients);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
