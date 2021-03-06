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
        // ????????? ?????? ??????
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

        // ????????? ??????
        idSearchEmailConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEmailAuthNumber(idSearchInputEditText.getText().toString());
                // ???????????? ?????? ????????? ????????? ??????
                if (cdt != null) {
                    cdt.cancel();
                    cdt = null;

                    idSearchAuthTimerTextView.setText("00:00");
                }

                // ????????? ??????
                AuthCodeTimmer();
            }
        });

        // ???????????? ??????
        idSearchAuthNumConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String authNumber = idSearchAuthNumInputEditText.getText().toString();
                Log.i("test", authNumber);
                if (authNumber.isEmpty()) {
                    Toast.makeText(getActivity(),"??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                } else if (authNumber.length() < 6) {
                    Toast.makeText(getActivity(),"???????????? ??????????????? 6?????? ???????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                } else {
                    authNumberCheckSubmit(authNumber);
                }
            }
        });

        // ????????? ??????
        idSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FindEmailById();
            }
        });
    }

    // ????????? ?????? ??????
    public boolean checkEmailValidate(String str) {
        String Email_Pattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_Pattern);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    // ????????? ???????????? ??????
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

    // ???????????? ????????????
    public void authNumberCheckSubmit(String number) {
        Call<Boolean> doEmailNumberAuthCheck = RetrofitClient.getApiService(getContext()).emailAuthNumberCheck(number, idSearchInputEditText.getText().toString());
        SimpleRetrofit.RetrofitService(doEmailNumberAuthCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();

                if (isCheck) {
                    idSearchAuthAvailableEmailTextView.setTextColor(Color.parseColor("#000000"));
                    idSearchAuthAvailableEmailTextView.setText("?????????????????????!");
                    idSearchBtn.setEnabled(true);
                } else {
                    idSearchAuthAvailableEmailTextView.setTextColor(Color.parseColor("#FFFF0000"));
                    idSearchAuthAvailableEmailTextView.setText("?????? ??????!");
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

    // ????????? ?????? ?????? ?????????
    public void AuthCodeTimmer() {
        idSearchAuthTimerTextView.setVisibility(View.VISIBLE);
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
                idSearchAuthTimerTextView.setText(min + ":" + second);
            }

            // ???????????? ?????????
            public void onFinish() {
                // ?????? ???
                idSearchAuthTimerTextView.setTextColor(Color.parseColor("#000000"));
                idSearchAuthTimerTextView.setText("00:00");
                // TODO : ???????????? ?????? ???????????? ?????? ???????????? ????????????
            }
        }.start();
    }

    // ????????? ??????
    public void FindEmailById() {
        // ?????? ?????? ??????
        Bundle bundle = new Bundle();
        bundle.putString("username", "test");

        // ??????????????? ??????
        profileActivity.onFragmentChange(new IdFindSuccessFragment(), bundle);
        Call<String> doFindId = RetrofitClient.getApiService(getContext()).findId(idSearchInputEditText.getText().toString());
        SimpleRetrofit.RetrofitService(doFindId, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                String username = (String) response.body();

                // ?????? ?????? ??????
                Bundle bundle = new Bundle();
                bundle.putString("username", username);

                // ??????????????? ??????
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
