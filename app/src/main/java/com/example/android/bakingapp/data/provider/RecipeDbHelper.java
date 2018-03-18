package com.example.android.bakingapp.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bakingapp.BuildConfig;

import timber.log.Timber;

/**
 * Created by Mauricio Torres on 01/03/2018.
 *
 */

class RecipeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bakingapp.db";
    private static final int DATABASE_VERSION = 2;

    RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RECIPE_TABLE = "CREATE TABLE " + RecipeContract.RecipeEntry.TABLE_NAME + " (" +
                RecipeContract.RecipeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecipeContract.RecipeEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_IMAGE_URL + " TEXT NULL, " +
                RecipeContract.RecipeEntry.COLUMN_SERVINGS + " INTEGER NULL, " +
                RecipeContract.RecipeEntry.COLUMN_IMAGE + " BLOB NULL)";

        db.execSQL(SQL_CREATE_RECIPE_TABLE);

        final String SQL_CREATE_INGREDIENT_TABLE = "CREATE TABLE " + RecipeContract.IngredientEntry.TABLE_NAME + " (" +
                RecipeContract.IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecipeContract.IngredientEntry.COLUMN_INGREDIENT + " TEXT NOT NULL, " +
                RecipeContract.IngredientEntry.COLUMN_MEASURE + " TEXT NULL, " +
                RecipeContract.IngredientEntry.COLUMN_QUANTITY + " REAL NULL, " +
                RecipeContract.IngredientEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);

        final String SQL_CREATE_STEP_TABLE = "CREATE TABLE " + RecipeContract.StepEntry.TABLE_NAME + " (" +
                RecipeContract.StepEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RecipeContract.StepEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NULL, " +
                RecipeContract.StepEntry.COLUMN_DESCRIPTION + " TEXT NULL, " +
                RecipeContract.StepEntry.COLUMN_THUMBNAIL_URL + " TEXT NULL, " +
                RecipeContract.StepEntry.COLUMN_THUMBNAIL + " BLOB NULL, " +
                RecipeContract.StepEntry.COLUMN_RECIPE_ID + " INTEGER NOT NULL, " +
                RecipeContract.StepEntry.COLUMN_VIDEO_URL + " TEXT NULL)";

        db.execSQL(SQL_CREATE_STEP_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE " + RecipeContract.RecipeEntry.TABLE_NAME + ";");
        } catch (SQLiteException ex) {
            Timber.e(ex.getMessage());
        }
        try {
            db.execSQL("DROP TABLE " + RecipeContract.IngredientEntry.TABLE_NAME + ";");
        } catch (SQLiteException ex) {
            Timber.e(ex.getMessage());
        }
        try {
            db.execSQL("DROP TABLE " + RecipeContract.StepEntry.TABLE_NAME + ";");
        } catch (SQLiteException ex) {
            Timber.e(ex.getMessage());
        }

        onCreate(db);
    }
}
