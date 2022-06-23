package com.example.diaryapplication.service.Retrofit.interfaces;

import com.example.diaryapplication.dto.AuthCredentialsResult;
import com.example.diaryapplication.service.Retrofit.API;

import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthAPI {
    // test
    @POST("auth/test")
    Call<String> test();

    // 이메일 중복확인
    @GET(API.EMAIL_CHECK)
    Call<Boolean> getEmailCheck(@Query("email") String e_mail);

    // 계정 중복확인
    @GET(API.ID_CHECK)
    Call<Boolean> getIdCheck(@Query("username") String username);

    // 아이디 찾기
    @GET(API.FIND_ID)
    Call<String> findId(@Query("email") String e_mail);

    // 임시 비밀번호 받기
    @PUT(API.EMAIL_NEW_PASSWORD)
    Call<Boolean> sendEmailNewPassword(
            @Query("username") String username,
            @Query("email") String e_mail
    );

    // 비밀번호 변경
    @FormUrlEncoded
    @PUT(API.CHANGE_PASSWORD)
    Call<Boolean> doChangePassword(
            @Field("username") String username,
            @Field("password") String password,
            @Field("new_password") String newPassword
    );

    // 로그인
    @FormUrlEncoded
    @POST(API.SIGN_IN)
    Call<AuthCredentialsResult> doSignIn(
            @Field("username") String username,
            @Field("password") String password
    );

    //회원가입
    @FormUrlEncoded
    @POST(API.SIGN_UP)
    Call<String> doSignUp(
            @Field("email") String e_mail,
            @Field("username") String username,
            @Field("password") String password
    );

    // 이메일 인증
    @POST(API.EMAIL_AUTH)
    Call<Boolean> emailAuthCheck(@Query("email") String e_mail);

    // 이메일 번호 인증
    @POST(API.EMAIL_AUTH_NUMBER_CHECK)
    Call<Boolean> emailAuthNumberCheck(
            @Path("number") String number,
            @Query("email") String e_mail
    );

    //회원가입
    @FormUrlEncoded
    @POST(API.KAKAO_LOGIN)
    Call<String> doKakaoLogin(
            @Field("token") String token,
            @Field("token_expiresAt") Date token_expiresAt,
            @Field("username") String username
    );
}
