package samanasoft.android.kiddielogicparamedicalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartServiceReceiver extends BroadcastReceiver {
    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        context.startService(new Intent(context.getApplicationContext(), AlarmStartService.class));
        context.startService(new Intent(context.getApplicationContext(), FCMMessagingService.class));

        //Intent i = new Intent(context, DBInitActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //i.putExtra("isAutoClose", "1");
        //context.startActivity(i);
        //finish();
        //context.startService(new Intent(context.getApplicationContext(), TimerService.class));

        /*if (!AlarmNotificationHelper.isAlarmExist(context.getApplicationContext())) {
            AlarmNotificationHelper alarm = new AlarmNotificationHelper();
            alarm.setAlarm(context.getApplicationContext());
        }*/
        if (!AlarmSyncDataHelper.isAlarmExist(context.getApplicationContext())) {
            AlarmSyncDataHelper alarm2 = new AlarmSyncDataHelper();
            alarm2.setAlarm(context.getApplicationContext());
        }

    }
}
