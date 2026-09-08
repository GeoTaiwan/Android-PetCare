package com.example.petcare;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    // 對應你的 [HttpPost("login")]
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}