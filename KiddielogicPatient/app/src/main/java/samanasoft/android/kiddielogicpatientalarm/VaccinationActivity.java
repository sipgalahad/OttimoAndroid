package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.VaccinationShotDt;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
import samanasoft.android.ottimo.dal.DataLayer.vVaccinationType;

public class VaccinationActivity extends BaseMainActivity {

    private View mProgressView;
    private ListView lvwAppointment;
    private Spinner spnVaccinationType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Patient entity = InitActivity(savedInstanceState, R.layout.activity_vaccination);
        lvwAppointment = (ListView)findViewById(R.id.lvwPatient);
        lvwAppointment.setDivider(null);
        lvwAppointment.setDividerHeight(0);

        List<vVaccinationType> lstVaccinationType = BusinessLayer.getvVaccinationTypeList(getApplicationContext(), "1 = 1 ORDER BY VaccinationTypeName");
        vVaccinationType defaultObj = new vVaccinationType();
        defaultObj.VaccinationTypeID = 0;
        defaultObj.VaccinationTypeName = "-- Jenis Vaksin --";
        lstVaccinationType.add(0, defaultObj);
        //Log.d("testVaksin",lstVaccinationType.get(0).VaccinationTypeID + "");

        VaccinationTypeAdapter vaccinationTypeAdapter = new VaccinationTypeAdapter(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, lstVaccinationType);
        spnVaccinationType = (Spinner) findViewById(R.id.spnVaccinationType);
        spnVaccinationType.setAdapter(vaccinationTypeAdapter); // Set the custom adapter to the spinner
        spnVaccinationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                refreshListVaccination();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mProgressView = findViewById(R.id.login_progress);
        //List<Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("MRN = '%1$s'", entity.MRN));
        refreshListVaccination();
    }

    private void refreshListVaccination() {
        int vaccinationTypeID = (int) spnVaccinationType.getSelectedItemId();
        List<DataLayer.VaccinationShotDt> lstVaccination = null;
        if(vaccinationTypeID != 0)
            lstVaccination = BusinessLayer.getVaccinationShotDtList(this, String.format("MRN = '%1$s' AND VaccinationTypeID = '%2$s' ORDER BY VaccinationNo", MRN, vaccinationTypeID));
        else
            lstVaccination = new ArrayList<DataLayer.VaccinationShotDt>();
        fillListVaccination(lstVaccination);
    }

    private VaccinationInformationAdapter adapter;
    private void fillListVaccination(List<VaccinationShotDt> lstVaccination){
        adapter = new VaccinationInformationAdapter(getBaseContext(), lstVaccination);
        lvwAppointment.setAdapter(adapter);
    }
    //region Adapter
    private class VaccinationInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<VaccinationShotDt> lstVaccination;

        public VaccinationInformationAdapter(Context context, List<VaccinationShotDt> lstVaccination1) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            lstVaccination = lstVaccination1;
        }
        public int getCount() {
            return lstVaccination.size();
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final VaccinationShotDt entity = lstVaccination.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_vaccination_information, null);

                holder.txtVaccinationDate = (TextView) convertView.findViewById(R.id.txtVaccinationDate);
                holder.txtVaccinationInformationParamedicName = (TextView) convertView.findViewById(R.id.txtVaccinationInformationParamedicName);
                holder.txtVaccinationInformationDose = (TextView) convertView.findViewById(R.id.txtVaccinationInformationDose);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtVaccinationDate.setText(entity.VaccinationDate.toString(Constant.FormatString.DATE_FORMAT) + " (" + entity.VaccinationNo + ")");
            holder.txtVaccinationInformationParamedicName.setText(entity.ParamedicName);
            holder.txtVaccinationInformationDose.setText(entity.Dose + " " + entity.DoseUnit);
            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtVaccinationDate;
        TextView txtVaccinationInformationParamedicName;
        TextView txtVaccinationInformationDose;
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
            loadAppointmentData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private LoadAppointmentTask mAuthTask = null;
    private void loadAppointmentData() {
        if (mAuthTask != null) {
            return;
        }

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        mAuthTask = new LoadAppointmentTask();
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

            lvwAppointment.setVisibility(show ? View.GONE : View.VISIBLE);
            lvwAppointment.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lvwAppointment.setVisibility(show ? View.GONE : View.VISIBLE);
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
            lvwAppointment.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class LoadAppointmentTask extends AsyncTask<Void, Void, WebServiceResponse> {
        LoadAppointmentTask() {

        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            String filterExpression = String.format("MRN = '%1$s'", MRN);
            Log.d("filterExpression", filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListVaccinationShotDt(getBaseContext(), filterExpression);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Vaccination Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            mAuthTask = null;
            showProgress(false);

            if(result == null) {
                Toast.makeText(getBaseContext(), "Update Data Gagal. Silakan Cek Koneksi Internet Anda", Toast.LENGTH_SHORT).show();
            }
            else if(result.returnObj != null){
                List<VaccinationShotDt> lstOldVaccination = BusinessLayer.getVaccinationShotDtList(getBaseContext(), String.format("MRN = '%1$s'", MRN));
                for (VaccinationShotDt entity : lstOldVaccination) {
                    BusinessLayer.deleteVaccinationShotDt(getBaseContext(), entity.Type, entity.ID);
                }

                @SuppressWarnings("unchecked")
                List<VaccinationShotDt> lstVaccination = (List<VaccinationShotDt>) result.returnObj;
                for (VaccinationShotDt entity : lstVaccination) {
                    BusinessLayer.insertVaccinationShotDt(getBaseContext(), entity);
                }
                fillListVaccination(lstVaccination);

                Patient entityPatient = BusinessLayer.getPatient(getBaseContext(), MRN);
                entityPatient.LastSyncVaccinationDateTime = result.timestamp;
                BusinessLayer.updatePatient(getBaseContext(), entityPatient);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class VaccinationTypeAdapter extends ArrayAdapter<vVaccinationType> {

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<vVaccinationType> values;

        public VaccinationTypeAdapter(Context context, int textViewResourceId,List<vVaccinationType> values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        public int getCount(){
            return values.size();
        }

        public vVaccinationType getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return values.get(position).VaccinationTypeID;
        }


        // And the "magic" goes here
        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(values.get(position).VaccinationTypeName);

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(values.get(position).VaccinationTypeName);

            return label;
        }
    }
    //endregion
}
