package samanasoft.android.ottimo.dal;

import android.content.Context;
import android.database.Cursor;
import samanasoft.android.framework.DateTime;
import samanasoft.android.framework.DbHelper;
import samanasoft.android.framework.DaoBase;
import samanasoft.android.framework.Helper;
import samanasoft.android.framework.attribute.Column;
import samanasoft.android.framework.attribute.Table;
import samanasoft.android.framework.attribute.EnumAttribute.Bool;
import samanasoft.android.framework.attribute.EnumAttribute.DataType;
import samanasoft.android.ottimo.common.Constant;

public class DataLayer{
    //region Variable
    public static class Variable {
        public int Code;
        public String Value;

        public Variable(int Code, String Value) {
            this.Code = Code;
            this.Value = Value;
        }
    }
    //endregion
    //region Appointment
    @Table(Name = "Appointment")
    public static class Appointment{
        @Column(DataType = DataType.INT, Name = "AppointmentID", IsPrimaryKey = Bool.TRUE)
        public int AppointmentID;

        @Column(DataType = DataType.STRING, Name = "PatientName")
        public String PatientName;

        @Column(DataType = DataType.STRING, Name = "PreferredName")
        public String PreferredName;

        @Column(DataType = DataType.STRING, Name = "ServiceUnitName")
        public String ServiceUnitName;

        @Column(DataType = DataType.INT, Name = "QueueNo")
        public int QueueNo;

        @Column(DataType = DataType.DATETIME, Name = "StartDate")
        public DateTime StartDate;

        @Column(DataType = DataType.DATETIME, Name = "EndDate")
        public DateTime EndDate;

        @Column(DataType = DataType.STRING, Name = "StartTime")
        public String StartTime;

        @Column(DataType = DataType.STRING, Name = "EndTime")
        public String EndTime;

        @Column(DataType = DataType.STRING, Name = "cfStartTime")
        public String cfStartTime;

        @Column(DataType = DataType.STRING, Name = "VisitTypeName")
        public String VisitTypeName;

        @Column(DataType = DataType.STRING, Name = "ParamedicName")
        public String ParamedicName;

        @Column(DataType = DataType.STRING, Name = "SpecialtyName")
        public String SpecialtyName;

        @Column(DataType = DataType.STRING, Name = "MobilePhoneNo")
        public String MobilePhoneNo;

        @Column(DataType = DataType.STRING, Name = "MobilePhoneNo2")
        public String MobilePhoneNo2;

        @Column(DataType = DataType.STRING, Name = "EmailAddress")
        public String EmailAddress;

        @Column(DataType = DataType.STRING, Name = "SMSLogStatus")
        public String SMSLogStatus;

        @Column(DataType = DataType.INT, Name = "NoOfMessageSent")
        public int NoOfMessageSent;

        @Column(DataType = DataType.DATETIME, Name = "LastUpdatedDate")
        public DateTime LastUpdatedDate;

        public Boolean IsChecked = false;
        public String getMobilePhoneNoDisplay(){
            if(MobilePhoneNo2 != null && !MobilePhoneNo2.isEmpty())
                return MobilePhoneNo + "/" + MobilePhoneNo2;
            return MobilePhoneNo;
        }
    }
    public static class AppointmentDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_AppointmentID = "@p_AppointmentID";

