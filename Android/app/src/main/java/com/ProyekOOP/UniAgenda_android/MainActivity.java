package com.ProyekOOP.UniAgenda_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ProyekOOP.UniAgenda_android.model.Task;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_ACCOUNT_ID = "account_id";
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String accountId = sharedPreferences.getString(KEY_ACCOUNT_ID, null);

        if (accountId == null) {
            Toast.makeText(this, "Account ID is missing", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);

        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.page_1) {
                return true;
            }

            else if (id == R.id.page_2) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                startActivity(intent);
                return true;
            }

            else if (id == R.id.page_3) {
                Intent intent = new Intent(MainActivity.this, AboutMeActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        Log.d("MainActivity", "Account ID: " + accountId);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(taskAdapter);

        loadTasks(accountId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                taskAdapter.filterByTitle(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskAdapter.filterByTitle(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter) {
            showDatePickerDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTasks(String accountId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<List<Task>> call = apiService.getTaskByUserId(accountId);
        Log.d("MainActivity", "Account ID used in request: " + accountId);

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful()) {
                    List<Task> tasks = response.body();
                    taskAdapter = new TaskAdapter(MainActivity.this, tasks);
                    recyclerView.setAdapter(taskAdapter);
                } else {
                    Log.e("MainActivity", "Failed to load tasks: " + response.message());
                    Toast.makeText(MainActivity.this, "Failed to load tasks: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a date");
        MaterialDatePicker<Long> datePicker = builder.build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String selectedDate = sdf.format(new Date(selection));
            taskAdapter.filterByDeadline(selectedDate);
        });

        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }
}
