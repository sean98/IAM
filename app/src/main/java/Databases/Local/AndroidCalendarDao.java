package Databases.Local;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import Databases.ICalendarEventDao;
import Databases.util.DbTask;
import Models.CalendarEvents.CalendarEvent;

public class AndroidCalendarDao implements ICalendarEventDao {
    private Context context;
    public AndroidCalendarDao(Context context){
        this.context = context;
    }

    @Override
    public DbTask<Long> pushCalendarEvent(CalendarEvent calendarEvent) {

        return  new DbTask<>("Android pushCalendarEvent",()->
              pushEventToCalender(
                    calendarEvent.getOwner().getEmail()!=null?calendarEvent.getOwner().getEmail():"",
                    calendarEvent.getTitle(),
                    calendarEvent.getDetails(),
                    calendarEvent.getAddress()!=null? calendarEvent.getAddress().
                            toStringFormat("C c , s n"):"",
                    CalendarContract.Events.STATUS_CONFIRMED,
                    calendarEvent.getDateTime().getTimeInMillis(),
                    calendarEvent.getEndDateTime().getTimeInMillis(),
                    calendarEvent.getDuration().getTimeInMillis(), false,
                    20,true,true,true,
                    Color.BLUE,
                    calendarEvent.getAttendees()));
    }

    private long pushEventToCalender(String OrganizerEmail,
                                     String title,
                                     String addInfo,
                                     String place,
                                     int status,
                                     long startDateTime,long endDateTime,long duration,boolean isAllDay ,
                                     int reminderMinutes,boolean hasAlarm,
                                     boolean needReminder,
                                     boolean needMailService, int color,
                                     List<CalendarEvent.Attendee> attendees ) {

        long calID = getCalendarID("gmail","@");//TODO change to company's domain
        ContentResolver cr = context.getContentResolver();

        ContentValues eventValues = new ContentValues();
        eventValues.put(CalendarContract.Events.CUSTOM_APP_PACKAGE,context.getApplicationContext().getPackageName());

        eventValues.put(CalendarContract.Events.ORGANIZER, OrganizerEmail);
        eventValues.put(CalendarContract.Events.CALENDAR_ID, calID);
        // id, We need to choose from our mobile ,for primary its 1
        eventValues.put(CalendarContract.Events.TITLE, title);
        eventValues.put(CalendarContract.Events.DESCRIPTION, addInfo);
        //Time
        eventValues.put(CalendarContract.Events.DTSTART, startDateTime);
        eventValues.put(CalendarContract.Events.ALL_DAY, isAllDay?1:0);
        if(!isAllDay)
            eventValues.put(CalendarContract.Events.DTEND, endDateTime);
            //eventValues.put(CalendarContract.Events.DURATION, duration);

        //Place
        eventValues.put(CalendarContract.Events.EVENT_LOCATION, place);

        eventValues.put(CalendarContract.Events.STATUS,status);
        eventValues.put(CalendarContract.Events.SELF_ATTENDEE_STATUS,status);

        eventValues.put(CalendarContract.Events.GUESTS_CAN_MODIFY,false);

        // This information is sufficient for most entries tentative(0), confirmed(1) or cancel(2)

        //eventValues.put("visibility", 3);
        // visibility to default (0), confidential(1), private(2), or public(3):

        // eventValues.put("transparency", 0); // You can control whether
        // an event consumes time opaque (0) or transparent (1)

        eventValues.put(CalendarContract.Events.HAS_ALARM, hasAlarm?1:0);

        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE,
                TimeZone.getDefault().getID());


        eventValues.put(CalendarContract.Events.EVENT_COLOR ,color);

        Uri eventUri = cr.insert(CalendarContract.Events.CONTENT_URI, eventValues);
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        if (needReminder) {
            ContentValues reminderValues = new ContentValues();
            reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
            reminderValues.put(CalendarContract.Reminders.MINUTES, reminderMinutes);
            // Default value of the system Minutes is an integer
            reminderValues.put(CalendarContract.Reminders.METHOD,
                    CalendarContract.Reminders.METHOD_ALERT);
            // Alert Methods: Default(0), Alert(1), Email(2),  SMS(3)
            Uri reminderUri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
        }

