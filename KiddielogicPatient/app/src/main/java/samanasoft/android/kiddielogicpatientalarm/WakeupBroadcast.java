package samanasoft.android.kiddielogicpatientalarm;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

//public class WakeupBroadcast extends WakefulBroadcastReceiver {
public class WakeupBroadcast extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent){
        //startWakefulService(context.getApplicationContext(), new Intent(context.getApplicationContext(), AlarmStartService.class));
        //startWakefulService(context.getApplicationContext(), new Intent(context.getApplicationContext(), FCMMessagingService.class));
        /*context.startService(new Intent(context.getApplicationContext(), AlarmStartService.class));
        context.startService(new Intent(context.getApplicationContext(), FCMMessagingService.class));
        WakeupAlarm alarm = new WakeupAlarm();
        alarm.setAlarm(context);*/
    }

}
