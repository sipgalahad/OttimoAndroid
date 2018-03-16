package samanasoft.android.kiddielogicparamedicalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

/**
 * Created by Ari on 2/2/2015.
 */
public class PatientSOAPActivity extends AppCompatActivity {

    private ListView lvwView;
    ScrollView scrlMain;
    private View mProgressView;
    Integer ParamedicID = null;
    Integer MRN = null;
    LoadConsultVisitTask mAuthTask = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patientsoap);

        Intent myIntent = getIntent();
        ParamedicID = myIntent.getIntExtra("paramedicid", 0);
        MRN = myIntent.getIntExtra("mrn", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("Bobob - SOAP");

        lvwView = (ListView)findViewById(R.id.lvwPatient);
        lvwView.setDivider(null);
        lvwView.setDividerHeight(0);
        mProgressView = findViewById(R.id.login_progress);
        //List<Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("MRN = '%1$s'", entity.MRN));
        //refreshListLabResult();

        showProgress(true);
        mAuthTask = new LoadConsultVisitTask(MRN);
        mAuthTask.execute((Void) null);
    }

    private PatientInformationAdapter adapter;

    private void fillListLabResult(List<DataLayer.ConsultVisit> lstLabResult){
        adapter = new PatientInformationAdapter(getBaseContext(), lstLabResult);
        Log.d("ahaha2" , "" + lstLabResult.size() + " ho");
        lvwView.setAdapter(adapter);
    }

    //region Adapter
    private class PatientInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<DataLayer.ConsultVisit> lstView;

        public PatientInformationAdapter(Context context, List<DataLayer.ConsultVisit> lstView1) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            lstView = lstView1;
        }
        public int getCount() {
            return lstView.size();
        }
        public Object getItem(int position) {
            return lstView.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final DataLayer.ConsultVisit entity = lstView.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_patient_information, null);

                holder.txtPatientName = (TextView) convertView.findViewById(R.id.txtPatientName);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtPatientName.setText(entity.ServiceUnitName);

            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtPatientName;
    }
    //endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_refresh) {
            loadLabResultData();
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadConsultVisitTask extends AsyncTask<Void, Void, WebServiceResponse> {
        private int MRN;
        LoadConsultVisitTask(int MRN1) {
            this.MRN = MRN1;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            String filterExpression = String.format("MRN = '%1$s'", MRN);
            Log.d("test",filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListConsultVisit(getBaseContext(), filterExpression);
                return result;
            } catch (Exception ex) {
                //Toast.makeText(getBaseContext(), "Get LabResult Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            mAuthTask = null;
            showProgress(false);
            Log.d("test","haha");
            if (result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("test","haha");
                if (result.returnObj != null) {
                    Log.d("test","masuk kok");
                    fillListLabResult((List<DataLayer.ConsultVisit>) result.returnObj);
                }
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //showProgress(false);
        }
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

            lvwView.setVisibility(show ? View.GONE : View.VISIBLE);
            lvwView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lvwView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            lvwView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    //endregion

}
