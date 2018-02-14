package com.example.android.bakingapp.data.entities;

import java.io.Serializable;

public class Step implements Serializable{
    public int Id;
    public String Description;
    public String ShortDescription;
    public String VideoURL;
    public String ThumbnailURL;
    public byte[] Thumbnail;
}
