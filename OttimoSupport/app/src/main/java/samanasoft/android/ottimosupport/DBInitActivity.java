package samanasoft.android.ottimosupport;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;

import samanasoft.android.framework.DbConfiguration;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.common.Methods;

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

        Log.d("test", "haha : " + isDBHasCreated);
        if(isDBHasCreated.equals("")){
            SharedPreferences.Editor editor = prefs.edit();
            Log.d("test", "1");
            editor.putString(Constant.SharedPreference.DB_CONF, "1");
            editor.putString(Constant.SharedPreference.APPOINTMENT_REMINDER_TIME, "12:00");
            //editor.putString(Constant.SharedPreference.APPOINTMENT_REMINDER_MESSAGE, "Mengingatkan {PreferredName} terjadwal {VisitTypeName} ke {ParamedicName} tgl {StartDate} Jam {cfStartTime} di KiddieCare. Jika setuju jawab 'KCC_YA', jika tidak menjawab kami anggap batal");
            //editor.putString(Constant.SharedPreference.WEB_SERVICE_URL, "http://192.168.0.100/research/Ottimov2.0/ControlPanel/Libs/Service/MethodService.asmx");

            //editor.putString(Constant.SharedPreference.WEB_SERVICE_URL, "http://10.18.18.252/kiddielogic/ControlPanel/Libs/Service/MethodService.asmx");

            //Miranti
            editor.putString(Constant.SharedPreference.WEB_SERVICE_URL, "http://192.168.1.103/ottimo/ControlPanel/Libs/Service/MethodService.asmx");
            editor.putString(Constant.SharedPreference.APPOINTMENT_REMINDER_MESSAGE, "Mengingatkan {PreferredName} terjadwal {VisitTypeName} ke {ParamedicName} tgl {StartDate} Jam {cfStartTime} di Puspita. Jika setuju jawab 'ya', jika tidak menjawab kami anggap batal");
            editor.commit();
            isCreateDb = true;
        }

        String dbName = getResources().getString(R.string.DBName) + ".db";
        int dbVersion = Integer.valueOf(getResources().getString(R.string.DBVersion));
        DbConfiguration.initDB(this, dbName, dbVersion, isCreateDb);

        String defaultActivity = getResources().getString(R.string.DefaultActivity);
        Class<?> c = Methods.loadActivity(this, defaultActivity.toString());
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}