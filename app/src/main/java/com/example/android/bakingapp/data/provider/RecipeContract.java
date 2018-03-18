package com.example.android.bakingapp.data.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by US
 * on 01/03/2018.
 */

public class RecipeContract {

    public static final String AUTHORITY = "com.example.android.bakingapp";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    static final String PATH_RECIPES = "recipes";
    static final String PATH_INGREDIENTS = "ingredients";
    static final String PATH_STEPS = "steps";

//    public static final long INVALID_RECIPE_ID = -1;

    public static final class RecipeEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();

        static final String TABLE_NAME = "recipe";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SERVINGS = "servings";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_IMAGE = "image";
    }

    public static final class IngredientEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        static final String TABLE_NAME = "ingredient";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_INGREDIENT = "ingredient";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_QUANTITY = "quantity";
    }

    public static final class StepEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_STEPS).build();

        static final String TABLE_NAME = "step";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_SHORT_DESCRIPTION = "short_description";
        public static final String COLUMN_VIDEO_URL = "video_url";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_THUMBNAIL = "thumbnail";
    }
}
