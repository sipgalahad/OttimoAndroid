package samanasoft.android.kiddielogicpatientalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
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
import samanasoft.android.ottimo.dal.DataLayer.LaboratoryResultHd;

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
            if (type.equals("AppReminder")) {
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
            } else if (type.equals("Announcement")) {
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
            }
        }
    }


    public class LoadAnnouncementTask extends AsyncTask<Void, Void, WebServiceResponse> {
        private Integer announcementID;
        LoadAnnouncementTask(Integer announcementID1) {
            this.announcementID = announcementID1;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            String filterExpression = String.format("AnnouncementID = '%1$s'", announcementID);
            Log.d("filterExpression", filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListAnnouncement(getBaseContext(), filterExpression);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Announcement Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            if(result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            }
            else if(result.returnObj != null){
                DataLayer.Announcement oldAnnouncement = BusinessLayer.getAnnouncement(getBaseContext(), announcementID);
                if(oldAnnouncement != null)
                    BusinessLayer.deleteAnnouncement(getBaseContext(), oldAnnouncement.AnnouncementID);

                @SuppressWarnings("unchecked")
                List<DataLayer.Announcement> lstAnnouncement = (List<DataLayer.Announcement>) result.returnObj;
                for (DataLayer.Announcement entity : lstAnnouncement) {
                    AnnouncementType = entity.AnnouncementType;
                    AnnouncementTitle = entity.Title;
                    BusinessLayer.insertAnnouncement(getBaseContext(), entity);
                }
            }
            sendAnnouncementNotification();
        }

        @Override
        protected void onCancelled() {
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    public class LoadLabResultTask extends AsyncTask<Void, Void, WebServiceResponsePatient> {
        private Integer labResultID;
        LoadLabResultTask(Integer labResultID1) {
            this.labResultID = labResultID1;
        }

        @Override
        protected WebServiceResponsePatient doInBackground(Void... params) {
            try {
                WebServiceResponsePatient result = SyncLabResultPerID(getBaseContext(), labResultID);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get LabResult Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponsePatient result) {
            Log.d(TAG, "Masuk kok");
            if (result == null) {
                Log.d(TAG, "a");
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "b");
                if (result.returnObjLabResultHd != null) {
                    @SuppressWarnings("unchecked")
                    List<DataLayer.LaboratoryResultHd> lstEntity = (List<DataLayer.LaboratoryResultHd>) result.returnObjLabResultHd;

                    String lstID = "";
                    for (DataLayer.LaboratoryResultHd entity : lstEntity) {
                        if (!lstID.equals(""))
                            lstID += ",";
                        lstID += entity.ID;
                    }

                    if (!lstID.equals("")) {
                        List<DataLayer.LaboratoryResultHd> lstOldEntity = BusinessLayer.getLaboratoryResultHdList(getBaseContext(), String.format("ID IN (%1$s)", lstID));
                        for (DataLayer.LaboratoryResultHd entity : lstOldEntity) {
                            BusinessLayer.deleteLaboratoryResultHd(getBaseContext(), entity.ID);
                        }

                        for (DataLayer.LaboratoryResultHd entity : lstEntity) {
                            BusinessLayer.insertLaboratoryResultHd(getBaseContext(), entity);
                        }
                    }
                }
                if (result.returnObjLabResultDt != null) {
                    @SuppressWarnings("unchecked")
                    List<DataLayer.LaboratoryResultDt> lstEntity = (List<DataLayer.LaboratoryResultDt>) result.returnObjLabResultDt;

                    String lstID = "";
                    for (DataLayer.LaboratoryResultDt entity : lstEntity) {
                        if (!lstID.equals(""))
                            lstID += ",";
                        lstID += entity.LaboratoryResultDtID;
                    }

                    if (!lstID.equals("")) {
                        List<DataLayer.LaboratoryResultDt> lstOldEntity = BusinessLayer.getLaboratoryResultDtList(getBaseContext(), String.format("LaboratoryResultDtID IN (%1$s)", lstID));
                        for (DataLayer.LaboratoryResultDt entity : lstOldEntity) {
                            BusinessLayer.deleteLaboratoryResultDt(getBaseContext(), entity.LaboratoryResultDtID);
                        }

                        for (DataLayer.LaboratoryResultDt entity : lstEntity) {
                            BusinessLayer.insertLaboratoryResultDt(getBaseContext(), entity);
                        }
                    }
                }
                sendLabResultNotification();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
    //endregion


    public WebServiceResponsePatient SyncLabResultPerID(Context context, Integer LabResultID){
        WebServiceResponsePatient result = new WebServiceResponsePatient();
        try {
            JSONObject response = WebServiceHelper.SyncLabResultPerID(context, LabResultID);

            JSONArray returnObjLabResultHd = WebServiceHelper.getCustomReturnObject(response, "ReturnObjLabResultHd");
            JSONArray returnObjLabResultDt = WebServiceHelper.getCustomReturnObject(response, "ReturnObjLabResultDt");
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.LaboratoryResultHd> lst4 = new ArrayList<DataLayer.LaboratoryResultHd>();
            for (int i = 0; i < returnObjLabResultHd.length();++i){
                JSONObject row = (JSONObject) returnObjLabResultHd.get(i);
                lst4.add((DataLayer.LaboratoryResultHd)WebServiceHelper.JSONObjectToObject(row, new DataLayer.LaboratoryResultHd()));
            }
            List<DataLayer.LaboratoryResultDt> lst5 = new ArrayList<DataLayer.LaboratoryResultDt>();
            for (int i = 0; i < returnObjLabResultDt.length();++i){
                JSONObject row = (JSONObject) returnObjLabResultDt.get(i);
                lst5.add((DataLayer.LaboratoryResultDt)WebServiceHelper.JSONObjectToObject(row, new DataLayer.LaboratoryResultDt()));
            }
            result.returnObjLabResultHd = lst4;
            result.returnObjLabResultDt = lst5;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public class WebServiceResponsePatient {
        public DateTime timestamp;
        public List<?> returnObjLabResultHd;
        public List<?> returnObjLabResultDt;
    }
    public class SendMessageTask extends AsyncTask<Void, Void, WebServiceResponse> {
        private final String mDeviceID;
        private final Integer mAppointmentID;
        private final String mGCAppointmentStatus;

        SendMessageTask(String deviceID, Integer appointmentID, String GCAppointmentStatus) {
            mDeviceID = deviceID;
            mAppointmentID = appointmentID;
            mGCAppointmentStatus = GCAppointmentStatus;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            try {
                WebServiceResponse result = BusinessLayer.insertPatientMobileAppointmentStatusLog(getApplicationContext(), mDeviceID, mAppointmentID, mGCAppointmentStatus);
                return result;
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Kirim Status Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            if (result == null)
                Toast.makeText(getBaseContext(), "Kirim Status Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {

        }
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
