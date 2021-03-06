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

        // ?????? ??????
        CalenderUtil.selectedDate = LocalDate.now();

        // ?????? ????????? ???
        int Month = CalenderUtil.selectedDate.getMonth().getValue();

        // ????????? ?????????
        listBoardResult = new ArrayList<BoardResult>();

        username = PreferenceManager.getString(getContext(), "USERNAME");

        // ?????? ??????
        setMonthView();

        // ????????? ?????????
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

    // ?????? ?????? ??????
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM??? yyyy");

        return date.format(formatter);
    }

    // ?????? ?????? ??????
    private String yearMonthFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy??? MM???");

        return date.format(formatter);
    }

    // ?????? ??????
    private void setMonthView() {
        // ??? ??? ????????? ??? ??????
        monthYearTextView.setText(yearMonthFromDate(CalenderUtil.selectedDate));

        // ?????? ??? ?????? ????????????
        ArrayList<LocalDate> dayList = daysInMonthArray(CalenderUtil.selectedDate);

        // ????????? ????????? ??????
        adapter = new CalenderAdapter(dayList, listBoardResult);

        // ???????????? ?????? (??? 7???)
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 7);

        // ???????????? ??????
        listRecyclerView.setLayoutManager(manager);

        // ????????? ??????
        listRecyclerView.setAdapter(adapter);

        // ??????????????? ??????
        runAnimationAgain();
    }

    // ?????? ??????
    private ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> dayList = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);

        // ?????? ??? ????????? ?????? ????????????(??? 28, 30, 31)
        int lastDay = yearMonth.lengthOfMonth();

        // ?????? ?????? ??? ?????? ??? ????????????(??? 4??? 1???)
        LocalDate firstDay = CalenderUtil.selectedDate.withDayOfMonth(1);

        // ??? ?????? ??? ?????? ????????????(??? : 1. ??? : 7)
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        // ?????? ??????
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

    // ???????????? ?????? ??? ????????? ????????????.
    private void setCalenderByImageViewBind(String username, int month) {
        // ???????????? ui
        showSampleData(true);

        Call<ArrayList<BoardResult>> doGetAllBoardRead = RetrofitClient.getApiService(getContext()).allBoardRead(username, month);
        doGetAllBoardRead.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    showSampleData(false);

                    ArrayList<BoardResult> boardResult = (ArrayList<BoardResult>) response.body();

                    listBoardResult = boardResult;

                    // ?????? ??? ?????? ??????????????? ??????
                    int nowDay = CalenderUtil.selectedDate.getDayOfMonth();

                    for (int cnt = 0; cnt < listBoardResult.size(); cnt++) {
                        int date = listBoardResult.get(cnt).getDatetime().getDate();

                        if (date == nowDay) {
                            CalenderUtil.isTodayCreateMatched = true;
                        }
                    }

                    // ?????? ??????
                    setMonthView();
                } else {
                    // ?????? ?????? ????????? ????????????
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

    // ???????????? ui ??????
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
