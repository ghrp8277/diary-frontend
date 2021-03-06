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

        // ????????? ????????? ??????????????? ????????? ????????? ?????????
        getHashKey();
    }

    // sub ?????????????????? ?????? ?????? ??????????????? ???????????? ????????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // ??????????????? ?????? ????????? ????????? edittext??? ????????????.
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
                // ?????????
                signIn(view);
                break;
            case R.id.loginAutoStatusCheckBox:
                // ?????? ?????????
                if (((CheckBox) view).isChecked()) {
                    // TODO : CheckBox is checked.
                    PreferenceManager.setBoolean(getApplicationContext(), "AUTO_LOGIN_CHECK", true);
                } else {
                    // TODO : CheckBox is unchecked.
                    PreferenceManager.setBoolean(getApplicationContext(), "AUTO_LOGIN_CHECK", false);
                }
                break;
            case R.id.loginIntentSignUpBtn:
                // ????????????
                onChangeActivity(
                        new Intent(LoginActivity.this, SignupActivity.class)
                );
                break;
            case R.id.loginIdSearchBtn:
                // ????????? ??????
                onChangeActivity(
                        new Intent(LoginActivity.this, ProfileActivity.class)
                                .putExtra("index", 0)
                );
                break;
            case R.id.loginPasswordChangeBtn:
                // ???????????? ????????? ??????
                onChangeActivity(
                        new Intent(LoginActivity.this, ProfileActivity.class)
                                .putExtra("index", 1)
                );
                break;
            case R.id.loginKakaoAuthBtn:
                // ????????? ?????????
                showMakeDialog("'????????????'?????? '????????????'???(???)\n????????? ?????????.");
                break;
        }
    }

    private void onChangeActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.activity_animation_slide_in_left, R.anim.activity_animation_slide_out_right);
    }

    // ?????????
    public void signIn(View v) {
        // ???????????? ??????
        String username = loginInputIdEditText.getText().toString();
        String password = loginInputPwdEditText.getText().toString();

        if(username.length() == 0
                && password.length() == 0) {
            Toast.makeText(this.getApplicationContext(), R.string.TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
        }else{
            doSignInAuth(username, password);
        }
    }

    // ????????? ?????? HTTP ??????
    private void doSignInAuth(String username, String password) {
        Call<AuthCredentialsResult> doSignIn = RetrofitClient.getApiService(getApplicationContext()).doSignIn(username, password);
        SimpleRetrofit.RetrofitService(doSignIn, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                try {
                    AuthCredentialsResult result = (AuthCredentialsResult) response.body();

                    String accessToken = (String) result.getAccessToken();
                    String refreshToken = (String) result.getRefreshToken();

                    // ????????? ??????, ???????????? ?????? ??????
                    PreferenceManager.setString(getApplicationContext(), "ACCESS_TOKEN", accessToken);
                    PreferenceManager.setString(getApplicationContext(), "REFRESH_TOKEN", refreshToken);

                    // ???????????? ?????? ?????? ??????
                    PreferenceManager.setString(getApplicationContext(), "USERNAME", username);

                    // ????????? ?????? ?????? ??????
                    PreferenceManager.setBoolean(getApplicationContext(), "SIGN_IN_AS", true);

                    // ???????????? ??????
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
                    // ????????? ??????
                    case 401:
                        Toast.makeText(LoginActivity.this.getApplicationContext(), "????????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                    case 500:
                        Toast.makeText(LoginActivity.this.getApplicationContext(), "?????? ????????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, this);
    }


    // ???????????? ?????????
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

    // ???????????????
    private void showMakeDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        kakaoLogin();
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
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

    // ????????? ????????? ?????? (OAuth)
    @Override
    public void onKakaoLoginResult(User user, String accessToken, String refreshToken, Date expiresAt) {
        // ?????? ?????????
        String id = "kakao.my/user_" + user.getId();

        // ????????? ??? ???????????? ?????? (??????)
        doKakaoLogin(id, accessToken, refreshToken, expiresAt);
    }

    // ????????? ????????? ??? ???????????? ?????? (??????)
    private void doKakaoLogin(String id, String accessToken, String refreshToken, Date expiresAt) {
        Call<String> doKakaoLogin = RetrofitClient.getApiService(getApplicationContext()).doKakaoLogin(refreshToken, expiresAt, id);
        SimpleRetrofit.RetrofitService(doKakaoLogin, new DoRetrofitService() {
            @Override
            public void doResponse(Call call, Response response) {
                try {
                    // ????????? ??????, ???????????? ?????? ??????
                    PreferenceManager.setString(getApplicationContext(), "ACCESS_TOKEN", accessToken);
                    PreferenceManager.setString(getApplicationContext(), "REFRESH_TOKEN", refreshToken);

                    // ???????????? ?????? ?????? ??????
                    PreferenceManager.setString(getApplicationContext(), "USERNAME", id);

                    // ????????? ?????? ?????? ??????
                    PreferenceManager.setBoolean(getApplicationContext(), "SIGN_IN_AS", true);

                    // OAuth ????????? ?????? ?????? ??????
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
