package com.example.phonephoto.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DownloadResponse {
    @SerializedName("name")
    ArrayList<String> name;

    @SerializedName("path")
    ArrayList<String> path;

    public ArrayList<String> getName() { return name; }
    public ArrayList<String> getPath() { return path; }
}
