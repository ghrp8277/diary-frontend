package com.example.diaryapplication.service.kakao;

import android.content.Context;
import android.util.Log;

import com.example.diaryapplication.service.Retrofit.RetrofitClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.time.LocalDateTime;
import java.util.Date;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import retrofit2.Call;

public class KakaoLogin {

    public interface IKLoginResult{
        void onKakaoLoginResult(User user, String accessToken, String refreshToken, Date expiresAt);
    }

    private static KakaoLogin kakaoLogin = new KakaoLogin();

    IKLoginResult listener;

    public void setListener(IKLoginResult listener){
        this.listener = listener;
    }

    public static KakaoLogin getInstance() {
        return kakaoLogin;
    }

    private KakaoLogin() {
        if (kakaoLogin != null)
            throw new AssertionError();
    }

    /**
     * @brief : 로그인 결과 수행에 관한 콜백메서드
     * @see : token이 전달되면 로그인 성공, token 전달 안되면 로그인 실패
     */
    private Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
            if (oAuthToken != null) {
                updateKakaoLogin(oAuthToken.getAccessToken(), oAuthToken.getRefreshToken(), oAuthToken.getRefreshTokenExpiresAt());
            }
            if (throwable != null) {
                // TBD
                Log.w("test", "invoke: " + throwable.getLocalizedMessage());
            }
            return null;
        }
    };

    // @brief : 로그인 여부를 확인 및 update UI
    private void updateKakaoLogin(String accessToken, String refreshToken, Date expiresAt) {
        UserApiClient.getInstance().me(new Function2<com.kakao.sdk.user.model.User, Throwable, Unit>() {
            @Override
            public Unit invoke(com.kakao.sdk.user.model.User user, Throwable throwable) {
                if (user != null) {
                    // @brief : 로그인한 유저의 email주소와 token 값 가져오기. pw는 제공 X

                    listener.onKakaoLoginResult(user, accessToken, refreshToken, expiresAt);
                } else {

                }
                return null;
            }
        });
    }

    public void login(Context context) {
        // @brief : 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(context)) {
            UserApiClient.getInstance().loginWithKakaoTalk(context, callback);
        } else {
            UserApiClient.getInstance().loginWithKakaoAccount(context, callback);
        }
    }

    // @brief : 로그아웃
    public void logout() {
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                if (throwable != null) {
                    // @brief : 로그아웃 실패
                    Log.e("[카카오] 로그아웃", "실패", throwable);
                } else {
                    // @brief : 로그아웃 성공
                    Log.i("[카카오] 로그아웃", "성공");
                }
                return null;
            }
        });

        // @brief : 카카오 연결 끊기
        UserApiClient.getInstance().unlink((throwable) -> {
            if (throwable != null) {
                // @brief : 연결 끊기 실패
                Log.e("[카카오] 로그아웃", "연결 끊기 실패", throwable);
            } else {
                // @brief : 연결 끊기 성공
                Log.i("kakaoLogout", "연결 끊기 성공. SDK에서 토큰 삭제");
            }
            return null;
        });
    }
}
