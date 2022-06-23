package com.example.diaryapplication.service.kakao;

import android.app.Application;

import com.example.diaryapplication.R;
import com.kakao.sdk.common.KakaoSdk;

public class App extends Application {
    private static App instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Kakao Sdk 초기화
        KakaoSdk.init(this, getString(R.string.KAKAO_APP_KEY));
    }

    // 어플리케이션 종료시 singleton 어플리케이션 객체를 초기화 한다.
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }

    public static App getInstance() {
        if (instance == null){
            throw new IllegalStateException("this app illegal state");
        }
        return instance;
    }
}
