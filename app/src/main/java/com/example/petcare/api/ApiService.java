package com.example.petcare.api;

import com.example.petcare.auth.LoginRequest;
import com.example.petcare.auth.LoginResponse;
import com.example.petcare.petdetail.PetDetailResponse;
import com.example.petcare.petlist.PetListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    // 對應你的 [HttpPost("login")]
    @POST("api/Auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @GET("api/Auth/profile")
    Call<Object> getProfile(); // 測試用，先用 Object 接收任意 JSON
    // 新增這行：取得我的寵物清單
    @GET("api/Pets/my")
    Call<PetListResponse> getMyPets();
    // 🌟 新增：取得單一寵物詳細資料
    // {id} 會被下面 @Path("id") 的變數替換掉
    @GET("api/Pets/{id}")
    Call<PetDetailResponse> getPetDetail(@Path("id") int petId);

}