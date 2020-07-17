package com.example.project2_photo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int MY_REQUEST_PERMISSIONS = 1234;                      // 내 임의로 정한 값
    public static final String[] PERMISSIONS = {            // 허용 필요한 권한 list
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
    int NUMBER_OF_PERMISSION = PERMISSIONS.length;          // 권한 개수

    RecyclerView photoList;                                 // 갤러리 출력될 recyclerView
    List<String> filesList;                                 // 갤러리 파일 경로들 저장
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("ham MainActivity", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** 접근 권한 허용 요청하기 **/
        if(arePermissionsDenied()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_REQUEST_PERMISSIONS);
        }

        photoList = findViewById(R.id.photoList);
    }

    @Override
    protected void onResume() {
        Log.d("ham MainActivity", "onResume");
        super.onResume();

        /** 권한을 거절했다면 view 출력하지 않도록 하기 **/
        if(arePermissionsDenied())
            return;

        photoList = findViewById(R.id.photoList);
        filesList = new ArrayList<>();  // 갤러리 파일 경로들

        /** 내부 이미지 파일 가져오기 **/

        /** 로컬 저장소의 Pictures 폴더 경로 가져오기 (1) **
         * 문제: getExternalStoragePublicDirectory()가 API level 29부터 deprecated */
        // File filePath;
        // filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        /** 로컬 저장소의 Pictures 폴더 경로 가져오기 (2) **
         * 출력: filePath: /storage/emulated/0/Android/data/com.example.project2_photo/files/Pictures
         * 문제: 다른 경로의 폴더도 따로 구해야 함
         */
        // File filePath;
        // filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        /** 로컬 저장소의 모든 이미지 파일 가져오기 **
         * MediaStore.Images: Collection of all media with MIME type of image/*
         * 출력: Uri: content://media/external/images/media
         *
         * ContentResolver.getType(): MIME 유형을 return
         * 출력: mimeType: vnd.android.cursor.dir/image
         */
        Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d("ham MainActivity", "Uri: " + String.valueOf(imgUri));

        String mimeType = getContentResolver().getType(imgUri);
        Log.d("ham MainActivity", "mimeType: " + mimeType);

        /** cursor 이용해 얻은 테이블 정보:
         *  | _id | _data | _size | _display_name | mime_type | title | date_added | date_modified | ...
         */
        Cursor cursor = getContentResolver().query(imgUri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        /*cursor.moveToFirst();
        Log.d("ham MainActivity", "name: " + nameIndex + " / " + cursor.getString(nameIndex)); // 3
        Log.d("ham MainActivity", "size: " + sizeIndex + " / " + cursor.getString(sizeIndex)); // 2
        Log.d("ham MainActivity", "size: " + 0 + " / " + cursor.getColumnName(0) + " / " + cursor.getString(0));
        Log.d("ham MainActivity", "size: " + 1 + " / " + cursor.getColumnName(1) + " / " + cursor.getString(1));
        Log.d("ham MainActivity", "size: " + 10 + " / " + cursor.getColumnName(10) + " / " + cursor.getString(10));
        Log.d("ham MainActivity", "size: " + 11 + " / " + cursor.getColumnName(11) + " / " + cursor.getString(11));
        */
        layoutManager = new GridLayoutManager(this,3);
        photoList.setLayoutManager(layoutManager);

        GalleryAdapter galleryAdapter = new GalleryAdapter(cursor);
        photoList.setAdapter(galleryAdapter);
    }
/*
    private void addImagesFrom(Uri imgUri){

        final File imagesDir = new File(dirPath);
        final File[] files = imagesDir.listFiles();

        for (File file : files) {
            final String path = file.getAbsolutePath();
            if (path.endsWith(".jpg") || path.endsWith(".png")) {
                filesList.add(path);
            }
        }
    }*/

    /** arePermissionsDenied()
     *
     * 필요한 권한들이 모두 허용이 된 상태인지 체크
     * @return 모두 허용된 경우 false
     */
    public boolean arePermissionsDenied() {
        Log.d("ham MainActivity", "arePermissionsDenied");
        for (int i = 0; i < NUMBER_OF_PERMISSION; i++) {
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    /** onRequestPermissionsResult()
     *
     * requestPermissions 으로 사용자가 앱 권한 요청에 응답하면
     * 시스템은 앱의 onRequestPermissionsResult() 메서드를 호출하여 사용자 응답을 전달한다.
     * @param requestCode ?
     * @param permissions ?
     * @param grantResults ?
     *
     * 권한 거절했다면 finish()
     * 모두 허용했다면 onResume()
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        Log.d("ham MainActivity", "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_REQUEST_PERMISSIONS && grantResults.length > 0) {
            if(arePermissionsDenied()) finish();
            onResume();
        }
    }
}