package com.example.petcare.petdetail;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.petcare.PetWeightCreateDto;
import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;
import com.example.petcare.petcreate.PetCreateActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetDetailActivity extends AppCompatActivity {
    private PetDetailDto currentDetail; // 🌟 宣告一個全域變數來存取這隻寵物的完整資料
    private int currentPetId;

    // 宣告畫面元件
    private TextView tvName, tvSpeciesBreed, tvDetails, tvWeightInfo;
    private ImageView ivProfilePhoto;
    private Button btnAddWeight,btnEdit,btnDelete;

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
        btnAddWeight = findViewById(R.id.btn_addWeight);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        btnAddWeight.setOnClickListener(v -> showAddWeightDialog());
        btnEdit.setOnClickListener(v -> {
            if (currentDetail == null) return; // 確保資料已經載入

            Intent intent = new Intent(PetDetailActivity.this, PetCreateActivity.class);

            // 標記這是編輯模式
            intent.putExtra("IS_EDIT_MODE", true);

            // 把現有資料帶過去
            intent.putExtra("PET_ID", currentPetId);
            intent.putExtra("PET_NAME", currentDetail.getName());
            intent.putExtra("PET_SPECIES", currentDetail.getSpecies());

            // 以下欄位因為可能為 null (後端傳來的)，所以做一下安全處理
            intent.putExtra("PET_BREED", currentDetail.getBreed() != null ? currentDetail.getBreed() : "");
            intent.putExtra("PET_GENDER", currentDetail.getGender() != null ? currentDetail.getGender() : "");

            // 生日格式可能是 "2024-05-15T00:00:00"，我們只需要前面日期部分
            String dob = "";
            if (currentDetail.getDateOfBirth() != null && currentDetail.getDateOfBirth().length() >= 10) {
                dob = currentDetail.getDateOfBirth().substring(0, 10);
            }
            intent.putExtra("PET_DOB", dob);

            intent.putExtra("PET_NOTES", currentDetail.getSpecialNotes() != null ? currentDetail.getSpecialNotes() : "");
            intent.putExtra("PET_MICROCHIP", currentDetail.getMicrochipNo() != null ? currentDetail.getMicrochipNo() : "");
            // 圖片 URL 也可以傳過去，如果你想在編輯畫面顯示舊照片的話
            intent.putExtra("PET_PHOTO_URL", currentDetail.getProfilePhotoUrl());

            startActivity(intent);
        });
        btnDelete.setOnClickListener(v -> {
            // 呼叫自訂的防呆對話框
            showDeleteConfirmDialog();
        });


        currentPetId = getIntent().getIntExtra("PET_ID", -1);

        if (currentPetId != -1) {
            // 呼叫 API 取資料
            fetchPetDetails(currentPetId);
        } else {
            Toast.makeText(this, "無法取得寵物資料", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPetDetails(currentPetId);
    }

    // 🌟 顯示輸入體重的 Dialog
    private void showAddWeightDialog() {
        // 1. 動態建立一個輸入框
        final EditText input = new EditText(this);
        // 設定鍵盤類型為包含小數點的數字鍵盤
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("請輸入體重 (kg)");

        // 給輸入框一點邊距，看起來比較舒服
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(48, 0, 48, 0);
        input.setLayoutParams(lp);

        // 建立一個容器來裝輸入框
        LinearLayout container = new LinearLayout(this);
        container.addView(input);

        // 2. 建立 AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("新增體重紀錄")
                .setView(container)
                .setPositiveButton("儲存", (dialog, which) -> {
                    String weightStr = input.getText().toString().trim();
                    if (!weightStr.isEmpty()) {
                        try {
                            double weight = Double.parseDouble(weightStr);
                            submitWeightToServer(weight);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "請輸入有效的數字", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "體重不能為空", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 🌟 呼叫 API 送出資料
    private void submitWeightToServer(double weight) {
        PetWeightCreateDto dto = new PetWeightCreateDto(weight);

        RetrofitClient.getApi(this).addPetWeight(currentPetId, dto).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PetDetailActivity.this, "體重新增成功", Toast.LENGTH_SHORT).show();

                    // 🌟 關鍵：新增成功後，重新呼叫 fetchPetDetails() 刷新整個畫面的資料
                    fetchPetDetails(currentPetId);
                } else {
                    Toast.makeText(PetDetailActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this, "連線錯誤", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPetDetails(int petId) {
        RetrofitClient.getApi(this).getPetDetail(petId).enqueue(new Callback<PetDetailResponse>() {
            @Override
            public void onResponse(Call<PetDetailResponse> call, Response<PetDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PetDetailDto detail = response.body().getData();
                    currentDetail = detail;
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
            //tvWeightInfo.setText("最新體重: " + detail.getLatestWeight() + " kg (" + detail.getLatestWeightDate() + ")");
            // 🌟 建議這樣寫：使用 String.format 強制顯示小數點後 1 位
            String weightText = String.format("最新體重: %.1f kg", detail.getLatestWeight());
            tvWeightInfo.setText(weightText);
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
    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("刪除寵物確認")
                .setMessage("確定要刪除這隻寵物嗎？\n\n警告：此動作無法復原，與牠相關的照片、體重與看診紀錄將會一併永久刪除。")
                .setPositiveButton("確定刪除", (dialog, which) -> {
                    // 使用者點擊確定，呼叫 API 執行刪除
                    performDeletePet();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    // 點擊取消，關閉對話框什麼都不做
                    dialog.dismiss();
                })
                .show();
    }
    private void performDeletePet() {
        // 可以選擇在這裡顯示一個 ProgressDialog 或鎖定畫面

        RetrofitClient.getApi(this).deletePet(currentPetId).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PetDetailActivity.this, "刪除成功", Toast.LENGTH_SHORT).show();

                    // 🌟 關鍵：刪除成功後，關閉詳細資料頁面，自動退回列表頁
                    finish();
                } else {
                    Toast.makeText(PetDetailActivity.this, "刪除失敗: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(PetDetailActivity.this, "網路連線異常，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });
    }
}