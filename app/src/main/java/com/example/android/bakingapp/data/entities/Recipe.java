package com.example.android.bakingapp.data.entities;

import android.graphics.Bitmap;

/**
 * Created by Mauricio Torres on 04/02/2018.
 */

public class Recipe {
    public int Id;
    public String Name;
    public int Servings;
    public String ImageUrl;
    public Bitmap Image;
    public Ingredient[] Ingredients;
    public Step[] Steps;
}

