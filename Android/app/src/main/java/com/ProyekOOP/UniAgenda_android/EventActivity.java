package com.ProyekOOP.UniAgenda_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ProyekOOP.UniAgenda_android.model.Event;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_ACCOUNT_ID = "account_id";
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String accountId = sharedPreferences.getString(KEY_ACCOUNT_ID, null);

        if (accountId == null) {
            Toast.makeText(this, "Account ID is missing", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EventActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        FloatingActionButton fab = findViewById(R.id.event_floating_action_button);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, AddEventActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.eventBottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.page_1) {
                Intent intent = new Intent(EventActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.page_2) {
                return true;
            } else if (id == R.id.page_3) {
                Intent intent = new Intent(EventActivity.this, AboutMeActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        Log.d("EventActivity", "Account ID: " + accountId);

        recyclerView = findViewById(R.id.event_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(eventAdapter);

        loadEvents(accountId);
    }

    private void loadEvents(String accountId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<List<Event>> call = apiService.getEventByUserId(accountId);

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> events = response.body();
                    eventAdapter.updateEventList(events);
                } else {
                    Log.e("EventActivity", "Failed to load events: " + response.message());
                    Toast.makeText(EventActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Log.e("EventActivity", "Error: " + t.getMessage(), t);
                Toast.makeText(EventActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
