package samanasoft.android.kiddielogicparamedicalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

/**
 * Created by DEV_ARI on 5/8/2017.
 */
public class FCMMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMMessagingService";
    String type = "";
    String deviceID = "";
    Integer appointmentID = 0;
    Integer labResultID = 0;
    Integer announcementID = 0;
    String AnnouncementType = "";
    String AnnouncementTitle = "";
    Integer MRN = 0;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        if(remoteMessage.getData().size() > 0) {
            Map<String, String> lstData = remoteMessage.getData();
            type = lstData.get("type").toString();
            /*Handler handler2 = new Handler(Looper.getMainLooper());
            handler2.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Test : " + type, Toast.LENGTH_SHORT).show();
                }
            });*/
            Log.d(TAG, "Type: " + type);
            /*if (type.equals("AppReminder")) {
                String message = lstData.get("message").toString();
                appointmentID = Integer.parseInt(lstData.get("appointmentID"));
                DataLayer.Appointment entityAppointment = BusinessLayer.getAppointment(getBaseContext(), appointmentID);
                entityAppointment.GCAppointmentStatus = samanasoft.android.framework.Constant.AppointmentStatus.SEND_CONFIRMATION;
                BusinessLayer.updateAppointment(getBaseContext(), entityAppointment);
                sendNotification(message);

                deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    new SendMessageTask(deviceID, appointmentID, samanasoft.android.framework.Constant.AppointmentStatus.SEND_CONFIRMATION).execute((Void) null);
                    }
                });

            } else if (type.equals("NewAppsVersion")) {
                String version = lstData.get("AppsVersion").toString();
                SharedPreferences prefs = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constant.SharedPreference.SERVER_APPS_VERSION, version);
                editor.commit();
            } else if (type.equals("SyncApp")) {
                Intent intentSyncData = new Intent(getApplicationContext(), AlarmSyncDataService.class);
                startService(intentSyncData);
            } else if (type.equals("LabResult")) {
                MRN = Integer.parseInt(lstData.get("MRN"));
                labResultID = Integer.parseInt(lstData.get("labResultID"));

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new LoadLabResultTask(labResultID).execute((Void) null);
                    }
                });
                /*LaboratoryResultHd entityLab = BusinessLayer.getLaboratoryResultHd(getApplicationContext(), labResultID);
                if(entityLab == null) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new LoadLabResultTask(labResultID).execute((Void) null);
                        }
                    });
                }
                else{
                    sendLabResultNotification();
                }*/
            /*} else if (type.equals("Announcement")) {
                List<DataLayer.Patient> lstPatient = BusinessLayer.getPatientList(getBaseContext(), "");
                if(lstPatient.size() > 0) {
                    MRN = lstPatient.get(0).MRN;
                    announcementID = Integer.parseInt(lstData.get("announcementID"));

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new LoadAnnouncementTask(announcementID).execute((Void) null);
                        }
                    });
                }
            }*/
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class WebServiceResponsePatient {
        public DateTime timestamp;
        public List<?> returnObjLabResultHd;
        public List<?> returnObjLabResultDt;
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        /*Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Push Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
*/
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Appointment Reminder")
                        .setContentText(messageBody);
        //Intent resultIntent = new Intent(this, NotificationOpenerActivity.class);
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("mrn", MRN);
        i.putExtra("isGotoMessageCenter", true);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        3,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendLabResultNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Laboratory Result");
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("mrn", MRN);
        i.putExtra("labresultid", labResultID);
        i.putExtra("isGoToLabResult", true);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        3,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendAnnouncementNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(AnnouncementType)
                        .setContentText(AnnouncementTitle);
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("mrn", MRN);
        i.putExtra("announcementid", announcementID);
        i.putExtra("isGoToAnnouncement", true);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        3,
                        i,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
