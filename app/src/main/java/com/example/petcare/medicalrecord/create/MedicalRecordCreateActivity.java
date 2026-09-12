package com.example.petcare.medicalrecord.create;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;
import com.example.petcare.utils.FileUtil;

import java.io.File;
import java.util.Calendar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicalRecordCreateActivity extends AppCompatActivity {

    private int currentPetId;
    private EditText editVisitDate, editSymptoms, editDiagnosis;
    private Button btnSelectFile, btnSaveRecord;
    private Uri selectedFileUri = null;

    // 🌟 處理選擇檔案的回呼
    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    btnSelectFile.setText("已選擇檔案");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_record_create);

        currentPetId = getIntent().getIntExtra("PET_ID", -1);

        editVisitDate = findViewById(R.id.editVisitDate);
        editSymptoms = findViewById(R.id.editSymptoms);
        editDiagnosis = findViewById(R.id.editDiagnosis);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSaveRecord = findViewById(R.id.btnSaveRecord);

        // 1. 日期選擇器
        editVisitDate.setOnClickListener(v -> showDatePicker());

        // 2. 檔案選擇器 (這裡設為選擇所有檔案，你也可以改為 "image/*" 或 "application/pdf")
        btnSelectFile.setOnClickListener(v -> filePickerLauncher.launch("*/*"));

        // 3. 送出表單
        btnSaveRecord.setOnClickListener(v -> submitForm());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editVisitDate.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void submitForm() {
        String date = editVisitDate.getText().toString();
        String symptoms = editSymptoms.getText().toString();
        String diagnosis = editDiagnosis.getText().toString();

        if (date.isEmpty()) {
            Toast.makeText(this, "請選擇就診日期", Toast.LENGTH_SHORT).show();
            return;
        }

        // 轉為 RequestBody
        RequestBody rbDate = RequestBody.create(MediaType.parse("text/plain"), date);
        RequestBody rbSymptoms = RequestBody.create(MediaType.parse("text/plain"), symptoms);
        RequestBody rbDiagnosis = RequestBody.create(MediaType.parse("text/plain"), diagnosis);

        MultipartBody.Part filePart = null;
        // 如果你有實作 FileUtil 來把 Uri 轉成 File (與寵物照片上傳相同做法)
         if (selectedFileUri != null) {
             // 呼叫我們剛寫好的工具類別
             File file = FileUtil.getFileFromUri(this, selectedFileUri);

             if (file != null) {
                 // 如果你不確定使用者會傳什麼 (圖片或 PDF)，可以先用通用的 "*/*"
                 // 或者透過 ContentResolver 抓取真實的 mime type
                 String mimeType = getContentResolver().getType(selectedFileUri);
                 if (mimeType == null) mimeType = "multipart/form-data";

                 RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);

                 // 這裡的第一個參數 "UploadFile" 必須跟 ASP.NET Core API 接收的變數名稱一模一樣
                 filePart = MultipartBody.Part.createFormData("UploadFile", file.getName(), requestFile);
             }
         }

        btnSaveRecord.setEnabled(false);

        RetrofitClient.getApi(this).createMedicalRecord(
                currentPetId, rbDate, rbSymptoms, rbDiagnosis, filePart
        ).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MedicalRecordCreateActivity.this, "病歷新增成功！", Toast.LENGTH_SHORT).show();
                    finish(); // 自動回到病歷列表
                } else {
                    Toast.makeText(MedicalRecordCreateActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
                    btnSaveRecord.setEnabled(true);
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(MedicalRecordCreateActivity.this, "連線異常", Toast.LENGTH_SHORT).show();
                btnSaveRecord.setEnabled(true);
            }
        });
    }
}