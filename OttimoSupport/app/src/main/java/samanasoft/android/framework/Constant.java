package samanasoft.android.framework;

public class Constant {
	public static final String[] SHORT_MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }; 
	public static final String[] MONTHS = { "January", "February", "March", "April", "May", "June", "July", 
											"August", "September", "October", "November", "December" }; 
	public static class FormatString
    {
        public static final String DATE_FORMAT = "dd-MMM-yyyy";
        public static final String DATE_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";
        public static final String DATE_FORMAT_DB = "yyyy-MM-dd";
        public static final String DATE_TIME_FORMAT_DB = "yyyy-MM-dd HH:mm:ss";
    }
    public static class ConstantDate
    {
        public static final String DEFAULT_NULL = "01-01-1900";
    }
}
