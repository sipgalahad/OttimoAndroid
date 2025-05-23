package samanasoft.android.ottimo.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;

import samanasoft.android.ottimosupport.SchedulerEventReceiver;

public class AlarmHelper {
    public static boolean isAlarmExist(Context context){
        Intent i = new Intent(context, SchedulerEventReceiver.class); // explicit intent
        return (PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static void startAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, SchedulerEventReceiver.class); // explicit intent
        PendingIntent intentExecuted = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(intentExecuted);

        SharedPreferences prefs = context.getSharedPreferences(Constant.SharedPreference.NAME, context.MODE_PRIVATE);
        String value = prefs.getString(Constant.SharedPreference.APPOINTMENT_REMINDER_TIME, "");
        Log.d("test", "Value : " + value);
        if (value != "") {
            String[] temp = value.split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Convert.ObjectToInt(temp[0]));
            calendar.set(Calendar.MINUTE, Convert.ObjectToInt(temp[1]));
            calendar.set(Calendar.SECOND, 0);

            Log.d("test", "1");
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), AlarmManager.INTERVAL_DAY, intentExecuted);
            //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            //        calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, intentExecuted);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, intentExecuted);

            //int delay = 10;
            //Calendar now = Calendar.getInstance();
            //now.add(Calendar.SECOND, delay);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), 1000 * delay, intentExecuted);
        } else
            intentExecuted.cancel();
    }
}
