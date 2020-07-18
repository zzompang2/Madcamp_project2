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
import com.example.phonephoto.network.RetrofitClient;
import com.example.phonephoto.network.ServiceApi;

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
        showFile();
    }

    private void showFile() {
        Log.d(TAG, "showFile");
        ServiceApi getResponse = RetrofitClient.getRetrofit().create(ServiceApi.class);
        Call<DownloadResponse> call = getResponse.downloadFile();

        Log.d(TAG, String.valueOf(call));

        call.enqueue(new Callback<DownloadResponse>() {
            @Override
            public void onResponse(Call<DownloadResponse> call, Response<DownloadResponse> response) {
                Log.d(TAG, "onResponse");

                DownloadResponse downloadResponse = response.body();
                Log.d(TAG, String.valueOf(downloadResponse));

                if (downloadResponse != null) {
                    Log.d(TAG, "downloadResponse 받았다!");
                    String[] imgArray = downloadResponse.getImg();
                    int num = downloadResponse.getName().length;
                    Log.d(TAG, String.valueOf(num));

                    Log.d(TAG, String.valueOf(imgArray));

                    for(int i=0; i<num; i++) {
                        byte[] bytes = Base64.decode(imgArray[i], Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imageView.setImageBitmap(bitmap);
                    }

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