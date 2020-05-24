package com.lavamancer.discord;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitApi {

    @GET("/users/{userId}")
    Call<User> getUser(@Path("userId") Long userId);

}
