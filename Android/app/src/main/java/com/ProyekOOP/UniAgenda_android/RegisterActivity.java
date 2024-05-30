package com.ProyekOOP.UniAgenda_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ProyekOOP.UniAgenda_android.model.Signup;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private BaseApiService mApiService;
    private Context mContext;
    private EditText username, email, password;
    private Button registerButton = null;
    public TextView login = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;
        username = findViewById(R.id.register_username);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.button_register);
        login = findViewById(R.id.login_now);

        registerButton.setOnClickListener(v -> handleRegister());
        login.setOnClickListener(v -> {moveActivity(this, LoginActivity.class);});

    }
    private void moveActivity(Context ctx, Class<?> cls){
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);

    }
    protected void handleRegister() {
        String usernameS = username.getText().toString();
        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();

        if (usernameS.isEmpty() || emailS.isEmpty() || passwordS.isEmpty()) {
            Toast.makeText(mContext, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Signup signup = new Signup(usernameS, emailS, passwordS);

        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<String> signupCall = apiService.signup(signup);

        signupCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String message = response.body();
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
            }

        });
    }

}