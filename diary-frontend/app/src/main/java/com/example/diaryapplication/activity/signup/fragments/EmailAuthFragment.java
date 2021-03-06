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
        // ????????? ????????????
        authEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailAuthNumber(authEmailEditText.getText().toString());
                // ???????????? ?????? ????????? ????????? ?????? ??? ?????? ??????
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;
                }
                // ????????? ??????
                AuthCodeTimmer();
            }
        });

        // ???????????? ????????????
        authSubmitNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authNumberEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(),"??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                } else if (authNumberEditText.getText().toString().length() < 6) {
                    Toast.makeText(getActivity(),"???????????? ??????????????? 6?????? ???????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    authSubmit(authNumberEditText.getText().toString());
                }
            }
        });

        // ??????
        authEmailNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("email", getArguments().getString("email"));

                signUpActivity.onFragmentChange(2, 35, bundle);
            }
        });
    }

    // ????????? ?????? ?????? ?????????
    public void AuthCodeTimmer() {
        authTimerTextView.setVisibility(View.VISIBLE);
        // TODO : ????????? ?????? ?????????
        String conversionTime = "000300";

        countDown(conversionTime);
    }

    public void countDown(String time) {
        long conversionTime = 0;
        // 1000 ????????? 1???
        // 60000 ????????? 1???
        // 60000 * 3600 = 1??????
        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"??? ?????????, ????????? ????????? 0 ?????? ??????
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }

        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }

        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // ????????????
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;
        // ????????? ?????? : ????????? ?????? (???????????? 30?????? 30 x 1000(??????))
        // ????????? ?????? : ??????( 1000 = 1???)
        cdt = new CountDownTimer(conversionTime, 1000) {
            // ?????? ???????????? ??? ??????
            public void onTick(long millisUntilFinished) {
                // ?????? ??????
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));
                // ??? ??????
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // ???
                // ??? ??????
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // ?????????
                // ??????????????? ??????
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // ???
                // ????????? ???????????? 0??? ?????????
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                // ?????? ???????????? 0??? ?????????
                if (min.length() == 1) {
                    min = "0" + min;
                }
                // ?????? ???????????? 0??? ?????????
                if (second.length() == 1) {
                    second = "0" + second;
                }
                authTimerTextView.setText(min + ":" + second);
            }

            // ???????????? ?????????
            public void onFinish() {
                // ?????? ???
                authTimerTextView.setTextColor(Color.parseColor("#000000"));
                authTimerTextView.setText("00:00");
                // TODO : ???????????? ?????? ???????????? ?????? ???????????? ????????????
            }
        }.start();
    }

    // ????????? ???????????? ??????
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

    // ???????????? ????????????
    public void authSubmit(String number) {
        Call<Boolean> doEmailNumberAuthCheck = RetrofitClient.getApiService(getContext()).emailAuthNumberCheck(number, getArguments().getString("email"));
        SimpleRetrofit.RetrofitService(doEmailNumberAuthCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();

                if (isCheck) {
                    authAvailableEmailTextView.setTextColor(Color.parseColor("#000000"));
                    authAvailableEmailTextView.setText("?????????????????????!");
                    authEmailNextBtn.setEnabled(true);

                    // ???????????? ?????? ????????? ????????? ??????
                    if (cdt != null) {
                        cdt.cancel();
                        cdt = null;

                        authTimerTextView.setText("00:00");
                    }
                } else {
                    authAvailableEmailTextView.setTextColor(Color.parseColor("#FFFF0000"));
                    authAvailableEmailTextView.setText("?????? ??????!");
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