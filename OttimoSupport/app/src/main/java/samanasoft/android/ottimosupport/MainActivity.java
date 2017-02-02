package samanasoft.android.ottimosupport;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import android.widget.CompoundButton;
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
import samanasoft.android.ottimo.dal.DataLayer;
import samanasoft.android.ottimo.dal.DataLayer.Variable;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;
import samanasoft.android.ottimo.dal.DataLayer.TemplateText;
import samanasoft.android.ottimo.dal.DataLayer.Paramedic;
import samanasoft.android.ottimo.dal.DataLayer.VaccinationType;

public class MainActivity extends ActionBarActivity {
    EditText txtAppointmentDate = null;
    Spinner spnParamedic = null;
    Spinner spnTemplateText = null;
    MultiSpinner spnVaccinationType = null;
    Spinner spnStatus = null;
    Spinner spnVisitType = null;
    List<TemplateText> lstTemplateText = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAppointmentDate = (EditText) findViewById(R.id.txtAppointmentDate);
        txtAppointmentDate.setText(DateTime.tomorrow().toString("dd-MM-yyyy"));

        handler.post(new Runnable() { public void run() { (new GetListParamedicTask()).execute();}});
        handler.post(new Runnable() { public void run() { (new GetListTemplateTextTask()).execute();}});
        handler.post(new Runnable() {
            public void run() {
                (new GetListVaccinationTypeTask()).execute();
            }
        });

        List<Variable> lstStatus = new ArrayList<>();
        lstStatus.add(new Variable(0, "--Semua--"));
        lstStatus.add(new Variable(1, "Belum Dikirim"));
        lstStatus.add(new Variable(2, "Sudah Dikirim"));
        StatusAdapter adapter = new StatusAdapter(getBaseContext(),android.R.layout.simple_spinner_item,lstStatus);
        spnStatus = (Spinner) findViewById(R.id.spnStatus);
        spnStatus.setAdapter(adapter); // Set the custom adapter to the spinner
        spnStatus.setSelection(1);

