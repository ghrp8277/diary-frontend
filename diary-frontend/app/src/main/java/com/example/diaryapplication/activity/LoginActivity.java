package com.example.diaryapplication.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.profile.ProfileActivity;
import com.example.diaryapplication.activity.signup.SignupActivity;
import com.example.diaryapplication.dto.AuthCredentialsResult;
import com.example.diaryapplication.service.Retrofit.DoRetrofitService;
import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.example.diaryapplication.service.Retrofit.SimpleRetrofit;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import retrofit2.Call;
import retrofit2.Response;

// https://develop-writing.tistory.com/31
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText loginInputPwdEditText, loginInputIdEditText;
    private Button loginIntentMainBtn, loginIntentSignUpBtn, loginIdSearchBtn, loginPasswordChangeBtn;
    CheckBox loginAutoStatusCheckBox;

    ActivityResultLauncher<Intent> activityResultLauncher;

    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);
        init();
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

        loginIntentMainBtn.setOnClickListener(this);
        loginAutoStatusCheckBox.setOnClickListener(this);
        loginIntentSignUpBtn.setOnClickListener(this);
        loginIdSearchBtn.setOnClickListener(this);
        loginPasswordChangeBtn.setOnClickListener(this);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        if (!checkPermission()) {
            requestpermission();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.loginIntentMainBtn:
                // 로그인
                if (checkPermission()) {
                    signIn(view);
                } else {
                    requestpermission();
                }
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

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }
        else {
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void requestpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageManager()})));
                activityResultLauncher.launch(intent);
            }
            catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activityResultLauncher.launch(intent);
            }
        }
    }
}
