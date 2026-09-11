package com.example.petcare.petcreate;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PetCreateActivity extends AppCompatActivity {
    // 宣告全域變數
    private boolean isEditMode = false;
    private int editPetId = -1;
    // 宣告改用 AutoCompleteTextView
    private AutoCompleteTextView editSpecies, editBreed, editGender;
    private TextInputEditText editDob, editNotes; // 新增這兩個
    private TextInputEditText editName, editMicrochip;
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

    // 🌟 將變數宣告為空集合，等待 API 填入
    private List<String> speciesOptions = new ArrayList<>();
    private List<String> genderOptions = new ArrayList<>();
    private Map<String, List<String>> breedMap = new HashMap<>();

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
        // 綁定所有 UI (記得把前面宣告的都 findViewById)
        editGender = findViewById(R.id.editGender);
        editDob = findViewById(R.id.editDob);
        editNotes = findViewById(R.id.editNotes);

        // 🌟 進入畫面時，先將輸入框鎖定，避免資料還沒載入完使用者就點擊
        editSpecies.setEnabled(false);
        editGender.setEnabled(false);
        editBreed.setEnabled(false);

        // 🌟 1. 檢查是否為編輯模式
        isEditMode = getIntent().getBooleanExtra("IS_EDIT_MODE", false);

        if (isEditMode) {
            editPetId = getIntent().getIntExtra("PET_ID", -1);

            // 更改畫面的標題與按鈕文字
            TextView tvTitle = findViewById(R.id.tvTitle);
            tvTitle.setText("修改寵物資料");
            btnSave.setText("更新資料");

            // 預先填入舊資料 (注意 AutoCompleteTextView 使用 setText 的第二個參數 false 避免觸發下拉清單)
            editName.setText(getIntent().getStringExtra("PET_NAME"));
            editSpecies.setText(getIntent().getStringExtra("PET_SPECIES"), false);
            editBreed.setText(getIntent().getStringExtra("PET_BREED"), false);
            editGender.setText(getIntent().getStringExtra("PET_GENDER"), false);
            editDob.setText(getIntent().getStringExtra("PET_DOB"));
            editNotes.setText(getIntent().getStringExtra("PET_NOTES"));
            editMicrochip.setText(getIntent().getStringExtra("PET_MICROCHIP"));
        }

        // 呼叫 API 取得動態選單資料
        fetchOptionsFromApi();
//        setupDropdowns();
        setupDatePicker();
        // 🌟 2. 點擊圖片時開啟相簿，限定選擇 image 類型
        ivSelectPhoto.setOnClickListener(v -> getContent.launch("image/*"));
        // 設定按鈕點擊事件
        btnSave.setOnClickListener(v -> submitForm());
    }
    private void fetchOptionsFromApi() {
        RetrofitClient.getApi(this).getPetOptions().enqueue(new Callback<PetOptionsResponse>() {
            @Override
            public void onResponse(Call<PetOptionsResponse> call, Response<PetOptionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PetOptionsDto options = response.body().getData();

                    // 1. 把 API 取回的資料存入全域變數
                    speciesOptions = options.getSpecies();
                    genderOptions = options.getGenders();
                    breedMap = options.getBreeds();

                    // 2. 解除輸入框的鎖定
                    editSpecies.setEnabled(true);
                    editGender.setEnabled(true);
                    editBreed.setEnabled(true);

                    // 3. 初始化選單
                    setupDropdowns();
                } else {
                    Toast.makeText(PetCreateActivity.this, "無法載入選項資料", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PetOptionsResponse> call, Throwable t) {
                Toast.makeText(PetCreateActivity.this, "網路連線異常", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDropdowns() {
        // 設定物種與性別的 Adapter
        ArrayAdapter<String> speciesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, speciesOptions);
        editSpecies.setAdapter(speciesAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genderOptions);
        editGender.setAdapter(genderAdapter);

        // 連動邏輯：當選擇物種時，從 API 拿到的 breedMap 中找出對應的品種陣列
        editSpecies.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSpecies = speciesOptions.get(position);
            List<String> breeds = breedMap.get(selectedSpecies);

            if (breeds != null) {
                ArrayAdapter<String> breedAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, breeds);
                editBreed.setAdapter(breedAdapter);
                editBreed.setText(""); // 切換物種時清空舊品種
            }
        });
    }

    private void setupDatePicker() {
        editDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // 月份從 0 開始，所以要 +1。格式化為 YYYY-MM-DD
                        String dateString = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        editDob.setText(dateString);
                    }, year, month, day);

            // 不允許選擇未來的日期
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void submitForm() {
        // 1. 取得輸入框的文字，並去除前後空白
        String name = editName.getText().toString().trim();
        String species = editSpecies.getText().toString().trim();
        String breed = editBreed.getText().toString().trim();
        String gender = editGender.getText().toString().trim();
        String dob = editDob.getText().toString().trim();
        Log.d("API_TEST", "準備送出的生日字串是: " + dob);
        String microchip = editMicrochip.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

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

        // 選填欄位：如果使用者有填寫，才建立 RequestBody；否則設為 null，Retrofit 會自動忽略這個 Part
        RequestBody rbBreed = breed.isEmpty() ? null : RequestBody.create(breed, MediaType.parse("text/plain"));
        RequestBody rbGender = gender.isEmpty() ? null : RequestBody.create(gender, MediaType.parse("text/plain"));
        RequestBody rbDob = dob.isEmpty() ? null : RequestBody.create(dob, MediaType.parse("text/plain"));
        RequestBody rbMicrochip = microchip.isEmpty() ? null : RequestBody.create(microchip, MediaType.parse("text/plain"));
        RequestBody rbNotes = notes.isEmpty() ? null : RequestBody.create(notes, MediaType.parse("text/plain"));

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

        Log.d("API_TEST", "準備更新的 Pet ID: " + editPetId);

        // 5. 發送 POST 請求
        // 因為是要寫入資料，可以考慮在按鈕上顯示 loading 或暫時停用按鈕防連點
        btnSave.setEnabled(false);
        btnSave.setText("儲存中...");

        if (isEditMode) {
            // 🌟 編輯模式：呼叫 PUT API
            RetrofitClient.getApi(this).updatePetWithPhoto(
                    editPetId, rbName, rbSpecies, rbBreed, rbGender, rbDob, rbNotes, rbMicrochip, photoPart
            ).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(PetCreateActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                        finish(); // 關閉畫面，回到詳細頁
                    } else {
                        Toast.makeText(PetCreateActivity.this, "更新失敗: " + response.code(), Toast.LENGTH_SHORT).show();
                        btnSave.setEnabled(true);
                        btnSave.setText("更新資料");
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Toast.makeText(PetCreateActivity.this, "網路連線異常", Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                    btnSave.setText("更新資料");
                }
            });
        } else {
            // 🌟 新增模式：呼叫原本的 POST API
            RetrofitClient.getApi(this).createPetWithPhoto(rbName, rbSpecies, rbBreed, rbMicrochip, photoPart,rbGender,rbDob,rbNotes).enqueue(new Callback<Object>() {
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                // 如果點擊的位置不在 EditText 範圍內
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    // 呼叫系統服務，隱藏鍵盤
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}