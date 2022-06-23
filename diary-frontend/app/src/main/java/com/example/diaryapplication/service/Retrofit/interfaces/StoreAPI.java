package com.example.diaryapplication.service.Retrofit.interfaces;

import com.example.diaryapplication.service.Retrofit.API;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface StoreAPI {
    // 기본 이모티콘 zip 파일 다운로드
    @Streaming
    @GET(API.BASIC_EMOJI_DOWNLOAD)
    Call<ResponseBody> downloadBasicEmoji();
}
