package com.example.diaryapplication.activity.profile.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
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

public class PasswordResetFragment extends Fragment {
    EditText passwordResetIdEditText;
    EditText passwordResetEmailEditText;
    Button newPasswordEmailSendBtn;
    ProfileActivity profileActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_password_reset, container, false);
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
        passwordResetIdEditText = (EditText) view.findViewById(R.id.passwordResetIdEditText);
        passwordResetEmailEditText = (EditText) view.findViewById(R.id.passwordResetEmailEditText);
        newPasswordEmailSendBtn = (Button) view.findViewById(R.id.newPasswordEmailSendBtn);
        profileActivity = (ProfileActivity) getActivity();

        setViewControlInit();
    }

    private void setViewControlInit() {
        // 임시 비밀번호 받기
        newPasswordEmailSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = passwordResetIdEditText.getText().toString();
                String e_mail = passwordResetEmailEditText.getText().toString();

                getEmailNewPassword(username, e_mail);
            }
        });

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
                String e_mail = passwordResetEmailEditText.getText().toString();
                String username = passwordResetIdEditText.getText().toString();

                if (checkEmailValidate(e_mail) && !username.isEmpty()) {
                    newPasswordEmailSendBtn.setEnabled(true);
                }
            }
        };
        passwordResetEmailEditText.addTextChangedListener(textWatcher);
    }

    // 이메일 형식 검사
    public boolean checkEmailValidate(String str) {
        String Email_Pattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(Email_Pattern);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    // 이메일로 임시 비밀번호 받기
    public void getEmailNewPassword(String username, String e_mail) {
        Call<Boolean> doSendEmailNewPassword = RetrofitClient.getApiService(getContext()).sendEmailNewPassword(username, e_mail);
        SimpleRetrofit.RetrofitService(doSendEmailNewPassword, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                boolean isChange = (Boolean) response.body();

                if (isChange) {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);

                    profileActivity.onFragmentChange(new PasswordSelectFragment(), bundle);
                } else {
                    Toast.makeText(getActivity(), "입력하신 아이디가 맞지 않습니다.\n다시 입력바랍니다.", Toast.LENGTH_SHORT).show();
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
