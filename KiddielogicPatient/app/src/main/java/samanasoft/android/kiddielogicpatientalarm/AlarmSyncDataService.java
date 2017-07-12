package samanasoft.android.kiddielogicpatientalarm;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Base64;

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
import samanasoft.android.ottimo.dal.DataLayer.VaccinationShotDt;
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
            WebServiceResponsePatient result = SyncPatient(context, entity.MRN, deviceID, entity.LastSyncDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB), entity.LastSyncDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB), entity.LastSyncAppointmentDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB));
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
                if (result.returnObjAppointment != null) {
                    @SuppressWarnings("unchecked")
                    List<Appointment> lstAppointment = (List<Appointment>) result.returnObjAppointment;

                    String lstAppointmentID = "";
                    for (Appointment entity : lstAppointment) {
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

    public WebServiceResponsePatient SyncPatient(Context context, Integer MRN, String deviceID, String patientLastUpdatedDate, String photoLastUpdatedDate, String appointmentLastUpdatedDate){
        WebServiceResponsePatient result = new WebServiceResponsePatient();
        try {
            JSONObject response = WebServiceHelper.SyncPatient(context, MRN, deviceID, patientLastUpdatedDate, photoLastUpdatedDate, appointmentLastUpdatedDate);

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

            List<DataLayer.Patient> lst = new ArrayList<Patient>();
            if (!response.isNull("ReturnObjPatient")) {
                JSONArray returnObjPatient = WebServiceHelper.getCustomReturnObject(response, "ReturnObjPatient");
                for (int i = 0; i < returnObjPatient.length();++i){
                    JSONObject row = (JSONObject) returnObjPatient.get(i);
                    lst.add((DataLayer.Patient)WebServiceHelper.JSONObjectToObject(row, new Patient()));
                }
            }

            String img = "";
            if (!response.isNull("ReturnObjImage"))
                img = response.optString("ReturnObjImage");

            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            result.returnObjPatient = lst;
            result.returnObjAppointment = lst2;
            result.returnObjVaccination = lst3;
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
        public String returnObjImg;
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

}