        if (needMailService && attendees!=null ) {
            for(CalendarEvent.Attendee attendee : attendees){
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Attendees.ATTENDEE_NAME, attendee.getName());
                values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, attendee.getEmail());
                values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP,
                        CalendarContract.Attendees.RELATIONSHIP_ATTENDEE);
                values.put(CalendarContract.Attendees.ATTENDEE_TYPE, CalendarContract.Attendees.TYPE_OPTIONAL);
                values.put(CalendarContract.Attendees.ATTENDEE_STATUS,
                        CalendarContract.Attendees.ATTENDEE_STATUS_INVITED);
                values.put(CalendarContract.Attendees.EVENT_ID, eventID);
                Uri attendeuesesUri = cr.insert(CalendarContract.Attendees.CONTENT_URI, values);
            }
        }
        return eventID;
    }



    private static final String DEBUG_TAG = "AndroidCalendarDao";


    @Override
    public DbTask<List<CalendarEvent>> getCalendarEvents(){

        return new DbTask<>("Android getCalendarEvents",()-> {
            // Specify the date range you want to search for recurring
            // event instances
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2011, 9, 23, 8, 0);
            long startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            endTime.set(2025, 10, 24, 8, 0);
            long endMillis = endTime.getTimeInMillis();
            ContentResolver cr = context.getContentResolver();

            // The ID of the recurring event whose instances you are searching
            // for in the Instances table
            String selection = CalendarContract.Events.CUSTOM_APP_PACKAGE + " != ?";
            String[] selectionArgs = new String[]{context.getApplicationContext().getPackageName()};

            // Construct the query with the desired date range.
            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, startMillis);
            ContentUris.appendId(builder, endMillis);

            // Submit the query
            Cursor cur = cr.query(builder.build(),
                    INSTANCE_PROJECTION,
                    null,
                    null,
                    null);


            List<CalendarEvent> calendarEventList = new ArrayList<>();
            String apppackage = context.getApplicationContext().getPackageName();
            while (cur.moveToNext()) {
                //Get the field values

                if (apppackage.equals(cur.getString(PROJECTION_CUSTOM_APP_PACKAGE_INDEX))) {
                    CalendarEvent e = new CalendarEvent(cur.getLong(PROJECTION_ID_INDEX));//eventID
                    Calendar startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(cur.getLong(PROJECTION_DTSTART_INDEX));
                    e.setDateTime(startTime);
                    e.setTitle(cur.getString(PROJECTION_TITLE_INDEX));
                    e.setDetails(cur.getString(PROJECTION_DESCRIPTION_INDEX));
                    e.setDuration(new CalendarEvent.Duration(cur.getLong(PROJECTION_DURATION_INDEX)));
                    calendarEventList.add(e);

                    //Do something with the values.
                    Log.i(DEBUG_TAG, "Event:  " + e.getTitle());
                    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    Log.i(DEBUG_TAG, "Date: " + formatter.format(e.getDateTime().getTime()));
                }
            }
            return calendarEventList;
        });
    }












    private long getCalendarID(String... searchCalendars){
        long CalendarsIDs [] = new long[searchCalendars.length];
        // Run query - get all available calendars
        ContentResolver cr1 = context.getContentResolver();
        Uri uri1 = CalendarContract.Calendars.CONTENT_URI;
        String selection = "";
        String[] selectionArgs = new String[] {};
        // Submit the query
        Cursor cur1 = cr1.query(uri1, EVENT_PROJECTION, selection, selectionArgs, null);

        while (cur1.moveToNext()) {
            long calID  = cur1.getLong(PROJECTION_ID_INDEX);
            String displayName = cur1.getString(PROJECTION_DISPLAY_NAME_INDEX);
            String accountName = cur1.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            String ownerName = cur1.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            for(int i =0 ; i<searchCalendars.length ;i++){
                String search = searchCalendars[i].toLowerCase();
                if(  CalendarsIDs[i] == 0 && (displayName.toLowerCase().contains(search) ||
                        accountName.toLowerCase().contains(search) ||
                        ownerName.toLowerCase().contains(search))) {
                    CalendarsIDs[i] = calID;
                    break;
                }
            }
        }
        for (int i = 0 ; i<searchCalendars.length;i++){
            if(CalendarsIDs[i]>0)
                return CalendarsIDs[i];
        }
        return 1 ;//default calendar
    }




    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;
    private static final int PROJECTION_DESCRIPTION_INDEX = 3;
    private static final int PROJECTION_DISPLAY_COLOR_INDEX = 4;
    private static final int PROJECTION_DTSTART_INDEX = 5;
    private static final int PROJECTION_DURATION_INDEX = 6;
    private static final int PROJECTION_CUSTOM_APP_PACKAGE_INDEX = 7;

    // The indices for the projection array above.
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


    static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    public static final String[] INSTANCE_PROJECTION = new String[] {
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.TITLE,         // 2
            CalendarContract.Instances.DESCRIPTION,   // 3
            CalendarContract.Instances.DISPLAY_COLOR, // 4
            CalendarContract.Instances.DTSTART,       // 5
            CalendarContract.Instances.DURATION,      // 6
            CalendarContract.Instances.CUSTOM_APP_PACKAGE,      // 7


    };


}
