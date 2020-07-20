package com.example.phonephoto;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.phonephoto.data.UploadResponse;
import com.example.phonephoto.network.RetrofitClient;
import com.example.phonephoto.network.ServiceApi;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoFragment extends Fragment {

    String TAG = "PJ2 PhotoFragment";

    Button backupButton;                                    // 서버로 모두 올리기
    Button serverButton;                                    // 서버 갤러리 보기
    RecyclerView recyclerView;                              // 갤러리 출력될 recyclerView
    RecyclerView.LayoutManager layoutManager;
    int columnNum = 3;                                      // grid view 행 개수

    Uri imgUri;                                             // 기기 이미지 파일 uri
    Cursor cursor;                                          // 갤러리 이미지 탐색
    GalleryAdapter galleryAdapter;
    Point size;                                             // 기기 화면 사이즈

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // view 객체 정의
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_photo, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        backupButton = rootView.findViewById(R.id.backupButton);
        serverButton = rootView.findViewById(R.id.serverButton);

        // recyclerView 에 그리드뷰 매니저 연결
        layoutManager = new GridLayoutManager(getContext(), columnNum);
        recyclerView.setLayoutManager(layoutManager);

        // 화면 크기의 1/3 크기로 이미지를 출력하기 위해 디스플레이의 사이즈를 구함
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor = getActivity().getContentResolver().query(imgUri, null, null, null, null);
                uploadAllFile(cursor);
            }
        });

        serverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ServerGalleryActivity.class);
                getContext().startActivity(intent);
            }
        });

        return rootView;
    }

    // MainActivity 에서 권한 허용 선택하기 전에
    // onCreate, onCreateView는 실행이 되어 버리나,
    // onResume 은 선택 후에 실행됨.
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

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

        /** 로컬 저장소의 모든 이미지 파일 가져오기
         * MediaStore.Images: image/* MIME 타입의 모든 media들의 Collection
         * 출력: Uri: content://media/external/images/media
         */
        imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Log.d(TAG, "imgUri: " + imgUri);

        /** cursor 이용해 얻은 테이블 정보:
         *  | _id | _data | _size | _display_name | mime_type | title | date_added | date_modified | ...
         *
         *  column index 구하기:
         *  filePathColumn[0]               => "_data"
         *  cursor.getColumnIndex("_data")  => 1
         *  cursor.getString(1)             => 파일의 절대경로
         */

        // 생성된 순서로 sorting
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

//        String[] projection = new String[]{
//                MediaStore.Images.Media._ID,
//                MediaStore.Images.Media.DISPLAY_NAME,
//                MediaStore.Images.Media.DATE_TAKEN };

        cursor = getActivity().getContentResolver().query(imgUri, null, null, null, sortOrder);

        galleryAdapter = new GalleryAdapter(cursor, size.x/3);
        recyclerView.setAdapter(galleryAdapter);
    }

    private void uploadAllFile(Cursor cursor) {
        Log.d(TAG, "uploadAllFile");

        String absolutePath; // 한 이미지 절대경로
        RequestBody requestBody;
        MultipartBody.Part fileToUpload;
        RequestBody filename;
        ServiceApi getResponse;
        Call call;
        File file;

        while(cursor.moveToNext()) {
            Log.d(TAG, "moveToNext!");
            // Map is used to multipart the file using okhttp3.RequestBody
            absolutePath = cursor.getString(cursor.getColumnIndex("_data"));
            file = new File(absolutePath);

            Log.d(TAG, "absolutePath: "+absolutePath);

            // Parsing any Media type file
            requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            fileToUpload = MultipartBody.Part.createFormData("ham1", file.getName(), requestBody);
            filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

            getResponse = RetrofitClient.getRetrofit().create(ServiceApi.class);

            call = getResponse.uploadFile(fileToUpload, filename);

            Log.d(TAG, "call: " + call.toString());
            Log.d(TAG, "file name: " + file.getName());

            // enqueue: (데이터의 아이템)을 큐(대기 행렬)에 더하다.
            call.enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    Log.d(TAG, "onResponse");
                    UploadResponse serverResponse = response.body();
                    Log.d(TAG, String.valueOf(serverResponse));
                    if (serverResponse != null) {
                        Log.d(TAG, "serverResponse 받았다!");
                        if (serverResponse.getSuccess()) {
                            Toast.makeText(getContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "server msg: " + serverResponse.getMessage());
                        } else {
                            Toast.makeText(getContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Call은 제대로 보냈으나 서버에서 이거뭐냐? 하고 reponse를 보낸 경우 (????)
                        Log.d(TAG, "serverResponse 못받았어ㅠ_ㅠ");
                    }
                }

                // Call을 서버쪽으로 아예 보내지 못한 경우
                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure");
                    Log.d(TAG, t.toString());
                }
            });
        }
    }
}
