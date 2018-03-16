package samanasoft.android.kiddielogicparamedicalarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AlarmStartService extends Service {
    public AlarmStartService() {
    }
    private static final String TAG = "AlarmStartService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        //Notification notification = new Notification();
        //startForeground(42, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(), "On Start", Toast.LENGTH_SHORT).show();
        /*if (!AlarmNotificationHelper.isAlarmExist(getApplicationContext())) {
            AlarmNotificationHelper alarm = new AlarmNotificationHelper();
            alarm.setAlarm(getApplicationContext());
        }*/
        if (!AlarmSyncDataHelper.isAlarmExist(getApplicationContext())) {
            AlarmSyncDataHelper alarm2 = new AlarmSyncDataHelper();
            alarm2.setAlarm(getApplicationContext());
        }
        startService(new Intent(getApplicationContext(), FCMMessagingService.class));
        /*if (!AlarmManagerService.isAlarmExist(getApplicationContext())) {
            AlarmManagerService alarm3 = new AlarmManagerService();
            alarm3.setAlarm(getApplicationContext());
        }*/
        /*Cursor cursor = getContentResolver().query(   ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
        while (cursor.moveToNext()) {
            String name =cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        AlarmRestartHelper alarm4 = new AlarmRestartHelper();
        alarm4.setAlarm(getApplicationContext());
*/
        //startService();
        return START_STICKY;
    }

    /*private static Timer timer = new Timer();
    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 1000 * 60 * 15);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            toastHandler.sendEmptyMessage(0);
        }
    }private final Handler toastHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (!AlarmSyncDataHelper.isAlarmExist(getApplicationContext())) {
                AlarmSyncDataHelper alarm2 = new AlarmSyncDataHelper();
                alarm2.setAlarm(getApplicationContext());
            }
            startService(new Intent(getApplicationContext(), FCMMessagingService.class));
        }
    };*/

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        sendBroadcast(new Intent("samanasoft.android.kiddielogicpatientalarm.intent.action.RESTART_SERVICE"));
        Toast.makeText(getApplicationContext(), "On Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        /*Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 6, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
        */
        Log.e(TAG, "onTaskRemoved");
        sendBroadcast(new Intent("samanasoft.android.kiddielogicpatientalarm.intent.action.RESTART_SERVICE"));
        Toast.makeText(getApplicationContext(), "On Task Removed", Toast.LENGTH_SHORT).show();
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
