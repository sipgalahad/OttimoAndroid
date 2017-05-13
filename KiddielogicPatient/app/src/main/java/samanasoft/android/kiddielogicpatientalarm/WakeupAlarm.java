package samanasoft.android.kiddielogicpatientalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import samanasoft.android.ottimo.common.Convert;

public class WakeupAlarm {
    public static boolean isAlarmExist(Context context){
        Intent i = new Intent(context, WakeupBroadcast.class); // explicit intent
        return (PendingIntent.getBroadcast(context, 2, i, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public void setAlarm(Context context)
    {
        /*AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, WakeupBroadcast.class);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context, 2, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(contentIntent);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 1000 * 60 * 60, contentIntent);*/
    }
}
