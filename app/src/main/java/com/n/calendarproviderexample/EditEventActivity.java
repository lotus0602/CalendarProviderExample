package com.n.calendarproviderexample;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class EditEventActivity extends AppCompatActivity {
    public static final String TAG = "EditEventActivity";

    private EditText title, memo, start, end;
    private Button editBtn, deleteBtn;
    private long id, startMillis, endMillis;
    private CalendarModel queryModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);
        startMillis = intent.getLongExtra("startMillis", -1);
        endMillis = intent.getLongExtra("endMillis", -1);

        Log.d(TAG, "Id : " + id);
        Log.d(TAG, "Start : " + startMillis);
        Log.d(TAG, "End : " + endMillis);

        initView();

        if (id != -1) {
            queryModel = getCalendarEvent(id);
            Log.d(TAG, queryModel.getEventToString());
            title.setText(queryModel.mTitle);
            memo.setText(queryModel.mDescription);
        } else {
            finish();
        }

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editTitle = title.getText().toString();
                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();
                Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
                values.put(CalendarContract.Events.TITLE, editTitle);
                int row = cr.update(uri, values, null, null);
                Log.d(TAG, "Rows Updated : " + row);
            }
        });
    }

    public void initView() {
        title = (EditText) findViewById(R.id.edit_title);
        memo = (EditText) findViewById(R.id.edit_memo);
        start = (EditText) findViewById(R.id.edit_start);
        end = (EditText) findViewById(R.id.edit_end);
        editBtn = (Button) findViewById(R.id.edit_btn);
        deleteBtn = (Button) findViewById(R.id.delete_btn);

        if (startMillis != -1) {
            DateTime startDate = DateTime.forInstant(startMillis, TimeZone.getDefault());
            start.setText(startDate.format("YYYY/MM/DD hh:mm"));
        }

        if (endMillis != -1) {
            DateTime endDate = DateTime.forInstant(endMillis, TimeZone.getDefault());
            end.setText(endDate.format("YYYY/MM/DD hh:mm"));
        }
    }

    public CalendarModel getCalendarEvent(long eventid) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALENDAR}, 1);
        }

//        List<CalendarModel> calendarModels = new ArrayList<CalendarModel>();
        CalendarModel calendarModel = null;
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
//        Uri uri = CalendarContract.Events.CONTENT_URI;
        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventid);

        cur = cr.query(uri,CalendarModel.EVENT_PROJECTION, null, null ,null);

        try {
            Log.d(TAG, "Cursor Count : " + cur.getCount());
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
//                calendarModels.add(calendarModel);
            }
        } finally {
            cur.close();
        }
        return calendarModel;
    }
}
