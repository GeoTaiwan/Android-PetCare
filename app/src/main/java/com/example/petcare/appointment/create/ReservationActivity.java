package com.example.petcare.appointment.create;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;
import com.example.petcare.petlist.PetDto;
import com.example.petcare.petlist.PetListResponse;

import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationActivity extends AppCompatActivity {

    private Spinner spinnerPets, spinnerInstitutions;
    private Button btnSelectDate, btnSubmitReservation;
    private LinearLayout layoutTimeSlots;
    private RecyclerView recyclerTimeSlots;

    private TimeSlotAdapter timeSlotAdapter;

    // 儲存使用者選擇的資料
    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // 1. 綁定 UI
        spinnerPets = findViewById(R.id.spinnerPets);
        spinnerInstitutions = findViewById(R.id.spinnerInstitutions);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSubmitReservation = findViewById(R.id.btnSubmitReservation);
        layoutTimeSlots = findViewById(R.id.layoutTimeSlots);
        recyclerTimeSlots = findViewById(R.id.recyclerTimeSlots);

        // 2. 設定時間網格 (一排顯示 3 個時段)
        recyclerTimeSlots.setLayoutManager(new GridLayoutManager(this, 3));
        timeSlotAdapter = new TimeSlotAdapter(time -> {
            selectedTime = time; // 記錄選中的時間
        });
        recyclerTimeSlots.setAdapter(timeSlotAdapter);

        // 3. 載入下拉選單資料
        fetchMyPets();
        fetchInstitutions();

        // 4. 設定日期選擇器
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // 5. 設定送出按鈕
        btnSubmitReservation.setOnClickListener(v -> submitReservation());
    }

    private void fetchMyPets() {
        // 呼叫原本寫好的 api/Pets/my (你需要準備對應的 Response 類別)
        RetrofitClient.getApi(this).getMyPets().enqueue(new Callback<PetListResponse>() {
            @Override
            public void onResponse(Call<PetListResponse> call, Response<PetListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PetDto> pets = response.body().getData();
                    // 🌟 利用 ArrayAdapter 綁定物件，它會自動呼叫 toString() 顯示名稱
                    ArrayAdapter<PetDto> adapter = new ArrayAdapter<>(
                            ReservationActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            pets
                    );
                    spinnerPets.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<PetListResponse> call, Throwable t) {}
        });
    }

    private void fetchInstitutions() {
        // 傳入 null 代表不特別過濾機構類型
        RetrofitClient.getApi(this).getInstitutionsForReservation(null).enqueue(new Callback<InstitutionListResponse>() {
            @Override
            public void onResponse(Call<InstitutionListResponse> call, Response<InstitutionListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<InstitutionDto> insts = response.body().getData();
                    if (insts == null || insts.isEmpty()) {
                        Toast.makeText(ReservationActivity.this, "後端回傳的機構清單是空的", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayAdapter<InstitutionDto> adapter = new ArrayAdapter<>(
                            ReservationActivity.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            insts
                    );

                    spinnerInstitutions.setAdapter(adapter);
                }else {
                    Toast.makeText(ReservationActivity.this, "API 錯誤碼：" + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<InstitutionListResponse> call, Throwable t) {
                Toast.makeText(ReservationActivity.this, "連線或解析失敗：" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        // 如果你有限制只能預約未來的時間，可以在這設定 minDate
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            btnSelectDate.setText(selectedDate);

            // 選好日期後，立刻去後端抓時段
            fetchTimeSlots(selectedDate);

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // 限制只能預約今天以後的日子
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void fetchTimeSlots(String date) {
        RetrofitClient.getApi(this).getAvailableTimeSlots(date).enqueue(new Callback<TimeSlotResponse>() {
            @Override
            public void onResponse(Call<TimeSlotResponse> call, Response<TimeSlotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> slots = response.body().getData();

                    layoutTimeSlots.setVisibility(View.VISIBLE);
                    selectedTime = ""; // 清空之前的選擇
                    timeSlotAdapter.setTimeSlots(slots);
                }
            }
            @Override
            public void onFailure(Call<TimeSlotResponse> call, Throwable t) {
                Toast.makeText(ReservationActivity.this, "無法取得時段", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitReservation() {
        // 1. 驗證資料是否都有填
        PetDto selectedPet = (PetDto) spinnerPets.getSelectedItem();
        InstitutionDto selectedInst = (InstitutionDto) spinnerInstitutions.getSelectedItem();

        if (selectedPet == null || selectedInst == null) {
            Toast.makeText(this, "請選擇寵物與機構", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "請選擇預約的日期與時段", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitReservation.setEnabled(false);

        // 2. 封裝請求
        AppointmentCreateRequest request = new AppointmentCreateRequest(
                selectedPet.getPetId(),
                selectedInst.getInstitutionID(),
                "一般預約", // 或是讓使用者自己填寫
                selectedDate,
                selectedTime
        );

        // 3. 呼叫 POST API
        RetrofitClient.getApi(this).createAppointment(request).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ReservationActivity.this, "預約成功！", Toast.LENGTH_SHORT).show();
                    finish(); // 關閉預約頁面，回到主畫面
                } else {
                    Toast.makeText(ReservationActivity.this, "預約失敗，該時段可能已被搶先", Toast.LENGTH_LONG).show();
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "未知錯誤";
                        Toast.makeText(ReservationActivity.this, "後端拒絕：" + errorBody, Toast.LENGTH_LONG).show();
                        Log.d("檢視送出項目", "onResponse: " +
                                "\nPetID:" +selectedPet.getPetId()+
                                "\nInstID:" + selectedInst.getInstitutionID()+
                                "\nselectedDate:" + selectedDate+
                                "\nselectedTime:" + selectedTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    btnSubmitReservation.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Toast.makeText(ReservationActivity.this, "連線異常", Toast.LENGTH_SHORT).show();
                btnSubmitReservation.setEnabled(true);
            }
        });
    }
}