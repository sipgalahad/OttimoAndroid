package samanasoft.android.kiddielogicparamedicalarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;

/**
 * Created by Ari on 2/2/2015.
 */
public class NotificationOpenerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        ParamedicMaster entity = BusinessLayer.getParamedicMasterList(this, "").get(0);
        int ParamedicID = entity.ParamedicID;
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.putExtra("mrn", ParamedicID);
        //i.putExtra("isGotoMessageCenter", true);
        startActivity(i);
    }
}