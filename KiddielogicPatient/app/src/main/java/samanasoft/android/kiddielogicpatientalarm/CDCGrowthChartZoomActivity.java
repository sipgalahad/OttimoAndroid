package samanasoft.android.kiddielogicpatientalarm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

/**
 * Created by DEV_ARI on 4/16/2018.
 */

public class CDCGrowthChartZoomActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        int MRN = myIntent.getIntExtra("mrn", 0);
        String type = myIntent.getStringExtra("cdctype");
        DataLayer.Patient entity = BusinessLayer.getPatient(this, MRN);
        setContentView(new Zoom(this, entity.MedicalNo, type));
    }
}