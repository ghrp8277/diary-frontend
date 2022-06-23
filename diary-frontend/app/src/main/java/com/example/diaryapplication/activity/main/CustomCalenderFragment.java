package com.example.diaryapplication.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.LoginActivity;
import com.example.diaryapplication.dto.BoardResult;
import com.example.diaryapplication.service.CustomCalender.CalenderAdapter;
import com.example.diaryapplication.service.CustomCalender.CalenderUtil;
import com.example.diaryapplication.service.CustomCalender.CustomGridRecyclerView;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomCalenderFragment extends Fragment implements View.OnClickListener {
    private TextView monthYearTextView;

    private CustomGridRecyclerView listRecyclerView;

    private ArrayList<BoardResult> listBoardResult;

    private CalenderAdapter adapter;

    private String username;

    private ImageButton customCalenderPreBtn, customCalenderNextBtn;

    private ShimmerFrameLayout sflShimmer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_calender, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
    }

    private void init(View view) {
        monthYearTextView = (TextView) view.findViewById(R.id.monthYearTextView);
        listRecyclerView = (CustomGridRecyclerView) view.findViewById(R.id.recyclerView);
        customCalenderPreBtn = (ImageButton) view.findViewById(R.id.customCalenderPreBtn);
        customCalenderNextBtn = (ImageButton) view.findViewById(R.id.customCalenderNextBtn);
        sflShimmer = (ShimmerFrameLayout) view.findViewById(R.id.sflShimmer);

        customCalenderPreBtn.setOnClickListener(this);
        customCalenderNextBtn.setOnClickListener(this);

        // 현재 날짜
        CalenderUtil.selectedDate = LocalDate.now();

        // 현재 날짜에 월
        int Month = CalenderUtil.selectedDate.getMonth().getValue();

        // 데이터 초기화
        listBoardResult = new ArrayList<BoardResult>();

        username = PreferenceManager.getString(getContext(), "USERNAME");

        // 화면 설정
        setMonthView();

        // 캘린더 바인딩
        setCalenderByImageViewBind(username, Month);
    }

    private void runAnimationAgain() {
        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(getContext(),
                        R.anim.gridlayout_animation_from_bottom);

        listRecyclerView.setLayoutAnimation(controller);

        adapter.notifyDataSetChanged();

        listRecyclerView.scheduleLayoutAnimation();
    }

    // 날짜 타입 설정
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 yyyy");

        return date.format(formatter);
    }

    // 날짜 타입 설정
    private String yearMonthFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");

        return date.format(formatter);
    }

    // 화면 설정
    private void setMonthView() {
        // 년 월 텍스트 뷰 셋팅
        monthYearTextView.setText(yearMonthFromDate(CalenderUtil.selectedDate));

        // 해당 월 날짜 가져오기
        ArrayList<LocalDate> dayList = daysInMonthArray(CalenderUtil.selectedDate);

        // 어뎁터 데이터 적용
        adapter = new CalenderAdapter(dayList, listBoardResult);

        // 레이아웃 설정 (열 7개)
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 7);

        // 레이아웃 적용
        listRecyclerView.setLayoutManager(manager);

        // 어뎁터 적용
        listRecyclerView.setAdapter(adapter);

        // 애니메이션 적용
        runAnimationAgain();
    }

    // 날짜 생성
    private ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> dayList = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);

        // 해당 월 마지막 날짜 가져오기(예 28, 30, 31)
        int lastDay = yearMonth.lengthOfMonth();

        // 헤당 월의 첫 번째 날 가져오기(예 4월 1일)
        LocalDate firstDay = CalenderUtil.selectedDate.withDayOfMonth(1);

        // 첫 번째 날 요일 가져오기(월 : 1. 일 : 7)
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        // 날짜 생성
        for (int cnt = 1; cnt < 42; cnt++) {
            if (cnt <= dayOfWeek || cnt > lastDay + dayOfWeek) {
                continue;
            }
            else {
                dayList.add(
                        LocalDate.of(
                            CalenderUtil.selectedDate.getYear(),
                            CalenderUtil.selectedDate.getMonth(),
                            cnt - dayOfWeek
                        )
                );
            }
        }

        return dayList;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.customCalenderPreBtn:
                CalenderUtil.selectedDate = CalenderUtil.selectedDate.minusMonths(1);

                int preMonth = CalenderUtil.selectedDate.getMonth().getValue();

                setMonthView();

                setCalenderByImageViewBind(username, preMonth);
                break;
            case R.id.customCalenderNextBtn:
                CalenderUtil.selectedDate = CalenderUtil.selectedDate.plusMonths(1);

                int nextMonth = CalenderUtil.selectedDate.getMonth().getValue();

                setMonthView();

                setCalenderByImageViewBind(username, nextMonth);
                break;
        }
    }

    // 다이어리 작성 글 전체를 가져온다.
    private void setCalenderByImageViewBind(String username, int month) {
        // 스켈레톤 ui
        showSampleData(true);

        Call<ArrayList<BoardResult>> doGetAllBoardRead = RetrofitClient.getApiService(getContext()).allBoardRead(username, month);
        doGetAllBoardRead.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    showSampleData(false);

                    ArrayList<BoardResult> boardResult = (ArrayList<BoardResult>) response.body();

                    listBoardResult = boardResult;

                    // 당일 글 작성 되어있는지 체크
                    int nowDay = CalenderUtil.selectedDate.getDayOfMonth();

                    for (int cnt = 0; cnt < listBoardResult.size(); cnt++) {
                        int date = listBoardResult.get(cnt).getDatetime().getDate();

                        if (date == nowDay) {
                            CalenderUtil.isTodayCreateMatched = true;
                        }
                    }

                    // 화면 설정
                    setMonthView();
                } else {
                    // 토큰 인증 실패시 로그아웃
                    if (response.code() == 403 && response.message().equals("Forbidden")) {
                        PreferenceManager.removeKey(getContext(), "AUTO_LOGIN_CHECK");
                        PreferenceManager.removeKey(getContext(), "ACCESS_TOKEN");
                        PreferenceManager.removeKey(getContext(), "REFRESH_TOKEN");
                        PreferenceManager.removeKey(getContext(), "USERNAME");
                        PreferenceManager.removeKey(getContext(), "SIGN_IN_AS");

                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        getContext().startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 스켈레톤 ui 표현
    private void showSampleData(boolean isLoading) {
        if (isLoading) {
            sflShimmer.setVisibility(View.VISIBLE);
            listRecyclerView.setVisibility(View.GONE);
        } else {
            sflShimmer.setVisibility(View.GONE);
            listRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
