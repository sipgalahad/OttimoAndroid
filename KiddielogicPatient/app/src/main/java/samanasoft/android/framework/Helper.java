package samanasoft.android.framework;

import android.app.ActivityManager;
import android.content.Context;

public class Helper {
	/**count Age
	 *  
	 * @param type : 1 => day; 2 => month; 3 => year
	 * @return age
	 */
	public static int getAge(DateTime dateOfBirth, DateTime nowDate, int type){		
		int day = nowDate.Day - dateOfBirth.Day;
        int month = nowDate.Month - dateOfBirth.Month;
        int year = nowDate.Year - dateOfBirth.Year;
        
        if (day < 0) {
            day = day + 30;
            month--;
        }
        if (month < 0) {
            month += 12;
            year--;
        }
        
        switch (type) {
			case 1:return day;
			case 2:return month;
			default:return year;
		}
	}

    public static boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
