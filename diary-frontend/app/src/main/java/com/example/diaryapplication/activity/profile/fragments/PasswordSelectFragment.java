package com.example.diaryapplication.activity.profile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.profile.ProfileActivity;
import com.example.diaryapplication.animation.CubeAnimation;

public class PasswordSelectFragment extends Fragment {
    Button passwordSelectResetPasswordBtn;
    Button passwordSelectLoginBtn;
    ProfileActivity profileActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_password_select, container, false);
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
        passwordSelectResetPasswordBtn = (Button) view.findViewById(R.id.passwordSelectResetPasswordBtn);
        passwordSelectLoginBtn = (Button) view.findViewById(R.id.passwordSelectLoginBtn);
        profileActivity = (ProfileActivity) getActivity();

        setViewControlInit();
    }

    private void setViewControlInit() {
        // 로그인 화면으로 이동
        passwordSelectLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        // 비밀번호 재설정 화면으로 이동
        passwordSelectResetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("username", getArguments().getString("username"));

                profileActivity.onFragmentChange(new PasswordChangeFragment(), bundle);
            }
        });
    }
}
