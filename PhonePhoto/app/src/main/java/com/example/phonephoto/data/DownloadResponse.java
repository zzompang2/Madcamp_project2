package com.example.phonephoto.data;

import com.google.gson.annotations.SerializedName;

public class DownloadResponse {
    @SerializedName("name")
    String[] name;

    @SerializedName("img")
    String[] img;

    public String[] getName() { return name; }
    public String[] getImg() { return img; }
}
