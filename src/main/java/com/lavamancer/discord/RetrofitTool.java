package com.lavamancer.discord;

import okhttp3.OkHttpClient;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RetrofitTool {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private RetrofitApi retrofitApi;


    @PostConstruct
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        .readTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .build())
                .baseUrl(BASE_URL)
                .build();
        retrofitApi = retrofit.create(RetrofitApi.class);
    }


    public User getUser(Long userId) {
        try {
            return retrofitApi.getUser(userId).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
