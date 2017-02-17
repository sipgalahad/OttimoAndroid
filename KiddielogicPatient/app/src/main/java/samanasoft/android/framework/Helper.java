package samanasoft.android.framework;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

public class Helper {
	/**count Age
	 *  
	 * @param type : 1 => day; 2 => month; 3 => year
	 * @return age
	 */
	public static int getAge(DateTime dateOfBirth, DateTime nowDate, int type){		
		int day = nowDate.Day - dateOfBirth.Day;
        int month = nowDate.Month - dateOfBirth.Month;
        int year = nowDate.Year - dateOfBirth.Year;
        
        if (day < 0) {
            day = day + 30;
            month--;
        }
        if (month < 0) {
            month += 12;
            year--;
        }
        
        switch (type) {
			case 1:return day;
			case 2:return month;
			default:return year;
		}
	}

    public static boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static void updateAppointmentFromEventCalender(Context ctx, DataLayer.Appointment entity) {
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        String[] temp = entity.StartTime.split(":");

        int startHour = Integer.parseInt(temp[0]);
        int startMinute = Integer.parseInt(temp[1]);
        beginTime.set(entity.StartDate.Year, entity.StartDate.Month - 1, entity.StartDate.Day, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        if (entity.EndTime != "")
            temp = entity.EndTime.split(":");
        endTime.set(entity.StartDate.Year, entity.StartDate.Month - 1, entity.StartDate.Day, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
        endMillis = endTime.getTimeInMillis();

        //List<String> lstCalendarID = getCalendar(getBaseContext());
        //for(String calID : lstCalendarID) {
        List<DataLayer.AppointmentCalendarEvent> lstEvent = BusinessLayer.getAppointmentCalendarEventList(ctx, String.format("AppointmentID = %1$s", entity.AppointmentID));
        for(DataLayer.AppointmentCalendarEvent event : lstEvent) {
            ContentResolver cr = ctx.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "Visit Ke KiddieCare Centre");
            values.put(CalendarContract.Events.DESCRIPTION, entity.VisitTypeName + " ke " + entity.ParamedicName + " (Harap Konfirmasi / Cancel Melalui Aplikasi Kiddielogic Sehari Sebelumnya)");
            values.put(CalendarContract.Events.CALENDAR_ID, 1);

            values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC/GMT +7:00");
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.CalendarEventID);
            ctx.getContentResolver().update(updateUri, values, null, null);

            DateTime dt = DateTime.now();
            int diffHour = dt.Hour - startHour;
            int diffMinute = dt.Minute - startMinute;
            int diff = (diffHour * 60) + diffMinute;
            setReminder(cr, event.CalendarEventID, 1440 + diff);
        }
    }
    public static void deleteAppointmentFromEventCalender(Context ctx, DataLayer.Appointment entity) {
        Uri deleteUri = null;

        List<DataLayer.AppointmentCalendarEvent> lstEvent = BusinessLayer.getAppointmentCalendarEventList(ctx, String.format("AppointmentID = %1$s", entity.AppointmentID));
        for(DataLayer.AppointmentCalendarEvent event : lstEvent) {
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.CalendarEventID);
            ctx.getContentResolver().delete(deleteUri, null, null);
            BusinessLayer.deleteAppointmentCalendarEvent(ctx, event.AppointmentID, event.CalendarEventID);
        }
    }

    public static void insertAppointmentToEventCalender(Context ctx, DataLayer.Appointment entity){

        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        String[] temp = entity.StartTime.split(":");

        int startHour = Integer.parseInt(temp[0]);
        int startMinute = Integer.parseInt(temp[1]);
        beginTime.set(entity.StartDate.Year, entity.StartDate.Month - 1, entity.StartDate.Day, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        if (entity.EndTime != "")
            temp = entity.EndTime.split(":");
        endTime.set(entity.StartDate.Year, entity.StartDate.Month - 1, entity.StartDate.Day, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
        endMillis = endTime.getTimeInMillis();

        //List<String> lstCalendarID = getCalendar(getBaseContext());
        //for(String calID : lstCalendarID) {
        ContentResolver cr = ctx.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Visit Ke KiddieCare Centre");
        values.put(CalendarContract.Events.DESCRIPTION, entity.VisitTypeName + " ke " + entity.ParamedicName + " (Harap Konfirmasi / Cancel Melalui Aplikasi Kiddielogic Sehari Sebelumnya)");
        values.put(CalendarContract.Events.CALENDAR_ID, 1);

        values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC/GMT +7:00");
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        DataLayer.AppointmentCalendarEvent entityEvent = new DataLayer.AppointmentCalendarEvent();
        entityEvent.AppointmentID = entity.AppointmentID;
        entityEvent.CalendarEventID = Long.parseLong(uri.getLastPathSegment());
        BusinessLayer.insertAppointmentCalendarEvent(ctx, entityEvent);

        DateTime dt = DateTime.now();
        int diffHour = dt.Hour - startHour;
        int diffMinute = dt.Minute - startMinute;
        int diff = (diffHour * 60) + diffMinute;
        setReminder(cr, entityEvent.CalendarEventID, 1440 + diff);
    }

    // routine to add reminders with the event
    public static void setReminder(ContentResolver cr, long eventID, int timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
            Cursor c = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES});
            if (c.moveToFirst()) {
                System.out.println("calendar"
                        + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
