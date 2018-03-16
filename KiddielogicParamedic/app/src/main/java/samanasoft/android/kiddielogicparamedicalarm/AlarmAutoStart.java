package samanasoft.android.kiddielogicparamedicalarm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmAutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        Log.d("Test Alarm", "AlarmAutoStart.onReceive() called");
        //Toast.makeText(ctx.getApplicationContext(), "Alarm Auto Start", Toast.LENGTH_SHORT).show();
        //ctx.getApplicationContext().startService(new Intent(ctx.getApplicationContext(), AlarmStartService.class));

        /*if (!AlarmNotificationHelper.isAlarmExist(ctx.getApplicationContext())) {
            AlarmNotificationHelper alarm = new AlarmNotificationHelper();
            alarm.setAlarm(ctx.getApplicationContext());
        }*/
        if (!AlarmSyncDataHelper.isAlarmExist(ctx.getApplicationContext())) {
            AlarmSyncDataHelper alarm2 = new AlarmSyncDataHelper();
            alarm2.setAlarm(ctx.getApplicationContext());
        }
    }
}
