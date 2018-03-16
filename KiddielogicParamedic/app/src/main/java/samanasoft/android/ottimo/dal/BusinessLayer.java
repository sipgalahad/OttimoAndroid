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
import samanasoft.android.framework.DbConfiguration;
import samanasoft.android.framework.DbHelper;
import samanasoft.android.framework.webservice.WebServiceHelper;
import samanasoft.android.framework.webservice.WebServiceResponse;
import samanasoft.android.ottimo.dal.DataLayer.ConsultVisit;
import samanasoft.android.ottimo.dal.DataLayer.ConsultVisitDao;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMaster;
import samanasoft.android.ottimo.dal.DataLayer.ParamedicMasterDao;
import samanasoft.android.ottimo.dal.DataLayer.Patient;
import samanasoft.android.ottimo.dal.DataLayer.PatientDao;
import samanasoft.android.ottimo.dal.DataLayer.MessageLog;
import samanasoft.android.ottimo.dal.DataLayer.MessageLogDao;
import samanasoft.android.ottimo.dal.DataLayer.Setting;
import samanasoft.android.ottimo.dal.DataLayer.SettingDao;

public class BusinessLayer {

    //region ConsultVisit
    public static ConsultVisit getConsultVisit(Context context, int MRN)
    {
        return new ConsultVisitDao(context).get(MRN);
    }

    public static int insertConsultVisit(Context context, ConsultVisit record)
    {
        return new ConsultVisitDao(context).insert(record);
    }
    public static int updateConsultVisit(Context context, ConsultVisit record)
    {
        return new ConsultVisitDao(context).update(record);
    }
    public static int deleteConsultVisit(Context context, int MRN)
    {
        return new ConsultVisitDao(context).delete(MRN);
    }
    public static List<ConsultVisit> getConsultVisitList(Context context, String filterExpression){
        List<ConsultVisit> result = new ArrayList<ConsultVisit>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(ConsultVisit.class);
            String query = helper.select(filterExpression);
            Log.d("query Pasien", query);
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add((ConsultVisit)helper.dataReaderToObject(reader, new ConsultVisit()));
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
    public static WebServiceResponse getWebServiceListConsultVisit(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetConsultVisitSOAPSyncList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.ConsultVisit> lst = new ArrayList<DataLayer.ConsultVisit>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((DataLayer.ConsultVisit)WebServiceHelper.JSONObjectToObject(row, new ConsultVisit()));
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
    //region ParamedicMaster
    public static ParamedicMaster getParamedicMaster(Context context, int ParamedicID)
    {
        return new ParamedicMasterDao(context).get(ParamedicID);
    }
    public static int insertParamedicMaster(Context context, ParamedicMaster record)
    {
        return new ParamedicMasterDao(context).insert(record);
    }
    public static int updateParamedicMaster(Context context, ParamedicMaster record)
    {
        return new ParamedicMasterDao(context).update(record);
    }
    public static int deleteParamedicMaster(Context context, int ParamedicID)
    {
        return new ParamedicMasterDao(context).delete(ParamedicID);
    }
    public static List<ParamedicMaster> getParamedicMasterList(Context context, String filterExpression){
        List<ParamedicMaster> result = new ArrayList<ParamedicMaster>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(ParamedicMaster.class);
            String query = helper.select(filterExpression);
            Log.d("query Pasien", query);
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add((ParamedicMaster)helper.dataReaderToObject(reader, new ParamedicMaster()));
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
    public static List<Integer> getParamedicMasterParamedicIDList(Context context, String filterExpression){
        List<Integer> result = new ArrayList<Integer>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(ParamedicMaster.class);
            String query = helper.selectListColumn(filterExpression, "ParamedicID");
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add(reader.getInt(0));
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
    public static WebServiceResponse getWebServiceListParamedicMaster(Context context, String filterExpression){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.getListObject(context, "GetvMobileParamedicMasterList", filterExpression);

            JSONArray returnObj = WebServiceHelper.getReturnObject(response);
            DateTime timestamp = WebServiceHelper.getTimestamp(response);

            List<DataLayer.ParamedicMaster> lst = new ArrayList<DataLayer.ParamedicMaster>();
            for (int i = 0; i < returnObj.length();++i){
                JSONObject row = (JSONObject) returnObj.get(i);
                lst.add((DataLayer.ParamedicMaster)WebServiceHelper.JSONObjectToObject(row, new ParamedicMaster()));
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
            Log.d("query Pasien", query);
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
    public static List<Integer> getPatientMRNList(Context context, String filterExpression){
        List<Integer> result = new ArrayList<Integer>();
        DaoBase daoBase = new DaoBase(context);
        try
        {
            DbHelper helper = new DbHelper(Patient.class);
            String query = helper.selectListColumn(filterExpression, "MRN");
            Cursor reader = daoBase.getDataReader(query);
            if(reader.moveToFirst()){
                do {
                    result.add(reader.getInt(0));
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
        } finally
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

    public static WebServiceResponse insertErrorLog(Context context, Integer MRN, String deviceID, String errorMessage, String stackTrace){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.InsertErrorLog(context, MRN, deviceID, errorMessage, stackTrace);
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public static WebServiceResponse insertErrorFeedback(Context context, String deviceID, String errorMessage){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.InsertErrorFeedback(context, deviceID, errorMessage);
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
    public static WebServiceResponse updateDeviceFCMToken(Context context, String deviceID, String newFCMToken){
        WebServiceResponse result = new WebServiceResponse();
        try {
            JSONObject response = WebServiceHelper.UpdateDeviceFCMToken(context, deviceID, newFCMToken);
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}
