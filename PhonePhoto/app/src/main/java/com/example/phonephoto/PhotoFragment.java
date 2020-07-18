package com.example.phonephoto;

import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PhotoFragment extends Fragment {

    String TAG = "PJ2 PhotoFragment";

    Button backupButton;                                    // button: 서버로 모두 올리기
    RecyclerView recyclerView;                              // 갤러리 출력될 recyclerView
    RecyclerView.LayoutManager layoutManager;
    int columnNum = 3;                                      // grid view 행 개수

    List<String> filesList = new ArrayList<>();             // 갤러리 파일 경로들 저장

    Uri imgUri;                                             // 기기 이미지 파일 uri
    Cursor cursor;                                          // 갤러리 이미지 탐색
    String absolutePath;                                    // 한 이미지 절대경로
    GalleryAdapter galleryAdapter;
    Point size;                                             // 기기 화면 사이즈

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        // view 객체 정의
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_photo, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        backupButton = rootView.findViewById(R.id.backupButton);

        layoutManager = new GridLayoutManager(getContext(),columnNum);
        recyclerView.setLayoutManager(layoutManager);

        // 화면 크기에 맞게 이미지를 출력하기 위해 디스플레이의 사이즈를 구함
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        return rootView;
    }

    // MainActivity 에서 권한 허용 선택하기 전에
    // onCreate, onCreateView는 실행이 되어 버리나,
    // onResume 은 선택 후에 실행됨.
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
         */

        cursor = getActivity().getContentResolver().query(imgUri, null, null, null, null);
        //absolutePath = cursor.getString(cursor.getColumnIndex("_data"));

        galleryAdapter = new GalleryAdapter(cursor, size.x/3);
        recyclerView.setAdapter(galleryAdapter);

    }
}
