package samanasoft.android.framework;

public class Constant {
    public static final String AppVersion = "1.0";

	public static final String[] SHORT_MONTHS = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" }; 
	public static final String[] MONTHS = { "January", "February", "March", "April", "May", "June", "July", 
											"August", "September", "October", "November", "December" };
    public static class Url
    {
        public static final String APP_DATA_URL = "http://192.168.0.102/appdata/ottimo";
        public static final String BRIDGING_SERVER = "http://114.199.103.10:8080/KiddielogicTest/BridgingServer/Program/Mobile/MobileService.asmx";
        //public static final String BRIDGING_SERVER = "http://192.168.0.100/research/Ottimov2.0/BridgingServer/Program/Mobile/MobileService.asmx";
    }
    public static final String APP_TOKEN = "40BD001563085FC35165329EA1FF5C5ECBDBBEEF";
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
    public static class Sex {
        public static final String MALE = "0003^M";
        public static final String FEMALE = "0003^F";
    }
    public static class AppointmentStatus
    {
        public static final String OPEN = "0278^001";
        public static final String SEND_CONFIRMATION = "0278^002";
        public static final String CONFIRMED = "0278^003";
        public static final String CHECK_IN = "0278^004";
        public static final String CANCELLED = "0278^005";
        public static final String VOID = "0278^999";
    }
}
