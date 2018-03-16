package samanasoft.android.kiddielogicparamedicalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import samanasoft.android.framework.DateTime;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.common.Convert;

/**
 * Created by DEV_ARI on 2/6/2017.
 */
public class AlarmNotificationHelper
{
    public static boolean isAlarmExist(Context context){
        Intent i = new Intent(context, AlarmNotificationService.class); // explicit intent
        return (PendingIntent.getBroadcast(context, 1, i, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public void setAlarm(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, AlarmNotificationService.class);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 1, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(contentIntent);

        //SharedPreferences prefs = context.getSharedPreferences(Constant.SharedPreference.NAME, context.MODE_PRIVATE);
        String value = "08:00";
        Log.d("test", "Value : " + value);
        if (value != "") {
            String[] temp = value.split(":");
            Calendar calendar = Calendar.getInstance();
            Calendar todayDate = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Convert.ObjectToInt(temp[0]));
            calendar.set(Calendar.MINUTE, Convert.ObjectToInt(temp[1]));
            calendar.set(Calendar.SECOND, 0);

            long mToday = todayDate.getTimeInMillis();
            long mCalendar = calendar.getTimeInMillis();

            Log.d("test", "1");
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), AlarmManager.INTERVAL_DAY, intentExecuted);
            //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            //        calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, intentExecuted);
            if(mToday > mCalendar) {
                //context.sendBroadcast(new Intent("samanasoft.android.kiddielogicpatient.intent.action.NOTIFICATION_SERVICE"));
                SharedPreferences prefs = context.getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, context.MODE_PRIVATE);
                String lastNotificationDate = prefs.getString(Constant.SharedPreference.LAST_NOTIFICATION_DATE, "");
                if(!lastNotificationDate.equals(DateTime.now().toString("yyyyMMdd"))) {
                    Intent notificationIntent2 = new Intent(context.getApplicationContext(), AlarmNotificationService.class);
                    PendingIntent pendingIntent = PendingIntent.getService(
                            context.getApplicationContext(), 123, notificationIntent2, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000,
                            pendingIntent);
                }

                calendar.add(Calendar.DATE, 1);
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, contentIntent);
            //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
            //        10 * 60 * 1000, contentIntent);
        } else
            contentIntent.cancel();
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmNotificationHelper.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 1, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
