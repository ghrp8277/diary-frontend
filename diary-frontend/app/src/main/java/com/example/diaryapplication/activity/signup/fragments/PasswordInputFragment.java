package com.example.diaryapplication.activity.signup.fragments;

import android.content.Intent;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

public class PasswordInputFragment extends Fragment {
    EditText passwordInputEditText;
    EditText passwordReEditText;
    TextView passwordAvailableEmail;
    SignupActivity signUpActivity;
    Button signUpBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_password_input, container, false);
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
        passwordInputEditText = (EditText) view.findViewById(R.id.passwordInputEditText);
        passwordReEditText = (EditText) view.findViewById(R.id.passwordReEditText);
        passwordAvailableEmail = (TextView) view.findViewById(R.id.passwordAvailableEmail);
        signUpBtn = (Button) view.findViewById(R.id.signUpBtn);

        signUpActivity = (SignupActivity) getActivity();

        setViewControlInit();
    }

    public void setViewControlInit() {
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            // ?????????????????? ????????? ??????????????? ????????? ??????
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            // ????????? ????????????
            @Override
            public void afterTextChanged(Editable editable) {
                String password = passwordInputEditText.getText().toString();

                if (password.length() < 10) {
                    passwordAvailableEmail.setText("?????? 10??? ?????? ??????????????????.");
                } else {
                    if (!textValidate(password)) {
                        passwordAvailableEmail.setText("????????? ??? ?????? ?????????????????????.");
                    } else {
                        passwordAvailableEmail.setText("");
                    }
                }
            }
        };

        TextWatcher passwordReWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = passwordInputEditText.getText().toString();
                String rePassword = passwordReEditText.getText().toString();

                if (rePassword.length() < 10) {
                    passwordAvailableEmail.setText("?????? 10??? ?????? ??????????????????.");
                } else {
                    if (!textValidate(rePassword)) {
                        passwordAvailableEmail.setText("????????? ??? ?????? ?????????????????????.");
                    } else {
                        if (password.equals(rePassword)) {
                            passwordAvailableEmail.setText("");
                            signUpBtn.setEnabled(true);
                        } else {
                            passwordAvailableEmail.setText("??????????????? ???????????? ????????????.");
                        }
                    }
                }
            }
        };

        passwordInputEditText.addTextChangedListener(passwordWatcher);
        passwordReEditText.addTextChangedListener(passwordReWatcher);

        // ????????????
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    // ?????? + ?????? + ???????????? ?????? 10 ~ 12 ??? ?????? ??????
    public boolean textValidate(String str) {
        String Passwrod_PATTERN = "^(?=.*[a-zA-Z]+)(?=.*[0-9]+).{10,}$";
        Pattern pattern = Pattern.compile(Passwrod_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    // ????????????
    public void signUp() {
        String e_mail = getArguments().getString("email");
        String username = getArguments().getString("username");
        String password = passwordInputEditText.getText().toString();

        Call<String> doSignUp = RetrofitClient.getApiService(getContext()).doSignUp(e_mail, username, password);

        SimpleRetrofit.RetrofitService(doSignUp, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();

                if (isCheck) {
                    Intent intent = new Intent();
                    intent.putExtra("username", username);

                    getActivity().setResult(100, intent);
                    getActivity().finish();

                    Snackbar.make(getView(), "??????????????? ??????????????? ?????????????????????.", Snackbar.LENGTH_SHORT).show();
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
