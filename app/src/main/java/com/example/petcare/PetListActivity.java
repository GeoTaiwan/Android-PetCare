package com.example.petcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list); // 對應你的 XML 檔名

        // 1. 初始化 RecyclerView 與 Adapter
        recyclerView = findViewById(R.id.recyclerViewPets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        petAdapter = new PetAdapter();
        recyclerView.setAdapter(petAdapter);

        // 🌟 4. 實作點擊事件，準備換頁並傳遞 PetID
        petAdapter.setOnItemClickListener(pet -> {
            // 建立 Intent 指定要跳轉的目標 Activity
            Intent intent = new Intent(PetListActivity.this, PetDetailActivity.class);

            // 使用 putExtra 夾帶資料，這裡的 "PET_ID" 是取資料用的鑰匙 (Key)
            intent.putExtra("PET_ID", pet.getPetId());

            // 啟動新的 Activity
            startActivity(intent);
        });

        // 2. 呼叫 API 抓取資料
        fetchMyPets();
    }

    private void fetchMyPets() {
        // 注意：這裡使用帶 Context 的 getApi，攔截器會自動補上 JWT Token
        RetrofitClient.getApi(this).getMyPets().enqueue(new Callback<PetListResponse>() {
            @Override
            public void onResponse(Call<PetListResponse> call, Response<PetListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 取出後端傳來的 Array
                    List<PetDto> myPets = response.body().getData();

                    // 把資料交給 Adapter，畫面就會自動生出清單了！
                    petAdapter.setPetList(myPets);
                } else {
                    Toast.makeText(PetListActivity.this, "讀取失敗: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PetListResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(PetListActivity.this, "連線錯誤", Toast.LENGTH_SHORT).show();
            }
        });
    }
}