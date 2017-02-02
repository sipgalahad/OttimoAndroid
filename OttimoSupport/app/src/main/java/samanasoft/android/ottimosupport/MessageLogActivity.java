package samanasoft.android.ottimosupport;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.common.Convert;
import samanasoft.android.ottimo.common.Methods;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;
import samanasoft.android.ottimo.dal.DataLayer.MessageLog;

public class MessageLogActivity extends ActionBarActivity {
    EditText txtLogDate = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log);

        txtLogDate = (EditText) findViewById(R.id.txtLogDate);
        txtLogDate.setText(DateTime.now().toString("dd-MM-yyyy"));

        lvwLog = (ListView)findViewById(R.id.lvwPatient);
        lvwLog.setDivider(null);
        lvwLog.setDividerHeight(0);
        fillListAppointment();

        txtLogDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MessageLogActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtLogDate.setText(sdf.format(myCalendar.getTime()));
        fillListAppointment();
    }

    private MessageLogInformationAdapter adapter;
    private void fillListAppointment(){
        adapter = new MessageLogInformationAdapter(getBaseContext());
        lvwLog.setAdapter(adapter);
    }
    private ListView lvwLog;
    private List<CheckBox> lstCheckBox = null;
    private List<MessageLog> lstMessageLog = null;
    //region Adapter
    private class MessageLogInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;

        public MessageLogInformationAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            lstCheckBox = new ArrayList<CheckBox>();

            String[] tempDate = txtLogDate.getText().toString().split("-");
            DateTime logDate = new DateTime(Convert.ObjectToInt(tempDate[2]),Convert.ObjectToInt(tempDate[1]),Convert.ObjectToInt(tempDate[0]),0,0,0);
            String test = logDate.toString("yyyy-MM-dd");
            String filterExpression = "LogDate LIKE '" + test + "%'";

            Log.d("filterExpression", filterExpression);
            lstMessageLog = BusinessLayer.getMessageLogList(getBaseContext(),filterExpression);

        }
        public int getEntityID(int position){
            return lstMessageLog.get(position).ID;
        }
        public int getCount() {
            return lstMessageLog.size();
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            MessageLog entity = lstMessageLog.get(position);

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.template_message_log_information, null);

                holder.txtMessageLogMobilePatientName = (TextView) convertView.findViewById(R.id.txtMessageLogMobilePatientName);
                holder.txtMessageLogMobilePhoneNo = (TextView) convertView.findViewById(R.id.txtMessageLogMobilePhoneNo);
                holder.txtMessageLogTime = (TextView) convertView.findViewById(R.id.txtMessageLogTime);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtMessageLogMobilePatientName.setText(entity.PatientName);
            holder.txtMessageLogMobilePhoneNo.setText(entity.MobilePhoneNo);
            holder.txtMessageLogTime.setText(entity.LogTime);
            return convertView;

        }

    }
    private static class ViewHolder {
        TextView txtMessageLogMobilePatientName;
        TextView txtMessageLogMobilePhoneNo;
        TextView txtMessageLogTime;
    }
    //endregion
}
