package com.example.android.bakingapp.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.example.android.bakingapp.data.entities.Ingredient;
import com.example.android.bakingapp.data.entities.Recipe;
import com.example.android.bakingapp.data.entities.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by US on 04/02/2018.
 */

public class FetchDataTask extends AsyncTask {

    private final Context context;
    private final AsyncTaskCompleteListener<Object> listener;

    private static final String REC_ID = "id";
    private static final String REC_NAME = "name";
    private static final String REC_INGREDIENTS = "ingredients";
    private static final String REC_STEPS = "steps";
    private static final String REC_SERVINGS = "servings";
    private static final String REC_IMAGE = "image";

    private static final String REC_ING_INGREDIENT = "ingredient";
    private static final String REC_ING_MEASURE = "measure";
    private static final String REC_ING_QUANTITY = "quantity";

    private static final String REC_STP_ID = "id";
    private static final String REC_STP_SRT_DESC = "shortDescription";
    private static final String REC_STP_DESC = "description";
    private static final String REC_STP_VIDEO_URL = "videoURL";
    private static final String REC_STP_TMBL_URL = "thumbnailURL";


    public FetchDataTask(Context _context, AsyncTaskCompleteListener<Object> _listener) {
        this.context = _context;
        listener = _listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        listener.onTaskComplete(o);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        JSONArray jRecArray = JsonUtils.getJArray(context);
        if (jRecArray == null) return null;
        int recLength = jRecArray.length();
        Recipe[] recipes = new Recipe[recLength];
        for (int i = 0; i < recLength; i++) {
            JSONObject recObj = null;
            Recipe r = null;
            try {
                recObj = jRecArray.getJSONObject(i);
                r = new Recipe();
                r.Id = recObj.getInt(REC_ID);
                r.Name = recObj.getString(REC_NAME);
                r.ImageUrl = recObj.getString(REC_IMAGE);
                r.Servings = recObj.getInt(REC_SERVINGS);
                if (r.ImageUrl != null && r.ImageUrl.length() > 0)
                    r.Image = BitmapUtility.getBytes(NetworkUtils.loadImgFrom(r.ImageUrl, context));

                JSONArray jIngrArray = recObj.getJSONArray(REC_INGREDIENTS);
                if (jIngrArray != null && jIngrArray.length() > 0) {
                    Ingredient[] ingredients = new Ingredient[jIngrArray.length()];
                    for (int j = 0; j < jIngrArray.length(); j++) {
                        JSONObject ingObj = jIngrArray.getJSONObject(j);
                        ingredients[j] = new Ingredient();
                        ingredients[j].Ingredient = " - " + ingObj.getString(REC_ING_INGREDIENT);
                        ingredients[j].Measure = ingObj.getString(REC_ING_MEASURE);
                        ingredients[j].Quantity = ingObj.getDouble(REC_ING_QUANTITY);
                    }
                    r.Ingredients = ingredients;
                }

                JSONArray jStepArray = recObj.getJSONArray(REC_STEPS);
                if (jStepArray != null && jStepArray.length() > 0) {
                    Step[] steps = new Step[jStepArray.length()];
                    for (int j = 0; j < jStepArray.length(); j++) {
                        JSONObject ingObj = jStepArray.getJSONObject(j);
                        steps[j] = new Step();
                        steps[j].Id = ingObj.getInt(REC_STP_ID);
                        steps[j].Description = ingObj.getString(REC_STP_DESC);
                        steps[j].ShortDescription = ingObj.getString(REC_STP_SRT_DESC);
                        steps[j].ThumbnailURL = ingObj.getString(REC_STP_TMBL_URL);
                        steps[j].VideoURL = ingObj.getString(REC_STP_VIDEO_URL);
                        if (steps[j].ThumbnailURL != null && steps[j].ThumbnailURL.length() > 0)
                            steps[j].Thumbnail = BitmapUtility.getBytes(NetworkUtils.loadImgFrom(steps[j].ThumbnailURL, context));
                    }
                    r.Steps = steps;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            recipes[i] = r;
        }
        return recipes;
    }
}
