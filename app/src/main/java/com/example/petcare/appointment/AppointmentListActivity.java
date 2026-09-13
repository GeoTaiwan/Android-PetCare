package com.example.petcare.appointment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private LinearLayout layoutEmpty;

    private FloatingActionButton fabAddReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 記得對應你的 XML 檔名
        setContentView(R.layout.activity_appointment_list);

        // 1. 綁定 UI
        layoutEmpty = findViewById(R.id.layoutEmpty);
        recyclerView = findViewById(R.id.recyclerViewAppointment);
        fabAddReservation = findViewById(R.id.fabAddReservation);
        fabAddReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AppointmentListActivity.this,ReservationActivity.class);
                startActivity(i);
            }
        });
        // 2. 設定 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentAdapter();
        recyclerView.setAdapter(adapter);

        // 3. 呼叫 API 取資料
        fetchMyAppointments();
    }

    private void fetchMyAppointments() {
        // 呼叫 API (不需要傳 petId，因為我們想看所有預約)
        RetrofitClient.getApi(this).getMyAppointments().enqueue(new Callback<AppointmentListResponse>() {
            @Override
            public void onResponse(Call<AppointmentListResponse> call, Response<AppointmentListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AppointmentDTO> appointments = response.body().getData();

                    if (appointments == null || appointments.isEmpty()) {
                        // 沒有預約紀錄
                        recyclerView.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        // 顯示預約紀錄
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        adapter.setAppointments(appointments);
                    }
                } else {
                    Toast.makeText(AppointmentListActivity.this, "讀取失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AppointmentListResponse> call, Throwable t) {
                Toast.makeText(AppointmentListActivity.this, "網路連線異常", Toast.LENGTH_SHORT).show();
            }
        });
    }
}