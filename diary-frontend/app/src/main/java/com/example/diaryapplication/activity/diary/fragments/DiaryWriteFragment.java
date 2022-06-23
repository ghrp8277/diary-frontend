package com.example.diaryapplication.activity.diary.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.MainActivity;
import com.example.diaryapplication.animation.CubeAnimation;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class DiaryWriteFragment extends Fragment {
    EditText diaryWriteInputContentEditText;
    EditText diaryWriteInputTitleEditText;
    ImageView diaryEmojiImageView;
    String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_write, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        // textUnderLineCustom();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return CubeAnimation.create(CubeAnimation.RIGHT, enter, 1000);
    }

    public void init(View view){
        diaryWriteInputTitleEditText = (EditText) view.findViewById(R.id.diaryWriteInputTitleEditText);
        diaryWriteInputContentEditText = (EditText) view.findViewById(R.id.diaryWriteInputContentEditText);
        diaryEmojiImageView = (ImageView) view.findViewById(R.id.diaryEmojiImageView);

        // 데이터 초기화
        username = PreferenceManager.getString(getContext(), "USERNAME");
        String imagePath = getArguments().getString("imagePath");

        // 선택한 이모티콘 렌더
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        diaryEmojiImageView.setImageBitmap(bitmap);
    }

    public void boardCreateCheck(){
        String title = diaryWriteInputTitleEditText.getText().toString();
        String content = diaryWriteInputContentEditText.getText().toString();

        if(title.isEmpty() && content.isEmpty() && title.length() != 0 && content.length() != 0)
        {
            Toast.makeText(this.getContext(), "제목과 내용을 작성해주세요", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("test", username);
            doBoardCreate(
                    username,
                    title,
                    content,
                    (LocalDateTime) getArguments().getSerializable("datetime"),
                    (ArrayList<String>) getArguments().getSerializable("imageFilesPath")
            );
        }
    }

    // 다이어리 업로드
    private void doBoardCreate(String username, String title, String content, LocalDateTime datetime, ArrayList<String> imageFilesPath) {
        Call<String> doBoardCreate = RetrofitClient.getApiService(getContext()).doBoardCreate(username, title, content, datetime, imageFilesPath);
        SimpleRetrofit.RetrofitService(doBoardCreate, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                // 액티비티 종료
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                // 메인 액티비티 재실행 실시
                getActivity().startActivity(intent);
            }

            @Override
            public void doFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doResponseFailedCodeMatch(int statusCode, String message) {

            }
        }, getContext());
    }

    // EditText(title, content) 입력 시 밑줄 제거
    public void textUnderLineCustom(){
        diaryWriteInputContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        diaryWriteInputContentEditText.clearComposingText();
                    }
                },200);
            }
        });

        diaryWriteInputTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        diaryWriteInputTitleEditText.clearComposingText();
                    }
                },200);
            }
        });
    }
}