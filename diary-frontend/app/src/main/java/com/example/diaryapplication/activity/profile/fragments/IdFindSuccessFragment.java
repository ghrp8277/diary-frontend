package com.example.diaryapplication.activity.profile.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaryapplication.R;
import com.example.diaryapplication.animation.CubeAnimation;

public class IdFindSuccessFragment extends Fragment {
    TextView idFindResultTextView;
    Button idFindResultLoginIntentBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_id_find_result, container, false);
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
        idFindResultTextView = (TextView) view.findViewById(R.id.idFindResultTextView);
        idFindResultLoginIntentBtn = (Button) view.findViewById(R.id.idFindResultLoginIntentBtn);

        setViewControlInit();
    }

    public void setViewControlInit() {
        idFindResultLoginIntentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        // 계정 값 색상 설정
        String username = getArguments().getString("username");
        String idFindResult = "고객님의 가입된 아이디는 '" + username + "' 입니다.\n아래 버튼을 눌러 로그인 해주세요.";
        idFindResultTextView.setText(idFindResult);

        String content = idFindResultTextView.getText().toString();
        SpannableString spannableString = new SpannableString(content);

        int start = content.indexOf(username);
        int end = start + username.length();

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#8A2233B1")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        idFindResultTextView.setText(spannableString);
    }
}
