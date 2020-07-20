package com.example.phonephoto.phone;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.phonephoto.R;
import com.example.phonephoto.ServerGalleryActivity;
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

public class PhoneFragment extends Fragment {

    String TAG = "PJ2 PhoneFragment";

    RecyclerView recyclerView;                              // 갤러리 출력될 recyclerView
    RecyclerView.LayoutManager layoutManager;

    Button backupButton;                                    // 서버로 모두 올리기
    Button serverButton;                                    // 서버 갤러리 보기

    Cursor cursor;                                          // 갤러리 이미지 탐색
    PhoneAdapter phoneAdapter;
    Point size;

    Uri phoneUri;
    private ServiceApi service;
    private ArrayList<PhoneItem> items = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        service = RetrofitClient.getRetrofit().create(ServiceApi.class);

        // view 객체 정의
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_phone, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        backupButton = rootView.findViewById(R.id.backupButton);
        serverButton = rootView.findViewById(R.id.serverButton);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

                int idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID);
                int nameTakenColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int phoneColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while(cursor.moveToNext()) {
                    int id = cursor.getInt(idColumn);
                    String name = cursor.getString(nameTakenColumn);
                    String phoneNum = cursor.getString(phoneColumn);

                    uploadAllInfo(new PhoneUploadData(id, name, phoneNum));
                }

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
    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        cursor = getActivity().getContentResolver().query(phoneUri, null, null, null, null);

        int idColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID);
        int nameTakenColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int phoneColumn = cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(idColumn);
            String name = cursor.getString(nameTakenColumn);
            String phoneNum = cursor.getString(phoneColumn);
            PhoneItem phoneItem = new PhoneItem(id, name, phoneNum);
            items.add(phoneItem);

            Log.d(TAG, "phone Item: " + id + name + phoneNum);
        }

        phoneAdapter = new PhoneAdapter(items);
        recyclerView.setAdapter(phoneAdapter);
    }

    private void uploadAllInfo(PhoneUploadData phoneUploadData) {
        service.phoneUpload(phoneUploadData).enqueue(new Callback<PhoneUploadResponse>() {
            @Override
            public void onResponse(Call<PhoneUploadResponse> call, Response<PhoneUploadResponse> response) {
                PhoneUploadResponse result = response.body();
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                //showProgress(false);
            }

            @Override
            public void onFailure(Call<PhoneUploadResponse> call, Throwable t) {
                Toast.makeText(getContext(), "로그인 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("로그인 에러 발생", t.getMessage());
                //showProgress(false);
            }
        });
    }
}