        public AppointmentDao(Context context){
            this.helper = new DbHelper(Appointment.class);
            this.daoBase = new DaoBase(context);
        }
        public Appointment get(int AppointmentID){
            String query = helper.getRecord();
            query = query.replace(p_AppointmentID, Integer.toString(AppointmentID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (Appointment)helper.dataRowToObject(row, new Appointment());
        }
        public int insert(Appointment record){
            String query = helper.insert(record);
            return daoBase.executeNonQuery(query);
        }
        public int update(Appointment record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int AppointmentID){
            Appointment record = get(AppointmentID);
            String query = helper.delete(record);
            return daoBase.executeNonQuery(query);
        }
    }
    //endregion
    //region MessageLog
    @Table(Name = "MessageLog")
    public static class MessageLog{
        @Column(DataType = DataType.INT, Name = "ID")
        public int ID;

        @Column(DataType = DataType.DATETIME, Name = "LogDate")
        public DateTime LogDate;

        @Column(DataType = DataType.STRING, Name = "LogTime")
        public String LogTime;

        @Column(DataType = DataType.STRING, Name = "PatientName")
        public String PatientName;

        @Column(DataType = DataType.STRING, Name = "MobilePhoneNo")
        public String MobilePhoneNo;

        @Column(DataType = DataType.STRING, Name = "MessageText")
        public String MessageText;
    }

    public static class MessageLogDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_ID = "@p_ID";

        public MessageLogDao(Context context){
            this.helper = new DbHelper(MessageLog.class);
            this.daoBase = new DaoBase(context);
        }
        public MessageLog get(int ID){
            String query = helper.getRecord();
            query = query.replace(p_ID, Integer.toString(ID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (MessageLog)helper.dataRowToObject(row, new MessageLog());
        }
        public int insert(MessageLog record){
            String query = helper.insert(record);
            return daoBase.executeNonQuery(query);
        }
        public int update(MessageLog record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int ID){
            MessageLog record = get(ID);
            String query = helper.delete(record);
            return daoBase.executeNonQuery(query);
        }
    }
    //endregion

    //region Paramedic
    @Table(Name = "Paramedic")
    public static class Paramedic{
        @Column(DataType = DataType.INT, Name = "ParamedicID")
        public Integer ParamedicID;
        @Column(DataType = DataType.STRING, Name = "ParamedicName")
        public String ParamedicName;
    }
    //endregion

	//region Menu
	@Table(Name = "Setting")
	public static class Setting{
		@Column(DataType = DataType.STRING, Name = "SettingCode", IsPrimaryKey = Bool.TRUE)
		public String SettingCode;

		@Column(DataType = DataType.STRING, Name = "SettingName")
		public String SettingName;

		@Column(DataType = DataType.STRING, Name = "SettingValue")
		public String SettingValue;
	}

	public static class SettingDao{
		private DbHelper helper;
		private DaoBase daoBase;
        private final String p_SettingCode = "@p_SettingCode";

		public SettingDao(Context context){
			this.helper = new DbHelper(Setting.class);
			this.daoBase = new DaoBase(context);
		}
		public Setting get(String SettingCode){
			String query = helper.getRecord();
			query = query.replace(p_SettingCode, SettingCode);
			Cursor row = daoBase.getDataRow(query);
			return (row == null) ? null : (Setting)helper.dataRowToObject(row, new Setting());
		}
		public int insert(Setting record){
			String query = helper.insert(record);
			return daoBase.executeNonQuery(query);
		}
		public int update(Setting record){
			String query = helper.update(record);
			return daoBase.executeNonQuery(query);
		}
		public int delete(String SettingCode){
            Setting record = get(SettingCode);
			String query = helper.delete(record);
			return daoBase.executeNonQuery(query);
		}
	}
	//endregion

    //region TemplateText
    @Table(Name = "TemplateText")
    public static class TemplateText{
        @Column(DataType = DataType.INT, Name = "TemplateID")
        public Integer TemplateID;
        @Column(DataType = DataType.STRING, Name = "TemplateName")
        public String TemplateName;
        @Column(DataType = DataType.STRING, Name = "TemplateContent")
        public String TemplateContent;
    }
    //endregion

    //region VaccinationType
    @Table(Name = "VaccinationType")
    public static class VaccinationType{
        @Column(DataType = DataType.INT, Name = "VaccinationTypeID")
        public Integer VaccinationTypeID;
        @Column(DataType = DataType.STRING, Name = "VaccinationTypeCode")
        public String VaccinationTypeCode;
        @Column(DataType = DataType.STRING, Name = "VaccinationTypeName")
        public String VaccinationTypeName;
    }
    //endregion
}
