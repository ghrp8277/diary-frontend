package com.example.diaryapplication.service.CustomCalender;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.transition.TransitionManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionSet;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.diary.DiaryActivity;
import com.example.diaryapplication.activity.diary.fragments.DiaryEmojiFragment;
import com.example.diaryapplication.activity.diary.fragments.DiaryUpdateFragment;
import com.example.diaryapplication.activity.diary.fragments.DiaryWriteFragment;
import com.example.diaryapplication.dto.BoardResult;
import com.example.diaryapplication.dto.EmotionFileResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import com.transitionseverywhere.*;

// 어댑터 : 사용자에게 보여줄 데이터와 리사이클러뷰 인스턴스 간의 중재자 역할
// 리사이클러뷰 어댑터 클래스의 서브 클래스로 생성됨
public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {
    DiaryEmojiFragment diaryEmojiFragment;

    DiaryActivity diaryActivity;

    ArrayList<String> emojiList;

    int selectIndex;

    public EmojiAdapter(ArrayList<String> emojiList, DiaryEmojiFragment diaryEmojiFragment, DiaryActivity diaryActivity) {
        this.diaryActivity = diaryActivity;

        this.diaryEmojiFragment = diaryEmojiFragment;

        this.emojiList = emojiList;
    }

    // 데이터를 보여주는데에 사용되는 뷰를 갖도록 초기화된 뷰홀더 객체를 생성과 반환
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.recyclerview_emoji_row, parent, false);

        return new ViewHolder(view);
    }

    // 두 개의 인자를 받음
    // 1. viewHolder
    // 2. 보여줄 리스트 항목을 나타내는 정수값
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageButton floatingActionButton = (ImageButton) diaryEmojiFragment.getView().findViewById(R.id.nextEmojiButton);

        String emojiPath = emojiList.get(position);

        File emojiFile = new File(emojiPath);

        // 이모티콘 이미지뷰에 바인딩 작업
        if (emojiFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(emojiPath);

            holder.emojiImageView.setImageBitmap(bitmap);
        }

        // 클릭 이벤트
        holder.emojiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectIndex = position;
                // 이모지 선택 애니메이션
                ObjectAnimator.ofFloat(holder.emojiImageView, View.ROTATION, 0f, 360f).start();

                setEmojiStatus(emojiPath);

                TransitionManager.beginDelayedTransition((ViewGroup) floatingActionButton.getParent());

                if (floatingActionButton.getVisibility() == View.INVISIBLE) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                // 다이어리 수정 프래그먼트에서 요청한 경우
                if (diaryEmojiFragment.getArguments() != null) {
                    BoardResult boardResult = (BoardResult) diaryEmojiFragment.getArguments().getSerializable("boardResult");
                    EmotionFileResult emotionFileResult = new EmotionFileResult();
                    emotionFileResult.setImage_file_path(emojiList.get(selectIndex));
                    boardResult.getEmotion_files().set(0, emotionFileResult);
                    bundle.putSerializable("boardResult", boardResult);
                    diaryActivity.onFragmentChange(3, bundle);
                }
                // 아니면 실행
                else {
                    // 이모지 파일 경로 전달
                    bundle.putString("imagePath", emojiList.get(selectIndex));
                    // 이모지 파일 경로 리스트 전달
                    ArrayList<String> imageFilesPath = new ArrayList<String>();
                    imageFilesPath.add(emojiList.get(selectIndex));
                    bundle.putSerializable("imageFilesPath", imageFilesPath);
                    diaryActivity.onFragmentChange(2, bundle);
                }
            }
        });
    }

    // 리스트에 보여줄 항목의 갯수를 반환
    @Override
    public int getItemCount() {
        return emojiList.size();
    }

    public void setEmojiStatus(String path) {
        TextView textView = (TextView) diaryEmojiFragment.getView().findViewById(R.id.diaryEmojiSelectTextView);

        ChangeText changeText = new ChangeText().setChangeBehavior(ChangeText.CHANGE_BEHAVIOR_OUT_IN);
        TransitionManager.beginDelayedTransition((ViewGroup) textView.getParent(), changeText);

        File file = new File(path);
        String fileName = file.getName();
        int Idx = fileName.lastIndexOf(".");
        String _fileName = fileName.substring(0, Idx);

        if (_fileName.equals("angry")) {
            textView.setText("화남");
        }
        if (_fileName.equals("easy")) {
            textView.setText("무난");
        }
        if (_fileName.equals("excited")) {
            textView.setText("신남");
        }
        if (_fileName.equals("expect")) {
            textView.setText("기대");
        }
        if (_fileName.equals("happy")) {
            textView.setText("행복");
       }
        if (_fileName.equals("sad")) {
            textView.setText("슬픔");
        }
        if (_fileName.equals("sick")) {
            textView.setText("아픔");
        }
        if (_fileName.equals("tired")) {
            textView.setText("피곤");
        }
        if (_fileName.equals("what")) {
            textView.setText("황당");
        }
    }

    // viewHolder
    // 뷰홀더 인스턴스는 리사이클러에서 리스트 항목을 보여주는 데 필요한 모든 것을 포함
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView emojiImageView;

        View emojiParentView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            emojiImageView = itemView.findViewById(R.id.emojiImageView);

            emojiParentView = itemView.findViewById(R.id.emojiParentView);
        }
    }
}
