package samanasoft.android.kiddielogicpatientalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

public class AppointmentActivity extends BaseMainActivity {

    private View mProgressView;
    private TextView tvLastSyncDate;
    private ListView lvwAppointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Patient entity = InitActivity(savedInstanceState, R.layout.activity_appointment);
        tvLastSyncDate = (TextView)findViewById(R.id.tvLastSyncDate);
        lvwAppointment = (ListView)findViewById(R.id.lvwPatient);
        lvwAppointment.setDivider(null);
        lvwAppointment.setDividerHeight(0);

        mProgressView = findViewById(R.id.login_progress);
        //List<Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("MRN = '%1$s'", entity.MRN));
        List<Appointment> lstAppointment = BusinessLayer.getAppointmentList(this, String.format("GCAppointmentStatus != '%1$s' AND StartDate >= '%2$s' AND MRN = '%3$s' ORDER BY StartDate", Constant.AppointmentStatus.VOID, DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB), entity.MRN));
        fillListAppointment(lstAppointment);

        Patient entityPatient = BusinessLayer.getPatient(this, MRN);
        tvLastSyncDate.setText("Last Sync : " + entityPatient.LastSyncAppointmentDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT));
    }

    private AppointmentInformationAdapter adapter;
    private void fillListAppointment(List<Appointment> lstAppointment1){
        adapter = new AppointmentInformationAdapter(getBaseContext(), lstAppointment1);
        lvwAppointment.setAdapter(adapter);
    }
    //region Adapter
    private class AppointmentInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<Appointment> lstAppointment;

        public AppointmentInformationAdapter(Context context, List<Appointment> lstAppointment1) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            lstAppointment = lstAppointment1;
        }
        public int getEntityID(int position){
            return lstAppointment.get(position).AppointmentID;
        }
        public int getCount() {
            return lstAppointment.size();
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final Appointment entity = lstAppointment.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_appointment_information, null);

                holder.txtAppointmentDate = (TextView) convertView.findViewById(R.id.txtAppointmentDate);
                holder.txtAppointmentInformationParamedicName = (TextView) convertView.findViewById(R.id.txtAppointmentInformationParamedicName);
                holder.txtAppointmentInformationVisitTypeName = (TextView) convertView.findViewById(R.id.txtAppointmentInformationVisitTypeName);
                holder.txtAppointmentConfirmed = (TextView) convertView.findViewById(R.id.txtAppointmentConfirmed);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtAppointmentDate.setText(entity.StartDate.toString(Constant.FormatString.DATE_FORMAT) + " (" + entity.StartTime + ")");
            holder.txtAppointmentInformationParamedicName.setText(entity.ParamedicName);
            holder.txtAppointmentInformationVisitTypeName.setText(entity.VisitTypeName);
            if(!entity.GCAppointmentStatus.equals(Constant.AppointmentStatus.CONFIRMED))
                holder.txtAppointmentConfirmed.setText("");
            else
                holder.txtAppointmentConfirmed.setText("Confirmed");
            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtAppointmentDate;
        TextView txtAppointmentInformationParamedicName;
        TextView txtAppointmentInformationVisitTypeName;
        TextView txtAppointmentConfirmed;
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
            String filterExpression = String.format("MRN = '%1$s' AND StartDate >= '%2$s' AND GCAppointmentStatus IN ('%3$s','%4$s','%5$s')", MRN, DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB), Constant.AppointmentStatus.OPEN, Constant.AppointmentStatus.SEND_CONFIRMATION, Constant.AppointmentStatus.CONFIRMED);
            Log.d("filterExpression", filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListAppointment(getBaseContext(), filterExpression);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Appointment Failed", Toast.LENGTH_SHORT).show();
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
                List<Appointment> lstOldAppointment = BusinessLayer.getAppointmentList(getBaseContext(), String.format("MRN = '%1$s'", MRN));
                for (Appointment entity : lstOldAppointment) {
                    Helper.deleteAppointmentFromEventCalender(getBaseContext(), entity);
                    BusinessLayer.deleteAppointment(getBaseContext(), entity.AppointmentID);
                }

                @SuppressWarnings("unchecked")
                List<Appointment> lstAppointment = (List<Appointment>) result.returnObj;
                for (Appointment entity : lstAppointment) {
                    BusinessLayer.insertAppointment(getBaseContext(), entity);
                    Helper.insertAppointmentToEventCalender(getBaseContext(), entity);
                }
                fillListAppointment(lstAppointment);
                setMessageCenterCounter();

                Patient entityPatient = BusinessLayer.getPatient(getBaseContext(), MRN);
                entityPatient.LastSyncAppointmentDateTime = result.timestamp;
                BusinessLayer.updatePatient(getBaseContext(), entityPatient);

                tvLastSyncDate.setText("Last Sync : " + entityPatient.LastSyncAppointmentDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
