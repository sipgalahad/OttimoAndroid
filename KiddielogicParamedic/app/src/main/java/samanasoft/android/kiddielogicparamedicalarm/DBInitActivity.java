package samanasoft.android.kiddielogicparamedicalarm;

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
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;

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

        Context context = getApplicationContext();

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
                List<Integer> lstParamedicID = BusinessLayer.getParamedicMasterParamedicIDList(getApplicationContext(), "");
                DbConfiguration.initDB(this, true);

                String listParamedicID = "";
                for (Integer ParamedicID : lstParamedicID) {
                    if(listParamedicID != "")
                        listParamedicID += ",";
                    listParamedicID += ParamedicID.toString();
                }

                if(listParamedicID.equals("")){
                    isDifferentDBVersion = false;
                }
                else {
                    editor.putString(Constant.SharedPreference.LIST_MRN, listParamedicID);

                    String deviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    showProgress(true);
                    mAuthTask = new ReloadDataTask(listParamedicID, deviceID);
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
        List<ParamedicMaster> lstPatient = BusinessLayer.getParamedicMasterList(getBaseContext(), "");
        if (lstPatient.size() == 1) {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.putExtra("paramedicid", lstPatient.get(0).ParamedicID);

            SharedPreferences sharedPreferences = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("paramedicid", lstPatient.get(0).ParamedicID);
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


    public class ReloadDataTask extends AsyncTask<Void, Void, WebServiceResponseParamedic> {

        private final String mListMRN;
        private final String mDeviceID;

        ReloadDataTask(String listMRN, String deviceID) {
            mListMRN = listMRN;
            mDeviceID = deviceID;
        }

        @Override
        protected WebServiceResponseParamedic doInBackground(Void... params) {
            try {
                WebServiceResponseParamedic result = ReloadDataAfterUpdateApps(getBaseContext(), mListMRN, mDeviceID);
                return result;
            } catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Patient Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponseParamedic result) {
            mAuthTask = null;
            if (result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
                showProgress(false);
            } else {
                if (result.returnObjParamedic != null) {
                    @SuppressWarnings("unchecked")
                    List<ParamedicMaster> lstParamedic = (List<ParamedicMaster>) result.returnObjParamedic;
                    int ctr = 0;
                    for (ParamedicMaster entity : lstParamedic) {
                        ParamedicMaster tempPatient = BusinessLayer.getParamedicMaster(getBaseContext(), entity.ParamedicID);
                        if (tempPatient == null) {
                            entity.LastSyncDateTime = result.timestamp;

                            BusinessLayer.insertParamedicMaster(getBaseContext(), entity);
                            FirebaseMessaging.getInstance().subscribeToTopic(entity.ParamedicCode);

                            String returnObjImg = result.returnObjImg.get(ctr);
                            if(!returnObjImg.equals("")) {
                                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                                File directory = cw.getDir("KiddielogicParamedic", Context.MODE_PRIVATE);
                                File mypath = new File(directory, entity.ParamedicCode + ".jpg");

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

    public WebServiceResponseParamedic ReloadDataAfterUpdateApps(Context context, String listMRN, String deviceID){
        WebServiceResponseParamedic result = new WebServiceResponseParamedic();
        try {
            JSONObject response = null;//WebServiceHelper.ReloadDataAfterUpdateApps(context, listMRN, deviceID);

            JSONArray returnObjParamedic = WebServiceHelper.getCustomReturnObject(response, "ReturnObjParamedic");
            JSONArray returnObjImg = WebServiceHelper.getCustomReturnObject(response, "ReturnObjImage");
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.ParamedicMaster> lst = new ArrayList<ParamedicMaster>();
            for (int i = 0; i < returnObjParamedic.length();++i){
                JSONObject row = (JSONObject) returnObjParamedic.get(i);
                lst.add((DataLayer.ParamedicMaster)WebServiceHelper.JSONObjectToObject(row, new ParamedicMaster()));
            }
            List<String> lst2 = new ArrayList();
            for (int i = 0; i < returnObjImg.length();++i){
                lst2.add(returnObjImg.get(i).toString());
            }
            result.returnObjParamedic = lst;
            result.returnObjImg = lst2;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public class WebServiceResponseParamedic {
        public DateTime timestamp;
        public List<?> returnObjParamedic;
        public List<String> returnObjImg;
    }
}