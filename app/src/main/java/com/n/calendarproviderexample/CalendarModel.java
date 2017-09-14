package com.n.calendarproviderexample;

import android.provider.CalendarContract;
import android.text.format.DateUtils;

import java.util.TimeZone;

import hirondelle.date4j.DateTime;

/**
 * Created by N on 2017-09-11.
 */

public class CalendarModel {
    long calID = 0;
    String displayName = null;
    String accountName = null;
    String ownerName = null;

//////////////
//    public String mUri = null;
    public long mId = -1;
    public long mCalendarId = -1;
//
    public String mSyncId = null;
//
    public int mEventColor = -1;
//    private boolean mEventColorInitialized = false;
//
    public String mOwnerAccount = null;
    public String mTitle = null;
    public String mLocation = null;
    public String mDescription = null;
    public String mRrule = null;
    public String mOrganizer = null;

    public boolean mIsOrganizer = true;
    public long mStart = -1;

    public long mEnd = -1;
    public String mDuration = null;
    public String mTimezone = null;

    public boolean mAllDay = false;
    public boolean mHasAlarm = false;
    public int mAvailability;   // = Events.AVAILABILITY_BUSY;
//
    public boolean mHasAttendeeData = true;
    public String mOriginalSyncId = null;
    public long mOriginalId = -1;
    public boolean mGuestsCanModify = false;

    public int mEventStatus;// = Events.STATUS_CONFIRMED;

    public int mAccessLevel = 0;

//////////////
    public long id;

    public int color;
    public String title;
    public String location;
    public boolean allDay;
    public String organizer;
    public boolean guestsCanModify;

    public int startDay;       // start Julian day
    public int endDay;         // end Julian day
    public int startTime;      // Start and end time are in minutes since midnight
    public int endTime;

    public String timezone;

    public long startMillis;   // UTC milliseconds since the epoch
    public long endMillis;     // UTC milliseconds since the epoch
    private int mColumn;
    private int mMaxColumns;

    public boolean hasAlarm;
    public boolean isRepeating;

    public int selfAttendeeStatus;


    public String getEventToString() {
        String s = "";
        s += "EventId : " + mId + ", CalendarId : " + mCalendarId + ", SyncId : " + mSyncId + ", EventColor : " + mEventColor
                + "\nOwnerAccount : " + mOwnerAccount + ", Title : " + mTitle + ", Location : " + mLocation + "Description : " + mDescription
                + "\nRrule : " + mRrule + ", Organizer : " + mOrganizer + ", Start : " + mStart + ", End : " + mEnd
                + "\nDuration : " + mDuration + ", Timezone : " + mTimezone + ", AllDay : " + mAllDay + ", HasAlarm : " + mHasAlarm
                + "\nAvailability : " + mAvailability + ", HasAttendeeData : " + mHasAttendeeData + ", OriginSyncId : " + mOriginalSyncId
                + "\nOriginId : " + mOriginalId + ", GuestsCanModify : " + mGuestsCanModify + ", EventStatus" + mEventStatus + ", AccessLevel : " + mAccessLevel;
        return s;
    }

    public String getInstanceToString () {
        DateTime sDate, eDate;
        sDate = DateTime.forInstant(startMillis, TimeZone.getDefault());
        eDate = DateTime.forInstant(endMillis, TimeZone.getDefault());

        String s = "";
        s += "Title : " + title + ", Location : " + location + ", AllDay : " + allDay + ", Timezone : " + timezone
                + "\nStart : " + sDate.format("YYYY/MM/DD hh:mm") + ", End : " + eDate.format("YYYY/MM/DD hh:mm")
                + "\nhasAlarm : " + hasAlarm + ", isRepeat : " + isRepeating;
        return s;
    }

