package samanasoft.android.kiddielogicpatientalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import samanasoft.android.ottimo.dal.DataLayer;

public class SelfRegistrationActivity extends BaseMainActivity {

    public static TextView tvresult;
    private DataLayer.Patient entity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entity = InitActivity(savedInstanceState, R.layout.activity_self_registration);

        tvresult = (TextView) findViewById(R.id.tvresult);

        Button btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelfRegistrationActivity.this, SelfRegistrationScanActivity.class);
                intent.putExtra("medicalno", entity.MedicalNo);
                startActivity(intent);
            }
        });
    }
}
