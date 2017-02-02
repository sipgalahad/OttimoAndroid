package samanasoft.android.framework;

import java.util.Calendar;

public class DateTime {
	public int Year;
	public int Month;
	public int Day;
	public int Hour;
	public int Minute;
	public int Second;
	
	public DateTime(){ 
		setDefaultDateTime();
	}
	private void setDefaultDateTime(){
		this.Year = 1900;
		this.Month = 1;
		this.Day = 1;
		this.Hour = 0;
		this.Minute = 0;
		this.Second = 0;
		
	}
	/** Assign value from String to DateTime
	 * 
	 * @param value : String yyyy-MM-dd or yyyy-MM-dd HH:mm:ss
	 */
	public DateTime(String value){
		if(value.trim() != ""){
			this.Year = Integer.valueOf(value.substring(0, 4));
			this.Month = Integer.valueOf(value.substring(5, 7));
			this.Day = Integer.valueOf(value.substring(8, 10));
			if(value.length() > 10){
				this.Hour = Integer.valueOf(value.substring(11, 13));
				this.Minute = Integer.valueOf(value.substring(14, 16));
				this.Second = Integer.valueOf(value.substring(17, 19));
			}
		}
		else
			setDefaultDateTime();
	}
	public DateTime(int year, int month, int day, int hour, int minute, int second){
		this.Year = year;
		this.Month = month;
		this.Day = day;
		this.Hour = hour;
		this.Minute = minute;
		this.Second = second;
	}
	public DateTime(DateTime value){
		this.setValue(value);
	}
	public void setValue(DateTime value){
		this.Year = value.Year;
		this.Month = value.Month;
		this.Day = value.Day;
		this.Hour = value.Hour;
		this.Minute = value.Minute;
		this.Second = value.Second;
	}
	public static DateTime now(){
		final Calendar c = Calendar.getInstance();
        int vYear = c.get(Calendar.YEAR);
        int vMonth = c.get(Calendar.MONTH) + 1;
        int vDay = c.get(Calendar.DAY_OF_MONTH);
        int vHour = c.get(Calendar.HOUR_OF_DAY);
        int vMinute = c.get(Calendar.MINUTE);
        int vSecond = c.get(Calendar.SECOND);
        return new DateTime(vYear, vMonth, vDay, vHour, vMinute, vSecond);
	}
	public static DateTime tomorrow(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 1);
		int vYear = c.get(Calendar.YEAR);
		int vMonth = c.get(Calendar.MONTH) + 1;
		int vDay = c.get(Calendar.DAY_OF_MONTH);
		int vHour = c.get(Calendar.HOUR_OF_DAY);
		int vMinute = c.get(Calendar.MINUTE);
		int vSecond = c.get(Calendar.SECOND);
		return new DateTime(vYear, vMonth, vDay, vHour, vMinute, vSecond);
	}
	/** Print DateTime Value To String
	 * 
	 * @param format : <br>yyyy : Year (2010 / 2009 / ....) 
	 * 			  	   <br>yy : Year (09 / 08 / ....) 
	 * 			  	   <br>MMMM : Month (January / February / ....) 
	 * 				   <br>MMM : Month (Jan / Feb / ...)
	 * 				   <br>MM : Month (01 / 02 / ..)
	 * 				   <br>dd : Day ( 01 / 02 / ...)
	 * 				   <br>HH : Hour ( 01 / 02 / ...)
	 * 				   <br>mm : Minute ( 01 / 02 / ...)
	 * 				   <br>ss : Second ( 01 / 02 / ...)
	 * @return String DateTime
	 */
	public String toString(String format){
		format = format.replace("yyyy", Integer.toString(this.Year));
		format = format.replace("yy", String.format("%02d", this.Year % 100));
		format = format.replace("MMMM", Constant.MONTHS[this.Month - 1]);
		format = format.replace("MMM", Constant.SHORT_MONTHS[this.Month - 1]);
		format = format.replace("MM", String.format("%02d", this.Month));
		format = format.replace("dd", String.format("%02d", this.Day));

		format = format.replace("HH", String.format("%02d", this.Hour));
		format = format.replace("mm", String.format("%02d", this.Minute));
		format = format.replace("ss", String.format("%02d", this.Second));		
		
		return format;
	}
}
