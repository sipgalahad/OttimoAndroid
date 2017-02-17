package samanasoft.android.kiddielogicpatient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmAutoStart extends BroadcastReceiver {
    AlarmSyncDataHelper alarm = new AlarmSyncDataHelper();
    AlarmNotificationHelper alarm2 = new AlarmNotificationHelper();

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        Log.d("Test Alarm", "AlarmAutoStart.onReceive() called");
        if(!AlarmSyncDataHelper.isAlarmExist(ctx))
            alarm.setAlarm(ctx);
        if(!AlarmNotificationHelper.isAlarmExist(ctx))
            alarm2.setAlarm(ctx);
    }
}
