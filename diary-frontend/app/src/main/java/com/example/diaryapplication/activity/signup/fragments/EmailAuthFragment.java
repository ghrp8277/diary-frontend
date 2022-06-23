package com.example.diaryapplication.activity.signup.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.signup.SignupActivity;
import com.example.diaryapplication.animation.CubeAnimation;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;

import retrofit2.Call;
import retrofit2.Response;

public class EmailAuthFragment extends Fragment {
    EditText authEmailEditText;
    EditText authNumberEditText;
    Button authEmailBtn;
    Button authSubmitNumberBtn;
    Button authEmailNextBtn;
    TextView authAvailableEmailTextView;
    SignupActivity signUpActivity;
    TextView authTimerTextView;
    CountDownTimer cdt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_auth, container, false);
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
        authEmailEditText = (EditText) view.findViewById(R.id.authEmailEditText);
        authEmailBtn = (Button) view.findViewById(R.id.authEmailBtn);
        authNumberEditText = (EditText) view.findViewById(R.id.authNumberEditText);
        authSubmitNumberBtn = (Button) view.findViewById(R.id.authSubmitNumberBtn);
        authAvailableEmailTextView = (TextView) view.findViewById(R.id.authAvailableEmailTextView);
        authEmailNextBtn = (Button) view.findViewById(R.id.authEmailNextBtn);
        signUpActivity = (SignupActivity) getActivity();
        authTimerTextView = (TextView) view.findViewById(R.id.authTimerTextView);

        authEmailEditText.setText(getArguments().getString("email"));
        setViewControlInit();
    }

    public void setViewControlInit() {
        // 이메일 인증하기
        authEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailAuthNumber(authEmailEditText.getText().toString());
                // 카운트가 되고 있다면 카운트 종료 후 다시 생성
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;
                }
                // 카운트 시작
                AuthCodeTimmer();
            }
        });

        // 인증번호 확인하기
        authSubmitNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authNumberEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(),"인증번호를 입력바랍니다.", Toast.LENGTH_SHORT).show();
                } else if (authNumberEditText.getText().toString().length() < 6) {
                    Toast.makeText(getActivity(),"입력하신 인증번호가 6글자 미만입니다.\n다시 입력바랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    authSubmit(authNumberEditText.getText().toString());
                }
            }
        });

        // 다음
        authEmailNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("email", getArguments().getString("email"));

                signUpActivity.onFragmentChange(2, 35, bundle);
            }
        });
    }

    // 이메일 인증 시간 카운트
    public void AuthCodeTimmer() {
        authTimerTextView.setVisibility(View.VISIBLE);
        // TODO : 타이머 돌릴 총시간
        String conversionTime = "000300";

        countDown(conversionTime);
    }

    public void countDown(String time) {
        long conversionTime = 0;
        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간
        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;
        // 첫번째 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번째 인자 : 주기( 1000 = 1초)
        cdt = new CountDownTimer(conversionTime, 1000) {
            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {
                // 시간 단위
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));
                // 분 단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫
                // 초 단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지
                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫
                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }
                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }
                authTimerTextView.setText(min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {
                // 변경 후
                authTimerTextView.setTextColor(Color.parseColor("#000000"));
                authTimerTextView.setText("00:00");
                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지
            }
        }.start();
    }

    // 이메일 인증번호 받기
    public void getEmailAuthNumber(String e_mail) {
        Call<Boolean> doEmailAuthCheck = RetrofitClient.getApiService(getContext()).emailAuthCheck(e_mail);
        SimpleRetrofit.RetrofitService(doEmailAuthCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();

                if (isCheck) {
                    authNumberEditText.setVisibility(View.VISIBLE);
                    authSubmitNumberBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void doFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doResponseFailedCodeMatch(int statusCode, String message) {

            }
        }, getContext());
    }

    // 인증번호 확인하기
    public void authSubmit(String number) {
        Call<Boolean> doEmailNumberAuthCheck = RetrofitClient.getApiService(getContext()).emailAuthNumberCheck(number, getArguments().getString("email"));
        SimpleRetrofit.RetrofitService(doEmailNumberAuthCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();

                if (isCheck) {
                    authAvailableEmailTextView.setTextColor(Color.parseColor("#000000"));
                    authAvailableEmailTextView.setText("인증되었습니다!");
                    authEmailNextBtn.setEnabled(true);

                    // 카운트가 되고 있다면 카운트 종료
                    if (cdt != null) {
                        cdt.cancel();
                        cdt = null;

                        authTimerTextView.setText("00:00");
                    }
                } else {
                    authAvailableEmailTextView.setTextColor(Color.parseColor("#FFFF0000"));
                    authAvailableEmailTextView.setText("인증 실패!");
                    authEmailNextBtn.setEnabled(false);
                }
            }

            @Override
            public void doFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doResponseFailedCodeMatch(int statusCode, String message) {

            }
        }, getContext());
    }
}