package com.example.petcare.medicalrecord;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.BuildConfig;
import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;
import com.example.petcare.medicalrecord.MedicalRecordAdapter;
import com.example.petcare.medicalrecord.MedicalRecordDto;
import com.example.petcare.medicalrecord.MedicalRecordListResponse;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalRecordListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicalRecordAdapter adapter;
    private LinearLayout layoutEmpty;
    private int currentPetId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record_list);

        // 1. 取得從前一頁傳來的 Pet ID
        currentPetId = getIntent().getIntExtra("PET_ID", -1);

        if (getIntent().getBooleanExtra("fromMain",false)){
            currentPetId = 0;
        }else {
            if (currentPetId == -1) {
                Toast.makeText(this, "無法取得寵物資料", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }


        // 2. 綁定 UI
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView = findViewById(R.id.recyclerViewMedical);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MedicalRecordAdapter();
        // 🌟 實作點擊事件
        adapter.setOnItemClickListener(new MedicalRecordAdapter.OnItemClickListener() {
            @Override
            public void onAttachmentClick(String fileName) {
                // 組合出完整的檔案下載網址 (對應你後端 wwwroot/uploads/mrFiles 的路徑)
                String fileUrl = BuildConfig.BASE_URL + "uploads/mrFiles/" + fileName;

                // 使用 Intent 呼叫系統瀏覽器來處理這個網址
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                startActivity(browserIntent);
            }
        });
        recyclerView.setAdapter(adapter);

        // 3. 呼叫 API 載入資料
        fetchMedicalRecords();
    }

    private void fetchMedicalRecords() {
        RetrofitClient.getApi(this).getMedicalRecords(currentPetId).enqueue(new Callback<MedicalRecordListResponse>() {
            @Override
            public void onResponse(Call<MedicalRecordListResponse> call, Response<MedicalRecordListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MedicalRecordDto> records = response.body().getData();

                    if (records == null || records.isEmpty()) {
                        // 顯示空狀態
                        recyclerView.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        // 顯示資料
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        adapter.setRecords(records);
                    }
                } else {
                    Toast.makeText(MedicalRecordListActivity.this, "讀取失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MedicalRecordListResponse> call, Throwable t) {
                Toast.makeText(MedicalRecordListActivity.this, "網路連線異常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}