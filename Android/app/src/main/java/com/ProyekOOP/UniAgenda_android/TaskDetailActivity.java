package com.ProyekOOP.UniAgenda_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ProyekOOP.UniAgenda_android.model.Task;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView titleTextView, courseTextView, descriptionTextView, deadlineTextView, statusTextView, typeTextView;
    private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private final SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());

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
        MaterialButton editButton = findViewById(R.id.edit_button);
        MaterialButton deleteButton = findViewById(R.id.delete_button);

        int taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId != -1) {
            getTaskById(taskId);
        } else {
            Toast.makeText(this, "Task ID not provided", Toast.LENGTH_SHORT).show();
        }

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskDetailActivity.this, EditTaskActivity.class);
            intent.putExtra("task_id", taskId);
            startActivity(intent);
        });


        deleteButton.setOnClickListener(v -> {
            if (taskId != -1) {
                deleteTaskById(taskId);
            } else {
                Toast.makeText(this, "Task ID not provided", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTaskById(int taskId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Task> call = apiService.getTaskById(taskId);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Task task = response.body();
                    titleTextView.setText(task.getTask_title());
                    courseTextView.setText("Course: " + task.getCourse());
                    descriptionTextView.setText("Description: " + task.getTask_description());
                    deadlineTextView.setText("Deadline: " + formatDate(task.getTask_deadline()));
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

    private void deleteTaskById(int taskId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Void> call = apiService.deleteTask(taskId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TaskDetailActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TaskDetailActivity.this, "Failed to delete task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(TaskDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatDate(String dateString) {
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
