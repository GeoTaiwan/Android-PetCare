package com.example.petcare.auth;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.petcare.MainActivity;
import com.example.petcare.R;
import com.example.petcare.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editAccount, editPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 先檢查有沒有儲存的 Token
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("JWT_TOKEN", "");

        if (!token.isEmpty()) {
            // 2. 有 Token 的話，可以選擇直接跳轉到 MainActivity
            // (更嚴謹的做法是先打一個 API 驗證 Token 是否過期，若沒過期再跳)
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // 結束 onCreate，不要再載入登入畫面了
        }
        setContentView(R.layout.activity_login); // 確保你的 XML 畫面檔名正確

        editAccount = findViewById(R.id.editAccount);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String account = editAccount.getText().toString();
            String password = editPassword.getText().toString();
            performLogin(account, password);
        });
    }

    private void performLogin(String account, String password) {
        LoginRequest request = new LoginRequest(account, password);

        // 呼叫 API
        RetrofitClient.getApi(LoginActivity.this).login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    saveToken(token);

                    Toast.makeText(LoginActivity.this, "登入成功！", Toast.LENGTH_SHORT).show();

                    // 🌟 跳轉到首頁 (假設你的首頁叫做 MainActivity)
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    // 🌟 關鍵：清除 Activity 堆疊。這樣按返回鍵就不會回到 LoginActivity 了
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                    finish(); // 關閉當前的 LoginActivity
                } else {
                    // 登入失敗 (例如 400 Bad Request)
//                    Toast.makeText(LoginActivity.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
                    try {
                        // 讀取後端實際回傳的錯誤內容
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "無內容";
                        Log.e("API_DEBUG", "HTTP狀態碼: " + response.code() + ", 錯誤內容: " + errorBody);
//                        Toast.makeText(LoginActivity.this, "連線失敗，代碼: " + response.code(), Toast.LENGTH_LONG).show();
                        Toast.makeText(LoginActivity.this, "登入失敗: 帳號或密碼錯誤", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
//                        Toast.makeText(LoginActivity.this, "登入失敗: 連線異常", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // 網路錯誤或伺服器沒開
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(LoginActivity.this, "連線失敗：" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // 將 Token 存入 SharedPreferences 的方法
    private void saveToken(String token) {
        SharedPreferences sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("JWT_TOKEN", token);
        editor.apply(); // 非同步寫入，效能較好
    }
    //點擊空白處可隱藏鍵盤，應不需與Scroll共用
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                // 如果點擊的位置不在 EditText 範圍內
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    // 呼叫系統服務，隱藏鍵盤
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}