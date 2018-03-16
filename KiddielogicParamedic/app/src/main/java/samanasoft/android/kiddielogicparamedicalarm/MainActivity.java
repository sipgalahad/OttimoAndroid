package samanasoft.android.kiddielogicparamedicalarm;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import samanasoft.android.framework.Constant;
import samanasoft.android.ottimo.control.CircularImageView;
import samanasoft.android.ottimo.dal.DataLayer;

public class MainActivity extends BaseMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataLayer.ParamedicMaster entity = InitActivity(savedInstanceState, R.layout.activity_main);

        TextView txtParamedicName = (TextView) findViewById(R.id.txtParamedicName);
        TextView txtParamedicCode = (TextView) findViewById(R.id.txtParamedicCode);
        txtParamedicName.setText(entity.ParamedicName);
        txtParamedicCode.setText(entity.ParamedicCode);

        // Create imageDir
        CircularImageView img = (CircularImageView) findViewById(R.id.imgParamedicProfile);
        img.setImageBitmap(loadImageFromStorage(entity));
    }

    /*@Override
    public void onDestroy() {
        //Log.e(TAG, "onDestroy");
        sendBroadcast(new Intent("samanasoft.android.kiddielogicpatientalarm.intent.action.RESTART_SERVICE"));
        Toast.makeText(getApplicationContext(), "On Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }*/
}
