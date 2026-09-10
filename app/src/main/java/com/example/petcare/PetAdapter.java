package com.example.petcare;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<PetDto> petList = new ArrayList<>();

    // 提供給外部更新資料的方法
    public void setPetList(List<PetDto> newList) {
        this.petList = newList;
        notifyDataSetChanged(); // 通知畫面重新繪製
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 載入 item_pet.xml 佈局
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        PetDto pet = petList.get(position);

        holder.tvPetName.setText(pet.getName());
        String info = pet.getSpecies() + (pet.getBreed() != null ? " - " + pet.getBreed() : "");
        holder.tvPetInfo.setText(info);

        // 🌟 處理圖片載入
        String photoUrl = pet.getProfilePhotoUrl();

        if (photoUrl != null && !photoUrl.isEmpty()) {
            // 將相對路徑補上伺服器網址 (請換成你實際的 Port，例如 5163)
            String fullImageUrl = "http://10.0.2.2:5163" + photoUrl;

            Glide.with(holder.itemView.getContext())
                    .load(fullImageUrl)
                    // 如果載入中或載入失敗，顯示預設相機圖示
                    .placeholder(android.R.drawable.ic_menu_camera)
                    .error(android.R.drawable.ic_menu_camera)
                    // 可選：讓圖片變成圓角，看起來更有質感
                    .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(24)))
                    .into(holder.ivPetImage);
        } else {
            // 如果這隻寵物根本沒有大頭貼，直接顯示預設圖示
            holder.ivPetImage.setImageResource(android.R.drawable.ic_menu_camera);
        }
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    // 綁定 item_pet.xml 裡面的元件
    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView tvPetName, tvPetInfo;
        ImageView ivPetImage;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvPetInfo = itemView.findViewById(R.id.tvPetInfo);
            ivPetImage = itemView.findViewById(R.id.ivPetImage);
        }
    }
}