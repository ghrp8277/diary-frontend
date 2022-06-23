package com.example.diaryapplication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.profile.ProfileActivity;
import com.example.diaryapplication.activity.signup.SignupActivity;
import com.example.diaryapplication.dto.AuthCredentialsResult;
import com.example.diaryapplication.service.kakao.KakaoLogin;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;
import com.kakao.sdk.user.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;

// https://develop-writing.tistory.com/31
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, KakaoLogin.IKLoginResult {
    private EditText loginInputPwdEditText, loginInputIdEditText;
    private Button loginIntentMainBtn, loginIntentSignUpBtn, loginIdSearchBtn, loginPasswordChangeBtn;
    private ImageButton loginKakaoAuthBtn;
    CheckBox loginAutoStatusCheckBox;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);
        init();

        // 카카오 개발자 홈페이지에 등록할 해시키 구하기
        getHashKey();
    }

    // sub 액티비티에서 다시 메인 액티비티로 돌아올때 실행한다
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 회원가입을 하고 가입된 계정을 edittext에 뿌려준다.
        if (resultCode == 100) {
            String username = data.getStringExtra("username");
            loginInputIdEditText.setText(username);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.activity_animation_slide_in_left, R.anim.activity_animation_slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        loginInputIdEditText = (EditText) findViewById(R.id.loginInputIdEditText);
        loginInputPwdEditText = (EditText) findViewById(R.id.loginInputPwdEditText);
        loginIntentMainBtn = (Button) findViewById(R.id.loginIntentMainBtn);
        loginIntentSignUpBtn = (Button) findViewById(R.id.loginIntentSignUpBtn);
        loginIdSearchBtn = (Button) findViewById(R.id.loginIdSearchBtn);
        loginPasswordChangeBtn = (Button) findViewById(R.id.loginPasswordChangeBtn);
        loginAutoStatusCheckBox = (CheckBox) findViewById(R.id.loginAutoStatusCheckBox);
        loginKakaoAuthBtn = (ImageButton) findViewById(R.id.loginKakaoAuthBtn);

        loginIntentMainBtn.setOnClickListener(this);
        loginAutoStatusCheckBox.setOnClickListener(this);
        loginIntentSignUpBtn.setOnClickListener(this);
        loginIdSearchBtn.setOnClickListener(this);
        loginPasswordChangeBtn.setOnClickListener(this);
        loginKakaoAuthBtn.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.loginIntentMainBtn:
                // 로그인
                signIn(view);
                break;
            case R.id.loginAutoStatusCheckBox:
                // 자동 로그인
                if (((CheckBox) view).isChecked()) {
                    // TODO : CheckBox is checked.
                    PreferenceManager.setBoolean(getApplicationContext(), "AUTO_LOGIN_CHECK", true);
                } else {
                    // TODO : CheckBox is unchecked.
                    PreferenceManager.setBoolean(getApplicationContext(), "AUTO_LOGIN_CHECK", false);
                }
                break;
            case R.id.loginIntentSignUpBtn:
                // 회원가입
                onChangeActivity(
                        new Intent(LoginActivity.this, SignupActivity.class)
                );
                break;
            case R.id.loginIdSearchBtn:
                // 아이디 찾기
                onChangeActivity(
                        new Intent(LoginActivity.this, ProfileActivity.class)
                                .putExtra("index", 0)
                );
                break;
            case R.id.loginPasswordChangeBtn:
                // 비밀번호 재설정 버튼
                onChangeActivity(
                        new Intent(LoginActivity.this, ProfileActivity.class)
                                .putExtra("index", 1)
                );
                break;
            case R.id.loginKakaoAuthBtn:
                // 카카오 로그인
                showMakeDialog("'다이어리'에서 '카카오톡'을(를)\n열려고 합니다.");
                break;
        }
    }

    private void onChangeActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_slide_in_left, R.anim.activity_animation_slide_out_right);
    }

    // 로그인
    public void signIn(View v) {
        // 액티비티 이동
        String username = loginInputIdEditText.getText().toString();
        String password = loginInputPwdEditText.getText().toString();

        if(username.length() == 0
                && password.length() == 0) {
            Toast.makeText(this.getApplicationContext(), R.string.TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
        }else{
            doSignInAuth(username, password);
        }
    }

    // 로그인 시도 HTTP 통신
    private void doSignInAuth(String username, String password) {
        Call<AuthCredentialsResult> doSignIn = RetrofitClient.getApiService(getApplicationContext()).doSignIn(username, password);
        SimpleRetrofit.RetrofitService(doSignIn, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                try {
                    AuthCredentialsResult result = (AuthCredentialsResult) response.body();

                    String accessToken = (String) result.getAccessToken();
                    String refreshToken = (String) result.getRefreshToken();

                    // 엑세스 토큰, 리프레시 토큰 저장
                    PreferenceManager.setString(getApplicationContext(), "ACCESS_TOKEN", accessToken);
                    PreferenceManager.setString(getApplicationContext(), "REFRESH_TOKEN", refreshToken);

                    // 로그인한 유저 정보 저장
                    PreferenceManager.setString(getApplicationContext(), "USERNAME", username);

                    // 로그인 성공 여부 저장
                    PreferenceManager.setBoolean(getApplicationContext(), "SIGN_IN_AS", true);

                    // 액티비티 이동
                    onChangeActivity(
                            new Intent(LoginActivity.this, MainActivity.class)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doResponseFailedCodeMatch(int statusCode, String message) {
                switch (statusCode) {
                    // 로그인 실패
                    case 401:
                        Toast.makeText(LoginActivity.this.getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(LoginActivity.this.getApplicationContext(), "서버 문제로 인한 로그인 실패입니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, this);
    }


    // 해시값을 가져옴
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("test", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("test", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("test", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    // 다이얼로그
    private void showMakeDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("열기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        kakaoLogin();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
        AlertDialog msgDlg = builder.create();
        msgDlg.show();
    }

    private void kakaoLogin() {
        KakaoLogin.getInstance().setListener(this);
        KakaoLogin.getInstance().login(this);
    }

    // 카카오 로그인 성공 (OAuth)
    @Override
    public void onKakaoLoginResult(User user, String accessToken, String refreshToken, Date expiresAt) {
        // 콜백 메서드
        String id = "kakao.my/user_" + user.getId();

        // 로그인 및 회원가입 시도 (서버)
        doKakaoLogin(id, accessToken, refreshToken, expiresAt);
    }

    // 카카오 로그인 및 회원가입 시도 (서버)
    private void doKakaoLogin(String id, String accessToken, String refreshToken, Date expiresAt) {
        Call<String> doKakaoLogin = RetrofitClient.getApiService(getApplicationContext()).doKakaoLogin(refreshToken, expiresAt, id);
        SimpleRetrofit.RetrofitService(doKakaoLogin, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                try {
                    // 엑세스 토큰, 리프레시 토큰 저장
                    PreferenceManager.setString(getApplicationContext(), "ACCESS_TOKEN", accessToken);
                    PreferenceManager.setString(getApplicationContext(), "REFRESH_TOKEN", refreshToken);

                    // 로그인한 유저 정보 저장
                    PreferenceManager.setString(getApplicationContext(), "USERNAME", id);

                    // 로그인 성공 여부 저장
                    PreferenceManager.setBoolean(getApplicationContext(), "SIGN_IN_AS", true);

                    // OAuth 로그인 성공 여부 저장
                    PreferenceManager.setBoolean(getApplicationContext(), "IS_PUBLIC", true);

                    onChangeActivity(
                            new Intent(LoginActivity.this, MainActivity.class)
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doFailure(Call call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doResponseFailedCodeMatch(int statusCode, String message) {

            }
        }, this);
    }
}
