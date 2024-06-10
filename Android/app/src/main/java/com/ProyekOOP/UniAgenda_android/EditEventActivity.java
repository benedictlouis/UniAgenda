package com.ProyekOOP.UniAgenda_android;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ProyekOOP.UniAgenda_android.model.Event;
import com.ProyekOOP.UniAgenda_android.request.BaseApiService;
import com.ProyekOOP.UniAgenda_android.request.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;

public class EditEventActivity extends AppCompatActivity {

    private TextInputEditText eventTitleEditText, eventDescriptionEditText, eventDateEditText, eventLocationEditText;
    private int eventId;
    private Event event;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventTitleEditText = findViewById(R.id.edit_event_title);
        eventDescriptionEditText = findViewById(R.id.edit_event_description);
        eventDateEditText = findViewById(R.id.edit_event_date);
        eventLocationEditText = findViewById(R.id.edit_event_location);
        MaterialButton updateButton = findViewById(R.id.update_event_button);
        calendar = Calendar.getInstance();

        eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId != -1){
            getEventById(eventId);
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
        }

        updateButton.setOnClickListener(v -> updateEvent());

        eventDateEditText.setOnClickListener(v -> showDateTimePicker());

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
            eventDateEditText.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", calendar));
        });

        timePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    private void getEventById(int eventId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Event> call = apiService.getEventById(eventId);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, retrofit2.Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    event = response.body();
                    populateEventDetails();
                } else {
                    Toast.makeText(EditEventActivity.this, "Event not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(EditEventActivity.this, "Failed to fetch event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateEventDetails() {
        eventTitleEditText.setText(event.getEvent_title());
        eventDescriptionEditText.setText(event.getEvent_description());
        eventDateEditText.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", calendar));
        eventLocationEditText.setText(event.getLocation());
    }

    private void updateEvent() {
        String title = eventTitleEditText.getText().toString();
        String description = eventDescriptionEditText.getText().toString();
        String date = eventDateEditText.getText().toString();
        String location = eventLocationEditText.getText().toString();

        event.setEvent_title(title);
        event.setEvent_description(description);
        event.setEvent_date(date);
        event.setLocation(location);

        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Event> call = apiService.updateEvent(eventId, event);

        call.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, retrofit2.Response<Event> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(EditEventActivity.this, "Event updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditEventActivity.this, "Update failed: Empty response body", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditEventActivity.this, "Failed to update event. Response code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(EditEventActivity.this, "Failed to update event. Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}