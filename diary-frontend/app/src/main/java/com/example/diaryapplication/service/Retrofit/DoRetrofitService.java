package com.example.diaryapplication.service.Retrofit;

import retrofit2.Call;
import retrofit2.Response;

public interface DoRetrofitService {
    public void doResponse(Call call, Response response);

    public void doFailure(Call call, Throwable t);

    public void doResponseFailedCodeMatch(int statusCode, String message);
}
