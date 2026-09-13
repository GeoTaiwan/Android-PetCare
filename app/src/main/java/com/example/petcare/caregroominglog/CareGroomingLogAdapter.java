package com.example.petcare.caregroominglog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;

import java.util.ArrayList;
import java.util.List;

public class CareGroomingLogAdapter extends RecyclerView.Adapter<CareGroomingLogAdapter.ViewHolder> {

    private List<CareGroomingLogDto> logs = new ArrayList<>();

    public void setLogs(List<CareGroomingLogDto> newLogs) {
        this.logs = newLogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_care_grooming, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CareGroomingLogDto log = logs.get(position);

        // 處理日期格式，只取 YYYY-MM-DD
        String formattedDate = log.getLogDate();
        if (formattedDate != null && formattedDate.length() >= 10) {
            formattedDate = formattedDate.substring(0, 10);
        }
//        holder.tvLogDate.setText(formattedDate);
        holder.tvLogDate.setText(log.getPetName() + " - " + formattedDate);
        // 處理照護人員
        holder.tvStaff.setText("照護員: " + (log.getStaffName() != null ? log.getStaffName() : "未知"));

        // 處理細節與備註的防呆
        holder.tvActivityDetails.setText(log.getActivityDetails() != null && !log.getActivityDetails().isEmpty()
                ? log.getActivityDetails() : "無特別紀錄");
        holder.tvDietNotes.setText(log.getDietNotes() != null && !log.getDietNotes().isEmpty()
                ? log.getDietNotes() : "無特別紀錄");
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLogDate, tvStaff, tvActivityDetails, tvDietNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogDate = itemView.findViewById(R.id.tvLogDate);
            tvStaff = itemView.findViewById(R.id.tvStaff);
            tvActivityDetails = itemView.findViewById(R.id.tvActivityDetails);
            tvDietNotes = itemView.findViewById(R.id.tvDietNotes);
        }
    }
}