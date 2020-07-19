package com.example.phonephoto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.phonephoto.data.DownloadResponse;
import com.example.phonephoto.data.UploadResponse;
import com.example.phonephoto.network.RetrofitClient;
import com.example.phonephoto.network.ServiceApi;

import java.io.InputStream;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerGalleryActivity extends AppCompatActivity {

    String TAG = "PJ2 ServerGalleryActivity";

    ConstraintLayout constraintLayout;
    RecyclerView recyclerView;
    ImageView imageView;
    LinearLayout selectedImage;
    TextView imageName;
    LinearLayoutManager layoutManager;
    ServerFileAdapter serverFileAdapter;
    String[] nameArray, pathArray;

    Handler handler = new Handler();  // 외부쓰레드 에서 메인 UI화면을 그릴때 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_gallery);

        constraintLayout = findViewById(R.id.constraintLayout);
        recyclerView = findViewById(R.id.recyclerView);
        imageView = findViewById(R.id.imageView);
        selectedImage = findViewById(R.id.selevtedImage);
        imageName = findViewById(R.id.imageName);
        // recyclerView 에 linear layout 매니저 연결
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        showFile();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        serverFileAdapter = new ServerFileAdapter(this, nameArray, pathArray);
        recyclerView.setAdapter(serverFileAdapter);
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

                    nameArray = downloadResponse.getName();
                    pathArray = downloadResponse.getPath();
                    //Log.d(TAG, nameArray[4]);
                    onResume();

                } else {
                    // Call은 제대로 보냈으나 response 값이 제대로 오지 않음
                    Log.d(TAG, "serverResponse 못받았어ㅠ_ㅠ");
                }
            }

            @Override
            public void onFailure(Call<DownloadResponse> call, Throwable t) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    public void zoomInPhoto (int position) {
        Log.d(TAG, "zoomInPhoto");
        final String name = nameArray[position];
        String path = pathArray[position];

        imageName.setText(name);

        Log.d(TAG, name + "////" + path);

        ServiceApi getResponse = RetrofitClient.getRetrofit().create(ServiceApi.class);
        Call<UploadResponse> call = getResponse.getOneFile(path);

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                Log.d(TAG, "onResponse");
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.d(TAG, "onFailure");
            }
        });

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다
                try{
                    URL url = new URL("http://192.249.19.242:7080/users/" + name );
                    InputStream is = url.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            imageView.setImageBitmap(bm);
                        }
                    });
                    imageView.setImageBitmap(bm); //비트맵 객체로 보여주기
                } catch(Exception e){

                }
            }
        });
        t.start();

        selectedImage.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.INVISIBLE);
    }

    public void backClick(View view) {
        setResult(RESULT_OK);
        finish();
    }
    public void backClick2(View view) {
        imageView.setImageBitmap(null);
        selectedImage.setVisibility(View.INVISIBLE);
        constraintLayout.setVisibility(View.VISIBLE);
    }
}