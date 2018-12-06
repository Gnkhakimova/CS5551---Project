package com.example.rohithkumar.cameramapsapplication;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {

    int selectedYear;
    int selectedMonth;
    int selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        final TextView dateText = (TextView) findViewById(R.id.dateTextView);
        final CalendarView calendar = (CalendarView) findViewById(R.id.calendarView);
        final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String selectedDate = sdf.format(new Date(calendar.getDate()));

        dateText.setText(selectedDate);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                selectedYear = year;
                selectedMonth= month;
                selectedDay = dayOfMonth;

                String date = sdf.format(new Date(selectedYear - 1900, selectedMonth, selectedDay));
                dateText.setText(date);
            }
        });
    }

    public void insertEvent(View v) {
        Intent intent = new Intent(Intent.ACTION_INSERT,
                CalendarContract.Events.CONTENT_URI);
        // Add the calendar event details
        intent.putExtra(CalendarContract.Events.TITLE, "Launch!");
        intent.putExtra(CalendarContract.Events.DESCRIPTION,
                "Learn Java Android Coding");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION,
                "UMKC.com");
        Calendar startTime = Calendar.getInstance();
        startTime.set(selectedYear, selectedMonth, selectedDay);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                startTime.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        // Use the Calendar app to add the new event.
        startActivity(intent);
    }
}
