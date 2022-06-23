package com.example.diaryapplication.activity.diary.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.diary.DiaryActivity;
import com.example.diaryapplication.animation.CubeAnimation;
import com.example.diaryapplication.service.CustomCalender.CustomGridRecyclerView;
import com.example.diaryapplication.service.CustomCalender.EmojiAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class DiaryEmojiFragment extends Fragment {

    private CustomGridRecyclerView emojiRecyclerView;

    private EmojiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary_emoji, container, false);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return CubeAnimation.create(CubeAnimation.RIGHT, enter, 1000);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    private void init(View view) {
        emojiRecyclerView = (CustomGridRecyclerView) view.findViewById(R.id.emojiRecyclerView);

        setEmojiView();

        runAnimationAgain();
    }

    private void setEmojiView() {
        String basePath = getActivity().getFilesDir().getAbsolutePath();

        ArrayList<String> emojiList = emojiPathArray(basePath);

        adapter = new EmojiAdapter(emojiList, this, (DiaryActivity) getActivity());

        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 3);

        emojiRecyclerView.setLayoutManager(manager);

        emojiRecyclerView.setAdapter(adapter);
    }

    private void runAnimationAgain() {
        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(),
                        R.anim.gridlayout_animation_from_bottom);

        emojiRecyclerView.setLayoutAnimation(controller);

        adapter.notifyDataSetChanged();

        emojiRecyclerView.scheduleLayoutAnimation();
    }

    private ArrayList<String> emojiPathArray(String basePath) {
            String emojiPath = basePath + File.separator + "emoji" + File.separator;

            File dir = new File(emojiPath);

            File[] list = dir.listFiles();

            ArrayList<String> emojiList = new ArrayList<>();


            for (int cnt = 0; cnt < Objects.requireNonNull(list).length; cnt++) {
                String fileName = list[cnt].getName();

                // 파일명에서 가장 마지막에 오는 '.'의 index 확인
                int index = fileName.lastIndexOf(".");

                // 파일 확장자가 이미지일 경우에만 리스트 추가
                if (index > 0) {

                    String extension = fileName.substring(index + 1);

                    if (
                            extension.equals("jpg") ||
                            extension.equals("png") ||
                            extension.equals("gif")
                    ) {
                        emojiList.add(emojiPath + fileName);
                    }
                }
            }

            return emojiList;
    }
}