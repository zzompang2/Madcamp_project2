package com.example.phonephoto.phone;

import com.google.gson.annotations.SerializedName;

public class PhoneUploadResponse {
    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}