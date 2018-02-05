package com.example.android.bakingapp.utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by US on 04/02/2018.
 */

public class JsonUtils {
    private static String getJSONString(Context context) {
        StringBuilder str = new StringBuilder("");
        try {
            AssetManager assetManager = context.getAssets();
            InputStream in = assetManager.open("recipes.json");
            InputStreamReader isr = new InputStreamReader(in);
            char[] inputBuffer = new char[100];

            int charRead;
            while ((charRead = isr.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                str.append(readString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str.toString();
    }

    public static JSONObject getJObject(Context context)
    {
        JSONObject json = null;

        try {
            json = new JSONObject(getJSONString(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    public static JSONArray getJArray(Context context)
    {
        JSONArray json = null;

        try {
            json = new JSONArray(getJSONString(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
