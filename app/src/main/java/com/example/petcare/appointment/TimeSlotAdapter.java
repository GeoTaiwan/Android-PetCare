package com.example.petcare.appointment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private List<String> timeSlots = new ArrayList<>();
    private int selectedPosition = -1; // 記錄目前選中的位置
    private OnTimeSelectedListener listener;

    public interface OnTimeSelectedListener {
        void onTimeSelected(String time);
    }

    public TimeSlotAdapter(OnTimeSelectedListener listener) {
        this.listener = listener;
    }

    public void setTimeSlots(List<String> slots) {
        this.timeSlots = slots;
        this.selectedPosition = -1; // 重新載入時清空選擇
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 這裡為了簡化，直接用內建的簡單 TextView 版面，
        // 你也可以自訂一個 item_time_slot.xml 讓它看起來更像圓角按鈕
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String time = timeSlots.get(position);
        holder.tvTime.setText(time);
        holder.tvTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // 🌟 處理選取與未選取的高亮效果
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.parseColor("#265B6A"));
            holder.tvTime.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));
            holder.tvTime.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;

            // 只更新有變動的兩個項目，效能更好
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            listener.onTimeSelected(time);
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(android.R.id.text1);
        }
    }
}