package samanasoft.android.kiddielogicpatientalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import samanasoft.android.ottimo.common.Convert;

public class AlarmSyncDataHelper {
    public static boolean isAlarmExist(Context context){
        Intent i = new Intent(context, AlarmSyncDataService.class); // explicit intent
        return (PendingIntent.getService(context, 2, i, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public void setAlarm(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, AlarmSyncDataService.class);
        PendingIntent contentIntent = PendingIntent.getService(context, 2, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(contentIntent);

        //SharedPreferences prefs = context.getSharedPreferences(Constant.SharedPreference.NAME, context.MODE_PRIVATE);
        String value = "03:00";
        //if(DateTime.now().Hour == 3)
        //    value = "15:00";
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
                //if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
                //    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), contentIntent);
                //else
                //    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), contentIntent);
                value = "15:00";
                temp = value.split(":");
                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR_OF_DAY, Convert.ObjectToInt(temp[0]));
                calendar2.set(Calendar.MINUTE, Convert.ObjectToInt(temp[1]));
                calendar2.set(Calendar.SECOND, 0);

                mToday = todayDate.getTimeInMillis();
                mCalendar = calendar2.getTimeInMillis();
                if(mToday > mCalendar) {
                    context.getApplicationContext().startService(new Intent(context.getApplicationContext(), AlarmSyncDataService.class));
                    calendar.add(Calendar.DATE, 1);
                }
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_HALF_DAY, contentIntent);
        } else
            contentIntent.cancel();
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmNotificationHelper.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
