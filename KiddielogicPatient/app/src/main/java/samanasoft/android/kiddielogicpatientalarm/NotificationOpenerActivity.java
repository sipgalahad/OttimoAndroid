package samanasoft.android.kiddielogicpatientalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

/**
 * Created by Ari on 2/2/2015.
 */
public class NotificationOpenerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        Patient entity = BusinessLayer.getPatientList(this,"").get(0);
        int MRN = entity.MRN;
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("mrn", MRN);
        i.putExtra("isGotoMessageCenter", true);
        startActivity(i);
    }
}