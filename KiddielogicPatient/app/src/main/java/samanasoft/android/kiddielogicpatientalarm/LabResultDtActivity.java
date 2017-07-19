package samanasoft.android.kiddielogicpatientalarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.LaboratoryResultDt;

public class LabResultDtActivity extends AppCompatActivity {

    List<LaboratoryResultDt> lstResultDt = null;
    private ListView lvwLabResult;
    Integer MRN = null;
    Integer LabResultID = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labresultdt);

        Intent myIntent = getIntent();
        MRN = myIntent.getIntExtra("mrn", 0);
        LabResultID = myIntent.getIntExtra("labresultid", 0);

        TextView tvLabResultNo = (TextView)findViewById(R.id.tvLabResultNo);
        TextView tvLabResultDateTime = (TextView)findViewById(R.id.tvLabResultDateTime);
        TextView tvLabResultProvider = (TextView)findViewById(R.id.tvLabResultProvider);
        TextView tvLabResultRemarks = (TextView)findViewById(R.id.tvLabResultRemarks);

        DataLayer.LaboratoryResultHd entityHd = BusinessLayer.getLaboratoryResultHd(this, LabResultID);
        tvLabResultNo.setText(entityHd.ReferenceNo);
        tvLabResultDateTime.setText(String.format("%1$s %2$s", entityHd.ResultDate.toString(Constant.FormatString.DATE_FORMAT), entityHd.ResultTime));
        tvLabResultProvider.setText(entityHd.ProviderName);
        tvLabResultRemarks.setText(entityHd.Remarks);

        lvwLabResult = (ListView)findViewById(R.id.lvwPatient);
        lvwLabResult.setDivider(null);
        lvwLabResult.setDividerHeight(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fillListView();
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

    private LabResultInformationAdapter adapter;
    private void fillListView(){
        lstResultDt = BusinessLayer.getLaboratoryResultDtList(this, String.format("ID = '%1$s'", LabResultID));
        Log.d("Lab Result Size", "Hello : " + lstResultDt.size() + "");
        Log.d("LabResultID", "Hello : " + LabResultID + "");
        Log.d("MRN", "Hello : " + MRN + "");
        adapter = new LabResultInformationAdapter(getBaseContext(), lstResultDt);
        lvwLabResult.setAdapter(adapter);

    }
    //region Adapter
    private class LabResultInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<DataLayer.LaboratoryResultDt> lstLabResult;

        public LabResultInformationAdapter(Context context, List<DataLayer.LaboratoryResultDt> lstLabResult1) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            lstLabResult = lstLabResult1;
        }
        public int getCount() {
            return lstLabResult.size();
        }
        public Object getItem(int position) {
            return lstLabResult.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final DataLayer.LaboratoryResultDt entity = lstLabResult.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_labresultdt_information, null);

                holder.txtFractionName = (TextView) convertView.findViewById(R.id.txtFractionName);
                holder.txtResult = (TextView) convertView.findViewById(R.id.txtResult);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtFractionName.setText(entity.FractionName1);

            String result = "";
            if (entity.LabTestResultTypeID == 1)
                result = entity.TextValue;
            else if (entity.LabTestResultTypeID == 2)
            {
                if (entity.MetricResultValue == 0)
                    result = String.format("'%1$s' (I)", entity.InternationalResultValue);
                else
                    result = entity.MetricResultValue.toString();
            }
            else
                result = entity.LabTestResultTypeDtName;
            if (!entity.GCResultSummary.equals(Constant.LabResultSummary.NORMAL))
            {
                if (entity.GCResultSummary.equals(Constant.LabResultSummary.HIGH))
                    result += " ↑";
                else
                    result += " ↓";

                holder.txtFractionName.setTextColor(Color.RED);
                holder.txtResult.setTextColor(Color.RED);
            }
            holder.txtResult.setText(result);

            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtFractionName;
        TextView txtResult;
    }
    //endregion
}

