package samanasoft.android.ottimosupport;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.common.Constant;
import samanasoft.android.ottimo.common.Methods;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer;

public class SchedulerEventService extends Service {
    private static final String APP_TAG = "com.hascode.android.scheduler";
    private Context context = null;
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(APP_TAG, "event received in service: " + new Date().toString());
        (new GetListAppointmentTask(this)).execute();
        return Service.START_NOT_STICKY;
    }

    private static class GetListAppointmentTask extends AsyncTask<String, Double, WebServiceResponse> {
        private static boolean isRefreshAppointmentList = false;
        private Context context = null;
        GetListAppointmentTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            if(!isRefreshAppointmentList){
                isRefreshAppointmentList = true;
            }
            else{
                this.cancel(true);
            }
        }

        protected WebServiceResponse doInBackground(String... args) {
            Calendar gc = Calendar.getInstance();
            gc.add(Calendar.DATE, 1);
            DateTime appointmentDate = new DateTime(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH) + 1, gc.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            String filterExpression = String.format("StartDate = '%1$s'", appointmentDate.toString(Constant.FormatString.DATE_FORMAT_112));
            WebServiceResponse result = null;
            try {
                result = BusinessLayer.getWebServiceListAppointment(context, filterExpression);
            } catch (final JSONException e) {

            }
            return result;
        }
        protected void onPostExecute(WebServiceResponse result) {
            isRefreshAppointmentList = false;

            //@SuppressWarnings("unchecked")
            //SharedPreferences prefs = context.getSharedPreferences(Constant.SharedPreference.NAME, MODE_PRIVATE);
            //String textMessage = prefs.getString(Constant.SharedPreference.APPOINTMENT_REMINDER_MESSAGE, "");
            //Log.d("message",textMessage);
           // @SuppressWarnings("unchecked")
            //List<DataLayer.Appointment> lst = (List<DataLayer.Appointment>) result.returnObj;
            //for(DataLayer.Appointment entity : lst) {
                //Log.d("testtest", entity.PreferredName + "; " + entity.MedicalNo + "; " + entity.HomeAddress + "; " + entity.DateOfBirth.toString("dd-MMM-yyyy"));
               // Log.d("test", entity.PatientName);
                //String message = textMessage.replace("{PatientName}", entity.PatientName)
                    //    .replace("{StartDate}", entity.StartDate.toString(Constant.FormatString.DATE_FORMAT))
                    //    .replace("{StartTime}", entity.StartTime)
                     //   .replace("{ParamedicName}", entity.ParamedicName)
                   //     .replace("{ServiceUnitName}", entity.ServiceUnitName);
               // SmsManager sms = SmsManager.getDefault();
               // sms.sendTextMessage(entity.MobilePhoneNo, null, message, null, null);

           //}
            //Log.d("testtest", "Timestamp : " + result.timestamp.toString("dd-MMM-yyyy HH:mm:ss"));
            //Methods.SharedPreference.setSharedPrefencesValue(context, Constant.SharedPreference.TIMESTAMP_UPDATE_APPOINTMENT, result.timestamp.toString(Constant.FormatString.DATE_TIME_FORMAT));
        }

    }

}
