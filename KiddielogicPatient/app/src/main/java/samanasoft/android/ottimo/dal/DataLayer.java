package samanasoft.android.ottimo.dal;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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

    //region Announcement
    @Table(Name = "Announcement")
    public static class Announcement{
        @Column(DataType = DataType.INT, Name = "AnnouncementID", IsPrimaryKey = Bool.TRUE)
        public int AnnouncementID;

        @Column(DataType = DataType.STRING, Name = "Title")
        public String Title;

        @Column(DataType = DataType.DATETIME, Name = "StartDate")
        public DateTime StartDate;

        @Column(DataType = DataType.DATETIME, Name = "EndDate")
        public DateTime EndDate;

        @Column(DataType = DataType.STRING, Name = "GCAnnouncementType")
        public String GCAnnouncementType;

        @Column(DataType = DataType.STRING, Name = "AnnouncementType")
        public String AnnouncementType;

        @Column(DataType = DataType.STRING, Name = "Remarks")
        public String Remarks;

        @Column(DataType = DataType.DATETIME, Name = "LastUpdatedDate")
        public DateTime LastUpdatedDate;
    }
    public static class AnnouncementDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_AnnouncementID = "@p_AnnouncementID";

        public AnnouncementDao(Context context){
            this.helper = new DbHelper(Announcement.class);
            this.daoBase = new DaoBase(context);
        }
        public Announcement get(int AnnouncementID){
            String query = helper.getRecord();
            query = query.replace(p_AnnouncementID, Integer.toString(AnnouncementID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (Announcement)helper.dataRowToObject(row, new Announcement());
        }
        public int insert(Announcement record){
            String query = helper.insert(record);
            return daoBase.executeNonQuery(query);
        }
        public int update(Announcement record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int AnnouncementID){
            Announcement record = get(AnnouncementID);
            String query = helper.delete(record);
            return daoBase.executeNonQuery(query);
        }
    }
    //endregion
    //region Appointment
    @Table(Name = "Appointment")
    public static class Appointment{
        @Column(DataType = DataType.INT, Name = "AppointmentID", IsPrimaryKey = Bool.TRUE)
        public int AppointmentID;

        @Column(DataType = DataType.INT, Name = "MRN")
        public int MRN;

        @Column(DataType = DataType.STRING, Name = "ServiceUnitName")
        public String ServiceUnitName;

        @Column(DataType = DataType.INT, Name = "QueueNo")
        public int QueueNo;

        @Column(DataType = DataType.DATETIME, Name = "StartDate")
        public DateTime StartDate;

        @Column(DataType = DataType.DATETIME, Name = "ReminderDate")
        public DateTime ReminderDate;

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

        @Column(DataType = DataType.STRING, Name = "GCAppointmentStatus")
        public String GCAppointmentStatus;

        @Column(DataType = DataType.DATETIME, Name = "LastUpdatedDate")
        public DateTime LastUpdatedDate;
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
            Log.d("query",query);
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
    //region AppointmentCalendarEvent
    @Table(Name = "AppointmentCalendarEvent")
    public static class AppointmentCalendarEvent{
        @Column(DataType = DataType.INT, Name = "AppointmentID", IsPrimaryKey = Bool.TRUE)
        public int AppointmentID;

        @Column(DataType = DataType.LONG, Name = "CalendarEventID")
        public Long CalendarEventID;
    }
    public static class AppointmentCalendarEventDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_AppointmentID = "@p_AppointmentID";
        private final String p_CalendarEventID = "@p_CalendarEventID";

        public AppointmentCalendarEventDao(Context context){
            this.helper = new DbHelper(AppointmentCalendarEvent.class);
            this.daoBase = new DaoBase(context);
        }
        public AppointmentCalendarEvent get(int AppointmentID, long CalendarEventID){
            String query = helper.getRecord();
            query = query.replace(p_AppointmentID, Integer.toString(AppointmentID));
            query = query.replace(p_CalendarEventID, Long.toString(CalendarEventID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (AppointmentCalendarEvent)helper.dataRowToObject(row, new AppointmentCalendarEvent());
        }
        public int insert(AppointmentCalendarEvent record){
            String query = helper.insert(record);
            return daoBase.executeNonQuery(query);
        }
        public int update(AppointmentCalendarEvent record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int AppointmentID, long CalendarEventID){
            AppointmentCalendarEvent record = get(AppointmentID, CalendarEventID);
            String query = helper.delete(record);
            return daoBase.executeNonQuery(query);
        }
    }
    //endregion
    //region LaboratoryResultDt
    @Table(Name = "LaboratoryResultDt")
    public static class LaboratoryResultDt{
        @Column(DataType = DataType.INT, Name = "LaboratoryResultDtID", IsPrimaryKey = Bool.TRUE)
        public int LaboratoryResultDtID;

        @Column(DataType = DataType.INT, Name = "ID")
        public int ID;

        @Column(DataType = DataType.INT, Name = "MRN")
        public int MRN;

        @Column(DataType = DataType.STRING, Name = "ItemName1")
        public String ItemName1;

        @Column(DataType = DataType.STRING, Name = "ItemGroupName1")
        public String ItemGroupName1;

        @Column(DataType = DataType.INT, Name = "LabTestResultTypeID")
        public int LabTestResultTypeID;

        @Column(DataType = DataType.STRING, Name = "LabTestResultTypeName")
        public String LabTestResultTypeName;

        @Column(DataType = DataType.INT, Name = "FractionID")
        public int FractionID;

        @Column(DataType = DataType.STRING, Name = "FractionName1")
        public String FractionName1;

        @Column(DataType = DataType.DOUBLE, Name = "MetricResultValue")
        public Double MetricResultValue;

        @Column(DataType = DataType.DOUBLE, Name = "MinMetricNormalValue")
        public Double MinMetricNormalValue;

        @Column(DataType = DataType.DOUBLE, Name = "MaxMetricNormalValue")
        public Double MaxMetricNormalValue;

        @Column(DataType = DataType.STRING, Name = "MetricUnit")
        public String MetricUnit;

        @Column(DataType = DataType.STRING, Name = "NilaiRujukanMetric")
        public String NilaiRujukanMetric;

        @Column(DataType = DataType.DOUBLE, Name = "InternationalResultValue")
        public Double InternationalResultValue;

        @Column(DataType = DataType.DOUBLE, Name = "MinInternationalNormalValue")
        public Double MinInternationalNormalValue;

        @Column(DataType = DataType.DOUBLE, Name = "MaxInternationalNormalValue")
        public Double MaxInternationalNormalValue;

        @Column(DataType = DataType.STRING, Name = "InternationalUnit")
        public String InternationalUnit;

        @Column(DataType = DataType.STRING, Name = "NilaiRujukanInternational")
        public String NilaiRujukanInternational;

        @Column(DataType = DataType.STRING, Name = "LabTestResultTypeDtName")
        public String LabTestResultTypeDtName;

        @Column(DataType = DataType.STRING, Name = "TextNormalValue")
        public String TextNormalValue;

        @Column(DataType = DataType.STRING, Name = "TextValue")
        public String TextValue;

        @Column(DataType = DataType.STRING, Name = "GCResultSummary")
        public String GCResultSummary;

        @Column(DataType = DataType.STRING, Name = "ResultSummary")
        public String ResultSummary;

        @Column(DataType = DataType.STRING, Name = "Remarks")
        public String Remarks;
    }
    public static class LaboratoryResultDtDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_LaboratoryResultDtID = "@p_LaboratoryResultDtID";

        public LaboratoryResultDtDao(Context context){
            this.helper = new DbHelper(LaboratoryResultDt.class);
            this.daoBase = new DaoBase(context);
        }
        public LaboratoryResultDt get(int LaboratoryResultDtID){
            String query = helper.getRecord();
            query = query.replace(p_LaboratoryResultDtID, Integer.toString(LaboratoryResultDtID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (LaboratoryResultDt)helper.dataRowToObject(row, new LaboratoryResultDt());
        }
        public int insert(LaboratoryResultDt record){
            String query = helper.insert(record);
            return daoBase.executeNonQuery(query);
        }
        public int update(LaboratoryResultDt record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int LaboratoryResultDtID){
            LaboratoryResultDt record = get(LaboratoryResultDtID);
            String query = helper.delete(record);
            return daoBase.executeNonQuery(query);
        }
    }
    //endregion
    //region LaboratoryResultHd
    @Table(Name = "LaboratoryResultHd")
    public static class LaboratoryResultHd{
        @Column(DataType = DataType.INT, Name = "ID", IsPrimaryKey = Bool.TRUE)
        public int ID;

        @Column(DataType = DataType.INT, Name = "MRN")
        public int MRN;

        @Column(DataType = DataType.STRING, Name = "ReferenceNo")
        public String ReferenceNo;

        @Column(DataType = DataType.DATETIME, Name = "ResultDate")
        public DateTime ResultDate;

        @Column(DataType = DataType.STRING, Name = "ResultTime")
        public String ResultTime;

        @Column(DataType = DataType.STRING, Name = "ProviderName")
        public String ProviderName;

        @Column(DataType = DataType.STRING, Name = "ParamedicName")
        public String ParamedicName;

        @Column(DataType = DataType.STRING, Name = "Remarks")
        public String Remarks;

        @Column(DataType = DataType.DATETIME, Name = "LastUpdatedDate")
        public DateTime LastUpdatedDate;
    }
    public static class LaboratoryResultHdDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_ID = "@p_ID";

        public LaboratoryResultHdDao(Context context){
            this.helper = new DbHelper(LaboratoryResultHd.class);
            this.daoBase = new DaoBase(context);
        }
        public LaboratoryResultHd get(int ID){
            String query = helper.getRecord();
            query = query.replace(p_ID, Integer.toString(ID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (LaboratoryResultHd)helper.dataRowToObject(row, new LaboratoryResultHd());
        }
        public int insert(LaboratoryResultHd record){
            String query = helper.insert(record);
            return daoBase.executeNonQuery(query);
        }
        public int update(LaboratoryResultHd record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int ID){
            LaboratoryResultHd record = get(ID);
            String query = helper.delete(record);
            return daoBase.executeNonQuery(query);
        }
    }
    //endregion
    //region Patient
    @Table(Name = "Patient")
    public static class Patient{
        @Column(DataType = DataType.INT, Name = "MRN", IsPrimaryKey = Bool.TRUE)
        public int MRN;

        @Column(DataType = DataType.STRING, Name = "MedicalNo")
        public String MedicalNo;

        @Column(DataType = DataType.STRING, Name = "FullName")
        public String FullName;

        @Column(DataType = DataType.STRING, Name = "PreferredName")
        public String PreferredName;

        @Column(DataType = DataType.STRING, Name = "CityOfBirth")
        public String CityOfBirth;

        @Column(DataType = DataType.DATETIME, Name = "DateOfBirth")
        public DateTime DateOfBirth;

        @Column(DataType = DataType.STRING, Name = "GCSex")
        public String GCSex;

        @Column(DataType = DataType.STRING, Name = "Sex")
        public String Sex;

        @Column(DataType = DataType.STRING, Name = "Gender")
        public String Gender;

        @Column(DataType = DataType.STRING, Name = "BloodType")
        public String BloodType;

        @Column(DataType = DataType.STRING, Name = "BloodRhesus")
        public String BloodRhesus;

        @Column(DataType = DataType.STRING, Name = "EmailAddress")
        public String EmailAddress;

        @Column(DataType = DataType.STRING, Name = "EmailAddress2")
        public String EmailAddress2;

        @Column(DataType = DataType.STRING, Name = "MobilePhoneNo1")
        public String MobilePhoneNo1;

        @Column(DataType = DataType.STRING, Name = "MobilePhoneNo2")
        public String MobilePhoneNo2;

        @Column(DataType = DataType.DATETIME, Name = "LastSyncDateTime")
        public DateTime LastSyncDateTime;

        @Column(DataType = DataType.DATETIME, Name = "LastSyncAppointmentDateTime")
        public DateTime LastSyncAppointmentDateTime;

        @Column(DataType = DataType.DATETIME, Name = "LastSyncVaccinationDateTime")
        public DateTime LastSyncVaccinationDateTime;

        @Column(DataType = DataType.DATETIME, Name = "LastSyncLabResultDateTime")
        public DateTime LastSyncLabResultDateTime;

        public String getMobilePhoneNoDisplay(){
            if(MobilePhoneNo2 != null && !MobilePhoneNo2.isEmpty())
                return MobilePhoneNo1 + "/" + MobilePhoneNo2;
            return MobilePhoneNo1;
        }
    }
    public static class PatientDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_MRN = "@p_MRN";

        public PatientDao(Context context){
            this.helper = new DbHelper(Patient.class);
            this.daoBase = new DaoBase(context);
        }
        public Patient get(int MRN){
            String query = helper.getRecord();
            query = query.replace(p_MRN, Integer.toString(MRN));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (Patient)helper.dataRowToObject(row, new Patient());
        }
        public int insert(Patient record){
            String query = helper.insert(record);
            //Log.d("query Pasien", query);
            return daoBase.executeNonQuery(query);
        }
        public int update(Patient record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int MRN){
            Patient record = get(MRN);
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
    //region Setting
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
    //region VaccinationShotDt
    @Table(Name = "VaccinationShotDt")
    public static class VaccinationShotDt{
        @Column(DataType = DataType.INT, Name = "Type", IsPrimaryKey = Bool.TRUE)
        public int Type;

        @Column(DataType = DataType.INT, Name = "ID", IsPrimaryKey = Bool.TRUE)
        public int ID;

        @Column(DataType = DataType.DATETIME, Name = "VaccinationDate")
        public DateTime VaccinationDate;

        @Column(DataType = DataType.INT, Name = "MRN")
        public int MRN;

        @Column(DataType = DataType.STRING, Name = "ParamedicName")
        public String ParamedicName;

        @Column(DataType = DataType.INT, Name = "VaccinationTypeID")
        public int VaccinationTypeID;

        @Column(DataType = DataType.STRING, Name = "VaccinationTypeName")
        public String VaccinationTypeName;

        @Column(DataType = DataType.STRING, Name = "VaccinationNo")
        public String VaccinationNo;

        @Column(DataType = DataType.STRING, Name = "VaccineName")
        public String VaccineName;

        @Column(DataType = DataType.DOUBLE, Name = "Dose")
        public Double Dose;

        @Column(DataType = DataType.STRING, Name = "DoseUnit")
        public String DoseUnit;

        @Column(DataType = DataType.DATETIME, Name = "LastUpdatedDate")
        public DateTime LastUpdatedDate;
    }
    public static class VaccinationShotDtDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_Type = "@p_Type";
        private final String p_ID = "@p_ID";

        public VaccinationShotDtDao(Context context){
            this.helper = new DbHelper(VaccinationShotDt.class);
            this.daoBase = new DaoBase(context);
        }
        public VaccinationShotDt get(int Type, int ID){
            String query = helper.getRecord();
            query = query.replace(p_Type, Integer.toString(Type));
            query = query.replace(p_ID, Integer.toString(ID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (VaccinationShotDt)helper.dataRowToObject(row, new VaccinationShotDt());
        }
        public int insert(VaccinationShotDt record){
            String query = helper.insert(record);
            return daoBase.executeNonQuery(query);
        }
        public int update(VaccinationShotDt record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int Type, int ID){
            VaccinationShotDt record = get(Type, ID);
            String query = helper.delete(record);
            return daoBase.executeNonQuery(query);
        }
    }
    //endregion
    //region vVaccinationType
    @Table(Name = "vVaccinationType")
    public static class vVaccinationType{
        @Column(DataType = DataType.INT, Name = "VaccinationTypeID")
        public int VaccinationTypeID;

        @Column(DataType = DataType.STRING, Name = "VaccinationTypeName")
        public String VaccinationTypeName;
    }
    //endregion
    //region vAppointment
    @Table(Name = "vAppointment")
    public static class vAppointment{
        @Column(DataType = DataType.INT, Name = "AppointmentID", IsPrimaryKey = Bool.TRUE)
        public int AppointmentID;

        @Column(DataType = DataType.INT, Name = "MRN")
        public int MRN;

        @Column(DataType = DataType.STRING, Name = "FullName")
        public String FullName;

        @Column(DataType = DataType.STRING, Name = "ServiceUnitName")
        public String ServiceUnitName;

        @Column(DataType = DataType.INT, Name = "QueueNo")
        public int QueueNo;

        @Column(DataType = DataType.DATETIME, Name = "StartDate")
        public DateTime StartDate;

        @Column(DataType = DataType.DATETIME, Name = "ReminderDate")
        public DateTime ReminderDate;

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

        @Column(DataType = DataType.STRING, Name = "GCAppointmentStatus")
        public String GCAppointmentStatus;

        @Column(DataType = DataType.DATETIME, Name = "LastUpdatedDate")
        public DateTime LastUpdatedDate;
    }
    //endregion
}
