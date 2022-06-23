package com.example.diaryapplication.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.profile.fragments.IdFindFragment;
import com.example.diaryapplication.activity.profile.fragments.PasswordChangeFragment;
import com.example.diaryapplication.activity.profile.fragments.PasswordResetFragment;
import com.google.android.material.tabs.TabLayout;

public class ProfileActivity extends AppCompatActivity {
    TabLayout tabs;
    IdFindFragment idFindFragment;
    PasswordResetFragment passwordResetFragment;
    Button profileBackIntentBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_animation_slide_in_left, R.anim.activity_animation_slide_out_right);
    }

    private void init() {
        profileBackIntentBtn = findViewById(R.id.profileBackIntentBtn);
        idFindFragment = new IdFindFragment();
        passwordResetFragment = new PasswordResetFragment();

        tabs = findViewById(R.id.profileTabLayout);
        tabs.addTab(tabs.newTab().setText("아이디 찾기"));
        tabs.addTab(tabs.newTab().setText("비밀번호 재설정"));

        int tabIndex = getIntent().getIntExtra("index", 0);
        tabs.getTabAt(tabIndex).select();

        setInitFragmentChange(tabIndex);

        setViewControlInit();
    }

    public void setViewControlInit() {
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = null;

                switch (tab.getPosition()) {
                    case 0:
                        selected = idFindFragment;
                        break;
                    case 1:
                         selected = passwordResetFragment;
                        break;
                    default:
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.profileFrameLayout, selected).commitNow();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        profileBackIntentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setInitFragmentChange(int tabIndex) {
        Fragment selected = null;

        switch (tabIndex) {
            case 0:
                selected = idFindFragment;
                break;
            case 1:
                selected = passwordResetFragment;
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.profileFrameLayout, selected).commitNow();
    }

    public void onFragmentChange(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.profileFrameLayout, fragment).commitNow();
    }
}
