package com.example.petcare;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

        // 組合字串：例如 "貓 - 波斯貓"
        String info = pet.getSpecies() + (pet.getBreed() != null ? " - " + pet.getBreed() : "");
        holder.tvPetInfo.setText(info);

        // TODO: 之後可以使用 Glide 套件載入 pet.getProfilePhotoUrl() 的圖片
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