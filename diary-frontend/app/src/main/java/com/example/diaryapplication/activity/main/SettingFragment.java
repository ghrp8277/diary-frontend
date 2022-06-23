package com.example.diaryapplication.activity.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.diaryapplication.R;
import com.example.diaryapplication.activity.LoginActivity;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.function.Function;


public class SettingFragment extends PreferenceFragmentCompat {
    CheckBoxPreference autoLoginCheckBoxPreference;
    Preference logoutPrefernce, usernamePrefernce;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.fragment_setting, rootKey);

        init();
    }

    private void init() {
        usernamePrefernce = (Preference) findPreference("username");
        logoutPrefernce = (Preference) findPreference("logout");
        autoLoginCheckBoxPreference = (CheckBoxPreference) findPreference("auto_login");

        // 사용자 아이디 표시
        usernamePrefernce.setSummary(PreferenceManager.getString(getContext(), "USERNAME"));

        // 자동 로그인 상태
        autoLoginCheckBoxPreference.setChecked(PreferenceManager.getBoolean(getContext(), "AUTO_LOGIN_CHECK"));
        autoLoginCheckBoxPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                boolean isCheck = PreferenceManager.getBoolean(getContext(), "AUTO_LOGIN_CHECK");

                PreferenceManager.setBoolean(getContext(), "AUTO_LOGIN_CHECK", !isCheck);

                return true;
            }
        });

        // 로그아웃
        logoutPrefernce.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                showMakeDialog("로그아웃 하시겠습니까?");

                return true;
            }
        });
    }

    // 다이얼로그
    private void showMakeDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doLogout();
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

    // 로그 아웃
    private void doLogout() {
        PreferenceManager.removeKey(getContext(), "AUTO_LOGIN_CHECK");
        PreferenceManager.removeKey(getContext(), "ACCESS_TOKEN");
        PreferenceManager.removeKey(getContext(), "REFRESH_TOKEN");
        PreferenceManager.removeKey(getContext(), "USERNAME");
        PreferenceManager.removeKey(getContext(), "SIGN_IN_AS");

        // 로그인 액티비티로 이동
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.putExtra("datetime", (LocalDateTime) LocalDateTime.now());
        startActivity(loginIntent);
    }
}
