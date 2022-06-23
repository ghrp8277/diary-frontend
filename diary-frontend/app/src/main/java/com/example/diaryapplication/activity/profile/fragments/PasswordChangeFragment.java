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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaryapplication.R;
import com.example.diaryapplication.animation.CubeAnimation;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;
import com.google.android.material.snackbar.Snackbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Response;

public class PasswordChangeFragment extends Fragment {
    EditText passwordChangeInputEditText;
    EditText newPasswordChangeInputEditText;
    EditText newPasswordChangeReInputEditText;
    TextView passwordChangeAvailableTextView;
    Button passwordChangeBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_password_change, container, false);
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
        passwordChangeInputEditText = (EditText) view.findViewById(R.id.passwordChangeInputEditText);
        newPasswordChangeInputEditText = (EditText) view.findViewById(R.id.newPasswordChangeInputEditText);
        newPasswordChangeReInputEditText = (EditText) view.findViewById(R.id.newPasswordChangeReInputEditText);
        passwordChangeAvailableTextView = (TextView) view.findViewById(R.id.passwordChangeAvailableTextView);
        passwordChangeBtn = (Button) view.findViewById(R.id.passwordChangeBtn);

        setViewControlInit();
    }

    private void setViewControlInit() {
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            // 텍스트박스의 내용이 바뀔대마다 글자수 출력
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            // 글자수 제한하기
            @Override
            public void afterTextChanged(Editable editable) {
                String password = newPasswordChangeInputEditText.getText().toString();

                if (password.length() < 10) {
                    passwordChangeAvailableTextView.setText("최소 10자 이상 입력바랍니다.");
                } else {
                    if (!textValidate(password)) {
                        passwordChangeAvailableTextView.setText("사용할 수 없는 비밀번호입니다.");
                    } else {
                        passwordChangeAvailableTextView.setText("");
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
                String password = newPasswordChangeInputEditText.getText().toString();
                String rePassword = newPasswordChangeReInputEditText.getText().toString();

                if (rePassword.length() < 10) {
                    passwordChangeAvailableTextView.setText("최소 10자 이상 입력바랍니다.");
                } else {
                    if (!textValidate(rePassword)) {
                        passwordChangeAvailableTextView.setText("사용할 수 없는 비밀번호입니다.");
                    } else {
                        if (password.equals(rePassword)) {
                            passwordChangeBtn.setEnabled(true);
                            passwordChangeAvailableTextView.setText("");
                        } else {
                            passwordChangeAvailableTextView.setText("비밀번호가 일치하지 않습니다.");
                        }
                    }
                }
            }
        };

        newPasswordChangeInputEditText.addTextChangedListener(passwordWatcher);
        newPasswordChangeReInputEditText.addTextChangedListener(passwordReWatcher);

        // 비밀번호 변경
        passwordChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = getArguments().getString("username");
                String password = passwordChangeInputEditText.getText().toString();
                String newPassword = newPasswordChangeInputEditText.getText().toString();

                doPasswordChange(username, password, newPassword);
            }
        });
    }

    // 영어 + 숫자 + 특수문자 혼합 10 ~ 12 자 까지 입력
    public boolean textValidate(String str) {
        String Passwrod_PATTERN = "^(?=.*[a-zA-Z]+)(?=.*[0-9]+).{10,}$";
        Pattern pattern = Pattern.compile(Passwrod_PATTERN);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    // 비밀번호 변경
    public void doPasswordChange(String username, String password, String newPassword) {
        Call<Boolean> doChangePassword = RetrofitClient.getApiService(getContext()).doChangePassword(username, password, newPassword);
        SimpleRetrofit.RetrofitService(doChangePassword, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                boolean isChange = (Boolean) response.body();
                if (isChange) {
                    getActivity().finish();
                    Snackbar.make(getView(), "비밀번호가 성공적으로 변경되었습니다.", Snackbar.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "입력하신 비밀번호가 틀렸습니다.\n다시 한번 확인 부탁드립니다.", Toast.LENGTH_SHORT).show();
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
