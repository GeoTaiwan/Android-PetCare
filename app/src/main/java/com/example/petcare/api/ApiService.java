package com.example.petcare.api;

import com.example.petcare.PetWeightCreateDto;
import com.example.petcare.auth.LoginRequest;
import com.example.petcare.auth.LoginResponse;
import com.example.petcare.medicalrecord.MedicalRecordListResponse;
import com.example.petcare.petcreate.PetCreateDto;
import com.example.petcare.petcreate.PetOptionsResponse;
import com.example.petcare.petdetail.PetDetailResponse;
import com.example.petcare.petlist.PetListResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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

    @POST("api/Pets")
    Call<Object> createPet(@Body PetCreateDto newPet);

    // 這裡的回傳值用 Object 即可，因為新增成功我們通常只看 HTTP 狀態碼是不是 2xx
    // 🌟 新增 Multipart 上傳 API
    @Multipart
    @POST("api/Pets")
    Call<Object> createPetWithPhoto(
            @Part("Name") RequestBody name,
            @Part("Species") RequestBody species,
            @Part("Breed") RequestBody breed,
            @Part("MicrochipNo") RequestBody microchipNo,
            @Part MultipartBody.Part uploadPhoto, // 檔案本身
            @Part("Gender") RequestBody gender,       // 🌟 新增
            @Part("DateOfBirth") RequestBody dob,     // 🌟 新增
            @Part("SpecialNotes") RequestBody notes  // 🌟 新增
    );
    //取得Pet下拉式清單所需資料
    @GET("api/Pets/options")
    Call<PetOptionsResponse> getPetOptions();

    @POST("api/Pets/{id}/weights")
    Call<Object> addPetWeight(@Path("id") int petId, @Body PetWeightCreateDto weightDto);

    @Multipart
    @PUT("api/Pets/{id}")
    Call<Object> updatePetWithPhoto(
            @Path("id") int petId,
            @Part("Name") RequestBody name,
            @Part("Species") RequestBody species,
            @Part("Breed") RequestBody breed,
            @Part("Gender") RequestBody gender,
            @Part("DateOfBirth") RequestBody dob,
            @Part("SpecialNotes") RequestBody notes,
            @Part("MicrochipNo") RequestBody microchipNo,
            @Part MultipartBody.Part uploadPhoto // 如果沒有選新圖片，這裡可以傳 null
    );
    // 🌟 新增：刪除寵物
    @DELETE("api/Pets/{id}")
    Call<Object> deletePet(@Path("id") int petId);

    @GET("api/Pets/medical-records/{id}")
    Call<MedicalRecordListResponse> getMedicalRecords(@Path("id") int petId);

    @Multipart
    @POST("api/Pets/{id}/medical-records")
    Call<Object> createMedicalRecord(
            @Path("id") int petId,
            @Part("VisitDate") RequestBody visitDate,
            @Part("Symptoms") RequestBody symptoms,
            @Part("Diagnosis") RequestBody diagnosis,
            @Part MultipartBody.Part uploadFile // 可為 null
    );

    @Multipart
    @PUT("api/Pets/medical-records/{recordId}")
    Call<Object> updateMedicalRecord(
            @Path("recordId") int recordId,
            @Part("VisitDate") RequestBody visitDate,
            @Part("Symptoms") RequestBody symptoms,
            @Part("Diagnosis") RequestBody diagnosis,
            @Part MultipartBody.Part uploadFile // 可為 null
    );
}