package com.example.android.bakingapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mauricio Torres on 05/02/2018.
 */

public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static Bitmap loadImgFrom(URL url, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = Picasso.with(context).load(url.toString()).get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return bitmap;
    }

    public static Bitmap loadImgFrom(String uriString, Context context){
        Uri uri = Uri.parse(uriString).buildUpon().build();
        URL url = null;
        try {
            url = new URL(uri.toString());
            return loadImgFrom(url, context);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
