package samanasoft.android.kiddielogicpatient;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

public class AlarmNotificationService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String filterExpression = String.format("GCAppointmentStatus = '%1$s' AND ReminderDate LIKE '%2$s%%'", Constant.AppointmentStatus.OPEN, DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB));
        List<DataLayer.vAppointment> lstAppointment = BusinessLayer.getvAppointmentList(context, filterExpression);
        Log.d("Test", "Total Appointment : " + lstAppointment.size());
        Log.d("Test", "Filter : " + filterExpression);
        if(lstAppointment.size() > 0) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle("Kiddielogic")
                            .setContentText("Appointment Reminder");
            Intent resultIntent = new Intent(context, MessageCenterActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(001, notification);
        }
    }
}
