package com.example.phonephoto;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class ServerGalleryActivity extends AppCompatActivity {

    String TAG = "PJ2 ServerGalleryActivity";
    Handler handler = new Handler();  // 외부쓰레드 에서 메인 UI화면을 그릴때
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_gallery);

        imageView = findViewById(R.id.imageView);

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
    }

    public void backClick(View view) {
        setResult(RESULT_OK);
        finish();
    }
}