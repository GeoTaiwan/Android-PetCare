package com.example.petcare.appointment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<AppointmentDTO> appointments = new ArrayList<>();

    public void setAppointments(List<AppointmentDTO> newAppointments) {
        this.appointments = newAppointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentDTO appointment = appointments.get(position);

        // 1. 綁定時間
        holder.tvDate.setText(appointment.getApptDateTime());

        // 2. 組合並綁定寵物與機構資訊
        String info = String.format("寵物：%s | 機構：%s",
                appointment.getPetName() != null ? appointment.getPetName() : "未知",
                appointment.getInstitutionName() != null ? appointment.getInstitutionName() : "未知");
        holder.tvPetInst.setText(info);

        // 3. 處理狀態與顏色
        String status = appointment.getStatus();
        if (status == null) status = "未知狀態";

        holder.tvStatus.setText(status);

        // 🌟 根據狀態改變顏色 (請依照你實際業務邏輯的狀態字串微調)
        if (status.contains("已確認") || status.contains("預約成功")) {
            // 綠色代表成功/已確認
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else if (status.contains("待確認") || status.contains("申請")) {
            // 橘色代表等待中[cite: 14]
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
        } else if (status.contains("取消") || status.contains("拒絕")) {
            // 紅色代表失敗/取消
            holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
        } else {
            // 預設灰色
            holder.tvStatus.setTextColor(Color.parseColor("#888888"));
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus, tvPetInst;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPetInst = itemView.findViewById(R.id.tvPetInst);
        }
    }
}