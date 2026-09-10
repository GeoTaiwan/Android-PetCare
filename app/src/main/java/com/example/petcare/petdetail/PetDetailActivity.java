package com.example.petcare.petdetail;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetDetailActivity extends AppCompatActivity {

    private int currentPetId;

    // 宣告畫面元件
    private TextView tvName, tvSpeciesBreed, tvDetails, tvWeightInfo;
    private ImageView ivProfilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail); // 記得在 XML 拉好對應的 ID

        // 綁定 UI (假設你已經在 XML 寫好這些 ID 了)
        tvName = findViewById(R.id.tvName);
        tvSpeciesBreed = findViewById(R.id.tvSpeciesBreed);
        tvDetails = findViewById(R.id.tvDetails);
        tvWeightInfo = findViewById(R.id.tvWeightInfo);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);

        currentPetId = getIntent().getIntExtra("PET_ID", -1);

        if (currentPetId != -1) {
            // 呼叫 API 取資料
            fetchPetDetails(currentPetId);
        } else {
            Toast.makeText(this, "無法取得寵物資料", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchPetDetails(int petId) {
        RetrofitClient.getApi(this).getPetDetail(petId).enqueue(new Callback<PetDetailResponse>() {
            @Override
            public void onResponse(Call<PetDetailResponse> call, Response<PetDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PetDetailDto detail = response.body().getData();
                    updateUI(detail); // 成功取得後，把資料交給更新畫面的方法
                } else {
                    Toast.makeText(PetDetailActivity.this, "載入失敗或無權限", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PetDetailResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    // 負責把資料填入畫面的方法
    private void updateUI(PetDetailDto detail) {
        tvName.setText(detail.getName());

        String breedInfo = detail.getSpecies() + (detail.getBreed() != null ? " - " + detail.getBreed() : "");
        tvSpeciesBreed.setText(breedInfo);

        String moreInfo = "生日: " + (detail.getDateOfBirth() != null ? detail.getDateOfBirth() : "未提供") + "\n"
                + "晶片: " + (detail.getMicrochipNo() != null ? detail.getMicrochipNo() : "無") + "\n"
                + "備註: " + (detail.getSpecialNotes() != null ? detail.getSpecialNotes() : "無");
        tvDetails.setText(moreInfo);

        if (detail.getLatestWeight() != null) {
            tvWeightInfo.setText("最新體重: " + detail.getLatestWeight() + " kg (" + detail.getLatestWeightDate() + ")");
        } else {
            tvWeightInfo.setText("尚未記錄體重");
        }

        // 處理大頭貼 (使用跟列表一樣的 Glide 邏輯)
        if (detail.getProfilePhotoUrl() != null && !detail.getProfilePhotoUrl().isEmpty()) {
            String fullImageUrl = "http://10.0.2.2:5163" + detail.getProfilePhotoUrl();
            Glide.with(this)
                    .load(fullImageUrl)
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .into(ivProfilePhoto);
        }
    }
}