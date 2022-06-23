package com.example.diaryapplication.activity.signup;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.signup.fragments.EmailAuthFragment;
import com.example.diaryapplication.activity.signup.fragments.IdConfirmFragment;
import com.example.diaryapplication.activity.signup.fragments.PasswordInputFragment;
import com.google.android.material.tabs.TabLayout;

public class SignupActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    EmailAuthFragment emailAuthFragment;
    IdConfirmFragment idConfirmFragment;
    PasswordInputFragment passwordInputFragment;
    ProgressBar signUpProgressBar;
    Button signUpBackIntentBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_animation_slide_in_left, R.anim.activity_animation_slide_out_right);
    }

    private void init() {
        emailAuthFragment = new EmailAuthFragment();
        idConfirmFragment = new IdConfirmFragment();
        passwordInputFragment = new PasswordInputFragment();
        signUpProgressBar = (ProgressBar) findViewById(R.id.signUpProgressBar);
        signUpBackIntentBtn = (Button) findViewById(R.id.signUpBackIntentBtn);

        setViewControlInit();
    }

    public void setViewControlInit() {
        signUpBackIntentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onFragmentChange(int index, int progressValue, Bundle bundle) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        signUpProgressBar.setProgress(signUpProgressBar.getProgress() + progressValue);
        Fragment selected = null;

        switch (index) {
            case 1:
                // 이메일 인증 프래그먼트
                emailAuthFragment.setArguments(bundle);
                selected = emailAuthFragment;
                break;
            case 2:
                // 계정 확인 프래그먼트
                idConfirmFragment.setArguments(bundle);
                selected = idConfirmFragment;
                break;
            case 3:
                // 비밀번호 입력 프래그먼트
                passwordInputFragment.setArguments(bundle);
                selected = passwordInputFragment;
                break;
            default:
                break;
        }

        fragmentTransaction.replace(R.id.signUpFragment, selected).commitNow();
    }

    public void onRemoveFragment(Fragment fragment) {
        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction().remove(fragment);
            fragmentTransaction.commit();
        }
    }
}
