package com.example.android.bakingapp.utils;

/**
 * Created by Mauricio Torres
 * on 04/02/2018.
 */

public interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T result);
    void onPreExecute();
}
