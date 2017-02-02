package samanasoft.android.kiddielogicpatient;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import java.util.List;

import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.BusinessLayer;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;
import samanasoft.android.framework.Constant;

public class SchedulerEventLoadDataService extends Service {
    private static final String APP_TAG = "com.hascode.android.scheduler";
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        (new GetPatientTask(this)).execute();
        return Service.START_NOT_STICKY;
    }

    private static class GetPatientTask extends AsyncTask<String, Double, WebServiceResponse> {
        private static boolean isRefreshPatientList = false;
        private Context context = null;
        GetPatientTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            if(!isRefreshPatientList){
                isRefreshPatientList = true;
            }
            else{
                this.cancel(true);
            }
        }

        protected WebServiceResponse doInBackground(String... args) {
            List<Patient> lstPatient = BusinessLayer.getPatientList(context, "");
            String filterExpression = "";
            for(Patient entityPatient : lstPatient){
                if(filterExpression != "")
                    filterExpression += " OR ";
                filterExpression += String.format("(MRN = '%1$s' AND LastUpdatedDate > '%2$s')", entityPatient.MRN, entityPatient.LastSyncDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB));
            }
            WebServiceResponse result = BusinessLayer.getWebServiceListPatient(context, filterExpression);
            return result;
        }
        protected void onPostExecute(WebServiceResponse result) {
            isRefreshPatientList = false;
            if(result != null && result.returnObj != null){
                @SuppressWarnings("unchecked")
                List<Patient> lstPatient = (List<Patient>) result.returnObj;

                String lstAppointmentID = "";
                for (Patient entity : lstPatient) {
                    entity.LastSyncDateTime = result.timestamp;
                    BusinessLayer.updatePatient(context, entity);
                }
            }
            (new GetListAppointmentTask(context)).execute();
        }

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
            List<Patient> lstPatient = BusinessLayer.getPatientList(context, "");
            String filterExpression = "";
            for(Patient entityPatient : lstPatient){
                if(filterExpression != "")
                    filterExpression += " OR ";
                filterExpression += String.format("(MRN = '%1$s' AND LastUpdatedDate > '%2$s')", entityPatient.MRN, entityPatient.LastSyncAppointmentDateTime.toString(Constant.FormatString.DATE_TIME_FORMAT_DB));
            }
            WebServiceResponse result = BusinessLayer.getWebServiceListAppointment(context, filterExpression);
            return result;
        }
        protected void onPostExecute(WebServiceResponse result) {
            isRefreshAppointmentList = false;
            if(result != null && result.returnObj != null){
                @SuppressWarnings("unchecked")
                List<Appointment> lstAppointment = (List<Appointment>) result.returnObj;

                String lstAppointmentID = "";
                for (Appointment entity : lstAppointment) {
                    if(lstAppointmentID != "")
                        lstAppointmentID += ",";
                    lstAppointmentID += entity.AppointmentID;
                }

                if(lstAppointmentID != "") {
                    List<Appointment> lstOldAppointment = BusinessLayer.getAppointmentList(context, String.format("AppointmentID IN (%1$s)", lstAppointmentID));
                    for (Appointment entity : lstOldAppointment) {
                        BusinessLayer.deleteAppointment(context, entity.AppointmentID);
                    }

                    for (Appointment entity : lstAppointment) {
                        Appointment oldData = BusinessLayer.getAppointment(context, entity.AppointmentID);
                        if(oldData == null) {
                            if(entity.GCAppointmentStatus != Constant.AppointmentStatus.CANCELLED || entity.GCAppointmentStatus != Constant.AppointmentStatus.VOID)
                                BusinessLayer.insertAppointment(context, entity);
                        }
                        else {
                            if(entity.GCAppointmentStatus == Constant.AppointmentStatus.CANCELLED || entity.GCAppointmentStatus == Constant.AppointmentStatus.VOID)
                                BusinessLayer.deleteAppointment(context, entity.AppointmentID);
                            else
                                BusinessLayer.updateAppointment(context, entity);
                        }
                    }
                }
                List<Patient> lstPatient = BusinessLayer.getPatientList(context, "");
                for(Patient entityPatient : lstPatient) {
                    entityPatient.LastSyncAppointmentDateTime = result.timestamp;
                    BusinessLayer.updatePatient(context, entityPatient);
                }
            }
        }

    }

}
