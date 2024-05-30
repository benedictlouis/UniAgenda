package com.ProyekOOP.UniAgenda_android;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ProyekOOP.UniAgenda_android.model.Task;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

    private TextInputEditText titleInput, courseInput, descriptionInput, deadlineInput;
    private Spinner statusInput, typeInput;
    private Button addTaskButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        titleInput = findViewById(R.id.title_input);
        courseInput = findViewById(R.id.course_input);
        descriptionInput = findViewById(R.id.description_input);
        deadlineInput = findViewById(R.id.deadline_input);
        statusInput = findViewById(R.id.status_spinner);
        typeInput = findViewById(R.id.type_spinner);
        addTaskButton = findViewById(R.id.add_task_button);
        calendar = Calendar.getInstance();

        deadlineInput.setOnClickListener(v -> showDateTimePicker());

        addTaskButton.setOnClickListener(v -> handleAddTask());

        Spinner statusSpinner = findViewById(R.id.status_spinner);
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(this, R.array.status_array, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        Spinner typeSpinner = findViewById(R.id.type_spinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskActivity.this,
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                deadlineInput.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", calendar));
                            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false);
                    timePickerDialog.show();
                }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void handleAddTask() {
        String taskTitle = titleInput.getText().toString();
        String course = courseInput.getText().toString();
        String taskDescription = descriptionInput.getText().toString();
        String taskDeadline = deadlineInput.getText().toString();
        String taskStatus = statusInput.getSelectedItem().toString();
        String taskType = typeInput.getSelectedItem().toString();

        if (taskTitle.isEmpty() || course.isEmpty() || taskDeadline.isEmpty() || taskStatus.isEmpty() || taskType.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task();
        newTask.setTask_title(taskTitle);
        newTask.setCourse(course);
        newTask.setTask_description(taskDescription);
        newTask.setTask_deadline(taskDeadline);
        newTask.setTask_status(taskStatus);
        newTask.setTask_type(taskType);

        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<String> call = apiService.addTask(newTask);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddTaskActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddTaskActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(AddTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}