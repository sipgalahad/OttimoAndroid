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
        @Column(DataType = DataType.STRING, Name = "Code")
        public String Code;

        @Column(DataType = DataType.STRING, Name = "Value")
        public String Value;

        public Variable() {
        }
        public Variable(String Code, String Value) {
            this.Code = Code;
            this.Value = Value;
        }
    }
    //endregion

    //region ConsultVisit
    @Table(Name = "ConsultVisit")
    public static class ConsultVisit{
        @Column(DataType = DataType.INT, Name = "VisitID", IsPrimaryKey = Bool.TRUE)
        public int VisitID;

        @Column(DataType = DataType.INT, Name = "MRN")
        public int MRN;

        @Column(DataType = DataType.STRING, Name = "RegistrationNo")
        public String RegistrationNo;

        @Column(DataType = DataType.DATETIME, Name = "VisitDate")
        public DateTime VisitDate;

        @Column(DataType = DataType.INT, Name = "AgeInYear")
        public int AgeInYear;

        @Column(DataType = DataType.INT, Name = "AgeInMonth")
        public int AgeInMonth;

        @Column(DataType = DataType.INT, Name = "AgeInDay")
        public int AgeInDay;

        @Column(DataType = DataType.STRING, Name = "ParamedicName")
        public String ParamedicName;

        @Column(DataType = DataType.STRING, Name = "ServiceUnitName")
        public String ServiceUnitName;

        @Column(DataType = DataType.STRING, Name = "ClassInitial")
        public String ClassInitial;

        @Column(DataType = DataType.STRING, Name = "VisitNoteSubjective")
        public String VisitNoteSubjective;

        @Column(DataType = DataType.STRING, Name = "VisitNoteObjective")
        public String VisitNoteObjective;

        @Column(DataType = DataType.STRING, Name = "VisitNoteAssessment")
        public String VisitNoteAssessment;

        @Column(DataType = DataType.STRING, Name = "VisitNotePlanning")
        public String VisitNotePlanning;

        @Column(DataType = DataType.STRING, Name = "VisitNoteInternalNotes")
        public String VisitNoteInternalNotes;

        @Column(DataType = DataType.STRING, Name = "VitalSign")
        public String VitalSign;

        @Column(DataType = DataType.STRING, Name = "DiagnosisText")
        public String DiagnosisText;

        @Column(DataType = DataType.STRING, Name = "ProcedureText")
        public String ProcedureText;

        @Column(DataType = DataType.STRING, Name = "LabOrderText")
        public String LabOrderText;

        @Column(DataType = DataType.STRING, Name = "Prescription")
        public String Prescription;

        @Column(DataType = DataType.STRING, Name = "Vaccination")
        public String Vaccination;

        @Column(DataType = DataType.STRING, Name = "FollowUpVisit")
        public String FollowUpVisit;

        @Column(DataType = DataType.STRING, Name = "ChargesService")
        public String ChargesService;
    }
    public static class ConsultVisitDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_VisitID = "@p_VisitID";

        public ConsultVisitDao(Context context){
            this.helper = new DbHelper(ConsultVisit.class);
            this.daoBase = new DaoBase(context);
        }
        public ConsultVisit get(int VisitID){
            String query = helper.getRecord();
            query = query.replace(p_VisitID, Integer.toString(VisitID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (ConsultVisit)helper.dataRowToObject(row, new ConsultVisit());
        }
        public int insert(ConsultVisit record){
            String query = helper.insert(record);
            //Log.d("query Pasien", query);
            return daoBase.executeNonQuery(query);
        }
        public int update(ConsultVisit record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int VisitID){
            ConsultVisit record = get(VisitID);
            String query = helper.delete(record);
            return daoBase.executeNonQuery(query);
        }
    }
    //endregion
    //region ParamedicMaster
    @Table(Name = "ParamedicMaster")
    public static class ParamedicMaster{
        @Column(DataType = DataType.INT, Name = "ParamedicID", IsPrimaryKey = Bool.TRUE)
        public int ParamedicID;

        @Column(DataType = DataType.STRING, Name = "ParamedicCode")
        public String ParamedicCode;

        @Column(DataType = DataType.STRING, Name = "UserName")
        public String UserName;

        @Column(DataType = DataType.STRING, Name = "ParamedicName")
        public String ParamedicName;

        @Column(DataType = DataType.STRING, Name = "PreferredName")
        public String PreferredName;

        @Column(DataType = DataType.STRING, Name = "Initial")
        public String Initial;

        @Column(DataType = DataType.STRING, Name = "GCGender")
        public String GCGender;

        @Column(DataType = DataType.STRING, Name = "Gender")
        public String Gender;

        @Column(DataType = DataType.DATETIME, Name = "LastSyncDateTime")
        public DateTime LastSyncDateTime;
    }
    public static class ParamedicMasterDao{
        private DbHelper helper;
        private DaoBase daoBase;
        private final String p_ParamedicID = "@p_ParamedicID";

        public ParamedicMasterDao(Context context){
            this.helper = new DbHelper(ParamedicMaster.class);
            this.daoBase = new DaoBase(context);
        }
        public ParamedicMaster get(int ParamedicID){
            String query = helper.getRecord();
            query = query.replace(p_ParamedicID, Integer.toString(ParamedicID));
            Cursor row = daoBase.getDataRow(query);
            return (row == null) ? null : (ParamedicMaster)helper.dataRowToObject(row, new ParamedicMaster());
        }
        public int insert(ParamedicMaster record){
            String query = helper.insert(record);
            //Log.d("query Pasien", query);
            return daoBase.executeNonQuery(query);
        }
        public int update(ParamedicMaster record){
            String query = helper.update(record);
            return daoBase.executeNonQuery(query);
        }
        public int delete(int ParamedicID){
            ParamedicMaster record = get(ParamedicID);
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

        @Column(DataType = DataType.DATETIME, Name = "LastSyncCDCGrowthChartDateTime")
        public DateTime LastSyncCDCGrowthChartDateTime;

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
}
