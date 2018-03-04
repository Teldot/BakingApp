package com.example.android.bakingapp.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.provider.RecipeContract;

/**
 * Created by Mauricio Torres on 01/03/2018.
 *
 */

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;

    ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) mCursor.close();
        mCursor = mContext.getContentResolver().query(
                RecipeContract.IngredientEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.recipe_ingredient_list_item);
        mCursor.moveToPosition(position);

        int ingredientIdx = mCursor.getColumnIndex(RecipeContract.IngredientEntry.COLUMN_INGREDIENT);
        int quantityIdx = mCursor.getColumnIndex(RecipeContract.IngredientEntry.COLUMN_QUANTITY);
        int measureIdx = mCursor.getColumnIndex(RecipeContract.IngredientEntry.COLUMN_MEASURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            views.setTextColor(R.id.tv_ingredient_name, mContext.getColor(R.color.appwidget_text_color));
            views.setTextColor(R.id.tv_ingredient_qty, mContext.getColor(R.color.appwidget_text_color));
            views.setTextColor(R.id.tv_ingredient_measure, mContext.getColor(R.color.appwidget_text_color));
        }
        views.setTextViewText(R.id.tv_ingredient_name, mCursor.getString(ingredientIdx));
        views.setTextViewText(R.id.tv_ingredient_qty, String.valueOf(mCursor.getDouble(quantityIdx)));
        views.setTextViewText(R.id.tv_ingredient_measure, mCursor.getString(measureIdx));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
