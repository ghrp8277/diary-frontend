package com.example.diaryapplication.dto;

import com.google.gson.annotations.SerializedName;

public class AuthCredentialsResult {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    public String getAccessToken () {
        return accessToken;
    }

    public void setAccessToken (String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken () {
        return refreshToken;
    }

    public void setRefreshToken (String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "ClassPojo [accessToken = "+accessToken+", refreshToken = "+refreshToken+"]";
    }
}