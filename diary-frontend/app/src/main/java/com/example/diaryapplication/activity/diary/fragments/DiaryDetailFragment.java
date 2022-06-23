package com.example.diaryapplication.activity.diary.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

public class DiaryDetailFragment extends Fragment {
    DiaryActivity diaryActivity;
    Button deleteDiaryButton;
    ImageView diaryDetailEmojiImageView;
    TextView diaryDetailTitleTextView;
    TextView diaryDetailContentTextView;
    BoardResult boardResult;
    String username;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_detail, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return CubeAnimation.create(CubeAnimation.RIGHT, enter, 1000);
    }

    public void init(View view) {
        diaryActivity = (DiaryActivity) getActivity();
        deleteDiaryButton = (Button) diaryActivity.findViewById(R.id.deleteDiaryButton);
        diaryDetailEmojiImageView = (ImageView) view.findViewById(R.id.diaryDetailEmojiImageView);
        diaryDetailTitleTextView = (TextView) view.findViewById(R.id.diaryDetailTitleTextView);
        diaryDetailContentTextView = (TextView) view.findViewById(R.id.diaryDetailContentTextView);
        username = PreferenceManager.getString(getContext(), "USERNAME");
        int id = getArguments().getInt("id");

        setBoardReadById(username, id);
        boardDeleteCheck(id);
    }

    // 디테일 화면 설정
    private void setDetailView(BoardResult boardResult){
        diaryDetailTitleTextView.setText(boardResult.getTitle());
        diaryDetailContentTextView.setText(boardResult.getContent());
        ArrayList<EmotionFileResult> imagePath = boardResult.getEmotion_files();

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath.get(0).getImage_file_path());
        diaryDetailEmojiImageView.setImageBitmap(bitmap);
    }

    // 삭제 버튼 클릭 확인
    public void boardDeleteCheck(int id) {
        deleteDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog(id);
            }
        });
    }

    // 다이얼로그
    private void deleteDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setMessage("다이어리를 삭제할까요?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteDairyById(username, id);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        AlertDialog msgDlg = builder.create();
        msgDlg.show();
    }

    // 특정 날짜 다이어리 조회
    private void setBoardReadById(String username, Integer id){
        Call<BoardResult> doBoardRead = RetrofitClient.getApiService(getContext()).doBoardRead(username, id);
        SimpleRetrofit.RetrofitService(doBoardRead, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                try {
                    BoardResult boardResults = (BoardResult) response.body();
                    boardResult = boardResults;

                    setDetailView(boardResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    // 특정 날짜 다이어리 삭제
    private void deleteDairyById(String username, Integer id) {
        Call<Boolean> doDeleteDiary = RetrofitClient.getApiService(getContext()).doDeleteDiary(username, id);
        SimpleRetrofit.RetrofitService(doDeleteDiary, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean deleteResult = (Boolean) response.body();

                // 액티비티 종료
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().finish();
                // 메인 액티비티 재실행 실시
                getActivity().startActivity(intent);
            }

            @Override
            public void doFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doResponseFailedCodeMatch(int statusCode, String message) {

            }
        }, getContext());
    }

}