package com.n.calendarproviderexample;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class EditEventActivity extends AppCompatActivity {
    public static final String TAG = "EditEventActivity";

    private EditText mTitleET, mMemoET, mLocationET, mParticipantET, mAlarmET, mStartET, mEndET, mRuleET;
    private Button editBtn, deleteBtn;
    private TextView displayRepeatTv;
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
            mTitleET.setText(queryModel.mTitle);
            mMemoET.setText(queryModel.mDescription);
            displayRepeatTv.setText("Rrule : " + queryModel.mRrule);
        } else {
            finish();
        }

        setDataToView();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(queryModel.mRrule)) {  // 미반복
                    Log.d(TAG, "미반복 변경 시작");
                    String editTitle = mTitleET.getText().toString();
                    ContentResolver cr = getContentResolver();
                    ContentValues values = new ContentValues();
                    Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
                    values.put(CalendarContract.Events.TITLE, editTitle);
                    int row = cr.update(uri, values, null, null);
                    Log.d(TAG, "Rows Updated : " + row);
                } else {
                    Log.d(TAG, "반복 변경 시작");
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditEventActivity.this);
                    builder.setTitle("Test");
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditEventActivity.this, android.R.layout.select_dialog_item);
                    adapter.add("이 일정만");
                    adapter.add("이후 일정만");
                    adapter.add("전체일정");

                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0 :
                                    saveEvent(0);
                                    break;
                                case 1 :
                                    saveEvent(1);
                                    break;
                                case 2 :
                                    saveEvent(2);
                                    break;
                            }
                        }
                    });
                    builder.show();
                }

            }
        });
    }

    public void initView() {
        mTitleET = (EditText) findViewById(R.id.edit_title);
        mMemoET = (EditText) findViewById(R.id.edit_memo);
        mLocationET = (EditText) findViewById(R.id.edit_location);
        mParticipantET = (EditText) findViewById(R.id.edit_participant);
        mAlarmET = (EditText) findViewById(R.id.edit_alarm);
        mStartET = (EditText) findViewById(R.id.edit_start);
        mEndET = (EditText) findViewById(R.id.edit_end);
        mRuleET = (EditText) findViewById(R.id.edit_rule);

        editBtn = (Button) findViewById(R.id.edit_btn);
        deleteBtn = (Button) findViewById(R.id.delete_btn);
        displayRepeatTv = (TextView) findViewById(R.id.edit_repeat_display_tv);

        if (startMillis != -1) {
            DateTime startDate = DateTime.forInstant(startMillis, TimeZone.getDefault());
            mStartET.setText(startDate.format("YYYY/MM/DD hh:mm"));
        }

        if (endMillis != -1) {
            DateTime endDate = DateTime.forInstant(endMillis, TimeZone.getDefault());
            mEndET.setText(endDate.format("YYYY/MM/DD hh:mm"));
        }
    }

    public void setDataToView() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CALENDAR}, 1);
        }

        String rule = queryModel.mRrule;
        String description = queryModel.mDescription;
        String location = queryModel.mLocation;
        boolean hasAttendee = queryModel.mHasAttendeeData;
        boolean hasAlarm = queryModel.mHasAlarm;

        if (!TextUtils.isEmpty(description)) {
            mMemoET.setText(description);
        } else {
            mMemoET.setHint("Empty");
        }
        if (!TextUtils.isEmpty(location)) {
            mLocationET.setText(location);
        } else {
            mLocationET.setHint("Empty");
        }
        if (hasAttendee) {
            ContentResolver cr = getContentResolver();
            Cursor cur;
            Uri attUri = CalendarContract.Attendees.CONTENT_URI;
            String[] whereArgs = { Long.toString(id) };

            cur = cr.query(attUri, CalendarModel.ATTENDEES_PROJECTION, CalendarModel.ATTENDEES_WHERE, whereArgs, null);
            Log.d(TAG, "Attendee Count : " + cur.getCount());
            try {
                while (cur.moveToNext()) {
                    String name = cur.getString(CalendarModel.ATTENDEES_INDEX_NAME);
                    String email = cur.getString(CalendarModel.ATTENDEES_INDEX_EMAIL);
                    int status = cur.getInt(CalendarModel.ATTENDEES_INDEX_STATUS);
                    int relationship = cur.getInt(CalendarModel.ATTENDEES_INDEX_RELATIONSHIP);
                    if (relationship == CalendarContract.Attendees.RELATIONSHIP_ORGANIZER) {
                        String s = "Organizer Email : " + email + ", Name : " + name;
                        mParticipantET.setText(s);
                    }
                    Log.d(TAG, "Email : " + email + ", Name : " + name);
                }
            } finally {
                cur.close();
            }

        } else {
            mParticipantET.setHint("Empty");
        }
        if (hasAlarm) {
            ContentResolver cr = getContentResolver();
            Cursor cur;
            Uri rUri = CalendarContract.Reminders.CONTENT_URI;
            String[] remArgs = { Long.toString(id) };

            cur = cr.query(rUri, CalendarModel.REMINDERS_PROJECTION, CalendarModel.REMINDERS_WHERE, remArgs, null);

            try {
                String s = "";
                if (cur.getCount() < 1) {
                    s = "없음";
                }
                while (cur.moveToNext()) {
                    int minutes = cur.getInt(CalendarModel.REMINDERS_INDEX_MINUTES);
                    int method = cur.getInt(CalendarModel.REMINDERS_INDEX_METHOD);
                    s += minutes + "분, Method : " + method + "/\n";
                }
                mAlarmET.setText(s);
            } finally {
                cur.close();
            }
        } else {
            mAlarmET.setHint("Empty");
        }

        if (TextUtils.isEmpty(rule)) {
            mRuleET.setText( (String) null );
        } else {

        }
    }

    public void saveEvent(int mode) {
        ContentResolver cr = getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ContentValues values = getValuesFromModel(queryModel);

        if (mode == 0) {    // 이 일정만
            Log.d(TAG, "saveEvent Start mode 0");
            String editTitle = mTitleET.getText().toString();
            values.put(CalendarContract.Events.TITLE, editTitle);
            values.put(CalendarContract.Events.DTSTART, startMillis);


            values.put(CalendarContract.Events.ORIGINAL_SYNC_ID, queryModel.mSyncId);
            values.put(CalendarContract.Events.ORIGINAL_INSTANCE_TIME, startMillis);
            values.put(CalendarContract.Events.ORIGINAL_ALL_DAY, queryModel.mAllDay ? 1 : 0);
            values.put(CalendarContract.Events.STATUS, queryModel.mEventStatus);

            ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(CalendarContract.Events.CONTENT_URI).withValues(values);
            ops.add(builder.build());
            Object result = null;
            try {
                result = cr.applyBatch(CalendarContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "result : " + result);
        } else if (mode == 1) {     // 이후 일정
            Log.d(TAG, "saveEvent Start mode 1");

            boolean origAllDay = queryModel.mAllDay;
            String origRrule = queryModel.mRrule;
            String newRrule = origRrule;

            EventRecurrence origRecurrence = new EventRecurrence();
            origRecurrence.parse(origRrule);

            Time dtstart = new Time();
            dtstart.timezone = queryModel.mTimezone;
            dtstart.set(queryModel.mStart);
//            dtstart.set(startMillis);

            ContentValues updateValues = new ContentValues();

            if (origRecurrence.count > 0) {
                RecurrenceSet recurSet = new RecurrenceSet(queryModel.mRrule, null, null, null);
                RecurrenceProcessor recurProc = new RecurrenceProcessor();
                long[] recurrences;
                try {
                    recurrences = recurProc.expand(dtstart, recurSet, queryModel.mStart, startMillis);
                } catch (DateException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                if (recurrences.length == 0) {
                    throw new RuntimeException("can't use this method on first instance");
                }
                EventRecurrence excepRecurrence = new EventRecurrence();
                excepRecurrence.parse(origRrule);
                excepRecurrence.count -= recurrences.length;
                newRrule = excepRecurrence.toString();

                origRecurrence.count = recurrences.length;
            } else {
                Time untilTime = new Time();
                untilTime.timezone = Time.TIMEZONE_UTC;
                untilTime.set(startMillis - 1000);

                if (origAllDay) {
                    untilTime.hour = 0;
                    untilTime.minute = 0;
                    untilTime.second = 0;
                    untilTime.allDay = true;
                    untilTime.normalize(false);

                    dtstart.hour = 0;
                    dtstart.minute = 0;
                    dtstart.second = 0;
                    dtstart.allDay = true;
                    dtstart.timezone = Time.TIMEZONE_UTC;
                }
                origRecurrence.until = untilTime.format2445();
            }
            updateValues.put(CalendarContract.Events.RRULE, origRecurrence.toString());
            updateValues.put(CalendarContract.Events.DTSTART, dtstart.normalize(true));

            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, id);
            ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(uri).withValues(updateValues);
            ops.add(builder.build());


            String editTitle = mTitleET.getText().toString();
            values.put(CalendarContract.Events.TITLE, editTitle);
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.RRULE, newRrule);
            values.put(CalendarContract.Events.STATUS, queryModel.mEventStatus);

            ContentProviderOperation.Builder builder1 =
                    ContentProviderOperation.newInsert(CalendarContract.Events.CONTENT_URI).withValues(values);
            ops.add(builder1.build());

            Object result = null;
            try {
                result = cr.applyBatch(CalendarContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "result : " + result);
        } else if (mode == 2) {     // 전체일정
            Log.d(TAG, "saveEvent Start mode 2");

        }

    }

    public ContentValues getValuesFromModel(CalendarModel model) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, model.mCalendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, model.mTimezone);
        values.put(CalendarContract.Events.TITLE, model.mTitle);
        values.put(CalendarContract.Events.ALL_DAY, model.mAllDay ? 1 : 0);
        values.put(CalendarContract.Events.DTSTART, model.mStart);
        String rrule = model.mRrule;
        values.put(CalendarContract.Events.RRULE, rrule);
        if (!TextUtils.isEmpty(rrule)) {
            values.put(CalendarContract.Events.DURATION, model.mDuration);
            values.put(CalendarContract.Events.DTEND, (Long) null);
        } else {
            values.put(CalendarContract.Events.DURATION, (String) null);
            values.put(CalendarContract.Events.DTEND, model.mEnd);
        }
        if (model.mDescription != null) {
            values.put(CalendarContract.Events.DESCRIPTION, model.mDescription.trim());
        } else {
            values.put(CalendarContract.Events.DESCRIPTION, (String) null);
        }
        if (model.mLocation != null) {
            values.put(CalendarContract.Events.EVENT_LOCATION, model.mLocation.trim());
        } else {
            values.put(CalendarContract.Events.EVENT_LOCATION, (String) null);
        }
        values.put(CalendarContract.Events.AVAILABILITY, model.mAvailability);
        values.put(CalendarContract.Events.HAS_ATTENDEE_DATA, model.mHasAttendeeData ? 1 : 0);

        values.put(CalendarContract.Events.ACCESS_LEVEL, model.mAccessLevel);
        values.put(CalendarContract.Events.STATUS, model.mEventStatus);

        values.put(CalendarContract.Events.HAS_ALARM, 0);   // TODO

        return values;
    }

    public String getRepeatString(EventRecurrence recurrence) {
        String s = "";
        String endString = "";
        StringBuilder sb = new StringBuilder();
        if (recurrence.until != null) {
            Time t = new Time();
            t.parse(recurrence.until);
            String dateStr = DateUtils.formatDateTime(this, t.toMillis(false), DateUtils.FORMAT_NUMERIC_DATE);
            sb.append("; until " + dateStr);
        }
        if (recurrence.count > 0) {
            sb.append("; for " + recurrence.count);
        }
        endString = sb.toString();

        int interval = recurrence.interval <= 1 ? 1 : recurrence.interval;
        switch (recurrence.freq) {
            case EventRecurrence.DAILY :
                if (interval > 1) {
                    s += "Every " + interval + " days" + endString;
                    return s;
                } else {
                    s += "Daily" + endString;
                    return s;
                }
            case EventRecurrence.WEEKLY :
                if (recurrence.repeatsOnEveryWeekDay()) {

                } else {}
        }

        return null;
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
