package com.example.petcare.medicalrecord;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;

import java.util.ArrayList;
import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    private List<MedicalRecordDto> records = new ArrayList<>();

    // 🌟 1. 定義一個 Interface
    public interface OnItemClickListener {
        void onAttachmentClick(String fileName);

        void onEditClick(MedicalRecordDto record);
    }

    private OnItemClickListener listener;

    // 🌟 2. 提供一個方法讓 Activity 可以註冊這個監聽器
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setRecords(List<MedicalRecordDto> newRecords) {
        this.records = newRecords;
        notifyDataSetChanged(); // 刷新清單
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicalRecordDto record = records.get(position);

        // 處理日期格式 (將 "2024-05-20T14:30:00" 轉為 "2024-05-20 14:30")
        String formattedDate = record.getVisitDate();
        if (formattedDate != null && formattedDate.length() >= 16) {
            formattedDate = formattedDate.substring(0, 16).replace("T", " ");
        }
        holder.tvVisitDate.setText(formattedDate);

        holder.tvVetId.setText("獸醫: " + record.getVetName());

        // 處理可能為 null 的文字
        holder.tvSymptoms.setText(record.getSymptoms() != null ? record.getSymptoms() : "無紀錄");
        holder.tvDiagnosis.setText(record.getDiagnosis() != null ? record.getDiagnosis() : "無紀錄");

        // 🌟 3. 處理附件與點擊事件
        final String fileName = record.getTestResultsFile();

        if (fileName != null && !fileName.isEmpty()) {
            holder.ivAttachment.setVisibility(View.VISIBLE);

            // 讓整張卡片可以點擊
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAttachmentClick(fileName);
                }
            });
        } else {
            holder.ivAttachment.setVisibility(View.GONE);
            // 沒檔案時，點擊卡片就沒反應
            holder.itemView.setOnClickListener(null);
        }
        holder.ivEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVisitDate, tvVetId, tvSymptoms, tvDiagnosis;
        ImageView ivAttachment, ivEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVisitDate = itemView.findViewById(R.id.tvVisitDate);
            tvVetId = itemView.findViewById(R.id.tvVetId);
            tvSymptoms = itemView.findViewById(R.id.tvSymptoms);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            ivAttachment = itemView.findViewById(R.id.ivAttachment);
            ivEdit = itemView.findViewById(R.id.ivEdit);
        }
    }
}