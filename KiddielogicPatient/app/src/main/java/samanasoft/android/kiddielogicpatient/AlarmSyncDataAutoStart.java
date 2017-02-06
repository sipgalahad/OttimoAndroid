package samanasoft.android.kiddielogicpatient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmSyncDataAutoStart extends BroadcastReceiver {
    AlarmSyncDataHelper alarm = new AlarmSyncDataHelper();

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        Log.d("Test Alarm", "AlarmSyncDataAutoStart.onReceive() called");
        alarm.setAlarm(ctx);
    }

}
