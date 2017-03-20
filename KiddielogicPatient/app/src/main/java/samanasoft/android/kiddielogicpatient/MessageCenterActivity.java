package samanasoft.android.kiddielogicpatient;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import samanasoft.android.framework.Constant;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;
import samanasoft.android.ottimo.dal.DataLayer.vAppointment;
import samanasoft.android.ottimo.dal.DataLayer.Patient;

public class MessageCenterActivity extends BaseMainActivity {

    private View mProgressView;
    private ListView lvwAppointment;
    private UpdateAppointmentTask mAuthTask = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Patient entity = InitActivity(savedInstanceState, R.layout.activity_message_center);
        lvwAppointment = (ListView)findViewById(R.id.lvwPatient);
        lvwAppointment.setDivider(null);
        lvwAppointment.setDividerHeight(0);

        mProgressView = findViewById(R.id.login_progress);

        fillListAppointment();
    }

    private AppointmentInformationAdapter adapter;
    private void fillListAppointment(){
        List<vAppointment> lstAppointment = BusinessLayer.getvAppointmentList(this, String.format("GCAppointmentStatus != '%1$s' AND ReminderDate LIKE '%2$s%%'", Constant.AppointmentStatus.VOID, DateTime.now().toString(Constant.FormatString.DATE_FORMAT_DB)));
        adapter = new AppointmentInformationAdapter(getBaseContext(), lstAppointment);
        lvwAppointment.setAdapter(adapter);
    }
    //region Adapter
    private class AppointmentInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private List<vAppointment> lstAppointment;

        public AppointmentInformationAdapter(Context context, List<vAppointment> lstAppointment1) {
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
            final vAppointment entity = lstAppointment.get(position);
            Log.d("position", position + "");
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_appointment_message_center_information, null);

                //holder.txtAppointmentDate = (TextView) convertView.findViewById(R.id.txtAppointmentDate);
                //holder.txtAppointmentInformationParamedicName = (TextView) convertView.findViewById(R.id.txtAppointmentInformationParamedicName);
                //holder.txtAppointmentInformationVisitTypeName = (TextView) convertView.findViewById(R.id.txtAppointmentInformationVisitTypeName);
                holder.txtAppointmentInformationMessage = (TextView) convertView.findViewById(R.id.txtAppointmentInformationMessage);

                holder.btnConfirm = (Button) convertView.findViewById(R.id.btnConfirm);
                holder.btnCancel = (Button) convertView.findViewById(R.id.btnCancel);
                holder.btnCall = (Button) convertView.findViewById(R.id.btnCall);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            //holder.txtAppointmentDate.setText(entity.FullName + " : " + entity.StartDate.toString(Constant.FormatString.DATE_FORMAT) + " (" + entity.StartTime + ")");
            //holder.txtAppointmentInformationParamedicName.setText(entity.ParamedicName);
            //holder.txtAppointmentInformationVisitTypeName.setText(entity.VisitTypeName);

            String message = "Mengingatkan {PatientName} terjadwal {VisitTypeName} ke {ParamedicName} tgl {StartDate} Jam {cfStartTime} di KiddieCare. Jika setuju tekan tombol 'Confirm', jika tdk dijwb dianggap batal";
                message = message.replace("{PatientName}", entity.FullName)
                    .replace("{StartDate}", entity.StartDate.toString(Constant.FormatString.DATE_FORMAT))
                    .replace("{StartTime}", entity.StartTime)
                    .replace("{EndTime}", entity.EndTime)
                    .replace("{cfStartTime}", entity.cfStartTime)
                    .replace("{ParamedicName}", entity.ParamedicName)
                    .replace("{VisitTypeName}", entity.VisitTypeName)
                    .replace("{ServiceUnitName}", entity.ServiceUnitName);
            if(entity.GCAppointmentStatus.equals(Constant.AppointmentStatus.CONFIRMED))
                message += " (Confirmed)";
            else if(entity.GCAppointmentStatus.equals(Constant.AppointmentStatus.CANCELLED))
                message += " (Cancelled)";
            holder.txtAppointmentInformationMessage.setText(message);

            if(entity.GCAppointmentStatus.equals(Constant.AppointmentStatus.OPEN) || entity.GCAppointmentStatus.equals(Constant.AppointmentStatus.SEND_CONFIRMATION)){
                holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateAppointment(entity, Constant.AppointmentStatus.CONFIRMED);
                    }
                });
                holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateAppointment(entity, Constant.AppointmentStatus.CANCELLED);
                    }
                });
            }
            else {
                holder.btnConfirm.setVisibility(View.INVISIBLE);
                holder.btnCancel.setVisibility(View.INVISIBLE);
            }
            holder.btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:+62216452121"));
                    if (ActivityCompat.checkSelfPermission(MessageCenterActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                }
            });

            return convertView;
        }

    }
    private static class ViewHolder {
        //TextView txtAppointmentDate;
        //TextView txtAppointmentInformationParamedicName;
        //TextView txtAppointmentInformationVisitTypeName;
        TextView txtAppointmentInformationMessage;
        Button btnConfirm;
        Button btnCancel;
        Button btnCall;
    }
    //endregion

    private void updateAppointment(vAppointment entity, String GCAppointmentStatus) {
        if (mAuthTask != null) {
            return;
        }
        showProgress(true);
        mAuthTask = new UpdateAppointmentTask(entity, GCAppointmentStatus);
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
    public class UpdateAppointmentTask extends AsyncTask<Void, Void, WebServiceResponse> {

        private final vAppointment mAppointment;
        private final String mGCAppointmentStatus;

        UpdateAppointmentTask(vAppointment entity, String GCAppointmentStatus) {
            mAppointment = entity;
            mGCAppointmentStatus = GCAppointmentStatus;
        }

        @Override
        protected WebServiceResponse doInBackground(Void... params) {
            try {
                WebServiceResponse result = BusinessLayer.postAppointmentAnswer(getBaseContext(), mAppointment.AppointmentID, mGCAppointmentStatus);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Update Appointment Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final WebServiceResponse result) {
            mAuthTask = null;
            Appointment entity = BusinessLayer.getAppointment(getBaseContext(), mAppointment.AppointmentID);
            entity.GCAppointmentStatus = mGCAppointmentStatus;
            BusinessLayer.updateAppointment(getBaseContext(), entity);

            showProgress(false);
            fillListAppointment();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
