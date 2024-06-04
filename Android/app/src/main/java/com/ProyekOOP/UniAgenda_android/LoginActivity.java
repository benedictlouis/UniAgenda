package com.ProyekOOP.UniAgenda_android;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ProyekOOP.UniAgenda_android.model.Login;
import com.ProyekOOP.UniAgenda_android.model.LoginResponse;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ACCOUNT_ID = "account_id";
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

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(v -> handleLogin());
        registerNow.setOnClickListener(v -> { moveActivity(this, RegisterActivity.class); });
    }

    private void moveActivity(Context ctx, Class<?> cls){
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void handleLogin() {
        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();

        if (emailS.isEmpty() || passwordS.isEmpty()) {
            Toast.makeText(mContext, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Login login = new Login(emailS, passwordS);

        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<LoginResponse> loginCall = apiService.login(login);

        loginCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        String message = loginResponse.getMessage();
                        String accountId = loginResponse.getAccount_id();
                        Log.d(TAG, "Login successful: " + message);
                        Log.d(TAG, "Account ID received: " + accountId);
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(KEY_IS_LOGGED_IN, true);
                        editor.putString(KEY_EMAIL, emailS);
                        editor.putString(KEY_ACCOUNT_ID, accountId);
                        editor.apply();

                        // Log stored account ID
                        Log.d(TAG, "Stored account ID in SharedPreferences: " + accountId);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
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
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "onFailure: ", t);
                Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
