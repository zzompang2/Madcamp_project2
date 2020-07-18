package com.example.phonephoto;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.phonephoto.data.DownloadResponse;
import com.example.phonephoto.data.UploadResponse;
import com.example.phonephoto.network.RetrofitClient;
import com.example.phonephoto.network.ServiceApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerGalleryActivity extends AppCompatActivity {

    String TAG = "PJ2 ServerGalleryActivity";
    Handler handler = new Handler();  // 외부쓰레드 에서 메인 UI화면을 그릴때
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_gallery);

        imageView = findViewById(R.id.imageView);

        /*
        // 파일 받기: Thread 사용
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL("http://192.249.19.242:7080/users/1594779081800.jpg");
                    InputStream inputStream = url.openStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                    imageView.setImageBitmap(bitmap); //비트맵 객체로 보여주기
                } catch (Exception e) {

                }
            }
        });
        thread.start();
         */

        downloadFile();
    }

    private void downloadFile() {
        Log.d(TAG, "downloadFile");
        ServiceApi getResponse = RetrofitClient.getRetrofit().create(ServiceApi.class);
        Call<DownloadResponse> call = getResponse.downloadFile();

        Log.d(TAG, String.valueOf(call));

        call.enqueue(new Callback<DownloadResponse>() {
            @Override
            public void onResponse(Call<DownloadResponse> call, Response<DownloadResponse> response) {
                Log.d(TAG, "onResponse");

                //boolean writtenToDisk = writeResponseBodyToDisk(response.body());

//                DownloadResponse downloadResponse = response.body();
//
//                Log.d(TAG, String.valueOf(downloadResponse));
//
//                Bitmap bitmap = downloadResponse.getBitmap();
//                imageView.setImageBitmap(bitmap);

                DownloadResponse downloadResponse = response.body();
                Log.d(TAG, String.valueOf(downloadResponse));
                if (downloadResponse != null) {
                    Log.d(TAG, "downloadResponse 받았다!");
                    if (response.isSuccessful()) {
                       // Toast.makeText(getApplicationContext(), downloadResponse.getName(), Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, "img name: " + downloadResponse.getName());
                        byte[] bytes = Base64.decode(response.body().getImg(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);

                    } else {
                        //Toast.makeText(getApplicationContext(), downloadResponse.getName(), Toast.LENGTH_SHORT).show();
                    }
                    //Log.d(TAG, downloadResponse.getImg());

                } else {
                    // Call은 제대로 보냈으나 서버에서 이거뭐냐? 하고 reponse를 보낸 경우 (????)
                    Log.d(TAG, "serverResponse 못받았어ㅠ_ㅠ");
                }
            }

            @Override
            public void onFailure(Call<DownloadResponse> call, Throwable t) {
                Log.d(TAG, "onFailure");
                Log.d(TAG, String.valueOf(t));
            }
        });
    }

    public void backClick(View view) {
        setResult(RESULT_OK);
        finish();
    }
}