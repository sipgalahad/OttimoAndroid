package samanasoft.android.ottimo.common;

public class Constant {
	public static class FormatString{
        public static final String DATE_FORMAT = "dd-MMM-yyyy";
        public static final String DATE_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";
        public static final String DATE_FORMAT_112 = "yyyy-MM-dd";
        public static final String DATE_TIME_FORMAT_112 = "yyyy-MM-dd HH:mm:ss";
    }
	public static class MenuCode{
		public static final String MY_PATIENT = "HP0001";
		public static final String MY_SCHEDULE = "HP0002";
		public static final String MY_TASK = "HP0003";
	}
	public static class SMSLogStatus{
		public static final String SENDING_SMS = "OT023^001";
		public static final String SMS_SENT = "OT023^002";
		public static final String SMS_DELIVERED = "OT023^003";
		public static final String FAILED = "OT023^999";
	}
	public static class FontType{
		public static final String NORMAL_FONT = "segoeui.ttf";
		public static final String BOLD_FONT = "segoeuib.ttf";
		public static final String ITALIC_FONT = "segoeuii.ttf";
	}
	public static class SharedPreference{
		public static final String NAME = "prefOttimoSupport";
		public static final String DB_CONF = "prefDBConf";
		public static final String FCM_TOKEN = "prefFCM_TOKEN";
		public static final String SERVER_APPS_VERSION = "prefServerAppsVersion";
		public static final String LAST_NOTIFICATION_DATE = "lastNotificationDate";

        public static final String APPOINTMENT_REMINDER_TIME = "prefAppointmentReminderTime";
        public static final String APPOINTMENT_REMINDER_MESSAGE = "prefAppointmentReminderMessage";
        public static final String WEB_SERVICE_URL = "prefWebServiceUrl";

        public static final String TIMESTAMP_UPDATE_APPOINTMENT = "prefTimstampUpdateAppointment";
	}
    public static class SettingCode{
        public static final String APPOINTMENT_REMINDER_TIME = "ST001";
        public static final String APPOINTMENT_REMINDER_MESSAGE = "ST002";
    }
}
