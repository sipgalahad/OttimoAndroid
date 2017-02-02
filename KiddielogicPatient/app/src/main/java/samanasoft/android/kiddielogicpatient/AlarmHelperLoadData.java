package samanasoft.android.kiddielogicpatient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import samanasoft.android.ottimo.common.Convert;

public class AlarmHelperLoadData {
    public static boolean isAlarmExist(Context context){
        Intent i = new Intent(context, SchedulerEventReceiver.class); // explicit intent
        return (PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static void startAlarm(Context context) {
        //AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Intent i = new Intent(context, SchedulerEventReceiver.class); // explicit intent

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, SchedulerEventLoadDataService.class);
        PendingIntent contentIntent = PendingIntent.getService(context, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(contentIntent);

        //SharedPreferences prefs = context.getSharedPreferences(Constant.SharedPreference.NAME, context.MODE_PRIVATE);
        String value = "03:00";
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
                    AlarmManager.INTERVAL_HALF_DAY, contentIntent);

            //int delay = 10;
            //Calendar now = Calendar.getInstance();
            //now.add(Calendar.SECOND, delay);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), 1000 * delay, intentExecuted);
        } else
            contentIntent.cancel();
    }
}
