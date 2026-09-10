package com.example.petcare.petcreate;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetCreateActivity extends AppCompatActivity {

    private TextInputEditText editName, editSpecies, editBreed, editMicrochip;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_create);

        // 綁定 UI 元件
        editName = findViewById(R.id.editName);
        editSpecies = findViewById(R.id.editSpecies);
        editBreed = findViewById(R.id.editBreed);
        editMicrochip = findViewById(R.id.editMicrochip);
        btnSave = findViewById(R.id.btnSave);

        // 設定按鈕點擊事件
        btnSave.setOnClickListener(v -> submitForm());
    }

    private void submitForm() {
        // 1. 取得輸入框的文字，並去除前後空白
        String name = editName.getText().toString().trim();
        String species = editSpecies.getText().toString().trim();
        String breed = editBreed.getText().toString().trim();
        String microchip = editMicrochip.getText().toString().trim();

        // 2. 表單驗證：檢查必填欄位
        if (TextUtils.isEmpty(name)) {
            editName.setError("寵物名字不能為空");
            editName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(species)) {
            editSpecies.setError("物種不能為空");
            editSpecies.requestFocus();
            return;
        }

        // 3. 把空白的選填欄位轉成 null，避免傳空字串給後端
        String finalBreed = breed.isEmpty() ? null : breed;
        String finalMicrochip = microchip.isEmpty() ? null : microchip;

        // 4. 包裝成要送出的資料物件
        PetCreateDto requestDto = new PetCreateDto(name, species, finalBreed, finalMicrochip);

        // 5. 發送 POST 請求
        // 因為是要寫入資料，可以考慮在按鈕上顯示 loading 或暫時停用按鈕防連點
        btnSave.setEnabled(false);
        btnSave.setText("儲存中...");

        RetrofitClient.getApi(this).createPet(requestDto).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PetCreateActivity.this, "新增成功！", Toast.LENGTH_SHORT).show();

                    // 🌟 關鍵：新增成功後，關閉當前畫面，回到前一頁 (寵物列表)
                    finish();
                } else {
                    Toast.makeText(PetCreateActivity.this, "新增失敗: " + response.code(), Toast.LENGTH_SHORT).show();
                    // 恢復按鈕狀態
                    btnSave.setEnabled(true);
                    btnSave.setText("儲存資料");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("API_ERROR", "連線失敗: " + t.getMessage());
                Toast.makeText(PetCreateActivity.this, "網路連線異常", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("儲存資料");
            }
        });
    }
}