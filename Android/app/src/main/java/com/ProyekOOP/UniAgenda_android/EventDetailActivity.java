package com.ProyekOOP.UniAgenda_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailActivity extends AppCompatActivity {
    private TextView EventTitleTextView, EventDescriptionTextView, EventDateTextView, EventLocationTextView;
    private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private final SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        EventTitleTextView = findViewById(R.id.event_title);
        EventDescriptionTextView = findViewById(R.id.event_description);
        EventDateTextView = findViewById(R.id.event_date);
        EventLocationTextView = findViewById(R.id.event_location);
        MaterialButton editButton = findViewById(R.id.event_edit_button);
        MaterialButton deleteButton = findViewById(R.id.event_delete_button);

        int eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId != -1) {
            getEventById(eventId);
        } else {
            Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
        }

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, EditEventActivity.class);
            intent.putExtra("event_id", eventId);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> {
            if (eventId != -1) {
                deleteEventById(eventId);
            } else {
                Toast.makeText(this, "Invalid event ID", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void getEventById(int eventId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Event> call = apiService.getEventById(eventId);

        call.enqueue(new Callback<Event>(){
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Event event = response.body();
                    EventTitleTextView.setText(event.getEvent_title());
                    EventDescriptionTextView.setText("Description: " + event.getEvent_description());
                    EventDateTextView.setText("Date: " + formatDate(event.getEvent_date()));
                    EventLocationTextView.setText("Location: " + event.getLocation());
                } else {
                    Toast.makeText(EventDetailActivity.this, "Failed to fetch event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this, "Failed to fetch event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteEventById(int eventId) {
        BaseApiService apiService = RetrofitClient.getClient().create(BaseApiService.class);
        Call<Void> call = apiService.deleteEvent(eventId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EventDetailActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EventDetailActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EventDetailActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
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