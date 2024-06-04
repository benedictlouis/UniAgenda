package com.ProyekOOP.UniAgenda_android;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ProyekOOP.UniAgenda_android.model.Task;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTaskActivity extends AppCompatActivity {

    private TextInputEditText titleEditText;
    private TextInputEditText courseEditText;
    private TextInputEditText descriptionEditText;
    private TextInputEditText deadlineEditText;
    private Spinner statusSpinner;
    private Spinner typeSpinner;
    private MaterialButton updateButton;

    private int taskId;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        titleEditText = findViewById(R.id.edit_title_input);
        courseEditText = findViewById(R.id.edit_course_input);
        descriptionEditText = findViewById(R.id.edit_description_input);
        deadlineEditText = findViewById(R.id.edit_deadline_input);
        statusSpinner = findViewById(R.id.edit_status_spinner);
        typeSpinner = findViewById(R.id.edit_type_spinner);
        updateButton = findViewById(R.id.update_task_button);

        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId != -1) {
            getTaskById(taskId);
        } else {
            Toast.makeText(this, "Task ID not provided", Toast.LENGTH_SHORT).show();
        }

        updateButton.setOnClickListener(v -> {
            updateTask();
        });

        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);
    }

    private void getTaskById(int taskId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Task> call = apiService.getTaskById(taskId);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    task = response.body();
                    populateTaskDetails();
                } else {
                    Toast.makeText(EditTaskActivity.this, "Task not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(EditTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateTaskDetails() {
        titleEditText.setText(task.getTask_title());
        courseEditText.setText(task.getCourse());
        descriptionEditText.setText(task.getTask_description());
        deadlineEditText.setText(task.getTask_deadline());
        // Set status spinner selection based on task status
        ArrayAdapter<CharSequence> statusAdapter = (ArrayAdapter<CharSequence>) statusSpinner.getAdapter();
        int statusPosition = statusAdapter.getPosition(task.getTask_status());
        statusSpinner.setSelection(statusPosition);

        // Set type spinner selection based on task type
        ArrayAdapter<CharSequence> typeAdapter = (ArrayAdapter<CharSequence>) typeSpinner.getAdapter();
        int typePosition = typeAdapter.getPosition(task.getTask_type());
        typeSpinner.setSelection(typePosition);
    }

    private void updateTask() {
        String title = titleEditText.getText().toString();
        String course = courseEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String deadline = deadlineEditText.getText().toString();
        String status = statusSpinner.getSelectedItem().toString();
        String type = typeSpinner.getSelectedItem().toString();

        task.setTask_title(title);
        task.setCourse(course);
        task.setTask_description(description);
        task.setTask_deadline(deadline);
        task.setTask_status(status);
        task.setTask_type(type);

        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Task> call = apiService.updateTask(taskId, task);
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditTaskActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditTaskActivity.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(EditTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        }

    }


