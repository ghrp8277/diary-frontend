package com.example.diaryapplication.activity.profile.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.diaryapplication.activity.profile.ProfileActivity;
import com.example.diaryapplication.animation.CubeAnimation;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

public class IdFindFragment extends Fragment {
    EditText idSearchInputEditText;
    Button idSearchEmailConfirmBtn;
    EditText idSearchAuthNumInputEditText;
    Button idSearchAuthNumConfirmBtn;
    TextView idSearchAuthAvailableEmailTextView;
    TextView idSearchAuthTimerTextView;
    Button idSearchBtn;
    ProfileActivity profileActivity;
    CountDownTimer cdt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_id_find, container, false);
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
        idSearchInputEditText = (EditText) view.findViewById(R.id.idSearchInputEditText);
        idSearchEmailConfirmBtn = (Button) view.findViewById(R.id.idSearchEmailConfirmBtn);
        idSearchAuthNumInputEditText = (EditText) view.findViewById(R.id.idSearchAuthNumInputEditText);
        idSearchAuthNumConfirmBtn = (Button) view.findViewById(R.id.idSearchAuthNumConfirmBtn);
        idSearchAuthAvailableEmailTextView = (TextView) view.findViewById(R.id.idSearchAuthAvailableEmailTextView);
        idSearchAuthTimerTextView = (TextView) view.findViewById(R.id.idSearchAuthTimerTextView);
        idSearchBtn = (Button) view.findViewById(R.id.idSearchBtn);
        profileActivity = (ProfileActivity) getActivity();

        setViewControlInit();
    }

    public void setViewControlInit() {
        // 이메일 형식 검사
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String e_mail = idSearchInputEditText.getText().toString();

                if (checkEmailValidate(e_mail)) {
                    idSearchEmailConfirmBtn.setEnabled(true);
                }
            }
        };
        idSearchInputEditText.addTextChangedListener(textWatcher);

        // 이메일 인증
        idSearchEmailConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailAuthNumber(idSearchInputEditText.getText().toString());
                // 카운트가 되고 있다면 카운트 종료
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;

                    idSearchAuthTimerTextView.setText("00:00");
                }

                // 카운트 시작
                AuthCodeTimmer();
            }
        });

        // 인증번호 확인
        idSearchAuthNumConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String authNumber = idSearchAuthNumInputEditText.getText().toString();
                Log.i("test", authNumber);
                if (authNumber.isEmpty()) {
                    Toast.makeText(getActivity(),"인증번호를 입력바랍니다.", Toast.LENGTH_SHORT).show();
                } else if (authNumber.length() < 6) {
                    Toast.makeText(getActivity(),"입력하신 인증번호가 6글자 미만입니다.\n다시 입력바랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    authNumberCheckSubmit(authNumber);
                }
            }
        });

        // 아이디 찾기
        idSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindEmailById();
            }
        });
    }

    // 이메일 형식 검사
    public boolean checkEmailValidate(String str) {
        String Email_Pattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_Pattern);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    // 이메일 인증번호 받기
    public void getEmailAuthNumber(String e_mail) {
        Call<Boolean> doEmailAuthCheck = RetrofitClient.getApiService(getContext()).emailAuthCheck(e_mail);
        SimpleRetrofit.RetrofitService(doEmailAuthCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();

                if (isCheck) {
                    idSearchAuthNumConfirmBtn.setEnabled(true);
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
    public void authNumberCheckSubmit(String number) {
        Call<Boolean> doEmailNumberAuthCheck = RetrofitClient.getApiService(getContext()).emailAuthNumberCheck(number, idSearchInputEditText.getText().toString());
        SimpleRetrofit.RetrofitService(doEmailNumberAuthCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();

                if (isCheck) {
                    idSearchAuthAvailableEmailTextView.setTextColor(Color.parseColor("#000000"));
                    idSearchAuthAvailableEmailTextView.setText("인증되었습니다!");
                    idSearchBtn.setEnabled(true);
                } else {
                    idSearchAuthAvailableEmailTextView.setTextColor(Color.parseColor("#FFFF0000"));
                    idSearchAuthAvailableEmailTextView.setText("인증 실패!");
                    idSearchBtn.setEnabled(false);
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

    // 이메일 인증 시간 카운트
    public void AuthCodeTimmer() {
        idSearchAuthTimerTextView.setVisibility(View.VISIBLE);
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
                idSearchAuthTimerTextView.setText(min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {
                // 변경 후
                idSearchAuthTimerTextView.setTextColor(Color.parseColor("#000000"));
                idSearchAuthTimerTextView.setText("00:00");
                // TODO : 타이머가 모두 종료될때 어떤 이벤트를 진행할지
            }
        }.start();
    }

    // 아이디 찾기
    public void FindEmailById() {
        // 번들 객체 생성
        Bundle bundle = new Bundle();
        bundle.putString("username", "test");

        // 프래그먼트 전환
        profileActivity.onFragmentChange(new IdFindSuccessFragment(), bundle);
        Call<String> doFindId = RetrofitClient.getApiService(getContext()).findId(idSearchInputEditText.getText().toString());
        SimpleRetrofit.RetrofitService(doFindId, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                String username = (String) response.body();

                // 번들 객체 생성
                Bundle bundle = new Bundle();
                bundle.putString("username", username);

                // 프래그먼트 전환
                profileActivity.onFragmentChange(new IdFindSuccessFragment(), bundle);
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
