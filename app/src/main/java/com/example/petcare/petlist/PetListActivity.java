package com.example.petcare.petlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;
import com.example.petcare.petcreate.PetCreateActivity;
import com.example.petcare.petdetail.PetDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    // 🌟 宣告新元件
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_list); // 對應你的 XML 檔名
        fabAddPet = findViewById(R.id.fabAddPet);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
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
        // 🌟 設定新增按鈕點擊事件
        fabAddPet.setOnClickListener(v -> {
            // 跳轉到新增寵物的頁面 (請確保你有建立 PetCreateActivity)
            Intent intent = new Intent(PetListActivity.this, PetCreateActivity.class);
            startActivity(intent);
        });

        // 2. 呼叫 API 抓取資料
//        fetchMyPets();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        fetchMyPets();
    }

    private void fetchMyPets() {
        // 注意：這裡使用帶 Context 的 getApi，攔截器會自動補上 JWT Token
        RetrofitClient.getApi(this).getMyPets().enqueue(new Callback<PetListResponse>() {
            @Override
            public void onResponse(Call<PetListResponse> call, Response<PetListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PetDto> myPets = response.body().getData();

                    // 🌟 檢查資料是否為空
                    if (myPets == null || myPets.isEmpty()) {
                        // 隱藏清單，顯示空狀態提示
                        recyclerView.setVisibility(View.GONE);
                        layoutEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        // 顯示清單，隱藏空狀態提示
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmptyState.setVisibility(View.GONE);

                        // 綁定資料到 Adapter
                        petAdapter.setPetList(myPets);
                    }
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