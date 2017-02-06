package samanasoft.android.ottimo.dal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import samanasoft.android.framework.DaoBase;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.DbHelper;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
import samanasoft.android.ottimo.dal.DataLayer.PatientDao;
import samanasoft.android.ottimo.dal.DataLayer.MessageLog;
import samanasoft.android.ottimo.dal.DataLayer.MessageLogDao;
import samanasoft.android.ottimo.dal.DataLayer.Setting;
import samanasoft.android.ottimo.dal.DataLayer.SettingDao;

public class BusinessLayer {
    //region Appointment
    public static DataLayer.Appointment getAppointment(Context context, int AppointmentID)
    {
        return new DataLayer.AppointmentDao(context).get(AppointmentID);
    }
    public static int insertAppointment(Context context, DataLayer.Appointment record)
    {
        return new DataLayer.AppointmentDao(context).insert(record);
    }
    public static int updateAppointment(Context context, DataLayer.Appointment record)
    {
        return new DataLayer.AppointmentDao(context).update(record);
    }
    public static int deleteAppointment(Context context, int AppointmentID)
    {
        return new DataLayer.AppointmentDao(context).delete(AppointmentID);
    }
    public static List<DataLayer.Appointment> getAppointmentList(Context context, String filterExpression){
        List<DataLayer.Appointment> result = new ArrayList<DataLayer.Appointment>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(DataLayer.Appointment.class);
            String query = helper.select(filterExpression);
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add((DataLayer.Appointment)helper.dataReaderToObject(reader, new DataLayer.Appointment()));
                }
                while(reader.moveToNext());
            }

            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            daoBase.close();
        }
        return result;
    }
    public static WebServiceResponse getWebServiceListAppointment(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetvMobileAppointmentPerPatientList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.Appointment> lst = new ArrayList<DataLayer.Appointment>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((DataLayer.Appointment)WebServiceHelper.JSONObjectToObject(row, new DataLayer.Appointment()));
            }
            result.returnObj = lst;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    //endregion
    //region Patient
    public static Patient getPatient(Context context, int MRN)
    {
        return new PatientDao(context).get(MRN);
    }
    public static int insertPatient(Context context, Patient record)
    {
        return new PatientDao(context).insert(record);
    }
    public static int updatePatient(Context context, Patient record)
    {
        return new PatientDao(context).update(record);
    }
    public static int deletePatient(Context context, int MRN)
    {
        return new PatientDao(context).delete(MRN);
    }
    public static List<Patient> getPatientList(Context context, String filterExpression){
        List<Patient> result = new ArrayList<Patient>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(Patient.class);
            String query = helper.select(filterExpression);
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add((Patient)helper.dataReaderToObject(reader, new Patient()));
                }
                while(reader.moveToNext());
            }

            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            daoBase.close();
        }
        return result;
    }
    public static WebServiceResponse getWebServiceListPatient(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetvMobilePatientList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.Patient> lst = new ArrayList<DataLayer.Patient>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((DataLayer.Patient)WebServiceHelper.JSONObjectToObject(row, new Patient()));
            }
            result.returnObj = lst;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    //endregion
    //region MessageLog
    public static MessageLog getMessageLog(Context context, int ID)
    {
        return new MessageLogDao(context).get(ID);
    }
    public static int insertMessageLog(Context context, MessageLog record)
    {
        return new MessageLogDao(context).insert(record);
    }
    public static int updateMessageLog(Context context, MessageLog record)
    {
        return new MessageLogDao(context).update(record);
    }
    public static int deleteMessageLog(Context context, int ID)
    {
        return new MessageLogDao(context).delete(ID);
    }
    public static List<MessageLog> getMessageLogList(Context context, String filterExpression){
        List<MessageLog> result = new ArrayList<MessageLog>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(MessageLog.class);
            String query = helper.select(filterExpression);
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add((MessageLog)helper.dataReaderToObject(reader, new MessageLog()));
                }
                while(reader.moveToNext());
            }

            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            daoBase.close();
        }
        return result;
    }
    public static WebServiceResponse getWebServiceListMessageLog(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetvMobileMessageLogList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.MessageLog> lst = new ArrayList<DataLayer.MessageLog>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((DataLayer.MessageLog)WebServiceHelper.JSONObjectToObject(row, new MessageLog()));
            }
            result.returnObj = lst;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    //endregion
	//region Setting
	public static Setting getSetting(Context context, String settingCode)
    {
        return new SettingDao(context).get(settingCode);
    }
	public static int insertSetting(Context context, Setting record)
    {
        return new SettingDao(context).insert(record);
    }
	public static int updateSetting(Context context, Setting record)
    {
        return new SettingDao(context).update(record);
    }
	public static int deleteSetting(Context context, String settingCode)
    {
        return new SettingDao(context).delete(settingCode);
    }
	public static List<Setting> getSettingList(Context context, String filterExpression){
		List<Setting> result = new ArrayList<Setting>();
		DaoBase daoBase = new DaoBase(context);
        try
        {
        	DbHelper helper = new DbHelper(Setting.class);
        	String query = helper.select(filterExpression);
        	Cursor reader = daoBase.getDataReader(query);
        	if(reader.moveToFirst()){
        		do {
        			 result.add((Setting)helper.dataReaderToObject(reader, new Setting()));
                }
                while(reader.moveToNext());
        	}
               
            reader.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
        	daoBase.close();
        }
		return result;
	}
	//endregion
    //region vAppointment
    public static List<DataLayer.vAppointment> getvAppointmentList(Context context, String filterExpression){
        List<DataLayer.vAppointment> result = new ArrayList<DataLayer.vAppointment>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(DataLayer.vAppointment.class);
            String query = helper.select(filterExpression);
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add((DataLayer.vAppointment)helper.dataReaderToObject(reader, new DataLayer.vAppointment()));
                }
                while(reader.moveToNext());
            }

            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            daoBase.close();
        }
        return result;
    }
    //endregion

    public static WebServiceResponse postAppointmentAnswer(Context context, Integer appointmentID, String GCAppointmentStatus){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.PostAppointmentAnswer(context, appointmentID, GCAppointmentStatus);
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public static WebServiceResponse ChangePassword(Context context, Integer MRN, String oldPassword, String newPassword){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.ChangePassword(context, MRN, oldPassword, newPassword);
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}
