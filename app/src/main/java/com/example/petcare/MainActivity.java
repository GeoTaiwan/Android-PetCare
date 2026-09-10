package com.example.petcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petcare.auth.LoginActivity;
import com.example.petcare.petlist.PetListActivity;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    ImageView btn_logout;
    MaterialCardView cardMyPets;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        btn_logout = findViewById(R.id.btnLogout);
        cardMyPets = findViewById(R.id.cardMyPets);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogout();
            }
        });

        cardMyPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PetListActivity.class);
                startActivity(i);
            }
        });

    }

    // 假設這是在 MainActivity 裡面的某個登出按鈕事件
    private void performLogout() {
        // 1. 清除手機裡的 Token
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("JWT_TOKEN");
        // editor.clear(); // 如果你有存其他無關緊要的設定，用 clear 會全部清空，建議用 remove
        editor.apply();

        // 2. 跳轉回登入頁面
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        // 確保按返回鍵不會回到登出前的頁面
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}