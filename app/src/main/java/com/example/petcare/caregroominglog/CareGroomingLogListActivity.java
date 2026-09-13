package com.example.petcare.caregroominglog;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CareGroomingLogListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CareGroomingLogAdapter adapter;
    private LinearLayout layoutEmpty;
    private int currentPetId;
    private boolean fromMain=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_care_grooming_log_list);

        // 1. 取得 Pet ID
        currentPetId = getIntent().getIntExtra("PET_ID", -1);
        fromMain = getIntent().getBooleanExtra("FROM_MAIN",false);
        if (fromMain){
            currentPetId = 0;
        }else {
            if (currentPetId == -1) {
                Toast.makeText(this, "無法取得寵物資料", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }


        // 2. 綁定元件與設定 RecyclerView
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView = findViewById(R.id.recyclerViewCares);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CareGroomingLogAdapter();
        recyclerView.setAdapter(adapter);

        // 3. 呼叫 API
        fetchGroomingCares();
    }

    private void fetchGroomingCares() {
        RetrofitClient.getApi(this).getGroomingCares(currentPetId).enqueue(new Callback<CareGroomingLogListResponse>() {
            @Override
            public void onResponse(Call<CareGroomingLogListResponse> call, Response<CareGroomingLogListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CareGroomingLogDto> logs = response.body().getData();

                    if (logs == null || logs.isEmpty()) {
                        // 沒資料，顯示空狀態
                        recyclerView.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        // 有資料，餵給 Adapter
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        adapter.setLogs(logs);
                    }
                } else {
                    Toast.makeText(CareGroomingLogListActivity.this, "讀取失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CareGroomingLogListResponse> call, Throwable t) {
                Toast.makeText(CareGroomingLogListActivity.this, "網路連線異常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}