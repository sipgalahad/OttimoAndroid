package samanasoft.android.ottimo.common;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Methods {
	public static class SharedPreference{
		public static String getSharedPrefencesValue(Context context, String key){
			SharedPreferences prefs = context.getSharedPreferences(Constant.SharedPreference.NAME, Context.MODE_PRIVATE); 
			return prefs.getString(key, "");  
		}
		public static void setSharedPrefencesValue(Context context, String key, String value){
			SharedPreferences prefs = context.getSharedPreferences(Constant.SharedPreference.NAME, Context.MODE_PRIVATE); 			
			SharedPreferences.Editor editor = prefs.edit();                           
	        editor.putString(key, value);    
	        editor.commit();  	
		}
	}
	
	public static Object getIntentParameter(Activity activity, String name){
		Bundle extras = activity.getIntent().getExtras();
		Object result = extras.get(name);
		return result;
	}
	
	public static String getFilterExpressionWithLimitClause(String filterExpression, int currentNumItems, int itemsPerPage){
		StringBuilder result = new StringBuilder();
		if(filterExpression == null || filterExpression.trim().equals(""))
			filterExpression = "1 = 1";
		result.append(filterExpression).append(" LIMIT ").append(currentNumItems).append(",").append(itemsPerPage);
		return result.toString();
	}
	
	public static <T> int getIndexFromList(List<?> lst, T value){
		try {
			int ctr = 0;
			for(Object obj : lst){
				if(obj.equals(value))
					 return ctr;
				ctr++;
			}	
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static <T> int getIndexFromListObject(List<?> lst, String fieldName, T fieldValue){
		try {
			int ctr = 0;
			for(Object obj : lst){
				if(obj.getClass().getField(fieldName).get(obj).equals(fieldValue))
					 return ctr;
				ctr++;
			}	
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static Class<?> loadActivity(Context context, String className){
		StringBuilder classFullName = new StringBuilder();
		classFullName.append(context.getPackageName()).append(".").append(className);
		Class<?> c = null;       
        try {
            c = Class.forName(classFullName.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return c;
	}
}
