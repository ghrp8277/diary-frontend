package com.example.diaryapplication.activity.diary.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.example.diaryapplication.activity.diary.DiaryActivity;
import com.example.diaryapplication.dto.BoardResult;
import com.example.diaryapplication.dto.EmotionFileResult;
import com.example.diaryapplication.animation.CubeAnimation;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class DiaryUpdateFragment extends Fragment {
    EditText diaryUpdateInputContentEditText;
    EditText diaryUpdateInputTitleEditText;
    ImageView diaryUpdateEmojiImageView;
    String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_update, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return CubeAnimation.create(CubeAnimation.RIGHT, enter, 1000);
    }

    private void init(View view) {
        diaryUpdateInputTitleEditText = (EditText) view.findViewById(R.id.diaryUpdateInputTitleEditText);
        diaryUpdateInputContentEditText = (EditText) view.findViewById(R.id.diaryUpdateInputContentEditText);
        diaryUpdateEmojiImageView = (ImageView) view.findViewById(R.id.diaryUpdateEmojiImageView);

        // 데이터 초기화
        username = PreferenceManager.getString(getContext(), "USERNAME");
        BoardResult boardResult = (BoardResult) getArguments().getSerializable("boardResult");

        // 선택한 이모티콘 렌더
        ArrayList<EmotionFileResult> emotionFileResults = boardResult.getEmotion_files();
        Bitmap bitmap = BitmapFactory.decodeFile(emotionFileResults.get(0).getImage_file_path());
        diaryUpdateEmojiImageView.setImageBitmap(bitmap);

        setViewControlInit(view);
    }

    private void setViewControlInit(View view) {
        // 이모티콘 클릭시 이모티콘 선택 프래그먼트로 이동
        diaryUpdateEmojiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoardResult boardResult = (BoardResult) getArguments().getSerializable("boardResult");
                Bundle bundle = new Bundle();
                bundle.putSerializable("boardResult", boardResult);
                DiaryActivity diaryActivity = (DiaryActivity) getActivity();
                diaryActivity.onFragmentChange(1, bundle);
            }
        });
    }

    public void boardUpdateCheck() {
        String title = diaryUpdateInputTitleEditText.getText().toString();
        String content = diaryUpdateInputContentEditText.getText().toString();
        BoardResult boardResult = (BoardResult) getArguments().getSerializable("boardResult");

        if(title.isEmpty() && content.isEmpty() && title.length() != 0 && content.length() != 0)
        {
            Toast.makeText(this.getContext(), "제목과 내용을 작성해주세요", Toast.LENGTH_SHORT).show();
        } else {
            LocalDateTime localDateTime = boardResult.getDatetime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            ArrayList<String> imageFilesPath = new ArrayList<String>();

            for (int cnt = 0; cnt < boardResult.getEmotion_files().size(); cnt++) {
                imageFilesPath.add(boardResult.getEmotion_files().get(cnt).getImage_file_path());
            }

            doBoardUpdate(
                    username,
                    boardResult.getId(),
                    title,
                    content,
                    localDateTime,
                    imageFilesPath
            );
        }
    }

    // 다이어리 수정
    public void doBoardUpdate(String username, Integer id, String title, String content, LocalDateTime datetime, ArrayList<String> imageFilesPath) {
        Call<Boolean> doBoardCreate = RetrofitClient.getApiService(getContext()).doBoardUpdate(username, id, title, content, datetime, imageFilesPath);
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
}
