package com.example.petcare;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:5163/"; // 請確認 Port 號
    private static Retrofit retrofit = null;

    public static ApiService getApi() {
        if (retrofit == null) {
            // 建立監視器，設定攔截級別為 BODY (印出完整的標頭與內容)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 把監視器裝到 OkHttpClient 上
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // 掛載設定好的 Client
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}