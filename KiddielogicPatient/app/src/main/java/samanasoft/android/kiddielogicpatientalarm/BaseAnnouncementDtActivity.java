package samanasoft.android.kiddielogicpatientalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

public class BaseAnnouncementDtActivity extends AppCompatActivity {

    Integer MRN = null;
    Integer AnnouncementID = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcementdt);

        Intent myIntent = getIntent();
        MRN = myIntent.getIntExtra("mrn", 0);
        AnnouncementID = myIntent.getIntExtra("announcementid", 0);

        TextView tvRemarks = (TextView)findViewById(R.id.tvRemarks);

        DataLayer.Announcement entityHd = BusinessLayer.getAnnouncement(this, AnnouncementID);
        tvRemarks.setText(Html.fromHtml(entityHd.Remarks));
        //tvLabResultDateTime.setText(String.format("%1$s %2$s", entityHd.ResultDate.toString(Constant.FormatString.DATE_FORMAT), entityHd.ResultTime));
        //tvLabResultProvider.setText(entityHd.ProviderName);
        //tvLabResultRemarks.setText(entityHd.Remarks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onBackPressed() {
        Log.d("MRN", "Hello2 : " + MRN + "");
        /*Intent i = new Intent(getBaseContext(), LabResultActivity.class);
        i.putExtra("mrn", MRN);
        startActivity(i);*/
        super.onBackPressed();
        finish();
    }
}

