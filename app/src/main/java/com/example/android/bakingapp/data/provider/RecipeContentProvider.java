package com.example.android.bakingapp.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

/**
 * Created by Mauricio Torres
 * on 01/03/2018.
 */

public class RecipeContentProvider extends ContentProvider {
    private RecipeDbHelper mDbHelper;

    private static final int RECIPES = 100;
    private static final int RECIPE_ID = 101;
    private static final int RECIPE_INGREDIENTS = 200;
    private static final int RECIPE_INGREDIENT_ID = 201;
    private static final int RECIPE_STEPS = 300;
    private static final int RECIPE_STEP_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_RECIPES + "/#", RECIPE_ID);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_INGREDIENTS, RECIPE_INGREDIENTS);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_INGREDIENTS + "/#", RECIPE_INGREDIENT_ID);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_STEPS, RECIPE_STEPS);
        uriMatcher.addURI(RecipeContract.AUTHORITY, RecipeContract.PATH_STEPS + "/#", RECIPE_STEP_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new RecipeDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor retCursor = null;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case RECIPES:
            case RECIPE_ID:
                retCursor = db.query(RecipeContract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECIPE_INGREDIENT_ID:
            case RECIPE_INGREDIENTS:
                retCursor = db.query(RecipeContract.IngredientEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case RECIPE_STEP_ID:
            case RECIPE_STEPS:
                retCursor = db.query(RecipeContract.StepEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                Timber.e(".query()Unknown uri: %s", uri);
                break;
        }
        if (retCursor != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;
        long id;
        switch (sUriMatcher.match(uri)) {
            case RECIPES:
            case RECIPE_ID:
                id = db.insert(RecipeContract.RecipeEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(RecipeContract.RecipeEntry.CONTENT_URI, id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case RECIPE_INGREDIENTS:
            case RECIPE_INGREDIENT_ID:
                id = db.insert(RecipeContract.IngredientEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(RecipeContract.IngredientEntry.CONTENT_URI, id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case RECIPE_STEPS:
            case RECIPE_STEP_ID:
                id = db.insert(RecipeContract.StepEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = ContentUris.withAppendedId(RecipeContract.StepEntry.CONTENT_URI, id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int res = 0;
        switch (sUriMatcher.match(uri)) {
            case RECIPE_INGREDIENT_ID:
            case RECIPE_STEP_ID:
                for (ContentValues value : values) {
                    insert(uri, value);
                    res++;
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return res;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int itemsDeleted = 0;
        try {
            switch (sUriMatcher.match(uri)) {
                case RECIPES:
                    itemsDeleted = db.delete(RecipeContract.RecipeEntry.TABLE_NAME,
                            selection,
                            selectionArgs);
                    break;
                case RECIPE_INGREDIENTS:
                    itemsDeleted = db.delete(RecipeContract.IngredientEntry.TABLE_NAME,
                            selection,
                            selectionArgs);
                    break;
                case RECIPE_STEPS:
                    itemsDeleted = db.delete(RecipeContract.StepEntry.TABLE_NAME,
                            selection,
                            selectionArgs);
                    break;
                default:
                    Timber.e(".delete().Unknown uri: %s", uri);
                    break;
            }
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception ex) {
            Timber.e( uri + " | " + ex.getMessage());
        }
        return itemsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int itemsUpdated = 0;
        try {
            switch (sUriMatcher.match(uri)) {
                case RECIPE_ID:
                    itemsUpdated = db.update(RecipeContract.RecipeEntry.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs);
                    break;
                default:
                    Timber.e("Unknown uri: %s", uri);
                    break;
            }
        } catch (Exception ex) {
            Timber.e(uri + " | " + ex.getMessage());
        }
        return itemsUpdated;
    }


}