        List<Variable> lstVisitType = new ArrayList<>();
        lstVisitType.add(new Variable(0, "--Semua Jenis Kunjungan--"));
        lstVisitType.add(new Variable(1, "Vaksin"));
        lstVisitType.add(new Variable(2, "Kecuali Vaksin"));
        adapter = new StatusAdapter(getBaseContext(),android.R.layout.simple_spinner_item,lstVisitType);
        spnVisitType = (Spinner) findViewById(R.id.spnVisitType);
        spnVisitType.setAdapter(adapter); // Set the custom adapter to the spinner
        spnVisitType.setSelection(0);
        spnVisitType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spnVaccinationType != null) {
                    int visitType = (int) spnVisitType.getSelectedItemId();
                    if (visitType == 1)
                        spnVaccinationType.setVisibility(View.VISIBLE);
                    else
                        spnVaccinationType.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        lvwAppointment = (ListView)findViewById(R.id.lvwPatient);
        lvwAppointment.setDivider(null);
        lvwAppointment.setDividerHeight(0);

        txtAppointmentDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(MainActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshListAppointment();
            }
        });


    }

    BroadcastReceiver sentReceiver;
    BroadcastReceiver deliveredReceiver;
    @Override
    protected void onStart() {
        startService(new Intent(this, SMSSentStatusService.class));
        /*String SENT = "sent";
        String DELIVERED = "delivered";
// ---when the SMS has been sent---

        sentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int appointmentID = intent.getIntExtra("appointmentID", -1);
                String mobilePhoneNo = intent.getStringExtra("mobilePhoneNo");
                String status = "";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        status = Constant.SMSLogStatus.SMS_SENT;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "Generic Failuere");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "No Service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "Error Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "Radio Off");
                        break;
                    default:
                        status = Constant.SMSLogStatus.FAILED;
                        Log.d("SMS Failed", "Default");
                        break;
                }

                Log.d("SMS Sent", status);

                ThreadUpdateAppointmentStatus thread = new ThreadUpdateAppointmentStatus(getBaseContext(), appointmentID, mobilePhoneNo, status);
                thread.start();
            }
        };
        registerReceiver(sentReceiver, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        deliveredReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                int appointmentID = intent.getIntExtra("appointmentID", -1);
                String mobilePhoneNo = intent.getStringExtra("mobilePhoneNo");
                String status = "";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        status = Constant.SMSLogStatus.SMS_DELIVERED;
                        break;
                    case Activity.RESULT_CANCELED:
                        status = Constant.SMSLogStatus.FAILED;
                        break;
                }
                ThreadUpdateAppointmentStatus thread = new ThreadUpdateAppointmentStatus(getBaseContext(), appointmentID, mobilePhoneNo, status);
                thread.start();
            }
        };
        registerReceiver(deliveredReceiver, new IntentFilter(DELIVERED));
*/
        super.onStart();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        //unregisterReceiver(sentReceiver);
        //unregisterReceiver(deliveredReceiver);
        super.onStop();
    }

    private class CTemp {
        public CheckBox chk;
        public Appointment entity;
    }
    private void sendSMS(int mobilePhoneType) {
        SharedPreferences prefs = getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);

        int templateTextID = (int)spnTemplateText.getSelectedItemId();
        String textMessage = "";
        for (TemplateText entity : lstTemplateText) {
            //Log.d("testtest", entity.PreferredName + "; " + entity.MedicalNo + "; " + entity.HomeAddress + "; " + entity.DateOfBirth.toString("dd-MMM-yyyy"));
            if(entity.TemplateID == templateTextID)
                textMessage = entity.TemplateContent;
        }

        Log.d("message", textMessage);
        String lstAppointmentID = "";
        int i = 0;
        for (Appointment entity : lstAppointment) {
            if (entity.IsChecked) {
                if (lstAppointmentID != "")
                    lstAppointmentID += ",";
                lstAppointmentID += entity.AppointmentID;

                try {
                    if (mobilePhoneType == 1) {
                        RunnableSendSMS obj = new RunnableSendSMS(textMessage, entity, 1);
                        handler.postDelayed(obj, 5000 * i);
                        i++;
                    } else {
                        if (entity.MobilePhoneNo2 != null && !entity.MobilePhoneNo2.isEmpty()) {
                            RunnableSendSMS obj = new RunnableSendSMS(textMessage, entity, 2);
                            handler.postDelayed(obj, 5000 * 1);
                            i++;
                        }
                    }

                            /*MessageLog log = new MessageLog();
                            log.LogDate = DateTime.now();
                            log.LogTime = DateTime.now().toString("HH:mm");
                            log.MobilePhoneNo = entity.MobilePhoneNo;
                            log.PatientName = entity.PatientName;
                            log.MessageText = message;
                            BusinessLayer.insertMessageLog(getBaseContext(), log);*/
                } catch (Exception ex) {
                    Toast.makeText(getBaseContext(), "SMS ke " + entity.MobilePhoneNo + " gagal terkirim. " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        Toast.makeText(getBaseContext(), "Message Sent", Toast.LENGTH_SHORT).show();
        RefreshListAppointment();
    }

    public class RunnableSendSMS implements Runnable {
        private String textMessage;
        private Appointment entity;
        private int type;
        public RunnableSendSMS(String textMessage, Appointment entity, int type) {
            this.textMessage = textMessage;
            this.entity = entity;
            this.type = type;
        }

        public void run() {
            if(type == 1)
                sendSMSPerMobilePhoneNo(textMessage, entity, entity.MobilePhoneNo, type);
            else
                sendSMSPerMobilePhoneNo(textMessage, entity, entity.MobilePhoneNo2, type);
        }
    }

    private void sendSMSPerMobilePhoneNo(String textMessage, Appointment entity, String mobilePhoneNo, int mobilePhoneType){
        String message = textMessage.replace("{PatientName}", entity.PatientName)
                .replace("{StartDate}", entity.StartDate.toString("dd/MM/yy"))
                .replace("{StartTime}", entity.StartTime)
                .replace("{EndTime}", entity.EndTime)
                .replace("{PreferredName}", entity.PreferredName)
                .replace("{cfStartTime}", entity.cfStartTime)
                .replace("{ParamedicName}", entity.ParamedicName)
                .replace("{VisitTypeName}", entity.VisitTypeName)
                .replace("{ServiceUnitName}", entity.ServiceUnitName);
        SmsManager sms = SmsManager.getDefault();
        String SENT = "sent";
        String DELIVERED = "delivered";

        Intent sentIntent = new Intent(SENT);
        sentIntent.putExtra("appointmentID", entity.AppointmentID);
        sentIntent.putExtra("mobilePhoneNo", mobilePhoneNo);

        Intent deliveredIntent = new Intent(DELIVERED);
        deliveredIntent.putExtra("appointmentID", entity.AppointmentID);
        deliveredIntent.putExtra("mobilePhoneNo", mobilePhoneNo);

        int id = (entity.AppointmentID * 10) + mobilePhoneType;
        ArrayList<PendingIntent> lstSentPI = new ArrayList<>();
        ArrayList<PendingIntent> lstDeliveredPI = new ArrayList<>();

        ArrayList parts = sms.divideMessage(message);
        int numParts = parts.size();

        for (int i = 0; i < numParts; i++) {
            lstSentPI.add(PendingIntent.getBroadcast(this, id, sentIntent, PendingIntent.FLAG_ONE_SHOT));
            lstDeliveredPI.add(PendingIntent.getBroadcast(this, id, deliveredIntent, PendingIntent.FLAG_ONE_SHOT));
        }

        sms.sendMultipartTextMessage(mobilePhoneNo, null, parts, lstSentPI, lstDeliveredPI);
        ThreadUpdateAppointmentStatus thread = new ThreadUpdateAppointmentStatus(getBaseContext(), entity.AppointmentID, mobilePhoneNo, Constant.SMSLogStatus.SENDING_SMS);
        thread.start();
    }

    private class ThreadUpdateAppointmentStatus extends Thread{
        private Context context;
        private int appointmentID;
        private String mobilePhoneNo;
        private String GCSMSLogStatus;
        ThreadUpdateAppointmentStatus(Context context, int appointmentID, String mobilePhoneNo, String GCSMSLogStatus){
            this.context = context;
            this.appointmentID = appointmentID;
            this.mobilePhoneNo = mobilePhoneNo;
            this.GCSMSLogStatus = GCSMSLogStatus;
        }

        @Override
        public void run() {
            try {
                BusinessLayer.updateAppointmentStatusSMS(context, appointmentID, mobilePhoneNo, GCSMSLogStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onRadioButtonClicked(View view) {
        switch(view.getId()) {
            case R.id.radio_checkall:
                for(CheckBox chk : lstCheckBox){
                    chk.setChecked(true);
                }
                break;
            case R.id.radio_uncheckall:
                for(CheckBox chk : lstCheckBox){
                    chk.setChecked(false);
                }
                break;
        }
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
        txtAppointmentDate.setText(sdf.format(myCalendar.getTime()));
    }

    //region Paramedic
    private class GetListParamedicTask extends AsyncTask<String, Double, WebServiceResponse> {
        private boolean isRefreshParamedicList = false;

        @Override
        protected void onPreExecute() {
            if (!isRefreshParamedicList) {
                isRefreshParamedicList = true;
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Dokter...", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Dokter Already In Progress...", Toast.LENGTH_SHORT).show();
                    }
                });
                this.cancel(true);
            }
        }

        protected WebServiceResponse doInBackground(String... args) {
            String filterExpression = String.format("");
            Log.d("filterExpression", filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListParamedic(getBaseContext(), filterExpression);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Dokter Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        protected void onPostExecute(WebServiceResponse result) {
            isRefreshParamedicList = false;
            if(result != null) {

                if (result.returnObj != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getBaseContext(), "Get Dokter Completed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    @SuppressWarnings("unchecked")
                    List<Paramedic> lstEntity = (List<Paramedic>) result.returnObj;
                    for (Paramedic entity : lstEntity) {
                        //Log.d("testtest", entity.PreferredName + "; " + entity.MedicalNo + "; " + entity.HomeAddress + "; " + entity.DateOfBirth.toString("dd-MMM-yyyy"));
                        Log.d("test", entity.ParamedicName);

                    }
                    ParamedicAdapter paramedicAdapter = new ParamedicAdapter(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, lstEntity);
                    spnParamedic = (Spinner) findViewById(R.id.spnParamedic);
                    spnParamedic.setAdapter(paramedicAdapter); // Set the custom adapter to the spinner
                }
            }
        }
    }
    public class ParamedicAdapter extends ArrayAdapter<Paramedic>{

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<Paramedic> values;

        public ParamedicAdapter(Context context, int textViewResourceId,List<Paramedic> values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        public int getCount(){
            return values.size();
        }

        public Paramedic getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return values.get(position).ParamedicID;
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
            label.setText(values.get(position).ParamedicName);

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
            label.setText(values.get(position).ParamedicName);

            return label;
        }
    }
    //endregion

    //region TemplateText
    private class GetListTemplateTextTask extends AsyncTask<String, Double, WebServiceResponse> {
        private boolean isRefreshTemplateTextList = false;

        @Override
        protected void onPreExecute() {
            if (!isRefreshTemplateTextList) {
                isRefreshTemplateTextList = true;
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Template Text...", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Template Text Already In Progress...", Toast.LENGTH_SHORT).show();
                    }
                });
                this.cancel(true);
            }
        }

        protected WebServiceResponse doInBackground(String... args) {
            String filterExpression = String.format("");
            Log.d("filterExpression", filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListTemplateText(getBaseContext(), filterExpression);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Template Text Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        protected void onPostExecute(WebServiceResponse result) {
            isRefreshTemplateTextList = false;
            if(result != null) {

                if (result.returnObj != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getBaseContext(), "Get Template Text Completed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    lstTemplateText = (List<TemplateText>) result.returnObj;
                    Log.d("Template Text", "" + lstTemplateText.size());
                    for (TemplateText entity : lstTemplateText) {
                        //Log.d("testtest", entity.PreferredName + "; " + entity.MedicalNo + "; " + entity.HomeAddress + "; " + entity.DateOfBirth.toString("dd-MMM-yyyy"));
                        Log.d("Template Text", "a");
                        Log.d("Template Text", entity.TemplateName);

                    }
                    TemplateTextAdapter TemplateTextAdapter = new TemplateTextAdapter(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, lstTemplateText);
                    spnTemplateText = (Spinner) findViewById(R.id.spnTemplateText);
                    spnTemplateText.setAdapter(TemplateTextAdapter); // Set the custom adapter to the spinner
                }
            }
        }
    }
    public class TemplateTextAdapter extends ArrayAdapter<TemplateText>{

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<TemplateText> values;

        public TemplateTextAdapter(Context context, int textViewResourceId,List<TemplateText> values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        public int getCount(){
            return values.size();
        }

        public TemplateText getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return values.get(position).TemplateID;
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
            label.setText(values.get(position).TemplateName);

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
            label.setText(values.get(position).TemplateName);

            return label;
        }
    }
    //endregion

    //region VaccinationType
    private class GetListVaccinationTypeTask extends AsyncTask<String, Double, WebServiceResponse> {
        private boolean isRefreshVaccinationTypeList = false;
        @Override
        protected void onPreExecute() {
            if (!isRefreshVaccinationTypeList) {
                isRefreshVaccinationTypeList = true;
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Vaccination...", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Vaccination Already In Progress...", Toast.LENGTH_SHORT).show();
                    }
                });
                this.cancel(true);
            }
        }

        protected WebServiceResponse doInBackground(String... args) {
            String filterExpression = String.format("1 = 1 ORDER BY DisplayOrder");
            Log.d("filterExpression", filterExpression);
            try {
                WebServiceResponse result = BusinessLayer.getWebServiceListVaccinationType(getBaseContext(), filterExpression);
                return result;
            }
            catch (Exception ex) {
                Toast.makeText(getBaseContext(), "Get Vaksin Failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
        protected void onPostExecute(WebServiceResponse result) {
            if(result != null) {
                isRefreshVaccinationTypeList = false;
                if (result.returnObj != null) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(getBaseContext(), "Get Vaccination Completed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    @SuppressWarnings("unchecked")
                    List<VaccinationType> lstEntity = (List<VaccinationType>) result.returnObj;
                    String lstVaccinationTypeName = "";
                    List<String> lstName = new ArrayList<>();
                    for (VaccinationType entity : lstEntity) {
                        if (lstVaccinationTypeName != "")
                            lstVaccinationTypeName += ", ";
                        //Log.d("testtest", entity.PreferredName + "; " + entity.MedicalNo + "; " + entity.HomeAddress + "; " + entity.DateOfBirth.toString("dd-MMM-yyyy"));
                        lstVaccinationTypeName += entity.VaccinationTypeName;
                        lstName.add(entity.VaccinationTypeName);

                    }
                    //VaccinationTypeAdapter VaccinationTypeAdapter = new VaccinationTypeAdapter(getBaseContext(),android.R.layout.mu/,lstEntity);
                    spnVaccinationType = (MultiSpinner) findViewById(R.id.spnVaccinationType);
                    spnVaccinationType.setItems(lstName, lstEntity, lstVaccinationTypeName, new MultiSpinner.MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] selected) {

                        }
                    });
                }
            }
        }
    }
    //endregion

    //region Status
    public class StatusAdapter extends ArrayAdapter<Variable>{

        // Your sent context
        private Context context;
        // Your custom values for the spinner (User)
        private List<Variable> values;

        public StatusAdapter(Context context, int textViewResourceId,List<Variable> values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        public int getCount(){
            return values.size();
        }

        public Variable getItem(int position){
            return values.get(position);
        }

        public long getItemId(int position){
            return values.get(position).Code;
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
            label.setText(values.get(position).Value);

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
            label.setText(values.get(position).Value);

            return label;
        }
    }
    //endregion

    private static Handler handler = new Handler();
    //region Appointment
    private class GetListAppointmentTask extends AsyncTask<String, Double, WebServiceResponse> {
        private boolean isRefreshAppointmentList = false;
        private DateTime appointmentDate = null;
        private int paramedicID = 0;
        private int status = 0;
        private int visitType = 0;
        private String lstVaccinationTypeID = "";
        public GetListAppointmentTask(DateTime appointmentDate, int paramedicID, int status, int visitType, String lstVaccinationTypeID){
            this.appointmentDate = appointmentDate;
            this.paramedicID = paramedicID;
            this.status = status;
            this.visitType = visitType;
            this.lstVaccinationTypeID = lstVaccinationTypeID;
        }
        @Override
        protected void onPreExecute() {
            if(!isRefreshAppointmentList){
                isRefreshAppointmentList = true;
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Appointment...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Appointment Already In Progress...", Toast.LENGTH_SHORT).show();
                    }
                });
                this.cancel(true);
            }
        }

        protected WebServiceResponse doInBackground(String... args) {
            String filterExpression = String.format("StartDate = '%1$s' AND ParamedicID = %2$s AND ISNULL(MobilePhoneNo,'') != ''", appointmentDate.toString("yyyy-MM-dd"), paramedicID);
            if(status == 1)
                filterExpression += " AND NoOfMessageSent = 0";
            else if(status == 2)
                filterExpression += " AND NoOfMessageSent > 0";

            if(visitType == 1) {
                filterExpression += String.format(" AND GCVisitType IN ('%1$s','%2$s')", "OT014^003", "OT014^007");
                if(lstVaccinationTypeID != "") {
                    filterExpression += String.format(" AND AppointmentID IN (SELECT AppointmentID FROM AppointmentVaccinationType WHERE VaccinationTypeID IN (%1$s))", lstVaccinationTypeID);
                }
            }
            else if(visitType == 2)
                filterExpression += String.format(" AND GCVisitType NOT IN ('%1$s','%2$s')", "OT014^003", "OT014^007");

            filterExpression += " ORDER BY StartTime";
            Log.d("test",filterExpression);
            WebServiceResponse result = BusinessLayer.getWebServiceListAppointment(getBaseContext(),filterExpression);
            return result;
        }
        public static final String ACTION_SMS_SENT = "com.example.android.apis.os.SMS_SENT_ACTION";
        protected void onPostExecute(WebServiceResponse result) {
            isRefreshAppointmentList = false;
            if(result.returnObj != null){
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getBaseContext(), "Get Appointment Completed", Toast.LENGTH_SHORT).show();
                    }
                });

                List<CheckBox> lstCheckBox = new ArrayList<>();
                @SuppressWarnings("unchecked")
                List<Appointment> lstAppointment1 = (List<Appointment>) result.returnObj;
                for (Appointment entity : lstAppointment1) {
                    //Log.d("testtest", entity.PreferredName + "; " + entity.MedicalNo + "; " + entity.HomeAddress + "; " + entity.DateOfBirth.toString("dd-MMM-yyyy"));
                    Log.d("test", entity.PatientName);
                    if(entity.MobilePhoneNo.startsWith("0")){
                        entity.MobilePhoneNo = "+62" + entity.MobilePhoneNo.substring(1);
                    }
                    if(entity.MobilePhoneNo2.startsWith("0")){
                        entity.MobilePhoneNo2 = "+62" + entity.MobilePhoneNo2.substring(1);
                    }
                    Log.d("test", entity.MobilePhoneNo);
                    Log.d("mobilePhoneNo2", entity.MobilePhoneNo2);

                    CheckBox chk = new CheckBox(getBaseContext());
                    lstCheckBox.add(chk);
                }
                fillListAppointment(lstAppointment1, lstCheckBox);
            }
        }
    }

    private AppointmentInformationAdapter adapter;
    private void fillListAppointment(List<Appointment> lstAppointment1, List<CheckBox> lstCheckBox){
        adapter = new AppointmentInformationAdapter(getBaseContext(), lstAppointment1, lstCheckBox);
        lvwAppointment.setAdapter(adapter);
    }
    private List<Appointment> lstAppointment = null;
    private ListView lvwAppointment;
    private List<CheckBox> lstCheckBox = null;
    //region Adapter
    private class AppointmentInformationAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;

        public AppointmentInformationAdapter(Context context, List<Appointment> lstAppointment1, List<CheckBox> lstCheckBox1) {
            mInflater = LayoutInflater.from(context);
            this.context = context;
            lstCheckBox = lstCheckBox1;
            lstAppointment = lstAppointment1;
            lstCheckedAppointment = new ArrayList<>();

        }
        public List<Appointment> lstCheckedAppointment;
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

                holder.chkAppointment = (CheckBox) convertView.findViewById(R.id.chkAppointment);
                holder.txtAppointmentInformationPatientName = (TextView) convertView.findViewById(R.id.txtAppointmentInformationPatientName);
                holder.txtAppointmentInformationParamedicName = (TextView) convertView.findViewById(R.id.txtAppointmentInformationParamedicName);
                holder.txtAppointmentInformationVisitTypeName = (TextView) convertView.findViewById(R.id.txtAppointmentInformationVisitTypeName);
                holder.txtAppointmentNoOfMessageSent = (TextView) convertView.findViewById(R.id.txtAppointmentNoOfMessageSent);
                holder.txtSMSLogStatus = (TextView) convertView.findViewById(R.id.txtSMSLogStatus);
                convertView.setTag(holder);

                lstCheckBox.set(position, holder.chkAppointment);
                holder.chkAppointment.setTag(position);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            holder.txtAppointmentInformationPatientName.setText(entity.PatientName + " (" + entity.getMobilePhoneNoDisplay() + ")");
            holder.txtAppointmentInformationParamedicName.setText(entity.ParamedicName);
            holder.txtAppointmentInformationVisitTypeName.setText(entity.VisitTypeName + " (" + entity.StartTime + ")");
            holder.txtAppointmentNoOfMessageSent.setText(Integer.toString(entity.NoOfMessageSent));
            holder.txtSMSLogStatus.setText(entity.SMSLogStatus);

            holder.chkAppointment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked)
                        entity.IsChecked = true;
                    else
                        entity.IsChecked = false;
                    Log.d("tes", entity.PatientName);

                }
            });
            holder.chkAppointment.setChecked(entity.IsChecked);
            return convertView;

        }

    }
    private static class ViewHolder {
        CheckBox chkAppointment;
        TextView txtAppointmentInformationPatientName;
        TextView txtAppointmentInformationParamedicName;
        TextView txtAppointmentInformationVisitTypeName;
        TextView txtAppointmentNoOfMessageSent;
        TextView txtSMSLogStatus;
    }
    //endregion
    //endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private static final int RESULT_SETTINGS = 1;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, UserSettingActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
        }
        else if (id == R.id.action_sendmessage1) {
            sendSMS(1);
        }
        else if (id == R.id.action_sendmessage2) {
            sendSMS(2);
        }
        else if (id == R.id.action_view_log) {
            Intent i = new Intent(this, MessageLogActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
        }

        return super.onOptionsItemSelected(item);
    }

    private void RefreshListAppointment(){
        handler.post(new Runnable() {
            public void run() {
                int paramedicID = (int)spnParamedic.getSelectedItemId();
                int status = (int)spnStatus.getSelectedItemId();
                int visitType = (int)spnVisitType.getSelectedItemId();
                String lstVaccinationTypeID = spnVaccinationType.getSelectedItemID();
                String[] tempDate = txtAppointmentDate.getText().toString().split("-");
                DateTime appointmentDate = new DateTime(Convert.ObjectToInt(tempDate[2]),Convert.ObjectToInt(tempDate[1]),Convert.ObjectToInt(tempDate[0]),0,0,0);
                (new GetListAppointmentTask(appointmentDate, paramedicID, status, visitType, lstVaccinationTypeID)).execute();
            }
        });
    }
}
