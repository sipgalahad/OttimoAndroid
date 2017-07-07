package samanasoft.android.framework;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import samanasoft.android.framework.webservice.WebServiceResponse;
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
            values.put(CalendarContract.Events.DESCRIPTION, entity.VisitTypeName + " ke " + entity.ParamedicName + " (Harap Konfirmasi / Cancel Melalui Aplikasi KiddieApps Paling Lambat Sehari Sebelumnya)");
            values.put(CalendarContract.Events.CALENDAR_ID, 1);

            values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC/GMT +7:00");
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.CalendarEventID);
            ctx.getContentResolver().update(updateUri, values, null, null);

            //DateTime dt = DateTime.now();
            //int diffHour = dt.Hour - startHour;
            //int diffMinute = dt.Minute - startMinute;
            //int diff = (diffHour * 60) + diffMinute;
            //setReminder(cr, event.CalendarEventID, 1440 + diff);
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
        Integer MRN = entity.MRN;
        try {
            long startMillis = 0;
            long endMillis = 0;
            Calendar beginTime = Calendar.getInstance();
            String[] temp = entity.StartTime.split(":");
            Log.d("test Insert Date", entity.StartDate.toString(Constant.FormatString.DATE_FORMAT));

            int startHour = Integer.parseInt(temp[0]);
            int startMinute = Integer.parseInt(temp[1]);
            beginTime.set(entity.StartDate.Year, entity.StartDate.Month - 1, entity.StartDate.Day, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
            startMillis = beginTime.getTimeInMillis();
            Calendar endTime = Calendar.getInstance();
            if (entity.EndTime != "")
                temp = entity.EndTime.split(":");
            endTime.set(entity.StartDate.Year, entity.StartDate.Month - 1, entity.StartDate.Day, Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
            endMillis = endTime.getTimeInMillis();

            List<String> lstCalendarID = getCalendar(ctx);
            String calID = lstCalendarID.get(0);
            //for(String calID : lstCalendarID) {
            ContentResolver cr = ctx.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "Visit Ke KiddieCare Centre");
            values.put(CalendarContract.Events.DESCRIPTION, entity.VisitTypeName + " ke " + entity.ParamedicName + " (Harap Konfirmasi / Cancel Melalui Aplikasi Kiddielogic Sehari Sebelumnya)");
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            //values.put(CalendarContract.Events.CALENDAR_ID, 1);

            values.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC/GMT +7:00");
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            /*if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);}else if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                //Uri uri = cr.insert(Uri.parse("content://com.android.calendar/events"), values);
            }*/
            Uri calendars = Uri.parse("content://com.android.calendar/events");
            //Uri calendars = Uri.parse("content://com.android.calendar/calendars");
            //Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            Uri uri = cr.insert(calendars, values);
            DataLayer.AppointmentCalendarEvent entityEvent = new DataLayer.AppointmentCalendarEvent();
            entityEvent.AppointmentID = entity.AppointmentID;
            entityEvent.CalendarEventID = Long.parseLong(uri.getLastPathSegment());
            BusinessLayer.insertAppointmentCalendarEvent(ctx, entityEvent);
            //}

            Calendar startDate = Calendar.getInstance();
            startDate.set(Calendar.DAY_OF_MONTH, entity.StartDate.Day);
            startDate.set(Calendar.MONTH, entity.StartDate.Month - 1); // 0-11 so 1 less
            startDate.set(Calendar.YEAR, entity.StartDate.Year);

            /*Calendar reminderDate = Calendar.getInstance();
            reminderDate.set(Calendar.DAY_OF_MONTH, entity.ReminderDate.Day);
            reminderDate.set(Calendar.MONTH, entity.ReminderDate.Month - 1); // 0-11 so 1 less
            reminderDate.set(Calendar.YEAR, entity.ReminderDate.Year);

            long diffDays = startDate.getTimeInMillis() - reminderDate.getTimeInMillis();
            long days = diffDays / (24 * 60 * 60 * 1000);
            int diffHour = startHour - 8;
            int diffMinute = startMinute - 0;
            int diff = (diffHour * 60) + diffMinute;
            setReminder(cr, entityEvent.CalendarEventID, (1440 * days) + diff);*/
        }
        catch (Exception ex){
            String stackTrace = ex.getStackTrace().toString();
            String message = ex.getMessage();
            String deviceID = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
            new InsertErrorLog(ctx, MRN, deviceID, message, stackTrace).execute((Void) null);
            ex.printStackTrace();
        }
    }

    public static void CopyReadAssets(Context context)
    {
        AssetManager assetManager = context.getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(context.getFilesDir(), "app_help.pdf");
        try
        {
            in = assetManager.open("app_help.pdf");
            out = context.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + context.getFilesDir() + "/app_help.pdf"),
                "application/pdf");

        context.startActivity(intent);
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }
    public static class InsertErrorLog extends AsyncTask<Void, Void, WebServiceResponse> {

        private final Integer MRN;
        private final String deviceID;
        private final String errorMessage;
        private final String stackTrace;
        private final Context ctx;

        public InsertErrorLog(Context mCtx, Integer mrn, String mDeviceID, String mErrorMessage, String mStackTrace) {
            MRN = mrn;
            deviceID = mDeviceID;
            errorMessage = mErrorMessage;
            stackTrace = mStackTrace;
            ctx = mCtx;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            try {
                WebServiceResponse result = BusinessLayer.insertErrorLog(ctx, MRN, deviceID, errorMessage, stackTrace);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(ctx, "Insert Error Log", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
        }

        @Override
        protected void onCancelled() {
        }
    }

    private static List<String> getCalendar(Context c) {
        List<String> lstCal = new ArrayList<>();
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = c.getContentResolver();
        //Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

        Cursor managedCursor = c.getContentResolver().query(calendars, projection, CalendarContract.Calendars.VISIBLE + " = 1 AND "  + CalendarContract.Calendars.IS_PRIMARY + "=1", null, CalendarContract.Calendars._ID + " ASC");
        if(managedCursor.getCount() <= 0){
            managedCursor = c.getContentResolver().query(calendars, projection, CalendarContract.Calendars.VISIBLE + " = 1", null, CalendarContract.Calendars._ID + " ASC");
        }

        if (managedCursor.moveToFirst()){
            String calID;
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calID = managedCursor.getString(idCol);
                lstCal.add(calID);
            } while(managedCursor.moveToNext());
            managedCursor.close();
        }
        return lstCal;

    }

    // routine to add reminders with the event
    public static void setReminder(ContentResolver cr, long eventID, long timeBefore) {
        try {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, timeBefore);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

            Uri uri = Uri.parse("content://com.android.calendar/reminders");
            //Uri uri = Uri.parse("content://com.android.calendar/events");
            cr.insert(uri, values);
            //Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
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
