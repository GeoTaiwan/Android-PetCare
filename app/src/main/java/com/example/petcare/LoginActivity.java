package com.example.petcare;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editAccount, editPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        RetrofitClient.getApi().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 登入成功！拿到 Token
                    String token = response.body().getToken();

                    // 儲存 Token 到手機的 SharedPreferences
                    saveToken(token);

                    Toast.makeText(LoginActivity.this, "登入成功！", Toast.LENGTH_SHORT).show();
                    // TODO: 跳轉到 App 首頁
                } else {
                    // 登入失敗 (例如 400 Bad Request)
//                    Toast.makeText(LoginActivity.this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
                    try {
                        // 讀取後端實際回傳的錯誤內容
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "無內容";
                        Log.e("API_DEBUG", "HTTP狀態碼: " + response.code() + ", 錯誤內容: " + errorBody);
                        Toast.makeText(LoginActivity.this, "連線失敗，代碼: " + response.code(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
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
}