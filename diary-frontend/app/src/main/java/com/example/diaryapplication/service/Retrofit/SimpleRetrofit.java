package com.example.diaryapplication.service.Retrofit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.example.diaryapplication.activity.LoginActivity;
import com.example.diaryapplication.activity.profile.ProfileActivity;
import com.example.diaryapplication.dialog.ProgressDialog;
import com.example.diaryapplication.service.SharedPreferences.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SimpleRetrofit {
    public static void RetrofitService(Call doCall, DoRetrofitService doRetrofitService, Context context) {
         // 로딩창 생성
        ProgressDialog loadingProgressDialog = new ProgressDialog(context);

        init(loadingProgressDialog);

        doCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    doRetrofitService.doResponse(call, response);
                } else {
                    // 토큰 인증 실패시 로그아웃
                    if (response.code() == 403 && response.message().equals("Forbidden")) {
                        PreferenceManager.removeKey(context, "AUTO_LOGIN_CHECK");
                        PreferenceManager.removeKey(context, "ACCESS_TOKEN");
                        PreferenceManager.removeKey(context, "REFRESH_TOKEN");
                        PreferenceManager.removeKey(context, "USERNAME");
                        PreferenceManager.removeKey(context, "SIGN_IN_AS");

                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                    doRetrofitService.doResponseFailedCodeMatch(response.code(), response.message());
                }
                // 로딩창을 끄기
                loadingProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                doRetrofitService.doFailure(call, t);

                // 로딩창을 끄기
                loadingProgressDialog.cancel();
            }
        });
    }

    private static void init(ProgressDialog loadingProgressDialog) {
        // 로딩창을 투명하게
        loadingProgressDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );
        // 주변 터치 방지
        loadingProgressDialog.setCanceledOnTouchOutside(false);
        // 로딩창 보여주기
        loadingProgressDialog.show();
    }
}