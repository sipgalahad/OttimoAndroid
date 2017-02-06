package samanasoft.android.kiddielogicpatient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import samanasoft.android.framework.DbConfiguration;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
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

        if(isDBHasCreated.equals("")){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constant.SharedPreference.DB_CONF, "1");
            editor.commit();
            isCreateDb = true;
        }

        String dbName = "OttimoPatient.db";
        int dbVersion = Integer.valueOf("1");
        DbConfiguration.initDB(this, dbName, dbVersion, isCreateDb);

        //if(isCreateDb)
        {
            AlarmNotificationHelper alarm = new AlarmNotificationHelper();
            alarm.setAlarm(this);
            AlarmSyncDataHelper alarm2 = new AlarmSyncDataHelper();
            alarm2.setAlarm(this);
        }
        Intent intentSyncData = new Intent(this, AlarmSyncDataService.class);
        startService(intentSyncData);

        if(isOnline(this)) {
            List<Patient> lstPatient = BusinessLayer.getPatientList(this, "");
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
        else
            Toast.makeText(this, "Anda tidak terhubung dengan internet", Toast.LENGTH_SHORT).show();
    }

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }
}