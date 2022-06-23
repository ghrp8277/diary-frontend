package com.example.diaryapplication.service.Retrofit.interfaces;

import com.example.diaryapplication.dto.BoardResult;
import com.example.diaryapplication.service.Retrofit.API;

import java.time.LocalDateTime;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BoardAPI {
    // 해당 월 게시글 전체 가져오기
    @GET(API.ALL_BOARD_READ)
    Call<ArrayList<BoardResult>> allBoardRead(
            @Path("username") String username,
            @Query("month") Integer month
    );

    // 작성 다이어리 업로드
    @FormUrlEncoded
    @POST(API.BOARD_CREATE)
    Call<String> doBoardCreate(
            @Path("username") String username,
            @Field("title") String title,
            @Field("content") String content,
            @Field("datetime") LocalDateTime datetime,
            @Field("image_files_path[]") ArrayList<String> imageFilesPath
    );

    // 특정 날짜 다이어리 조회
    @GET(API.BOARD_READ)
    Call<BoardResult> doBoardRead(
            @Path("username") String username,
            @Path("id") Integer id
    );

    // 특정 날짜 다이어리 삭제
    @DELETE(API.BOARD_DELETE)
    Call<Boolean> doDeleteDiary(
            @Path("username") String username,
            @Path("id") Integer id
    );

    // 특정 날짜 다이어리 수정
    @FormUrlEncoded
    @PUT(API.BOARD_UPDATE)
    Call<Boolean> doBoardUpdate(
            @Path("username") String username,
            @Path("id") Integer id,
            @Field("title") String title,
            @Field("content") String content,
            @Field("datetime") LocalDateTime datetime,
            @Field("image_files_path[]") ArrayList<String> imageFilesPath
    );
}
