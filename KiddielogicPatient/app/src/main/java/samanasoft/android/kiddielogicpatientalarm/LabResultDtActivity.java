package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.LaboratoryResultDt;

public class LabResultDtActivity extends AppCompatActivity {

    List<LaboratoryResultDt> lstResultDt = null;
    private ListView lvwLabResult;
    ScrollView scrlMain;
    private View mProgressView;
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

        scrlMain = (ScrollView)findViewById(R.id.scrlMain);
        lvwLabResult = (ListView)findViewById(R.id.lvwPatient);
        lvwLabResult.setDivider(null);
        lvwLabResult.setDividerHeight(0);
        mProgressView = findViewById(R.id.login_progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fillListView();

        if(!entityHd.GCPatientMobileStatus.equals(Constant.PatientMobileStatus.READ))
            updateLabResultMobileStatus();
    }

    private UpdateLabResultMobileStatusTask mAuthTask = null;
    private void updateLabResultMobileStatus() {
        if (mAuthTask != null) {
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new UpdateLabResultMobileStatusTask(LabResultID, Constant.PatientMobileStatus.READ);
        mAuthTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            lvwLabResult.setVisibility(show ? View.GONE : View.VISIBLE);
            lvwLabResult.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lvwLabResult.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            lvwLabResult.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class UpdateLabResultMobileStatusTask extends AsyncTask<Void, Void, Boolean> {
        private Integer ID;
        private String GCPatientMobileStatus;
        UpdateLabResultMobileStatusTask(Integer ID, String GCPatientMobileStatus) {
            this.ID = ID;
            this.GCPatientMobileStatus = GCPatientMobileStatus;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Boolean result = UpdateLabResultMobileStatus(getBaseContext(), ID, GCPatientMobileStatus);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get LabResult Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            mAuthTask = null;
            showProgress(false);

            if (result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            } else {
                DataLayer.LaboratoryResultHd entity = BusinessLayer.getLaboratoryResultHd(getBaseContext(), this.ID);
                entity.GCPatientMobileStatus = GCPatientMobileStatus;
                if (result)
                    entity.IsPendingUpdateData = false;
                else
                    entity.IsPendingUpdateData = true;
                BusinessLayer.updateLaboratoryResultHd(getBaseContext(), entity);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public Boolean UpdateLabResultMobileStatus(Context context, Integer ID, String GCPatientMobileStatus){
        Boolean result = true;
        try {
            JSONObject response = WebServiceHelper.UpdateLabResultMobileStatus(context, ID, GCPatientMobileStatus);
            result = response.optBoolean("Result");
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
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

        setListViewHeightBasedOnChildren(lvwLabResult);

        scrlMain.fullScroll(ScrollView.FOCUS_UP);
        scrlMain.smoothScrollTo(0, 0);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        LabResultInformationAdapter listAdapter = (LabResultInformationAdapter) listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ListView.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();

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

