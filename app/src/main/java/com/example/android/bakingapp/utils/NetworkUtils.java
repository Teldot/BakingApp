package com.example.android.bakingapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;

/**
 * Created by Mauricio Torres
 * on 05/02/2018.
 */

final class NetworkUtils {

    private static Bitmap loadImgFrom(URL url, Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = Picasso.with(context).load(url.toString()).get();
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
        return bitmap;
    }

    static Bitmap loadImgFrom(String uriString, Context context) {
        Uri uri = Uri.parse(uriString).buildUpon().build();
        try {
            URL url = new URL(uri.toString());
            return loadImgFrom(url, context);
        } catch (MalformedURLException e) {
            Timber.e(e.getMessage());
            return null;
        }
    }
}
