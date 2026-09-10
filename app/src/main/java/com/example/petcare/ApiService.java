package com.example.petcare;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;

public interface ApiService {
    // 對應你的 [HttpPost("login")]
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @GET("api/Auth/profile")
    Call<Object> getProfile(); // 測試用，先用 Object 接收任意 JSON
    // 新增這行：取得我的寵物清單
    @GET("api/Pets/my")
    Call<PetListResponse> getMyPets();

}