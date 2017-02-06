package samanasoft.android.kiddielogicpatient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by DEV_ARI on 2/6/2017.
 */
public class AlarmNotificationAutoStart extends BroadcastReceiver
{
    AlarmNotificationHelper alarm = new AlarmNotificationHelper();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("Test Alarm", "AlarmNotificationAutoStart.onReceive() called");
        alarm.setAlarm(context);
    }
}
