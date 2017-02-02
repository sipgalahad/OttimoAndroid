package samanasoft.android.ottimo.dal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;

import samanasoft.android.framework.DaoBase;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.DbHelper;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.DataLayer.Appointment;
import samanasoft.android.ottimo.dal.DataLayer.AppointmentDao;
import samanasoft.android.ottimo.dal.DataLayer.MessageLog;
import samanasoft.android.ottimo.dal.DataLayer.MessageLogDao;
import samanasoft.android.ottimo.dal.DataLayer.Paramedic;
import samanasoft.android.ottimo.dal.DataLayer.VaccinationType;
import samanasoft.android.ottimo.dal.DataLayer.Setting;
import samanasoft.android.ottimo.dal.DataLayer.SettingDao;

public class BusinessLayer {
    //region Appointment
    public static Appointment getAppointment(Context context, int AppointmentID)
    {
        return new AppointmentDao(context).get(AppointmentID);
    }
    public static int insertAppointment(Context context, Appointment record)
    {
        return new AppointmentDao(context).insert(record);
    }
    public static int updateAppointment(Context context, Appointment record)
    {
        return new AppointmentDao(context).update(record);
    }
    public static int deleteAppointment(Context context, int AppointmentID)
    {
        return new AppointmentDao(context).delete(AppointmentID);
    }
    public static List<Appointment> getAppointmentList(Context context, String filterExpression){
        List<Appointment> result = new ArrayList<Appointment>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(Appointment.class);
            String query = helper.select(filterExpression);
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add((Appointment)helper.dataReaderToObject(reader, new Appointment()));
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
    public static WebServiceResponse getWebServiceListParamedic(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetvMobileParamedicMasterList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.Paramedic> lst = new ArrayList<DataLayer.Paramedic>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((DataLayer.Paramedic)WebServiceHelper.JSONObjectToObject(row, new Paramedic()));
            }
            result.returnObj = lst;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public static WebServiceResponse getWebServiceListVaccinationType(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetvMobileVaccinationTypeList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<VaccinationType> lst = new ArrayList<VaccinationType>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((VaccinationType)WebServiceHelper.JSONObjectToObject(row, new VaccinationType()));
            }
            result.returnObj = lst;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public static WebServiceResponse getWebServiceListTemplateText(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetvMobileSMSReminderTemplateTextList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.TemplateText> lst = new ArrayList<DataLayer.TemplateText>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((DataLayer.TemplateText)WebServiceHelper.JSONObjectToObject(row, new DataLayer.TemplateText()));
            }
            result.returnObj = lst;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public static WebServiceResponse getWebServiceListAppointment(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetvMobileAppointmentList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.Appointment> lst = new ArrayList<DataLayer.Appointment>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((DataLayer.Appointment)WebServiceHelper.JSONObjectToObject(row, new Appointment()));
            }
            result.returnObj = lst;
            result.timestamp = timestamp;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public static WebServiceResponse postAppointmentAnswer(Context context, String mobilePhoneNo, String replyText){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.PostAppointmentAnswer(context, mobilePhoneNo, replyText);
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public static WebServiceResponse updateAppointmentStatusSMS(Context context, int appointmentID, String mobilePhoneNo, String GCSMSLogStatus){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.UpdateAppointmentStatusSMS(context, appointmentID, mobilePhoneNo, GCSMSLogStatus);
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
}
