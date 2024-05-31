package com.ProyekOOP.UniAgenda_android;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ProyekOOP.UniAgenda_android.model.Task;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView courseTextView;
    private TextView descriptionTextView;
    private TextView deadlineTextView;
    private TextView statusTextView;
    private TextView typeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        titleTextView = findViewById(R.id.title);
        courseTextView = findViewById(R.id.course);
        descriptionTextView = findViewById(R.id.description);
        deadlineTextView = findViewById(R.id.deadline);
        statusTextView = findViewById(R.id.status);
        typeTextView = findViewById(R.id.type);

        String taskIdStr = getIntent().getStringExtra("task_id");
        if (taskIdStr != null) {
            try {
                UUID taskId = UUID.fromString(taskIdStr);
                getTaskById(taskId);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Invalid Task ID format", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Task ID not provided", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTaskById(UUID taskId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Task> call = apiService.getTaskById(taskId.toString());
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Task task = response.body();
                    titleTextView.setText(task.getTask_title());
                    courseTextView.setText(task.getCourse());
                    descriptionTextView.setText(task.getTask_description());
                    deadlineTextView.setText("Deadline: " + task.getTask_deadline());
                    statusTextView.setText("Status: " + task.getTask_status());
                    typeTextView.setText("Type: " + task.getTask_type());
                } else {
                    Toast.makeText(TaskDetailActivity.this, "Task not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
