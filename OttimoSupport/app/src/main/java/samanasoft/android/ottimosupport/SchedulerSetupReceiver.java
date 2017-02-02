package samanasoft.android.ottimosupport;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import samanasoft.android.ottimo.common.AlarmHelper;

public class SchedulerSetupReceiver extends BroadcastReceiver {
    private static final String APP_TAG = "com.hascode.android";

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        Log.d(APP_TAG, "SchedulerSetupReceiver.onReceive() called");
        AlarmHelper.startAlarm(ctx);
    }

}
