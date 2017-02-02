package samanasoft.android.kiddielogicpatient;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SchedulerEventLoadDataReceiver extends BroadcastReceiver {
    private static final String APP_TAG = "com.hascode.android";

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        Log.d(APP_TAG, "SchedulerEventLoadDataReceiver.onReceive() called");
        Intent eventService = new Intent(ctx, SchedulerEventLoadDataService.class);
        ctx.startService(eventService);
    }
}

