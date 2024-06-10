package com.ProyekOOP.UniAgenda_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ProyekOOP.UniAgenda_android.model.Event;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText eventTitle, eventDescription, eventDate, eventLocation;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_ACCOUNT_ID = "account_id";
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventTitle = findViewById(R.id.event_title_input);
        eventDate = findViewById(R.id.event_date_input);
        eventDescription = findViewById(R.id.event_description_input);
        eventLocation = findViewById(R.id.event_location_input);
        Button addEventButton = findViewById(R.id.add_event_button);
        calendar = Calendar.getInstance();

        eventDate.setOnClickListener(v -> showDateTimePicker());
        addEventButton.setOnClickListener(v -> handleAddEvent());

    }

    private void showDateTimePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            showTimePicker();
        });

        datePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select time")
                .build();

        timePicker.addOnPositiveButtonClickListener(view -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            eventDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", calendar));
        });

        timePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    private void handleAddEvent() {
        String title = eventTitle.getText().toString();
        String date = eventDate.getText().toString();
        String location = eventLocation.getText().toString();
        String description = eventDescription.getText().toString();

        if (title.isEmpty() || date.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String accountId = sharedPreferences.getString(KEY_ACCOUNT_ID, "");

        if (accountId.isEmpty()) {
            Toast.makeText(this, "Account ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Event newEvent = new Event();
        newEvent.setEvent_title(title);
        newEvent.setEvent_date(date);
        newEvent.setLocation(location);
        newEvent.setEvent_description(description);
        newEvent.setAccount_id(accountId);

        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<String> call = apiService.addEvent(newEvent);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    Toast.makeText(AddEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddEventActivity.this, "Failed to add event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(AddEventActivity.this, "Failed to add event", Toast.LENGTH_SHORT).show();
            }
        });

    }
}