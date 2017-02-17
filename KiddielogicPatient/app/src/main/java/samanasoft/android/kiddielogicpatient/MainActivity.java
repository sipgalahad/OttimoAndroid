package samanasoft.android.kiddielogicpatient;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import samanasoft.android.framework.Constant;
import samanasoft.android.ottimo.control.CircularImageView;
import samanasoft.android.ottimo.dal.DataLayer;

public class MainActivity extends BaseMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataLayer.Patient entity = InitActivity(savedInstanceState, R.layout.activity_main);

        TextView txtPatientName = (TextView) findViewById(R.id.txtPatientName);
        TextView txtMedicalNo1 = (TextView) findViewById(R.id.txtMedicalNo1);
        TextView tvNumber1 = (TextView) findViewById(R.id.tvNumber1);
        TextView tvNumber2 = (TextView) findViewById(R.id.tvNumber2);
        TextView tvNumber3 = (TextView) findViewById(R.id.tvNumber3);
        TextView tvNumber4 = (TextView) findViewById(R.id.tvNumber4);
        TextView tvNumber5 = (TextView) findViewById(R.id.tvNumber5);
        TextView tvNumber6 = (TextView) findViewById(R.id.tvNumber6);
        TextView tvNumber7 = (TextView) findViewById(R.id.tvNumber7);
        txtPatientName.setText(entity.FullName);
        txtMedicalNo1.setText(entity.MedicalNo);
        tvNumber5.setText(entity.PreferredName);
        tvNumber6.setText(entity.CityOfBirth);
        tvNumber7.setText(entity.DateOfBirth.toString(Constant.FormatString.DATE_FORMAT));
        if(!entity.MobilePhoneNo1.equals(""))
            tvNumber1.setText(entity.MobilePhoneNo1);
        if(!entity.MobilePhoneNo2.equals(""))
            tvNumber2.setText(entity.MobilePhoneNo2);
        if(!entity.EmailAddress.equals(""))
            tvNumber3.setText(entity.EmailAddress);
        if(!entity.EmailAddress2.equals(""))
            tvNumber4.setText(entity.EmailAddress2);
        // Create imageDir
        CircularImageView img = (CircularImageView) findViewById(R.id.imgPatientProfile);
        img.setImageBitmap(loadImageFromStorage(entity));

        if(AlarmNotificationHelper.isAlarmExist(this))
            Toast.makeText(this, "Alarm Exists", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Alarm Doesn't Exists", Toast.LENGTH_LONG).show();
    }
}
