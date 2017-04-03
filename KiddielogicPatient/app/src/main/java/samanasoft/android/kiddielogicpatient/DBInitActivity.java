package samanasoft.android.kiddielogicpatient;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.DbConfiguration;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ari on 2/2/2015.
 */
public class DBInitActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
        String isDBHasCreated = prefs.getString(Constant.SharedPreference.DB_CONF, "");

        boolean isCreateDb = false;

        if (isDBHasCreated.equals("")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constant.SharedPreference.DB_CONF, "1");
            editor.commit();
            isCreateDb = true;
        }

        //String dbName = "OttimoPatient.db";
        //int dbVersion = Integer.valueOf("1");
        //DbConfiguration.initDB(this, dbName, dbVersion, isCreateDb);
        DbConfiguration.initDB(this, isCreateDb);

        if (AlarmNotificationHelper.isAlarmExist(this))
            Toast.makeText(this, "Alarm Exists", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Alarm Doesn't Exists", Toast.LENGTH_LONG).show();

        if (!AlarmNotificationHelper.isAlarmExist(this)) {
            AlarmNotificationHelper alarm = new AlarmNotificationHelper();
            alarm.setAlarm(this);
        }
        if (!AlarmSyncDataHelper.isAlarmExist(this)) {
            AlarmSyncDataHelper alarm2 = new AlarmSyncDataHelper();
            alarm2.setAlarm(this);
        }

        /*List<String> lstCalendarID = getCalendar(this);
        //String calID = lstCalendarID.get(0);
        String lstCal = "";
        for(String calID : lstCalendarID) {
            lstCal += calID + " | ";

        }
        Toast.makeText(this, "Cal ID : " + lstCal, Toast.LENGTH_SHORT).show();*/

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(001);

        Intent intentSyncData = new Intent(this, AlarmSyncDataService.class);
        startService(intentSyncData);

        //buat test notifikasi
        //Intent notificationService = new Intent(this, AlarmNotificationService.class);
        //startService(notificationService);

        if (!isOnline(this))
            Toast.makeText(this, "Anda tidak terhubung dengan internet", Toast.LENGTH_SHORT).show();
        CheckVersionTask mAuthTask = new CheckVersionTask();
        mAuthTask.execute((Void) null);

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
    public class CheckVersionTask extends AsyncTask<Void, Void, JSONObject> {
        CheckVersionTask() {

        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                JSONObject result = WebServiceHelper.GetAndroidAppVersion(getBaseContext());
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Version Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            if (result == null) {
                Toast.makeText(getBaseContext(), "Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            } else if (result != null) {
                String version = result.optString("Version");
                if(version.equals(samanasoft.android.framework.Constant.AppVersion)) {
                    List<Patient> lstPatient = BusinessLayer.getPatientList(getBaseContext(), "");
                    if (lstPatient.size() == 1) {
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        i.putExtra("mrn", lstPatient.get(0).MRN);
                        startActivity(i);
                    } else if (lstPatient.size() > 1) {
                        Intent i = new Intent(getBaseContext(), ManageAccountActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(i);
                    }
                }
                else {
                    Intent i = new Intent(getBaseContext(), UpdateApplicationActivity.class);
                    startActivity(i);
                }
            }
        }
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}