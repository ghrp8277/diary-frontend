package com.example.diaryapplication.service.Retrofit;

interface AuthAPI {
    String EMAIL_CHECK = "auth/check/email";
    String ID_CHECK = "auth/check/id";
    String SIGN_IN = "auth/signin";
    String SIGN_UP = "auth/signup";
    String EMAIL_AUTH = "auth/email";
    String EMAIL_AUTH_NUMBER_CHECK = "auth/email/{number}";
    String FIND_ID = "auth/find/id";
    String EMAIL_NEW_PASSWORD = "auth/email/password";
    String CHANGE_PASSWORD = "auth/change/password";
    // kakao login oauth
    String KAKAO_LOGIN = "auth/kakao";
}

interface BoardAPI {
    String ALL_BOARD_READ = "board/{username}/read";
    String BOARD_CREATE = "board/{username}/create";
    String BOARD_READ = "board/{username}/read/{id}";
    String BOARD_DELETE = "board/{username}/delete/{id}";
    String BOARD_UPDATE = "board/{username}/update/{id}";
}

interface StoreAPI {
    String BASIC_EMOJI_DOWNLOAD = "store/emoji/download/basic";
}

public class API implements AuthAPI, BoardAPI, StoreAPI {
    public static final String BASE_URL = "http://leejehyeon.synology.me:3000/v1/";
//public static final String BASE_URL = "http://10.0.2.2:3000/v1/";
}