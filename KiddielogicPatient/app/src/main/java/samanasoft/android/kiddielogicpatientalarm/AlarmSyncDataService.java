package samanasoft.android.kiddielogicpatientalarm;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;
import samanasoft.android.ottimo.dal.DataLayer.Announcement;
import samanasoft.android.ottimo.dal.DataLayer.VaccinationShotDt;
import samanasoft.android.ottimo.dal.DataLayer.LaboratoryResultHd;
import samanasoft.android.ottimo.dal.DataLayer.LaboratoryResultDt;
import samanasoft.android.framework.Constant;

public class AlarmSyncDataService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isOnline(this.getApplicationContext())) {
            DateTime today = DateTime.now();
            String deviceID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            List<Patient> lstEntity = BusinessLayer.getPatientList(this.getApplicationContext(), "");
            for (Patient entity : lstEntity) {
                //if(!entity.LastSyncDateTime.toString("ddMMyyyy").equals(today.toString("ddMMyyyy")) || entity.LastSyncDateTime.Hour - today.Hour > 6)
                    (new GetPatientTask(this.getApplicationContext(), entity, deviceID)).execute();
            }
        }
        //AlarmSyncDataHelper alarm = new AlarmSyncDataHelper();
        //alarm.setAlarm(this.getApplicationContext().getApplicationContext());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private class GetPatientTask extends AsyncTask<String, Double, WebServiceResponsePatient> {
        private boolean isRefreshPatientList = false;
        private Context context = null;
        private Patient entity = null;
        private String deviceID;
        GetPatientTask(Context context, Patient mEntity, String mDeviceID){
            this.context = context;
            this.entity = mEntity;
            this.deviceID = mDeviceID;
        }
        @Override
        protected void onPreExecute() {
            if(!isRefreshPatientList){
                isRefreshPatientList = true;
            }
            else{
                this.cancel(true);
            }
        }

        protected WebServiceResponsePatient doInBackground(String... args) {
            SharedPreferences prefs = getSharedPreferences(samanasoft.android.ottimo.common.Constant.SharedPreference.NAME, MODE_PRIVATE);
            String lastSyncAnnouncement = prefs.getString(samanasoft.android.ottimo.common.Constant.SharedPreference.LAST_SYNC_ANNOUNCEMENT, "");
            DateTime dtLastSyncAnnouncement = null;
            if(lastSyncAnnouncement.equals(""))
                dtLastSyncAnnouncement = new DateTime(2018, 1, 1, 0, 0, 0);
            else
                dtLastSyncAnnouncement = new DateTime(lastSyncAnnouncement);
            WebServiceResponsePatient result = SyncPatient(context, entity.MRN, deviceID, entity.LastSyncDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB), entity.LastSyncDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB), entity.LastSyncAppointmentDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB), entity.LastSyncVaccinationDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB), entity.LastSyncLabResultDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB), dtLastSyncAnnouncement.toString(Constant.FormatString.DATE_TIME_FORMAT_DB));
            return result;
        }
        protected void onPostExecute(WebServiceResponsePatient result) {
            isRefreshPatientList = false;
            if(result != null) {
                if (result.returnObjPatient != null) {
                    @SuppressWarnings("unchecked")
                    List<Patient> lstPatient = (List<Patient>) result.returnObjPatient;
                    for (Patient entity1 : lstPatient) {
                        entity1.LastSyncAppointmentDateTime = result.timestamp;
                        entity1.LastSyncDateTime = result.timestamp;
                        BusinessLayer.updatePatient(context, entity1);
                    }
                }

                int editedMRN = 0;
                if (result.returnObjAppointment != null) {
                    @SuppressWarnings("unchecked")
                    List<Appointment> lstAppointment = (List<Appointment>) result.returnObjAppointment;

                    String lstAppointmentID = "";
                    for (Appointment entity : lstAppointment) {
                        if(editedMRN == 0)
                            editedMRN = entity.MRN;
                        if (!lstAppointmentID.equals(""))
                            lstAppointmentID += ",";
                        lstAppointmentID += entity.AppointmentID;
                    }

                    if (!lstAppointmentID.equals("")) {
                        List<Appointment> lstOldAppointment = BusinessLayer.getAppointmentList(context, String.format("AppointmentID IN (%1$s)", lstAppointmentID));
                        for (Appointment entity : lstOldAppointment) {
                            Helper.deleteAppointmentFromEventCalender(context, entity);
                            BusinessLayer.deleteAppointment(context, entity.AppointmentID);
                        }

                        for (Appointment entity : lstAppointment) {
                            BusinessLayer.insertAppointment(context, entity);
                            Helper.insertAppointmentToEventCalender(context, entity);
                        }
                    }
                }
                if (result.returnObjAnnouncement != null) {
                    @SuppressWarnings("unchecked")
                    List<DataLayer.Announcement> lstAnnouncement = (List<Announcement>) result.returnObjAnnouncement;

                    String lstAnnouncementID = "";
                    for (Announcement entity : lstAnnouncement) {
                        if (!lstAnnouncementID.equals(""))
                            lstAnnouncementID += ",";
                        lstAnnouncementID += entity.AnnouncementID;
                    }

                    if (!lstAnnouncementID.equals("")) {
                        List<Announcement> lstOldAnnouncement = BusinessLayer.getAnnouncementList(context, String.format("AnnouncementID IN (%1$s)", lstAnnouncementID));
                        for (Announcement entity : lstOldAnnouncement) {
                            BusinessLayer.deleteAnnouncement(context, entity.AnnouncementID);
                        }

                        for (Announcement entity : lstAnnouncement) {
                            BusinessLayer.insertAnnouncement(context, entity);
                        }
                    }
                }
                if(editedMRN != 0)
                    sendAppointmentNotification(editedMRN);
                if (result.returnObjVaccination != null) {
                    @SuppressWarnings("unchecked")
                    List<VaccinationShotDt> lstVaccination = (List<VaccinationShotDt>) result.returnObjVaccination;

                    String lstVaccinationID = "";
                    for (VaccinationShotDt entity : lstVaccination) {
                        if (!lstVaccinationID.equals(""))
                            lstVaccinationID += ",";
                        lstVaccinationID += entity.ID;
                    }

                    if (!lstVaccinationID.equals("")) {
                        List<VaccinationShotDt> lstOldVaccination = BusinessLayer.getVaccinationShotDtList(context, String.format("ID IN (%1$s) AND Type = 1", lstVaccinationID));
                        for (VaccinationShotDt entity : lstOldVaccination) {
                            BusinessLayer.deleteVaccinationShotDt(context, entity.Type, entity.ID);
                        }

                        for (VaccinationShotDt entity : lstVaccination) {
                            BusinessLayer.insertVaccinationShotDt(context, entity);
                        }
                    }
                }
                if (result.returnObjLabResultHd != null) {
                    @SuppressWarnings("unchecked")
                    List<LaboratoryResultHd> lstEntity = (List<LaboratoryResultHd>) result.returnObjLabResultHd;

                    String lstID = "";
                    for (LaboratoryResultHd entity : lstEntity) {
                        if (!lstID.equals(""))
                            lstID += ",";
                        lstID += entity.ID;
                    }

                    if (!lstID.equals("")) {
                        List<LaboratoryResultHd> lstOldEntity = BusinessLayer.getLaboratoryResultHdList(context, String.format("ID IN (%1$s)", lstID));
                        for (LaboratoryResultHd entity : lstOldEntity) {
                            BusinessLayer.deleteLaboratoryResultHd(context, entity.ID);
                        }

                        for (LaboratoryResultHd entity : lstEntity) {
                            BusinessLayer.insertLaboratoryResultHd(context, entity);
                        }
                    }
                }
                if (result.returnObjLabResultDt != null) {
                    @SuppressWarnings("unchecked")
                    List<LaboratoryResultDt> lstEntity = (List<LaboratoryResultDt>) result.returnObjLabResultDt;

                    String lstID = "";
                    for (LaboratoryResultDt entity : lstEntity) {
                        if (!lstID.equals(""))
                            lstID += ",";
                        lstID += entity.LaboratoryResultDtID;
                    }

                    if (!lstID.equals("")) {
                        List<LaboratoryResultDt> lstOldEntity = BusinessLayer.getLaboratoryResultDtList(context, String.format("LaboratoryResultDtID IN (%1$s)", lstID));
                        for (LaboratoryResultDt entity : lstOldEntity) {
                            BusinessLayer.deleteLaboratoryResultDt(context, entity.LaboratoryResultDtID);
                        }

                        for (LaboratoryResultDt entity : lstEntity) {
                            BusinessLayer.insertLaboratoryResultDt(context, entity);
                        }
                    }
                }

                if (result.returnObjCDCGrowthChart != null) {
                    List<DataLayer.Variable> lstCDCGrowthChart = (List<DataLayer.Variable>) result.returnObjCDCGrowthChart;
                    Log.d("lstCDCGrowthChart", lstCDCGrowthChart.size() + ";");
                    for (DataLayer.Variable entity2 : lstCDCGrowthChart) {
                        if (!entity2.Value.equals("")) {
                            Log.d("haha", "hahahaha");
                            ContextWrapper cw = new ContextWrapper(getApplicationContext());
                            File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                            File mypath = new File(directory, entity.MedicalNo + "_" + entity2.Code + ".jpg");

                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(mypath);

                                byte[] decodedString = Base64.decode(entity2.Value, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                decodedByte.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                if (!result.returnObjImg.equals("")) {
                    ContextWrapper cw = new ContextWrapper(context);
                    File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                    File mypath = new File(directory, entity.MedicalNo + ".jpg");

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(mypath);

                        byte[] decodedString = Base64.decode(result.returnObjImg, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        decodedByte.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    private void sendAppointmentNotification(int MRN) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("Update Data");
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("mrn", MRN);
        i.putExtra("isGoToAppointment", true);
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

    public WebServiceResponsePatient SyncPatient(Context context, Integer MRN, String deviceID, String patientLastUpdatedDate, String photoLastUpdatedDate, String appointmentLastUpdatedDate, String vaccinationLastUpdatedDate, String labResultLastUpdatedDate, String announcementLastUpdatedDate){
        WebServiceResponsePatient result = new WebServiceResponsePatient();
        try {
            JSONObject response = WebServiceHelper.SyncPatient(context, MRN, deviceID, patientLastUpdatedDate, photoLastUpdatedDate, appointmentLastUpdatedDate, vaccinationLastUpdatedDate, labResultLastUpdatedDate, announcementLastUpdatedDate);

            List<DataLayer.Appointment> lst2 = new ArrayList<DataLayer.Appointment>();
            if (!response.isNull("ReturnObjAppointment")) {
                JSONArray returnObjAppointment = WebServiceHelper.getCustomReturnObject(response, "ReturnObjAppointment");
                for (int i = 0; i < returnObjAppointment.length();++i){
                    JSONObject row = (JSONObject) returnObjAppointment.get(i);
                    lst2.add((DataLayer.Appointment)WebServiceHelper.JSONObjectToObject(row, new DataLayer.Appointment()));
                }
            }
            List<DataLayer.VaccinationShotDt> lst3 = new ArrayList<DataLayer.VaccinationShotDt>();
            if (!response.isNull("ReturnObjVaccination")) {
                JSONArray returnObjVaccination = WebServiceHelper.getCustomReturnObject(response, "ReturnObjVaccination");
                for (int i = 0; i < returnObjVaccination.length();++i){
                    JSONObject row = (JSONObject) returnObjVaccination.get(i);
                    lst3.add((DataLayer.VaccinationShotDt)WebServiceHelper.JSONObjectToObject(row, new DataLayer.VaccinationShotDt()));
                }
            }
            List<DataLayer.LaboratoryResultHd> lst4 = new ArrayList<DataLayer.LaboratoryResultHd>();
            if (!response.isNull("ReturnObjLabResultHd")) {
                JSONArray returnObjLabResultHd = WebServiceHelper.getCustomReturnObject(response, "ReturnObjLabResultHd");
                for (int i = 0; i < returnObjLabResultHd.length(); ++i) {
                    JSONObject row = (JSONObject) returnObjLabResultHd.get(i);
                    lst4.add((DataLayer.LaboratoryResultHd) WebServiceHelper.JSONObjectToObject(row, new DataLayer.LaboratoryResultHd()));
                }
            }
            List<DataLayer.LaboratoryResultDt> lst5 = new ArrayList<DataLayer.LaboratoryResultDt>();
            if (!response.isNull("ReturnObjLabResultDt")) {
                JSONArray returnObjLabResultDt = WebServiceHelper.getCustomReturnObject(response, "ReturnObjLabResultDt");
                for (int i = 0; i < returnObjLabResultDt.length(); ++i) {
                    JSONObject row = (JSONObject) returnObjLabResultDt.get(i);
                    lst5.add((DataLayer.LaboratoryResultDt) WebServiceHelper.JSONObjectToObject(row, new DataLayer.LaboratoryResultDt()));
                }
            }

            List<DataLayer.Patient> lst = new ArrayList<Patient>();
            if (!response.isNull("ReturnObjPatient")) {
                JSONArray returnObjPatient = WebServiceHelper.getCustomReturnObject(response, "ReturnObjPatient");
                for (int i = 0; i < returnObjPatient.length();++i){
                    JSONObject row = (JSONObject) returnObjPatient.get(i);
                    lst.add((DataLayer.Patient)WebServiceHelper.JSONObjectToObject(row, new Patient()));
                }
            }
            List<DataLayer.Announcement> lst6 = new ArrayList<DataLayer.Announcement>();
            if (!response.isNull("ReturnObjAnnouncement")) {
                JSONArray returnObjAnnouncement = WebServiceHelper.getCustomReturnObject(response, "ReturnObjAnnouncement");
                for (int i = 0; i < returnObjAnnouncement.length();++i){
                    JSONObject row = (JSONObject) returnObjAnnouncement.get(i);
                    lst6.add((DataLayer.Announcement)WebServiceHelper.JSONObjectToObject(row, new DataLayer.Announcement()));
                }
            }
            List<DataLayer.Variable> lst7 = new ArrayList<DataLayer.Variable>();
            if (!response.isNull("ReturnObjCDCGrowthChart")) {
                JSONArray returnObjCDCGrowthChart = WebServiceHelper.getCustomReturnObject(response, "ReturnObjCDCGrowthChart");
                for (int i = 0; i < returnObjCDCGrowthChart.length(); ++i) {
                    JSONObject row = (JSONObject) returnObjCDCGrowthChart.get(i);
                    lst7.add((DataLayer.Variable) WebServiceHelper.JSONObjectToObject(row, new DataLayer.Variable()));
                }
            }

            String img = "";
            if (!response.isNull("ReturnObjImage"))
                img = response.optString("ReturnObjImage");

            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            result.returnObjPatient = lst;
            result.returnObjAppointment = lst2;
            result.returnObjVaccination = lst3;
            result.returnObjLabResultHd = lst4;
            result.returnObjLabResultDt = lst5;
            result.returnObjAnnouncement = lst6;
            result.returnObjCDCGrowthChart = lst7;
            result.returnObjImg = img;
            result.timestamp = timestamp;
        } catch (Exception ex) {
            result = null;
            ex.printStackTrace();
            String stackTrace = ex.getStackTrace().toString();
            String message = ex.getMessage();
            new Helper.InsertErrorLog(getBaseContext(), MRN, deviceID, message, stackTrace).execute((Void) null);
        }
        return result;
    }
    public class WebServiceResponsePatient {
        public DateTime timestamp;
        public List<?> returnObjPatient;
        public List<?> returnObjAppointment;
        public List<?> returnObjVaccination;
        public List<?> returnObjLabResultHd;
        public List<?> returnObjLabResultDt;
        public List<?> returnObjAnnouncement;
        public List<?> returnObjCDCGrowthChart;
        public String returnObjImg;
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

}
