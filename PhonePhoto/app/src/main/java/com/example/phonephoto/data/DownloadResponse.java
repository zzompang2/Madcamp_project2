package com.example.phonephoto.data;

import com.google.gson.annotations.SerializedName;

public class DownloadResponse {
    @SerializedName("name")
    String[] name;

    @SerializedName("path")
    String[] path;

    public String[] getName() { return name; }
    public String[] getPath() { return path; }
}
