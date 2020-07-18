package com.example.phonephoto.data;

import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class DownloadResponse {
    @SerializedName("name")
    String name;

    @SerializedName("img")
    String img;

    public String getName() { return name; }
    public String getImg() { return img; }
//    public Bitmap getBitmap() {
//        try {
//            byte[] encodeByte = Base64.decode(img, Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            return bitmap;
//        } catch (Exception e) {
//            e.getMessage();
//            return null;
//        }
//    }
}
