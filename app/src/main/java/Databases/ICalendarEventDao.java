package Databases;

import java.util.List;

import Databases.util.DbTask;
import Models.CalendarEvents.CalendarEvent;

public interface ICalendarEventDao {

    DbTask<Long> pushCalendarEvent(CalendarEvent calendarEvent);

    DbTask<List<CalendarEvent>> getCalendarEvents();

}
