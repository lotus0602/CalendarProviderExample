package com.n.calendarproviderexample;

import android.Manifest;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class MainActivity extends AppCompatActivity {
    private Button preBtn, nextBtn;
    private Button calendarBtn, eventBtn, instanceBtn;
    private TextView titleTv;
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;

    private DateTime start, end, current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        current = DateTime.today(TimeZone.getDefault());
        start = current.getStartOfMonth();
        end = current.getEndOfMonth();
        initView();
    }

    public void initView() {
        preBtn = (Button) findViewById(R.id.pre_btn);
        nextBtn = (Button) findViewById(R.id.next_btn);
        calendarBtn = (Button) findViewById(R.id.calendar_btn);
        eventBtn = (Button) findViewById(R.id.event_btn);
        instanceBtn = (Button) findViewById(R.id.instance_btn);
        titleTv = (TextView) findViewById(R.id.title_tv);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        adapter = new CalendarAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        titleTv.setText(current.format("YYYY-MM"));

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = start.minusDays(1);
                start = current.getStartOfMonth();
                end = current.getEndOfMonth();
                titleTv.setText(current.format("YYYY-MM"));
                adapter.setData(getEventInstance(start.getMilliseconds(TimeZone.getDefault()),
                        end.getMilliseconds(TimeZone.getDefault())), 2);
                Log.d("", "Start : " + start.toString());
                Log.d("", "End : " + end.toString());
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = end.plusDays(1);
                start = current.getStartOfMonth();
                end = current.getEndOfMonth();
                titleTv.setText(current.format("YYYY-MM"));
                adapter.setData(getEventInstance(start.getMilliseconds(TimeZone.getDefault()),
                        end.getMilliseconds(TimeZone.getDefault())), 2);
                Log.d("", "Start : " + start.toString());
                Log.d("", "End : " + end.toString());
            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setData(getCalendar(), 0);
            }
        });

        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setData(getCalendarEvent(), 1);
            }
        });

        instanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setData(getEventInstance(start.getMilliseconds(TimeZone.getDefault()),
                        end.getMilliseconds(TimeZone.getDefault())), 2);
            }
        });
    }

    public List<CalendarModel> getCalendar() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALENDAR}, 1);
        }

        List<CalendarModel> calendarModels = new ArrayList<CalendarModel>();
        CalendarModel calendarModel;
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        cur = cr.query(uri, CalendarModel.CALENDAR_PROJECTION, null, null, null);

        try {
            while (cur.moveToNext()) {
                calendarModel = new CalendarModel();
                // Get the field values
                calendarModel.calID = cur.getLong(CalendarModel.CALENDARS_INDEX_ID);
                calendarModel.displayName = cur.getString(CalendarModel.CALENDARS_INDEX_DISPLAY_NAME);
                calendarModel.accountName = cur.getString(CalendarModel.CALENDARS_INDEX_ACCOUNT_NAME);
                calendarModel.ownerName = cur.getString(CalendarModel.CALENDARS_INDEX_OWNER_ACCOUNT);

                calendarModels.add(calendarModel);
            }
        } finally {
            cur.close();
        }

        return calendarModels;
    }

    public List<CalendarModel> getCalendarEvent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALENDAR}, 1);
        }

        List<CalendarModel> calendarModels = new ArrayList<CalendarModel>();
        CalendarModel calendarModel;
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        cur = cr.query(uri,CalendarModel.EVENT_PROJECTION, null, null ,null);

        try {
            while (cur.moveToNext()) {
                calendarModel = new CalendarModel();

                calendarModel.mId = cur.getInt(CalendarModel.EVENT_INDEX_ID);
                calendarModel.mTitle = cur.getString(CalendarModel.EVENT_INDEX_TITLE);
                calendarModel.mDescription = cur.getString(CalendarModel.EVENT_INDEX_DESCRIPTION);
                calendarModel.mLocation = cur.getString(CalendarModel.EVENT_INDEX_EVENT_LOCATION);
                calendarModel.mAllDay = cur.getInt(CalendarModel.EVENT_INDEX_ALL_DAY) != 0;
                calendarModel.mHasAlarm = cur.getInt(CalendarModel.EVENT_INDEX_HAS_ALARM) != 0;
                calendarModel.mCalendarId = cur.getInt(CalendarModel.EVENT_INDEX_CALENDAR_ID);
                calendarModel.mStart = cur.getLong(CalendarModel.EVENT_INDEX_DTSTART);
                calendarModel.mTimezone = cur.getString(CalendarModel.EVENT_INDEX_TIMEZONE);
                calendarModel.mRrule = cur.getString(CalendarModel.EVENT_INDEX_RRULE);
                calendarModel.mSyncId = cur.getString(CalendarModel.EVENT_INDEX_SYNC_ID);
                calendarModel.mAvailability = cur.getInt(CalendarModel.EVENT_INDEX_AVAILABILITY);
                calendarModel.mAccessLevel = cur.getInt(CalendarModel.EVENT_INDEX_ACCESS_LEVEL);
                calendarModel.mOwnerAccount = cur.getString(CalendarModel.EVENT_INDEX_OWNER_ACCOUNT);
                calendarModel.mHasAttendeeData = cur.getInt(CalendarModel.EVENT_INDEX_HAS_ATTENDEE_DATA) != 0;
                calendarModel.mOriginalSyncId = cur.getString(CalendarModel.EVENT_INDEX_ORIGINAL_SYNC_ID);
                calendarModel.mOriginalId = cur.getLong(CalendarModel.EVENT_INDEX_ORIGINAL_ID);
                calendarModel.mOrganizer = cur.getString(CalendarModel.EVENT_INDEX_ORGANIZER);
                calendarModel.mGuestsCanModify = cur.getInt(CalendarModel.EVENT_INDEX_GUESTS_CAN_MODIFY) != 0;

                if (cur.isNull(calendarModel.EVENT_INDEX_EVENT_COLOR)) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    calendarModel.mEventColor = cur.getInt(CalendarModel.EVENT_INDEX_CALENDAR_COLOR);
                } else {
                    calendarModel.mEventColor = cur.getInt(CalendarModel.EVENT_INDEX_EVENT_COLOR);
                }
                calendarModel.mEventStatus = cur.getInt(CalendarModel.EVENT_INDEX_EVENT_STATUS);

                if (!TextUtils.isEmpty(calendarModel.mRrule)) {
                    calendarModel.mDuration = cur.getString(CalendarModel.EVENT_INDEX_DURATION);
                } else {
                    calendarModel.mEnd = cur.getLong(CalendarModel.EVENT_INDEX_DTEND);
                }
                calendarModels.add(calendarModel);
            }
        } finally {
            cur.close();
        }
        return calendarModels;
    }

    public List<CalendarModel> getEventInstance(long startMillis, long endMillis) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALENDAR}, 1);
        }

        List<CalendarModel> calendarModels = new ArrayList<CalendarModel>();
        CalendarModel calendarModel;
        Cursor cur = null;
        ContentResolver cr = getContentResolver();

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        cur = cr.query(builder.build(), CalendarModel.INSTANCES_PROJECTION, null, null, null);

        try {
            while (cur.moveToNext()) {
                calendarModel = new CalendarModel();
                calendarModel.id = cur.getLong(CalendarModel.INSTANCES_PROJECTION_EVENT_ID_INDEX);
                calendarModel.title = cur.getString(CalendarModel.INSTANCES_PROJECTION_TITLE_INDEX);
                calendarModel.location = cur.getString(CalendarModel.INSTANCES_PROJECTION_LOCATION_INDEX);
                calendarModel.allDay = cur.getInt(CalendarModel.INSTANCES_PROJECTION_ALL_DAY_INDEX) != 0;
                calendarModel.organizer = cur.getString(CalendarModel.INSTANCES_PROJECTION_ORGANIZER_INDEX);
                calendarModel.guestsCanModify = cur.getInt(CalendarModel.INSTANCES_PROJECTION_GUESTS_CAN_INVITE_OTHERS_INDEX) != 0;

                if (!cur.isNull(CalendarModel.INSTANCES_PROJECTION_COLOR_INDEX)) {
                    calendarModel.color = cur.getInt(CalendarModel.INSTANCES_PROJECTION_COLOR_INDEX);
                } else {
//                    calendarModel.color = #ff6bd697;
                }

                calendarModel.startMillis = cur.getLong(CalendarModel.INSTANCES_PROJECTION_BEGIN_INDEX);
                calendarModel.endMillis = cur.getLong(CalendarModel.INSTANCES_PROJECTION_END_INDEX);

                calendarModel.startTime = cur.getInt(CalendarModel.INSTANCES_PROJECTION_START_MINUTE_INDEX);
                calendarModel.startDay = cur.getInt(CalendarModel.INSTANCES_PROJECTION_START_DAY_INDEX);

                calendarModel.endTime = cur.getInt(CalendarModel.INSTANCES_PROJECTION_END_MINUTE_INDEX);
                calendarModel.endDay = cur.getInt(CalendarModel.INSTANCES_PROJECTION_END_DAY_INDEX);

                calendarModel.timezone = cur.getString(CalendarModel.INSTANCES_PROJECTION_TIMEZONE_INDEX);
                calendarModel.hasAlarm = cur.getInt(CalendarModel.INSTANCES_PROJECTION_HAS_ALARM_INDEX) != 0;

                String rrule = cur.getString(CalendarModel.INSTANCES_PROJECTION_RRULE_INDEX);
                String rdate = cur.getString(CalendarModel.INSTANCES_PROJECTION_RDATE_INDEX);

                if (!TextUtils.isEmpty(rrule) || !TextUtils.isEmpty(rdate)) {
                    calendarModel.isRepeating = true;
                } else {
                    calendarModel.isRepeating = false;
                }

                calendarModel.selfAttendeeStatus = cur.getInt(CalendarModel.INSTANCES_PROJECTION_SELF_ATTENDEE_STATUS_INDEX);

                calendarModels.add(calendarModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cur.close();
        }

        return calendarModels;
    }

}
