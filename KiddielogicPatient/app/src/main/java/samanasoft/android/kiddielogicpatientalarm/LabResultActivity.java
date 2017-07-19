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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
import samanasoft.android.ottimo.dal.DataLayer.LaboratoryResultHd;

public class LabResultActivity extends BaseMainActivity {

    private View mProgressView;
    private ListView lvwLabResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Patient entity = InitActivity(savedInstanceState, R.layout.activity_labresult);
        lvwLabResult = (ListView)findViewById(R.id.lvwPatient);
        lvwLabResult.setDivider(null);
        lvwLabResult.setDividerHeight(0);
        mProgressView = findViewById(R.id.login_progress);
        //List<Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("MRN = '%1$s'", entity.MRN));
        refreshListLabResult();
    }

    private void refreshListLabResult() {
        List<LaboratoryResultHd> lstLabResult = BusinessLayer.getLaboratoryResultHdList(this, String.format("MRN = '%1$s' ORDER BY ID DESC", MRN));
        fillListLabResult(lstLabResult);
    }

    private LabResultInformationAdapter adapter;
    private void fillListLabResult(List<LaboratoryResultHd> lstLabResult){
        adapter = new LabResultInformationAdapter(getBaseContext(), lstLabResult);
        lvwLabResult.setAdapter(adapter);
        lvwLabResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                LaboratoryResultHd entity = (LaboratoryResultHd) myAdapter.getItemAtPosition(myItemInt);
                Intent i = new Intent(getBaseContext(), LabResultDtActivity.class);
                i.putExtra("mrn", MRN);
                i.putExtra("labresultid", entity.ID);
                startActivity(i);
            }
        });
    }

    //region Adapter
    private class LabResultInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<LaboratoryResultHd> lstLabResult;

        public LabResultInformationAdapter(Context context, List<LaboratoryResultHd> lstLabResult1) {
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
            final LaboratoryResultHd entity = lstLabResult.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_labresult_information, null);

                holder.txtLabResultDate = (TextView) convertView.findViewById(R.id.txtLabResultDate);
                holder.txtLabResultNo = (TextView) convertView.findViewById(R.id.txtLabResultNo);
                holder.txtLabResultProvider = (TextView) convertView.findViewById(R.id.txtLabResultProvider);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtLabResultDate.setText(entity.ResultDate.toString(Constant.FormatString.DATE_FORMAT) + " (" + entity.ResultTime + ")");
            holder.txtLabResultNo.setText(entity.ReferenceNo);
            holder.txtLabResultProvider.setText(entity.ProviderName);
            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtLabResultDate;
        TextView txtLabResultNo;
        TextView txtLabResultProvider;
    }
    //endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            loadLabResultData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private LoadLabResultTask mAuthTask = null;
    private void loadLabResultData() {
        if (mAuthTask != null) {
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        Patient entityPatient = BusinessLayer.getPatient(getBaseContext(), MRN);
        mAuthTask = new LoadLabResultTask(entityPatient);
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadLabResultTask extends AsyncTask<Void, Void, WebServiceResponsePatient> {
        private Patient entityPatient;
        LoadLabResultTask(Patient entity1) {
            this.entityPatient = entity1;
        }

        @Override
        protected WebServiceResponsePatient doInBackground(Void... params) {
            String filterExpression = String.format("MRN = '%1$s'", MRN);
            try {
                WebServiceResponsePatient result = SyncLabResult(getBaseContext(), entityPatient.MRN, entityPatient.LastSyncLabResultDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB));
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get LabResult Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponsePatient result) {
            mAuthTask = null;
            showProgress(false);

            if (result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            } else {

                if (result.returnObjLabResultHd != null) {
                    @SuppressWarnings("unchecked")
                    List<LaboratoryResultHd> lstEntity = (List<LaboratoryResultHd>) result.returnObjLabResultHd;

                    String lstID = "";
                    for (LaboratoryResultHd entity : lstEntity) {
                        if (!lstID.equals(""))
                            lstID += ",";
                        lstID += entity.ID;
                    }

                    if (!lstID.equals("")) {
                        List<LaboratoryResultHd> lstOldEntity = BusinessLayer.getLaboratoryResultHdList(getBaseContext(), String.format("ID IN (%1$s)", lstID));
                        for (LaboratoryResultHd entity : lstOldEntity) {
                            BusinessLayer.deleteLaboratoryResultHd(getBaseContext(), entity.ID);
                        }

                        for (LaboratoryResultHd entity : lstEntity) {
                            BusinessLayer.insertLaboratoryResultHd(getBaseContext(), entity);
                        }
                    }
                }
                if (result.returnObjLabResultDt != null) {
                    @SuppressWarnings("unchecked")
                    List<DataLayer.LaboratoryResultDt> lstEntity = (List<DataLayer.LaboratoryResultDt>) result.returnObjLabResultDt;

                    String lstID = "";
                    for (DataLayer.LaboratoryResultDt entity : lstEntity) {
                        if (!lstID.equals(""))
                            lstID += ",";
                        lstID += entity.LaboratoryResultDtID;
                    }

                    if (!lstID.equals("")) {
                        List<DataLayer.LaboratoryResultDt> lstOldEntity = BusinessLayer.getLaboratoryResultDtList(getBaseContext(), String.format("LaboratoryResultDtID IN (%1$s)", lstID));
                        for (DataLayer.LaboratoryResultDt entity : lstOldEntity) {
                            BusinessLayer.deleteLaboratoryResultDt(getBaseContext(), entity.LaboratoryResultDtID);
                        }

                        for (DataLayer.LaboratoryResultDt entity : lstEntity) {
                            BusinessLayer.insertLaboratoryResultDt(getBaseContext(), entity);
                        }
                    }
                }
            }

            Patient entityPatient = BusinessLayer.getPatient(getBaseContext(), MRN);
            entityPatient.LastSyncLabResultDateTime = result.timestamp;
            BusinessLayer.updatePatient(getBaseContext(), entityPatient);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
    //endregion


    public WebServiceResponsePatient SyncLabResult(Context context, Integer MRN, String labResultLastUpdatedDate){
        WebServiceResponsePatient result = new WebServiceResponsePatient();
        try {
            JSONObject response = WebServiceHelper.SyncLabResult(context, MRN, labResultLastUpdatedDate);

            JSONArray returnObjLabResultHd = WebServiceHelper.getCustomReturnObject(response, "ReturnObjLabResultHd");
            JSONArray returnObjLabResultDt = WebServiceHelper.getCustomReturnObject(response, "ReturnObjLabResultDt");
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.LaboratoryResultHd> lst4 = new ArrayList<DataLayer.LaboratoryResultHd>();
            for (int i = 0; i < returnObjLabResultHd.length();++i){
                JSONObject row = (JSONObject) returnObjLabResultHd.get(i);
                lst4.add((DataLayer.LaboratoryResultHd)WebServiceHelper.JSONObjectToObject(row, new DataLayer.LaboratoryResultHd()));
            }
            List<DataLayer.LaboratoryResultDt> lst5 = new ArrayList<DataLayer.LaboratoryResultDt>();
            for (int i = 0; i < returnObjLabResultDt.length();++i){
                JSONObject row = (JSONObject) returnObjLabResultDt.get(i);
                lst5.add((DataLayer.LaboratoryResultDt)WebServiceHelper.JSONObjectToObject(row, new DataLayer.LaboratoryResultDt()));
            }
            result.returnObjLabResultHd = lst4;
            result.returnObjLabResultDt = lst5;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public class WebServiceResponsePatient {
        public DateTime timestamp;
        public List<?> returnObjLabResultHd;
        public List<?> returnObjLabResultDt;
    }
}
