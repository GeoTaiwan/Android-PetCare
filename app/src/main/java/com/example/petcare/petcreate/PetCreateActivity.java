package com.example.petcare.petcreate;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PetCreateActivity extends AppCompatActivity {

    private TextInputEditText editName, editSpecies, editBreed, editMicrochip;
    private Button btnSave;
    // 宣告元件
    private ImageView ivSelectPhoto;
    private Uri selectedImageUri = null; // 儲存選到的照片 URI

    // 🌟 1. 設定相簿啟動器 (取代舊版的 startActivityForResult)
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ivSelectPhoto.setImageURI(uri); // 將選好的照片顯示在畫面上
                }
            });

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
        ivSelectPhoto = findViewById(R.id.ivSelectPhoto);

        // 🌟 2. 點擊圖片時開啟相簿，限定選擇 image 類型
        ivSelectPhoto.setOnClickListener(v -> getContent.launch("image/*"));
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

        // 🌟 3. 將文字轉換成 Multipart 需要的 RequestBody
        RequestBody rbName = RequestBody.create(name, MediaType.parse("text/plain"));
        RequestBody rbSpecies = RequestBody.create(species, MediaType.parse("text/plain"));

        // 處理可為 null 的欄位
        RequestBody rbBreed = breed.isEmpty() ? null : RequestBody.create(breed, MediaType.parse("text/plain"));
        RequestBody rbMicrochip = microchip.isEmpty() ? null : RequestBody.create(microchip, MediaType.parse("text/plain"));

        // 🌟 4. 處理圖片檔案轉換
        MultipartBody.Part photoPart = null;
        if (selectedImageUri != null) {
            File file = getFileFromUri(selectedImageUri); // 呼叫下方的輔助方法
            if (file != null) {
                RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
                // "UploadPhoto" 必須跟 ASP.NET 裡面 ViewModel 的屬性名稱完全一致
                photoPart = MultipartBody.Part.createFormData("UploadPhoto", file.getName(), requestFile);
            }
        }


        // 5. 發送 POST 請求
        // 因為是要寫入資料，可以考慮在按鈕上顯示 loading 或暫時停用按鈕防連點
        btnSave.setEnabled(false);
        btnSave.setText("儲存中...");

        RetrofitClient.getApi(this).createPetWithPhoto(rbName, rbSpecies, rbBreed, rbMicrochip, photoPart).enqueue(new Callback<Object>() {
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

    // 🌟 5. 將 Uri 複製到暫存檔的輔助方法 (Android 10 以上的標準做法)
    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "upload_temp.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}