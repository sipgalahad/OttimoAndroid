package samanasoft.android.kiddielogicpatient;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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

public class AlarmNotificationService extends Service {
    private static final String APP_TAG = "com.hascode.android.scheduler";
    private Context context = null;
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        List<DataLayer.vAppointment> lstAppointment = BusinessLayer.getvAppointmentList(this, String.format("GCAppointmentStatus = '%1$s' AND StartDate LIKE '%2$s%%'", Constant.AppointmentStatus.OPEN, DateTime.tomorrow().toString(Constant.FormatString.DATE_FORMAT_DB)));
        Log.d("Test", "Total Appointment : " + lstAppointment.size());
        if(lstAppointment.size() > 0) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle("Kiddielogic")
                            .setContentText("Appointment Reminder");
            Intent resultIntent = new Intent(this, MessageCenterActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(001, mBuilder.build());
        }
        return Service.START_NOT_STICKY;
    }

}
