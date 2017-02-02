package samanasoft.android.framework.webservice;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.attribute.Column;
import samanasoft.android.framework.attribute.EnumAttribute.Bool;
import samanasoft.android.framework.attribute.EnumAttribute.DataType;
import samanasoft.android.ottimo.common.Constant;

public class WebServiceHelper {
	private static final String NAMESPACE = "http://tempuri.org/";
    //private static final String URL = "http://10.0.2.2/research/Ottimov2.0/ControlPanel/Libs/Service/MethodService.asmx";
    private static String URL = "";
    private static final String SOAP_ACTION = "http://tempuri.org/GetMobileListObject";     
    private static final String METHOD_NAME = "GetMobileListObject";
	public static JSONObject getListObject(Context ctx, String methodName, String filterExpression){
        URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

		JSONObject result = null;
    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME); 

		PropertyInfo propMethodName = new PropertyInfo();         
		propMethodName.setName("methodName");         
		propMethodName.setValue(methodName);         
		propMethodName.setType(String.class);                     
    	
    	PropertyInfo propFilterExpression = new PropertyInfo();         
    	propFilterExpression.setName("filterExpression");         
    	propFilterExpression.setValue(filterExpression);         
    	propFilterExpression.setType(String.class);        

        request.addProperty(propMethodName);
        request.addProperty(propFilterExpression);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);         
    	envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)         
    	envelope.setOutputSoapObject(request);         
    	HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
        	androidHttpTransport.call(SOAP_ACTION, envelope);        
        	result = new JSONObject((String)envelope.getResponse());
        } catch (Exception e) {
        	result = null;
    		e.printStackTrace();   
        }
        return result;
	}

    public static JSONObject PostAppointmentAnswer(Context ctx, String mobilePhoneNo, String replyText){
        URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

        JSONObject result = null;
        SoapObject request = new SoapObject(NAMESPACE, "PostAppointmentAnswer");

        PropertyInfo propMethodName = new PropertyInfo();
        propMethodName.setName("mobilePhoneNo");
        propMethodName.setValue(mobilePhoneNo);
        propMethodName.setType(String.class);

        PropertyInfo propFilterExpression = new PropertyInfo();
        propFilterExpression.setName("replyText");
        propFilterExpression.setValue(replyText);
        propFilterExpression.setType(String.class);

        request.addProperty(propMethodName);
        request.addProperty(propFilterExpression);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            androidHttpTransport.call("http://tempuri.org/PostAppointmentAnswer", envelope);
            //result = new JSONObject((String)envelope.getResponse());
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }

	public static JSONObject UpdateAppointmentStatusSMS(Context ctx, int appointmentID, String mobilePhoneNo, String GCSMSLogStatus){
		URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

		JSONObject result = null;
		SoapObject request = new SoapObject(NAMESPACE, "UpdateAppointmentStatusSMS");

		PropertyInfo propAppointmentID = new PropertyInfo();
		propAppointmentID.setName("appointmentID");
		propAppointmentID.setValue(appointmentID);
		propAppointmentID.setType(Integer.class);

		PropertyInfo propMobilePhoneNo = new PropertyInfo();
		propMobilePhoneNo.setName("mobilePhoneNo");
		propMobilePhoneNo.setValue(mobilePhoneNo);
		propMobilePhoneNo.setType(String.class);

		PropertyInfo propGCSMSLogStatus = new PropertyInfo();
		propGCSMSLogStatus.setName("GCSMSLogStatus");
		propGCSMSLogStatus.setValue(GCSMSLogStatus);
		propGCSMSLogStatus.setType(String.class);

		request.addProperty(propAppointmentID);
		request.addProperty(propMobilePhoneNo);
		request.addProperty(propGCSMSLogStatus);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call("http://tempuri.org/UpdateAppointmentStatusSMS", envelope);
			//result = new JSONObject((String)envelope.getResponse());
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}
	
	public static JSONArray getReturnObject(JSONObject response){
		try {
			return response.getJSONArray("ReturnObj");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static DateTime getTimestamp(JSONObject response){
		return JsonDateToDateTime(response.optString("Timestamp"));
	}
	
	public static Object JSONObjectToObject(JSONObject jsonObject, Object obj){
		Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
        	Column colAttribute = (Column) field.getAnnotation(Column.class); 
        	if(field.isAnnotationPresent(Column.class)){		        			
        		try {
        			setFieldValue(colAttribute, field, jsonObject, obj);
        		} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();				
				}
        	}
        }
        return obj;
    }
	
	private static void setFieldValue(Column colAttribute, Field field, JSONObject row, Object obj) throws JSONException, IllegalArgumentException, IllegalAccessException{
		if(colAttribute.IsIdentity() == Bool.FALSE){
			Object value = null;
			if(colAttribute.DataType() == DataType.BOOLEAN){
				value = row.optBoolean(colAttribute.Name());
				//boolean val = (value.equals("1") ? true : false);
			}
			else if(colAttribute.DataType() == DataType.DATETIME){
				String s = row.optString(colAttribute.Name());
				if(s != "")
					value = JsonDateToDateTime(s);
				else
					value = new DateTime();
			}
			else if(colAttribute.DataType() == DataType.INT)
				value = row.optInt(colAttribute.Name());
			else
				value = row.optString(colAttribute.Name());
			field.set(obj, value);
		}
	}
	
	private static DateTime JsonDateToDateTime(String jsonDate){
		int idx1 = jsonDate.indexOf("(");
		int idx2 = jsonDate.indexOf(")");
		String s = jsonDate.substring(idx1 + 1, idx2);
		long l = Long.valueOf(s);
		Date d = new Date(l);
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		calendar.setTime(d);   // assigns calendar to given date 

		return new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE), 
				calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
	}
	
	
	/*public static List<Patient> getListObjectTest(String methodName, String filterExpression){		
		List<Patient> result = new ArrayList<Patient>();
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME); 

		PropertyInfo propMethodName = new PropertyInfo();         
		propMethodName.setName("methodName");         
		propMethodName.setValue("ListPatient");         
		propMethodName.setType(String.class);                     
    	
    	PropertyInfo propFilterExpression = new PropertyInfo();         
    	propFilterExpression.setName("filterExpression");         
    	propFilterExpression.setValue("MRN < 10");         
    	propFilterExpression.setType(String.class);        

        request.addProperty(propMethodName);
        request.addProperty(propFilterExpression);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);         
    	envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)         
    	envelope.setOutputSoapObject(request);         
    	HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
        	androidHttpTransport.call(SOAP_ACTION, envelope);        
    		JSONArray response = new JSONArray((String)envelope.getResponse());  
    		for (int i = 0; i < response.length();++i){
	    		JSONObject row = (JSONObject) response.get(i);
    			Patient obj = new Patient();
	    		
	    		Field[] fields = Patient.class.getDeclaredFields();
	            for (Field field : fields) {
	            	Column colAttribute = (Column) field.getAnnotation(Column.class); 
	            	if(field.isAnnotationPresent(Column.class)){		        			
	            		try {
	            			setFieldValue(colAttribute, field, row, obj);
	            		} catch (IllegalArgumentException e) {
	    					e.printStackTrace();
	    				} catch (IllegalAccessException e) {
	    					e.printStackTrace();
	    				} 
	            	}
	            }
	            result.add(obj);
    		}
        } catch (Exception e) {
        	result = null;
    		e.printStackTrace();   
        }
        return result;
	}*/
}
