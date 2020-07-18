package com.example.phonephoto.network;

import com.example.phonephoto.data.JoinData;
import com.example.phonephoto.data.JoinResponse;
import com.example.phonephoto.data.LoginData;
import com.example.phonephoto.data.LoginResponse;
import com.example.phonephoto.data.ServerResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceApi {
    @Multipart
    @POST("/upload")
    Call<ServerResponse> uploadFile(@Part MultipartBody.Part file, @Part("ham2") RequestBody name);

    @POST("/user/login")
    Call<LoginResponse> userLogin(@Body LoginData data);

    @POST("/user/join")
    Call<JoinResponse> userJoin(@Body JoinData data);
}
