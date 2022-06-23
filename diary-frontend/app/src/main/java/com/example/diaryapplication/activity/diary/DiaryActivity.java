package com.example.diaryapplication.activity.diary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.diary.fragments.DiaryDetailFragment;
import com.example.diaryapplication.activity.diary.fragments.DiaryEmojiFragment;
import com.example.diaryapplication.activity.diary.fragments.DiaryUpdateFragment;
import com.example.diaryapplication.activity.diary.fragments.DiaryWriteFragment;
import com.example.diaryapplication.dto.BoardResult;
import com.example.diaryapplication.dto.EmotionFileResult;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

public class DiaryActivity extends AppCompatActivity implements View.OnClickListener {
    Button diaryBackIntentBtn, datePickerButton, createDiaryButton, updateDiaryButton, deleteDiaryButton;

    DatePickerDialog datePickerDialog;

    DiaryEmojiFragment diaryEmojiFragment;
    DiaryWriteFragment diaryWriteFragment;
    DiaryDetailFragment diaryDetailFragment;
    DiaryUpdateFragment diaryUpdateFragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    LinearLayout linearUpdateAndDelete;

    LocalDateTime selectLocalDateTime;

    // 1번 이모지, 2번 글쓰기, 3번 수정
    int fragmentIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        init();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_animation_slide_in_left, R.anim.activity_animation_slide_out_right);
    }

    public void init() {
        diaryBackIntentBtn = (Button) findViewById(R.id.diaryBackIntentBtn);
        datePickerButton = (Button) findViewById(R.id.datePickerButton);
        createDiaryButton = (Button) findViewById(R.id.createDiaryButton);
        updateDiaryButton = (Button) findViewById(R.id.updateDiaryButton);
        deleteDiaryButton = (Button) findViewById(R.id.deleteDiaryButton);
        linearUpdateAndDelete = (LinearLayout) findViewById(R.id.linearUpdateAndDelete);
        diaryWriteFragment = new DiaryWriteFragment();
        diaryEmojiFragment = new DiaryEmojiFragment();
        diaryDetailFragment = new DiaryDetailFragment();
        diaryUpdateFragment = new DiaryUpdateFragment();
        diaryBackIntentBtn.setOnClickListener(this);
        datePickerButton.setOnClickListener(this);
        createDiaryButton.setOnClickListener(this);
        updateDiaryButton.setOnClickListener(this);

        boolean isWrite = getIntent().getBooleanExtra("isWrite", false);

        // 글 작성이 되어있다면? 디테일 화면을 뿌려준다
        if (isWrite) {
            BoardResult boardResult = (BoardResult) getIntent().getSerializableExtra("boardResult");
            int id = boardResult.getId();
            Bundle bundle = new Bundle();
            bundle.putInt("id", id);

            onFragmentChange(4, bundle);
        }

        selectLocalDateTime = (LocalDateTime) getIntent().getSerializableExtra("datetime");

        initDatePicker();

        datePickerButton.setText(getTodaysDate());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.diaryBackIntentBtn:
                // 이전 버튼
                onBackFragment();
                break;
            case R.id.datePickerButton:
                // 날짜 선택 버튼
                openDatePicker();
                break;
            case R.id.createDiaryButton:
                // 작성 완료 버튼
                // 글쓰기 프래그먼트 일때
                if (fragmentIndex == 2) {
                    diaryWriteFragment.boardCreateCheck();
                }
                // 수정 프래그먼트 일때
                else if (fragmentIndex == 3) {
                    diaryUpdateFragment.boardUpdateCheck();
                }
                break;
            case R.id.updateDiaryButton:
                // 수정 완료 버튼
                BoardResult boardResult = (BoardResult) getIntent().getSerializableExtra("boardResult");

                Bundle bundle = new Bundle();
                bundle.putSerializable("boardResult", boardResult);
                onFragmentChange(3, bundle);
                break;
        }
    }

    public void onBackFragment() {
        // writeFragment
        if (fragmentIndex == 2){
            createDiaryButton.setVisibility(View.INVISIBLE);

            fragmentIndex = 1;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.diaryFrameLayout, diaryEmojiFragment)
                    .addToBackStack(null).commit();
        }
        // updateFragment
        else if (fragmentIndex == 3) {
            createDiaryButton.setVisibility(View.INVISIBLE);
            linearUpdateAndDelete.setVisibility(View.VISIBLE);

            fragmentIndex = 1;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.diaryFrameLayout, diaryDetailFragment)
                    .addToBackStack(null).commit();
        }
        // emojiFragment, detailFragment
        else {
            finish();
        }
    }

    public void onFragmentChange(int index, Bundle bundle) {
        fragmentIndex = index;
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment selected = null;

        switch (index) {
            case 1:
                // 이모지 프래그먼트
                createDiaryButton.setVisibility(View.GONE);
                diaryEmojiFragment.setArguments(bundle);
                selected = diaryEmojiFragment;
                break;
            case 2:
                // 글쓰기 프래그먼트
                createDiaryButton.setVisibility(View.VISIBLE);
                bundle.putSerializable("datetime", (LocalDateTime) selectLocalDateTime);
                diaryWriteFragment.setArguments(bundle);
                selected = diaryWriteFragment;
                break;
            case 3:
                // 수정 프래그먼트
                createDiaryButton.setVisibility(View.VISIBLE);
                linearUpdateAndDelete.setVisibility(View.GONE);
                diaryUpdateFragment.setArguments(bundle);
                selected = diaryUpdateFragment;
                break;
            case 4:
                // 디테일 프래그먼트
                // 등록 버튼 비활성화 및 수정 삭제 버튼 활성화
                createDiaryButton.setVisibility(View.GONE);
                linearUpdateAndDelete.setVisibility(View.VISIBLE);
                // 날짜 선택 버튼 활성화
                setEnableDatePicker(false);
                diaryDetailFragment.setArguments(bundle);
                selected = diaryDetailFragment;
                break;
        }

        fragmentTransaction.replace(R.id.diaryFrameLayout, selected).commitNow();
    }

    private String getTodaysDate() {
        // 현재 날짜 지정
        LocalDateTime localDateTime = (LocalDateTime) getIntent().getSerializableExtra("datetime");
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        int day = localDateTime.getDayOfMonth();

        calendar.set(year, month, day);

        int calendarYear = calendar.get(Calendar.YEAR);
        int calendarMonth = calendar.get(Calendar.MONTH);
        int calendarDay = calendar.get(Calendar.DAY_OF_MONTH);

        return makeDataString(calendarDay, calendarMonth, calendarYear);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDataString(day, month, year);
                datePickerButton.setText(date);

                LocalDate localDate = LocalDate.of(year, month, day);
                LocalTime localTime = LocalTime.now();

                selectLocalDateTime = LocalDateTime.of(localDate, localTime);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDataString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day;
    }

    private String getMonthFormat(int month) {
        if (month == 1) return "JAN";
        if (month == 2) return "FEB";
        if (month == 3) return "MAR";
        if (month == 4) return "APR";
        if (month == 5) return "MAY";
        if (month == 6) return "JUN";
        if (month == 7) return "JUL";
        if (month == 8) return "AUG";
        if (month == 9) return "SEP";
        if (month == 10) return "OCT";
        if (month == 11) return "NOV";
        if (month == 12) return "DEC";

        return "JAN";
    }

    private void openDatePicker() {
        datePickerDialog.show();
    }

    public void setEnableDatePicker(boolean isEnabled) {
        // 날짜 선택 버튼 비활성화
        datePickerButton.setEnabled(isEnabled);
    }
}
