package com.ProyekOOP.UniAgenda_android;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.ProyekOOP.UniAgenda_android.model.Login;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private Context mContext;
    public TextView registerNow = null;
    public Button loginButton = null;
    private EditText email, password;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContext = this;
        registerNow = findViewById(R.id.register_now);
        loginButton = findViewById(R.id.login_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        loginButton.setOnClickListener(v -> handleLogin());
        registerNow.setOnClickListener(v -> { moveActivity(this, RegisterActivity.class); });
    }

    private void moveActivity(Context ctx, Class<?> cls){
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void handleLogin(){
        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();

        if (emailS.isEmpty() || passwordS.isEmpty()) {
            Toast.makeText(mContext, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek request
        Login login = new Login(emailS, passwordS);

        // Panggil metode login dari ApiService
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<String> loginCall = apiService.login(login);

        // Lakukan permintaan login
        loginCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String message = response.body();
                    Log.d(TAG, "Login successful: " + message);
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Optional: to close the LoginActivity
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.d(TAG, "Login failed: " + errorBody);
                        Toast.makeText(mContext, "Login failed: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Login failed: " + response.errorBody());
                        Toast.makeText(mContext, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "onFailure: ", t);
                Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
