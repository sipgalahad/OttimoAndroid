package samanasoft.android.framework.webservice;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

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
    private static final String URL = samanasoft.android.framework.Constant.Url.BRIDGING_SERVER;
    //private static String URL = "";
    private static final String SOAP_ACTION = "http://tempuri.org/GetMobileListObject";     
    private static final String METHOD_NAME = "GetMobileListObject";
	public static JSONObject getListObject(Context ctx, String methodName, String filterExpression){
        //URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

		JSONObject result = null;
    	SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		request.addProperty(createPropertyInfo("appToken", samanasoft.android.framework.Constant.APP_TOKEN, String.class));
		request.addProperty(createPropertyInfo("methodName", methodName, String.class));
		request.addProperty(createPropertyInfo("filterExpression", filterExpression, String.class));

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
	public static JSONObject Login(Context ctx, String userName, String password, String deviceID, String deviceName, String manufacturerName, String androidVersion, Integer sdkVersion, String appVersion, String FCMToken){
		//URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

		JSONObject result = null;
		SoapObject request = new SoapObject(NAMESPACE, "LoginParamedic");

		String data = "<REQUEST><DATA>";
		data += addXMLElement("USER_NAME", userName);
		data += addXMLElement("PASSWORD", password);
		data += addXMLElement("DEVICE_ID", deviceID);
		data += addXMLElement("DEVICE_NAME", deviceName);
		data += addXMLElement("MANUFACTURER_NAME", manufacturerName);
		data += addXMLElement("ANDROID_VERSION", androidVersion);
		data += addXMLElement("SDK_VERSION", sdkVersion.toString());
		data += addXMLElement("APP_VERSION", appVersion);
		data += addXMLElement("FCM_TOKEN", FCMToken);
		data += "</DATA></REQUEST>";
		Log.d("testrequest", data);

		request.addProperty(createPropertyInfo("appToken", samanasoft.android.framework.Constant.APP_TOKEN, String.class));
		request.addProperty(createPropertyInfo("data", data, String.class));

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call("http://tempuri.org/LoginParamedic", envelope);
			result = new JSONObject((String)envelope.getResponse());
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}
	public static JSONObject LoadPatientList(Context ctx, Integer paramedicID){
		//URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

		JSONObject result = null;
		SoapObject request = new SoapObject(NAMESPACE, "LoadPatientList");

		String data = "<REQUEST><DATA>";
		data += addXMLElement("PARAMEDIC_ID", paramedicID.toString());
		data += "</DATA></REQUEST>";

		request.addProperty(createPropertyInfo("appToken", samanasoft.android.framework.Constant.APP_TOKEN, String.class));
		request.addProperty(createPropertyInfo("data", data, String.class));

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call("http://tempuri.org/LoadPatientList", envelope);
			result = new JSONObject((String)envelope.getResponse());
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	private static String addXMLElement(String elementName, String value){
		return "<" + elementName + ">" + value + "</" + elementName + ">";
	}

	private static PropertyInfo createPropertyInfo(String name, Object value, Class type){
		PropertyInfo prop = new PropertyInfo();
		prop.setName(name);
		prop.setValue(value);
		prop.setType(type);
		return prop;
	}
	public static JSONObject GetAndroidAppVersion(Context ctx){
		JSONObject result = null;
		SoapObject request = new SoapObject(NAMESPACE, "GetAndroidAppVersion2");

		request.addProperty(createPropertyInfo("appToken", samanasoft.android.framework.Constant.APP_TOKEN, String.class));

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call("http://tempuri.org/GetAndroidAppVersion2", envelope);
			result = new JSONObject((String)envelope.getResponse());
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	public static JSONObject InsertErrorLog(Context ctx, Integer MRN, String deviceID, String errorMessage, String stackTrace){
		//URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

		JSONObject result = null;
		SoapObject request = new SoapObject(NAMESPACE, "InsertErrorLog2");

		String data = "<REQUEST><DATA>";
		data += addXMLElement("MRN", MRN.toString());
		data += addXMLElement("DEVICE_ID", deviceID);
		data += addXMLElement("ERROR_MESSAGE", errorMessage);
		data += addXMLElement("STACK_TRACE", stackTrace);
		data += "</DATA></REQUEST>";

		request.addProperty(createPropertyInfo("appToken", samanasoft.android.framework.Constant.APP_TOKEN, String.class));
		request.addProperty(createPropertyInfo("data", data, String.class));

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call("http://tempuri.org/InsertErrorLog2", envelope);
			//result = new JSONObject((String)envelope.getResponse());
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	public static JSONObject InsertErrorFeedback(Context ctx, String deviceID, String errorMessage){
		//URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

		JSONObject result = null;
		SoapObject request = new SoapObject(NAMESPACE, "InsertErrorFeedback2");

		String data = "<REQUEST><DATA>";
		data += addXMLElement("DEVICE_ID", deviceID);
		data += addXMLElement("ERROR_MESSAGE", errorMessage);
		data += "</DATA></REQUEST>";

		request.addProperty(createPropertyInfo("appToken", samanasoft.android.framework.Constant.APP_TOKEN, String.class));
		request.addProperty(createPropertyInfo("data", data, String.class));

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call("http://tempuri.org/InsertErrorFeedback2", envelope);
			//result = new JSONObject((String)envelope.getResponse());
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		}
		return result;
	}

	public static JSONObject UpdateDeviceFCMToken(Context ctx, String deviceID, String newFCMToken){
		//URL = ctx.getSharedPreferences(Constant.SharedPreference.NAME, ctx.MODE_PRIVATE).getString(Constant.SharedPreference.WEB_SERVICE_URL, "");

		JSONObject result = null;
		SoapObject request = new SoapObject(NAMESPACE, "UpdateDeviceFCMToken2");

		String data = "<REQUEST><DATA>";
		data += addXMLElement("DEVICE_ID", deviceID);
		data += addXMLElement("NEW_FCM_TOKEN", newFCMToken);
		data += "</DATA></REQUEST>";

		request.addProperty(createPropertyInfo("appToken", samanasoft.android.framework.Constant.APP_TOKEN, String.class));
		request.addProperty(createPropertyInfo("data", data, String.class));

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;	//used only if we use the webservice from a dot net file (asmx)
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call("http://tempuri.org/UpdateDeviceFCMToken2", envelope);
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

	public static JSONArray getCustomReturnObject(JSONObject response, String customField){
		try {
			return response.getJSONArray(customField);
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
			else if(colAttribute.DataType() == DataType.DOUBLE)
				value = row.optDouble(colAttribute.Name());
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
