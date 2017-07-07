package samanasoft.android.kiddielogicpatientalarm;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import samanasoft.android.framework.DbConfiguration;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ari on 2/2/2015.
 */
public class DBInitActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Intent myIntent = getIntent();
        String isAutoClose = myIntent.getStringExtra("isAutoClose");
        if(isAutoClose != null) {
            Log.d("DB Init", "Auto Close Kok");
            finish();
        }

        SharedPreferences prefs = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
        String isDBHasCreated = prefs.getString(Constant.SharedPreference.DB_CONF, "");

        boolean isCreateDb = false;

        if (isDBHasCreated.equals("")) {
            FirebaseMessaging.getInstance().subscribeToTopic("android");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constant.SharedPreference.DB_CONF, "1");
            editor.commit();
            isCreateDb = true;

            if(!isMyServiceRunning(AlarmStartService.class))
                startService(new Intent(getApplicationContext(), AlarmStartService.class));

        }
        else {
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
        DbConfiguration.initDB(this, isCreateDb);

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
        if(serverAppsVersion.equals("")) {
            serverAppsVersion = samanasoft.android.framework.Constant.AppVersion;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constant.SharedPreference.SERVER_APPS_VERSION, serverAppsVersion);
            editor.commit();
        }
        if(serverAppsVersion.equals(samanasoft.android.framework.Constant.AppVersion)) {
            List<Patient> lstPatient = BusinessLayer.getPatientList(getBaseContext(), "");
            if (lstPatient.size() == 1) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("mrn", lstPatient.get(0).MRN);
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
        else {
            Intent i = new Intent(getBaseContext(), UpdateApplicationActivity.class);
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
}