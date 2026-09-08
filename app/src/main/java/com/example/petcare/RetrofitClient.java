package com.example.petcare;

import android.content.Context;
import android.content.SharedPreferences;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:5163/"; // 確認你的 Port
    private static Retrofit retrofit = null;

    // 加上 Context 參數，才能讀取 SharedPreferences
    public static ApiService getApi(Context context) {
        if (retrofit == null) {
            // 1. 網路日誌攔截器 (你剛裝好的)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 2. 🌟 新增：JWT Token 攔截器
            Interceptor authInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    // 從 SharedPreferences 拿出 Token
                    SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                    String token = prefs.getString("JWT_TOKEN", "");

                    Request originalRequest = chain.request();

                    // 如果沒有 Token (例如還沒登入)，就直接送出原請求
                    if (token.isEmpty()) {
                        return chain.proceed(originalRequest);
                    }

                    // 如果有 Token，就在 HTTP Header 加上 Authorization: Bearer <token>
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();

                    return chain.proceed(newRequest);
                }
            };

            // 把兩個攔截器都裝進 OkHttpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}