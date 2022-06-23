package com.example.diaryapplication.activity.signup.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

public class EmailConfirmFragment extends Fragment {
    TextView emailConfirmAvailableTextView;
    EditText emailConfirmInputEditText;
    Button emailConfirmBtn;
    Button emailConfirmNextBtn;
    SignupActivity signUpActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email_comfirm, container, false);
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
        emailConfirmAvailableTextView = (TextView) view.findViewById(R.id.emailConfirmAvailableTextView);
        emailConfirmInputEditText = (EditText) view.findViewById(R.id.emailConfirmInputEditText);
        emailConfirmBtn = (Button) view.findViewById(R.id.emailConfirmBtn);
        emailConfirmNextBtn = (Button) view.findViewById(R.id.emailConfirmNextBtn);
        signUpActivity = (SignupActivity) getActivity();

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
                String e_mail = emailConfirmInputEditText.getText().toString();

                if (checkEmailValidate(e_mail)) {
                    emailConfirmBtn.setEnabled(true);
                }
            }
        };

        emailConfirmInputEditText.addTextChangedListener(textWatcher);
        // 이메일 중복 확인
        emailConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailConfirmInputEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "이메일을 입력해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    emailCheck(view);
                }
            }
        });

        // 다음 버튼
        emailConfirmNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("email", emailConfirmInputEditText.getText().toString());

                signUpActivity.onFragmentChange(1, 35, bundle);
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

    // 이메일 체크
    public void emailCheck(View v) {
        String e_mail = emailConfirmInputEditText.getText().toString();

        Call<Boolean> getEmailCheck = RetrofitClient.getApiService(getContext()).getEmailCheck(e_mail);
        SimpleRetrofit.RetrofitService(getEmailCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();
                // 가입한 이메일이 있을 경우
                if (isCheck) {
                    emailConfirmAvailableTextView.setTextColor(Color.parseColor("#FFFF0000"));
                    emailConfirmAvailableTextView.setText("이미 가입된 이메일입니다!");
                    emailConfirmNextBtn.setEnabled(false);
                }
                // 가입한 이메일이 없을 경우
                else {
                    emailConfirmAvailableTextView.setTextColor(Color.parseColor("#000000"));
                    emailConfirmAvailableTextView.setText("사용 가능한 이메일입니다!");
                    emailConfirmNextBtn.setEnabled(true);
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