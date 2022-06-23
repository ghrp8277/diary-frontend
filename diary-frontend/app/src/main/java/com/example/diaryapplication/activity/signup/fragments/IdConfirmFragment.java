package com.example.diaryapplication.activity.signup.fragments;

import android.graphics.Color;
import android.os.Bundle;
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

public class IdConfirmFragment extends Fragment {
    TextView idConfirmAvailableTextView;
    EditText idConfirmInputEditText;
    Button idConfirmBtn;
    Button idConfirmNextBtn;
    SignupActivity signUpActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_id_comfirm, container, false);
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
        idConfirmAvailableTextView = (TextView) view.findViewById(R.id.idConfirmAvailableTextView);
        idConfirmInputEditText = (EditText) view.findViewById(R.id.idConfirmInputEditText);
        idConfirmBtn = (Button) view.findViewById(R.id.idConfirmBtn);
        idConfirmNextBtn = (Button) view.findViewById(R.id.idConfirmNextBtn);
        signUpActivity = (SignupActivity) getActivity();

        setViewControlInit();
    }

    public void setViewControlInit() {
        // 계정 중복 확인
        idConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idConfirm = idConfirmInputEditText.getText().toString();
                if (idConfirm.isEmpty()) {
                    Toast.makeText(getActivity(), "아이디를 입력해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                } else if (idConfirm.length() < 6) {
                    Toast.makeText(getActivity(), "6글자 이상 입력해주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                } else {
                    idCheck(view);
                }
            }
        });

        // 다음 버튼
        idConfirmNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 번들 객체 생성
                Bundle bundle = new Bundle();
                bundle.putString("email", getArguments().getString("email"));
                bundle.putString("username", idConfirmInputEditText.getText().toString());
                // 프래그먼트 이동
                signUpActivity.onFragmentChange(3, 30, bundle);
            }
        });
    }

    // 계정 중복 확인
    public void idCheck(View v) {
        String username = idConfirmInputEditText.getText().toString();

        Call<Boolean> getIdCheck = RetrofitClient.getApiService(getContext()).getIdCheck(username);
        SimpleRetrofit.RetrofitService(getIdCheck, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                Boolean isCheck = (Boolean) response.body();
                // 가입한 계정이 있을 경우
                if (isCheck) {
                    idConfirmAvailableTextView.setTextColor(Color.parseColor("#FFFF0000"));
                    idConfirmAvailableTextView.setText("이미 가입된 아이디입니다!");
                    idConfirmNextBtn.setEnabled(false);
                }
                // 가입한 계정이 없을 경우
                else {
                    idConfirmAvailableTextView.setTextColor(Color.parseColor("#000000"));
                    idConfirmAvailableTextView.setText("사용 가능한 아이디입니다!");
                    idConfirmNextBtn.setEnabled(true);
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
