package com.example.diaryapplication.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.diary.DiaryActivity;
import com.example.diaryapplication.activity.main.CustomCalenderFragment;
import com.example.diaryapplication.activity.main.SettingFragment;
import com.example.diaryapplication.service.CustomCalender.CalenderUtil;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FloatingActionButton createDiaryFab;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_animation_slide_in_left, R.anim.activity_animation_slide_out_right);
    }

    private void init() {
        createDiaryFab = (FloatingActionButton) findViewById(R.id.createDiaryFab);

        tabs = findViewById(R.id.mainTabLayout);
        tabs.addTab(tabs.newTab().setText("메인"));
        tabs.addTab(tabs.newTab().setText("설정"));

        createDiaryFab.setOnClickListener(this);

        CustomCalenderFragment customCalenderFragment = new CustomCalenderFragment();
        SettingFragment settingFragment = new SettingFragment();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = null;

                switch (tab.getPosition()) {
                    case 0:
                        selected = customCalenderFragment;
                        break;
                    case 1:
                        selected = settingFragment;
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, selected).commitNow();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.createDiaryFab:
                // 당일 글 작성이 되어있다면
                if (CalenderUtil.isTodayCreateMatched) {
                    Snackbar.make(view, "기존에 작성된 다이어리가 있습니다", Snackbar.LENGTH_SHORT).show();
                } else {
                    // 다이어리 액티비티로 이동
                    Intent mainIntent = new Intent(MainActivity.this, DiaryActivity.class);
                    mainIntent.putExtra("datetime", (LocalDateTime) LocalDateTime.now());
                    startActivity(mainIntent);
                }
                break;
        }
    }

    // 뒤로가기 막기
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
    }
}