    public static final String[] CALENDAR_PROJECTION = new String[] {
            CalendarContract.Calendars._ID, // 0
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, // 1
            CalendarContract.Calendars.OWNER_ACCOUNT, // 2
            CalendarContract.Calendars.CALENDAR_COLOR, // 3
            CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, // 4
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, // 5
            CalendarContract.Calendars.VISIBLE, // 6
            CalendarContract.Calendars.MAX_REMINDERS, // 7
            CalendarContract.Calendars.ALLOWED_REMINDERS, // 8
            CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, // 9
            CalendarContract.Calendars.ALLOWED_AVAILABILITY, // 10
            CalendarContract.Calendars.ACCOUNT_NAME, // 11
            CalendarContract.Calendars.ACCOUNT_TYPE, //12
    };
    public static final int CALENDARS_INDEX_ID = 0;
    public static final int CALENDARS_INDEX_DISPLAY_NAME = 1;
    public static final int CALENDARS_INDEX_OWNER_ACCOUNT = 2;
    public static final int CALENDARS_INDEX_COLOR = 3;
    public static final int CALENDARS_INDEX_CAN_ORGANIZER_RESPOND = 4;
    public static final int CALENDARS_INDEX_ACCESS_LEVEL = 5;
    public static final int CALENDARS_INDEX_VISIBLE = 6;
    public static final int CALENDARS_INDEX_MAX_REMINDERS = 7;
    public static final int CALENDARS_INDEX_ALLOWED_REMINDERS = 8;
    public static final int CALENDARS_INDEX_ALLOWED_ATTENDEE_TYPES = 9;
    public static final int CALENDARS_INDEX_ALLOWED_AVAILABILITY = 10;
    public static final int CALENDARS_INDEX_ACCOUNT_NAME = 11;
    public static final int CALENDARS_INDEX_ACCOUNT_TYPE = 12;

    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Events._ID, // 0
            CalendarContract.Events.TITLE, // 1
            CalendarContract.Events.DESCRIPTION, // 2
            CalendarContract.Events.EVENT_LOCATION, // 3
            CalendarContract.Events.ALL_DAY, // 4
            CalendarContract.Events.HAS_ALARM, // 5
            CalendarContract.Events.CALENDAR_ID, // 6
            CalendarContract.Events.DTSTART, // 7
            CalendarContract.Events.DTEND, // 8
            CalendarContract.Events.DURATION, // 9
            CalendarContract.Events.EVENT_TIMEZONE, // 10
            CalendarContract.Events.RRULE, // 11
            CalendarContract.Events._SYNC_ID, // 12
            CalendarContract.Events.AVAILABILITY, // 13
            CalendarContract.Events.ACCESS_LEVEL, // 14
            CalendarContract.Events.OWNER_ACCOUNT, // 15
            CalendarContract.Events.HAS_ATTENDEE_DATA, // 16
            CalendarContract.Events.ORIGINAL_SYNC_ID, // 17
            CalendarContract.Events.ORGANIZER, // 18
            CalendarContract.Events.GUESTS_CAN_MODIFY, // 19
            CalendarContract.Events.ORIGINAL_ID, // 20
            CalendarContract.Events.STATUS, // 21
            CalendarContract.Events.CALENDAR_COLOR, // 22
            CalendarContract.Events.EVENT_COLOR, // 23
            CalendarContract.Events.EVENT_COLOR_KEY // 24
    };
    public static final int EVENT_INDEX_ID = 0;
    public static final int EVENT_INDEX_TITLE = 1;
    public static final int EVENT_INDEX_DESCRIPTION = 2;
    public static final int EVENT_INDEX_EVENT_LOCATION = 3;
    public static final int EVENT_INDEX_ALL_DAY = 4;
    public static final int EVENT_INDEX_HAS_ALARM = 5;
    public static final int EVENT_INDEX_CALENDAR_ID = 6;
    public static final int EVENT_INDEX_DTSTART = 7;
    public static final int EVENT_INDEX_DTEND = 8;
    public static final int EVENT_INDEX_DURATION = 9;
    public static final int EVENT_INDEX_TIMEZONE = 10;
    public static final int EVENT_INDEX_RRULE = 11;
    public static final int EVENT_INDEX_SYNC_ID = 12;
    public static final int EVENT_INDEX_AVAILABILITY = 13;
    public static final int EVENT_INDEX_ACCESS_LEVEL = 14;
    public static final int EVENT_INDEX_OWNER_ACCOUNT = 15;
    public static final int EVENT_INDEX_HAS_ATTENDEE_DATA = 16;
    public static final int EVENT_INDEX_ORIGINAL_SYNC_ID = 17;
    public static final int EVENT_INDEX_ORGANIZER = 18;
    public static final int EVENT_INDEX_GUESTS_CAN_MODIFY = 19;
    public static final int EVENT_INDEX_ORIGINAL_ID = 20;
    public static final int EVENT_INDEX_EVENT_STATUS = 21;
    public static final int EVENT_INDEX_CALENDAR_COLOR = 22;
    public static final int EVENT_INDEX_EVENT_COLOR = 23;
    public static final int EVENT_INDEX_EVENT_COLOR_KEY = 24;

    public static final String[] INSTANCES_PROJECTION = new String[] {
            CalendarContract.Instances.TITLE,                 // 0
            CalendarContract.Instances.EVENT_LOCATION,        // 1
            CalendarContract.Instances.ALL_DAY,               // 2
            CalendarContract.Instances.DISPLAY_COLOR,         // 3 If SDK < 16, set to Instances.CALENDAR_COLOR.
            CalendarContract.Instances.EVENT_TIMEZONE,        // 4
            CalendarContract.Instances.EVENT_ID,              // 5
            CalendarContract.Instances.BEGIN,                 // 6
            CalendarContract.Instances.END,                   // 7
            CalendarContract.Instances._ID,                   // 8
            CalendarContract.Instances.START_DAY,             // 9
            CalendarContract.Instances.END_DAY,               // 10
            CalendarContract.Instances.START_MINUTE,          // 11
            CalendarContract.Instances.END_MINUTE,            // 12
            CalendarContract.Instances.HAS_ALARM,             // 13
            CalendarContract.Instances.RRULE,                 // 14
            CalendarContract.Instances.RDATE,                 // 15
            CalendarContract.Instances.SELF_ATTENDEE_STATUS,  // 16
            CalendarContract.Events.ORGANIZER,                // 17
            CalendarContract.Events.GUESTS_CAN_MODIFY,        // 18
            CalendarContract.Instances.ALL_DAY + "=1 OR (" + CalendarContract.Instances.END + "-" + CalendarContract.Instances.BEGIN + ")>="
                    + DateUtils.DAY_IN_MILLIS + " AS " + "dispAllday" // 19
    };

    public static final int INSTANCES_PROJECTION_TITLE_INDEX = 0;
    public static final int INSTANCES_PROJECTION_LOCATION_INDEX = 1;
    public static final int INSTANCES_PROJECTION_ALL_DAY_INDEX = 2;
    public static final int INSTANCES_PROJECTION_COLOR_INDEX = 3;
    public static final int INSTANCES_PROJECTION_TIMEZONE_INDEX = 4;
    public static final int INSTANCES_PROJECTION_EVENT_ID_INDEX = 5;
    public static final int INSTANCES_PROJECTION_BEGIN_INDEX = 6;
    public static final int INSTANCES_PROJECTION_END_INDEX = 7;
    public static final int INSTANCES_PROJECTION_START_DAY_INDEX = 9;
    public static final int INSTANCES_PROJECTION_END_DAY_INDEX = 10;
    public static final int INSTANCES_PROJECTION_START_MINUTE_INDEX = 11;
    public static final int INSTANCES_PROJECTION_END_MINUTE_INDEX = 12;
    public static final int INSTANCES_PROJECTION_HAS_ALARM_INDEX = 13;
    public static final int INSTANCES_PROJECTION_RRULE_INDEX = 14;
    public static final int INSTANCES_PROJECTION_RDATE_INDEX = 15;
    public static final int INSTANCES_PROJECTION_SELF_ATTENDEE_STATUS_INDEX = 16;
    public static final int INSTANCES_PROJECTION_ORGANIZER_INDEX = 17;
    public static final int INSTANCES_PROJECTION_GUESTS_CAN_INVITE_OTHERS_INDEX = 18;
    public static final int INSTANCES_PROJECTION_DISPLAY_AS_ALLDAY = 19;

    public static final String[] ATTENDEES_PROJECTION = new String[] {
            CalendarContract.Attendees._ID, // 0
            CalendarContract.Attendees.ATTENDEE_NAME, // 1
            CalendarContract.Attendees.ATTENDEE_EMAIL, // 2
            CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, // 3
            CalendarContract.Attendees.ATTENDEE_STATUS, // 4
    };
    public static final int ATTENDEES_INDEX_ID = 0;
    public static final int ATTENDEES_INDEX_NAME = 1;
    public static final int ATTENDEES_INDEX_EMAIL = 2;
    public static final int ATTENDEES_INDEX_RELATIONSHIP = 3;
    public static final int ATTENDEES_INDEX_STATUS = 4;
    public static final String ATTENDEES_WHERE = CalendarContract.Attendees.EVENT_ID + "=? AND attendeeEmail IS NOT NULL";

    public static final String[] REMINDERS_PROJECTION = new String[] {
            CalendarContract.Reminders._ID, // 0
            CalendarContract.Reminders.MINUTES, // 1
            CalendarContract.Reminders.METHOD, // 2
    };
    public static final int REMINDERS_INDEX_MINUTES = 1;
    public static final int REMINDERS_INDEX_METHOD = 2;
    public static final String REMINDERS_WHERE = CalendarContract.Reminders.EVENT_ID + "=?";
}
