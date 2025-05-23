package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.DbConfiguration;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ari on 2/2/2015.
 */
public class DBInitActivity extends Activity {

    private ReloadDataTask mAuthTask = null;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init);
        mProgressView = findViewById(R.id.login_progress);

        Intent myIntent = getIntent();
        String isAutoClose = myIntent.getStringExtra("isAutoClose");
        if(isAutoClose != null) {
            Log.d("DB Init", "Auto Close Kok");
            finish();
        }

        SharedPreferences prefs = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
        String isDBHasCreated = prefs.getString(Constant.SharedPreference.DB_CONF, "");

        boolean isDifferentDBVersion = false;

        if (isDBHasCreated.equals("")) {
            FirebaseMessaging.getInstance().subscribeToTopic(samanasoft.android.framework.Constant.Url.SUBSRICE_ANDROID);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constant.SharedPreference.DB_CONF, "1");
            editor.putString(Constant.SharedPreference.DB_VERSION, samanasoft.android.framework.Constant.DB_VERSION);
            editor.commit();

            if(!isMyServiceRunning(AlarmStartService.class))
                startService(new Intent(getApplicationContext(), AlarmStartService.class));

            DbConfiguration.initDB(this, true);
        }
        else {
            String DBVersion = prefs.getString(Constant.SharedPreference.DB_VERSION, "1");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constant.SharedPreference.DB_VERSION, samanasoft.android.framework.Constant.DB_VERSION);
            Log.d("DBVersion", DBVersion);
            Log.d("DBVersion", samanasoft.android.framework.Constant.DB_VERSION);
            if(!DBVersion.equals(samanasoft.android.framework.Constant.DB_VERSION)) {
                isDifferentDBVersion = true;
                List<Integer> lstMRN = BusinessLayer.getPatientMRNList(getApplicationContext(), "");
                DbConfiguration.initDB(this, true);

                String listMRN = "";
                for (Integer MRN : lstMRN) {
                    if(listMRN != "")
                        listMRN += ",";
                    listMRN += MRN.toString();
                }

                if(listMRN.equals("")){
                    isDifferentDBVersion = false;
                }
                else {
                    editor.putString(Constant.SharedPreference.LIST_MRN, listMRN);

                    String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    showProgress(true);
                    mAuthTask = new ReloadDataTask(listMRN, deviceID);
                    mAuthTask.execute((Void) null);
                }
            }
            else{
                String listMRN = prefs.getString(Constant.SharedPreference.LIST_MRN, "");
                if(listMRN.equals("")){
                    isDifferentDBVersion = false;
                }
                else {
                    isDifferentDBVersion = true;
                    String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    showProgress(true);
                    mAuthTask = new ReloadDataTask(listMRN, deviceID);
                    mAuthTask.execute((Void) null);
                }
            }

            if(!isMyServiceRunning(AlarmStartService.class)) {
                startService(new Intent(getApplicationContext(), AlarmStartService.class));
                Toast.makeText(this, "Service Stop Working", Toast.LENGTH_LONG).show();
            }
            if (!AlarmSyncDataHelper.isAlarmExist(getApplicationContext()))
                Toast.makeText(this, "Alarm Stop Working", Toast.LENGTH_LONG).show();
        }

        //String dbName = "OttimoPatient.db";
        //int dbVersion = Integer.valueOf("1");
        //DbConfiguration.initDB(this, dbName, dbVersion, isCreateDb);

        //TimerService mTimerService = new TimerService(getApplicationContext());
        //Intent mServiceIntent = new Intent(getApplicationContext(), mTimerService.getClass());
        //if (!isMyServiceRunning(mTimerService.getClass())) {
        //    startService(mServiceIntent);
        //}

        //startService(new Intent(getApplicationContext(), TimerService.class));

        /*if (!AlarmNotificationHelper.isAlarmExist(getApplicationContext())) {
            AlarmNotificationHelper alarm = new AlarmNotificationHelper();
            alarm.setAlarm(getApplicationContext());
        }*/
        if (!AlarmSyncDataHelper.isAlarmExist(getApplicationContext())) {
            AlarmSyncDataHelper alarm2 = new AlarmSyncDataHelper();
            alarm2.setAlarm(getApplicationContext());
        }

        /*List<String> lstCalendarID = getCalendar(this);
        //String calID = lstCalendarID.get(0);
        String lstCal = "";
        for(String calID : lstCalendarID) {
            lstCal += calID + " | ";

        }
        Toast.makeText(this, "Cal ID : " + lstCal, Toast.LENGTH_SHORT).show();*/

        Intent intentSyncData = new Intent(getApplicationContext(), AlarmSyncDataService.class);
        startService(intentSyncData);

        //buat test notifikasi
        //Intent notificationService = new Intent(this, AlarmNotificationService.class);
        //startService(notificationService);

        if (!isOnline(this))
            Toast.makeText(this, "Anda tidak terhubung dengan internet", Toast.LENGTH_SHORT).show();

        String serverAppsVersion = prefs.getString(Constant.SharedPreference.SERVER_APPS_VERSION, "");
        if(serverAppsVersion.equals("") || serverAppsVersion.compareTo(samanasoft.android.framework.Constant.AppVersion) < 0) {
            serverAppsVersion = samanasoft.android.framework.Constant.AppVersion;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constant.SharedPreference.SERVER_APPS_VERSION, serverAppsVersion);
            editor.commit();
        }

        //Log.d("isDifferentDBVersion", isDifferentDBVersion + "");
        if(!isDifferentDBVersion) {
            if (serverAppsVersion.equals(samanasoft.android.framework.Constant.AppVersion)) {
                goToNextPage();
            } else {
                //Intent i = new Intent(getBaseContext(), UpdateApplicationActivity.class);
                //startActivity(i);
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        }
    }

    private void goToNextPage(){
        List<Patient> lstPatient = BusinessLayer.getPatientList(getBaseContext(), "");
        if (lstPatient.size() == 1) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.putExtra("mrn", lstPatient.get(0).MRN);

            SharedPreferences sharedPreferences = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("mrn", lstPatient.get(0).MRN);
            editor.commit();
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
        } else if (lstPatient.size() > 1) {
            Intent i = new Intent(getBaseContext(), ManageAccountActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            i.putExtra("isinit", true);
            startActivity(i);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    private static List<String> getCalendar(Context c) {
        List<String> lstCal = new ArrayList<>();
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = c.getContentResolver();
        //Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

        Cursor managedCursor = c.getContentResolver().query(calendars, projection, CalendarContract.Calendars.VISIBLE + " = 1 AND "  + CalendarContract.Calendars.IS_PRIMARY + "=1", null, CalendarContract.Calendars._ID + " ASC");
        if(managedCursor.getCount() <= 0){
            managedCursor = c.getContentResolver().query(calendars, projection, CalendarContract.Calendars.VISIBLE + " = 1", null, CalendarContract.Calendars._ID + " ASC");
        }

        if (managedCursor.moveToFirst()){
            String calID;
            int idCol = managedCursor.getColumnIndex(projection[0]);
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            do {
                calID = managedCursor.getString(idCol) + " ; " + managedCursor.getString(nameCol);
                lstCal.add(calID);
            } while(managedCursor.moveToNext());
            managedCursor.close();
        }
        return lstCal;
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }


    public class ReloadDataTask extends AsyncTask<Void, Void, WebServiceResponsePatient> {

        private final String mListMRN;
        private final String mDeviceID;

        ReloadDataTask(String listMRN, String deviceID) {
            mListMRN = listMRN;
            mDeviceID = deviceID;
        }

        @Override
        protected WebServiceResponsePatient doInBackground(Void... params) {
            try {
                WebServiceResponsePatient result = ReloadDataAfterUpdateApps(getBaseContext(), mListMRN, mDeviceID);
                return result;
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Patient Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponsePatient result) {
            mAuthTask = null;
            if (result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                showProgress(false);
            } else {
                if (result.returnObjPatient != null) {
                    @SuppressWarnings("unchecked")
                    List<Patient> lstPatient = (List<Patient>) result.returnObjPatient;
                    int ctr = 0;
                    for (Patient entity : lstPatient) {
                        Patient tempPatient = BusinessLayer.getPatient(getBaseContext(), entity.MRN);
                        if (tempPatient == null) {
                            entity.LastSyncDateTime = result.timestamp;
                            entity.LastSyncAppointmentDateTime = result.timestamp;
                            entity.LastSyncVaccinationDateTime = result.timestamp;
                            entity.LastSyncLabResultDateTime = result.timestamp;
                            entity.LastSyncCDCGrowthChartDateTime = result.timestamp;

                            BusinessLayer.insertPatient(getBaseContext(), entity);
                            FirebaseMessaging.getInstance().subscribeToTopic(entity.MedicalNo);

                            String returnObjImg = result.returnObjImg.get(ctr);
                            if(!returnObjImg.equals("")) {
                                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                                File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                                File mypath = new File(directory, entity.MedicalNo + ".jpg");

                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(mypath);

                                    byte[] decodedString = Base64.decode(returnObjImg, Base64.DEFAULT);
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
                            ctr++;
                        }
                    }

                    List<DataLayer.Variable> lstCDCGrowthChart = (List<DataLayer.Variable>) result.returnObjCDCGrowthChart;
                    Log.d("lstCDCGrowthChart", lstCDCGrowthChart.size() + ";");
                    for (DataLayer.Variable entity2 : lstCDCGrowthChart) {
                        if(!entity2.Value.equals("")) {
                            Log.d("haha", "hahahaha");
                            ContextWrapper cw = new ContextWrapper(getApplicationContext());
                            File directory = cw.getDir("Kiddielogic", Context.MODE_PRIVATE);
                            File mypath = new File(directory, entity2.Code + ".jpg");

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

                    @SuppressWarnings("unchecked")
                    List<DataLayer.Appointment> lstAppointment = (List<DataLayer.Appointment>) result.returnObjAppointment;
                    for (DataLayer.Appointment entity2 : lstAppointment) {
                        BusinessLayer.insertAppointment(getBaseContext(), entity2);
                        Helper.insertAppointmentToEventCalender(getBaseContext(), entity2);
                    }

                    @SuppressWarnings("unchecked")
                    List<DataLayer.VaccinationShotDt> lstVaccination = (List<DataLayer.VaccinationShotDt>) result.returnObjVaccination;
                    for (DataLayer.VaccinationShotDt entity2 : lstVaccination) {
                        BusinessLayer.insertVaccinationShotDt(getBaseContext(), entity2);
                    }

                    @SuppressWarnings("unchecked")
                    List<DataLayer.LaboratoryResultHd> lstLabResultHd = (List<DataLayer.LaboratoryResultHd>) result.returnObjLabResultHd;
                    for (DataLayer.LaboratoryResultHd entity2 : lstLabResultHd) {
                        BusinessLayer.insertLaboratoryResultHd(getBaseContext(), entity2);
                    }

                    @SuppressWarnings("unchecked")
                    List<DataLayer.LaboratoryResultDt> lstLabResultDt = (List<DataLayer.LaboratoryResultDt>) result.returnObjLabResultDt;
                    for (DataLayer.LaboratoryResultDt entity2 : lstLabResultDt) {
                        BusinessLayer.insertLaboratoryResultDt(getBaseContext(), entity2);
                    }

                    @SuppressWarnings("unchecked")
                    List<DataLayer.Announcement> lstAnnouncement = (List<DataLayer.Announcement>) result.returnObjAnnouncement;
                    for (DataLayer.Announcement entity2 : lstAnnouncement) {
                        BusinessLayer.insertAnnouncement(getBaseContext(), entity2);
                    }

                    SharedPreferences sharedPreferences = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constant.SharedPreference.LIST_MRN, "");
                    editor.putString(samanasoft.android.ottimo.common.Constant.SharedPreference.LAST_SYNC_ANNOUNCEMENT, result.timestamp.toString(samanasoft.android.framework.Constant.FormatString.DATE_TIME_FORMAT_DB));
                    editor.commit();

                    goToNextPage();
                } else {
                    showProgress(false);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    public WebServiceResponsePatient ReloadDataAfterUpdateApps(Context context, String listMRN, String deviceID){
        WebServiceResponsePatient result = new WebServiceResponsePatient();
        try {
            JSONObject response = WebServiceHelper.ReloadDataAfterUpdateApps(context, listMRN, deviceID);

            JSONArray returnObjAppointment = WebServiceHelper.getCustomReturnObject(response, "ReturnObjAppointment");
            JSONArray returnObjPatient = WebServiceHelper.getCustomReturnObject(response, "ReturnObjPatient");
            JSONArray returnObjVaccination = WebServiceHelper.getCustomReturnObject(response, "ReturnObjVaccination");
            JSONArray returnObjLabResultHd = WebServiceHelper.getCustomReturnObject(response, "ReturnObjLabResultHd");
            JSONArray returnObjLabResultDt = WebServiceHelper.getCustomReturnObject(response, "ReturnObjLabResultDt");
            JSONArray returnObjAnnouncement = WebServiceHelper.getCustomReturnObject(response, "ReturnObjAnnouncement");
            JSONArray returnObjImg = WebServiceHelper.getCustomReturnObject(response, "ReturnObjImage");
            JSONArray returnObjCDCGrowthChart = WebServiceHelper.getCustomReturnObject(response, "ReturnObjCDCGrowthChart");
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.Patient> lst = new ArrayList<Patient>();
            for (int i = 0; i < returnObjPatient.length();++i){
                JSONObject row = (JSONObject) returnObjPatient.get(i);
                lst.add((DataLayer.Patient)WebServiceHelper.JSONObjectToObject(row, new Patient()));
            }
            List<DataLayer.Appointment> lst2 = new ArrayList<DataLayer.Appointment>();
            for (int i = 0; i < returnObjAppointment.length();++i){
                JSONObject row = (JSONObject) returnObjAppointment.get(i);
                lst2.add((DataLayer.Appointment)WebServiceHelper.JSONObjectToObject(row, new DataLayer.Appointment()));
            }
            List<DataLayer.VaccinationShotDt> lst3 = new ArrayList<DataLayer.VaccinationShotDt>();
            for (int i = 0; i < returnObjVaccination.length();++i){
                JSONObject row = (JSONObject) returnObjVaccination.get(i);
                lst3.add((DataLayer.VaccinationShotDt)WebServiceHelper.JSONObjectToObject(row, new DataLayer.VaccinationShotDt()));
            }
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
            List<String> lst6 = new ArrayList();
            for (int i = 0; i < returnObjImg.length();++i){
                lst6.add(returnObjImg.get(i).toString());
            }
            List<DataLayer.Announcement> lst7 = new ArrayList<DataLayer.Announcement>();
            for (int i = 0; i < returnObjAnnouncement.length();++i){
                JSONObject row = (JSONObject) returnObjAnnouncement.get(i);
                lst7.add((DataLayer.Announcement)WebServiceHelper.JSONObjectToObject(row, new DataLayer.Announcement()));
            }
            List<DataLayer.Variable> lst8 = new ArrayList<DataLayer.Variable>();
            for (int i = 0; i < returnObjCDCGrowthChart.length();++i){
                JSONObject row = (JSONObject) returnObjCDCGrowthChart.get(i);
                lst8.add((DataLayer.Variable)WebServiceHelper.JSONObjectToObject(row, new DataLayer.Variable()));
            }
            result.returnObjPatient = lst;
            result.returnObjAppointment = lst2;
            result.returnObjVaccination = lst3;
            result.returnObjLabResultHd = lst4;
            result.returnObjLabResultDt = lst5;
            result.returnObjImg = lst6;
            result.returnObjAnnouncement = lst7;
            result.returnObjCDCGrowthChart = lst8;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
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
        public List<String> returnObjImg;
    }
}