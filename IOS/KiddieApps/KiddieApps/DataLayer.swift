//
//  DataLayer.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/20/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation


public class Announcement : BaseClass{
    var AnnouncementID:NSNumber?
    var Title:String?
    var StartDate:DateTime?
    var EndDate:DateTime?
    var GCAnnouncementType:String?
    var AnnouncementType:String?
    var Remarks:String?
    var LastUpdatedDate:DateTime?
    
    override func getPrimaryKey() -> [String]{
        return ["AnnouncementID"];
    }
}
public class AnnouncementDao{
    private var helper:DBHelper;
    private let p_AnnouncementID = "@p_AnnouncementID";
    
    public init() {
        helper = DBHelper();
    }
    public func get(AnnouncementID:Int) -> Announcement?{
        sharedInstance.database!.open()
        var query = helper.getRecord(tableName: "Announcement", lstPrimaryKey: Announcement().getPrimaryKey());
        query = query.replacingOccurrences(of: p_AnnouncementID, with: String(AnnouncementID));
        let row = DaoBase.getInstance().getDataRow(query: query);
        let temp = helper.dataListRowToObject(row: row, obj: Announcement());
        sharedInstance.database!.close()
        if(temp != nil) {
            let result = temp as! Announcement?;
            return result;
        }
        return nil
    }
    public func insert(record:Announcement) -> Bool{
        let query = helper.insert(tableName: "Announcement", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func update(record:Announcement) -> Bool{
        let query = helper.update(tableName: "Announcement", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func delete(AnnouncementID:Int) -> Bool{
        let record = get(AnnouncementID: AnnouncementID);
        if(record != nil){
            let query = helper.delete(tableName: "Announcement", record: record!);
            return DaoBase.getInstance().executeNonQuery(query: query!);
        }
        return false;
    }
}


public class Appointment : BaseClass{
    var AppointmentID:NSNumber?
    var MRN:NSNumber?
    var ServiceUnitName:String?
    var QueueNo:NSNumber?
    var StartDate:DateTime?
    var ReminderDate:DateTime?
    var EndDate:DateTime?
    var StartTime:String?
    var EndTime:String?
    var cfStartTime:String?
    var VisitTypeName:String?
    var ParamedicName:String?
    var SpecialtyName:String?
    var GCAppointmentStatus:String?
    var LastUpdatedDate:DateTime?
    
    override func getPrimaryKey() -> [String]{
        return ["AppointmentID"];
    }
}
public class AppointmentDao{
    private var helper:DBHelper;
    private let p_AppointmentID = "@p_AppointmentID";
    
    public init() {
        helper = DBHelper();
    }
    public func get(AppointmentID:Int) -> Appointment?{
        sharedInstance.database!.open()
        var query = helper.getRecord(tableName: "Appointment", lstPrimaryKey: Appointment().getPrimaryKey());
        query = query.replacingOccurrences(of: p_AppointmentID, with: String(AppointmentID));
        let row = DaoBase.getInstance().getDataRow(query: query);
        let temp = helper.dataListRowToObject(row: row, obj: Appointment());
        sharedInstance.database!.close()
        if(temp != nil) {
            let result = temp as! Appointment?;
            return result;
        }
        return nil
    }
    public func insert(record:Appointment) -> Bool{
        let query = helper.insert(tableName: "Appointment", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func update(record:Appointment) -> Bool{
        let query = helper.update(tableName: "Appointment", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func delete(AppointmentID:Int) -> Bool{
        let record = get(AppointmentID: AppointmentID);
        let query = helper.delete(tableName: "Appointment", record: record!);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
}

public class LaboratoryResultDt : BaseClass{
    var LaboratoryResultDtID:NSNumber?
    var ID:NSNumber?
    var MRN:NSNumber?
    var ItemName1:String?
    var ItemGroupName1:String?
    var LabTestResultTypeID:NSNumber?
    var LabTestResultTypeName:String?
    var FractionID:NSNumber?
    var FractionName1:String?
    var MetricResultValue:NSNumber?
    var MinMetricNormalValue:NSNumber?
    var MaxMetricNormalValue:NSNumber?
    var MetricUnit:String?
    var NilaiRujukanMetric:String?
    var InternationalResultValue:NSNumber?
    var MinInternationalNormalValue:NSNumber?
    var MaxInternationalNormalValue:NSNumber?
    var InternationalUnit:String?
    var NilaiRujukanInternational:String?
    var LabTestResultTypeDtName:String?
    var TextNormalValue:String?
    var TextValue:String?
    var GCResultSummary:String?
    var ResultSummary:String?
    var Remarks:String?
    
    override func getPrimaryKey() -> [String]{
        return ["LaboratoryResultDtID"];
    }
}

public class LaboratoryResultDtDao{
    private var helper:DBHelper;
    private let p_LaboratoryResultDtID = "@p_LaboratoryResultDtID";
    
    public init() {
        helper = DBHelper();
    }
    public func get(LaboratoryResultDtID:Int) -> LaboratoryResultDt?{
        sharedInstance.database!.open()
        var query = helper.getRecord(tableName: "LaboratoryResultDt", lstPrimaryKey: LaboratoryResultDt().getPrimaryKey());
        query = query.replacingOccurrences(of: p_LaboratoryResultDtID, with: String(LaboratoryResultDtID));
        let row = DaoBase.getInstance().getDataRow(query: query);
        let temp = helper.dataListRowToObject(row: row, obj: LaboratoryResultDt());
        sharedInstance.database!.close()
        if(temp != nil) {
            let result = temp as! LaboratoryResultDt?;
            return result;
        }
        return nil
    }
    public func insert(record:LaboratoryResultDt) -> Bool{
        let query = helper.insert(tableName: "LaboratoryResultDt", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func update(record:LaboratoryResultDt) -> Bool{
        let query = helper.update(tableName: "LaboratoryResultDt", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func delete(LaboratoryResultDtID:Int) -> Bool{
        let record = get(LaboratoryResultDtID: LaboratoryResultDtID);
        let query = helper.delete(tableName: "LaboratoryResultDt", record: record!);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
}

public class LaboratoryResultHd : BaseClass{
    var ID:NSNumber?
    var MRN:NSNumber?
    var ReferenceNo:String?
    var ResultDate:DateTime?
    var ResultTime:String?
    var ProviderName:String?
    var ParamedicName:String?
    var Remarks:String?
    var LastUpdatedDate:DateTime?
    
    override func getPrimaryKey() -> [String]{
        return ["ID"];
    }
}

public class LaboratoryResultHdDao{
    private var helper:DBHelper;
    private let p_ID = "@p_ID";
    
    public init() {
        helper = DBHelper();
    }
    public func get(ID:Int) -> LaboratoryResultHd?{
        sharedInstance.database!.open()
        var query = helper.getRecord(tableName: "LaboratoryResultHd", lstPrimaryKey: LaboratoryResultHd().getPrimaryKey());
        query = query.replacingOccurrences(of: p_ID, with: String(ID));
        let row = DaoBase.getInstance().getDataRow(query: query);
        let temp = helper.dataListRowToObject(row: row, obj: LaboratoryResultHd());
        sharedInstance.database!.close()
        if(temp != nil) {
            let result = temp as! LaboratoryResultHd?;
            return result;
        }
        return nil
    }
    public func insert(record:LaboratoryResultHd) -> Bool{
        let query = helper.insert(tableName: "LaboratoryResultHd", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func update(record:LaboratoryResultHd) -> Bool{
        let query = helper.update(tableName: "LaboratoryResultHd", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func delete(ID:Int) -> Bool{
        let record = get(ID: ID);
        let query = helper.delete(tableName: "LaboratoryResultHd", record: record!);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
}

public class Patient : BaseClass{
    var MRN:NSNumber?;
    var MedicalNo:String?;
    var FullName:String?;
    var PreferredName:String?;
    var CityOfBirth:String?;
    var DateOfBirth:DateTime?;
    var GCSex:String?;
    var Sex:String?;
    var Gender:String?;
    var BloodType:String?;
    var BloodRhesus:String?;
    var EmailAddress:String?;
    var EmailAddress2:String?;
    var MobilePhoneNo1:String?;
    var MobilePhoneNo2:String?;
    var LastSyncDateTime:DateTime?;
    var LastSyncAppointmentDateTime:DateTime?;
    var LastSyncVaccinationDateTime:DateTime?;
    var LastSyncLabResultDateTime:DateTime?;
    public func getMobilePhoneNoDisplay() -> String{
        if (self.MobilePhoneNo2 != ""){
            return MobilePhoneNo1! + " / " + MobilePhoneNo2!;
        }
        return MobilePhoneNo1!;
    }
    public func getEmailAddressDisplay() -> String{
        if (self.EmailAddress2 != ""){
            return EmailAddress! + " / " + EmailAddress2!;
        }
        return EmailAddress!;
    }

    override func getPrimaryKey() -> [String]{
        return ["MRN"];
    }
}
public class PatientDao{
    private var helper:DBHelper;
    private let p_MRN = "@p_MRN";
    
    public init() {
        helper = DBHelper();
    }
    public func get(MRN:Int) -> Patient?{
        sharedInstance.database!.open()
        var query = helper.getRecord(tableName: "Patient", lstPrimaryKey: Patient().getPrimaryKey());
        query = query.replacingOccurrences(of: p_MRN, with: String(MRN));
        let row = DaoBase.getInstance().getDataRow(query: query);
        let temp = helper.dataListRowToObject(row: row, obj: Patient());
        sharedInstance.database!.close()
        if(temp != nil) {
            let result = temp as! Patient?;
            return result;
        }
        return nil
    }
    public func insert(record:Patient) -> Bool{
        let query = helper.insert(tableName: "Patient", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func update(record:Patient) -> Bool{
        let query = helper.update(tableName: "Patient", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func delete(MRN:Int) -> Bool{
        let record = get(MRN: MRN);
        let query = helper.delete(tableName: "Patient", record: record!);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
}


public class Variable{
    var Code:String;
    var Value:String;
    
    public init (Code:String, Value:String){
        self.Code = Code;
        self.Value = Value;
    }
}

public class Setting : BaseClass{
    var SettingCode:String?
    var SettingName:String?
    var SettingValue:String?
    
    override func getPrimaryKey() -> [String]{
        return ["SettingCode"];
    }
    override func getNullableColumn() -> [String] {
        return ["SettingValue"];
    }


}

public class SettingDao{
    private var helper:DBHelper;
    private let p_SettingCode = "@p_SettingCode";
    
    public init() {
        helper = DBHelper();
    }
    public func get(SettingCode:String) -> Setting?{
        sharedInstance.database!.open()
        var query = helper.getRecord(tableName: "Setting", lstPrimaryKey: Setting().getPrimaryKey());
        query = query.replacingOccurrences(of: p_SettingCode, with: SettingCode);
        let row = DaoBase.getInstance().getDataRow(query: query);
        let temp = helper.dataListRowToObject(row: row, obj: Setting());
        sharedInstance.database!.close()
        if(temp != nil) {
            let result = temp as! Setting?;
            return result;
        }
        return nil    }
    public func insert(record:Setting) -> Bool{
        let query = helper.insert(tableName: "Setting", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func update(record:Setting) -> Bool{
        let query = helper.update(tableName: "Setting", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func delete(SettingCode:String) -> Bool{
        //let record:Setting = get(SettingCode);
        var query = helper.getRecord(tableName: "Setting", lstPrimaryKey: Setting().getPrimaryKey());
        query = query.replacingOccurrences(of: p_SettingCode, with: SettingCode);
        return DaoBase.getInstance().executeNonQuery(query: query);
    }
}

public class VaccinationShotDt : BaseClass{
    var `Type`:NSNumber?;
    var ID:NSNumber?;
    var VaccinationDate:DateTime?;
    var MRN:NSNumber?;
    var ParamedicName:String?;
    var VaccinationTypeID:NSNumber?;
    var VaccinationTypeName:String?;
    var VaccinationNo:String?;
    var VaccineName:String?;
    var Dose:NSNumber?;
    var DoseUnit:String?;
    var LastUpdatedDate:DateTime?
    
    override func getPrimaryKey() -> [String]{
        return ["Type", "ID"];
    }
}
public class VaccinationShotDtDao{
    private var helper:DBHelper;
    private let p_Type = "@p_Type";
    private let p_ID = "@p_ID";
    
    public init() {
        helper = DBHelper();
    }
    public func get(Type:Int, ID:Int) -> VaccinationShotDt?{
        sharedInstance.database!.open()
        var query = helper.getRecord(tableName: "VaccinationShotDt", lstPrimaryKey: VaccinationShotDt().getPrimaryKey());
        query = query.replacingOccurrences(of: p_Type, with: String(Type));
        query = query.replacingOccurrences(of: p_ID, with: String(ID));
        let row = DaoBase.getInstance().getDataRow(query: query);
        let temp = helper.dataListRowToObject(row: row, obj: VaccinationShotDt());
        sharedInstance.database!.close()
        if(temp != nil) {
            let result = temp as! VaccinationShotDt?;
            return result;
        }
        return nil
    }
    public func insert(record:VaccinationShotDt) -> Bool{
        let query = helper.insert(tableName: "VaccinationShotDt", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func update(record:VaccinationShotDt) -> Bool{
        let query = helper.update(tableName: "VaccinationShotDt", record: record);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
    public func delete(Type:Int, ID:Int) -> Bool{
        let record = get(Type: Type, ID: ID);
        let query = helper.delete(tableName: "VaccinationShotDt", record: record!);
        return DaoBase.getInstance().executeNonQuery(query: query!);
    }
}


public class vAppointment : BaseClass{
    var AppointmentID:NSNumber?
    var MRN:NSNumber?
    var FullName:String?
    var ServiceUnitName:String?
    var QueueNo:NSNumber?
    var StartDate:DateTime?
    var ReminderDate:DateTime?
    var EndDate:DateTime?
    var StartTime:String?
    var EndTime:String?
    var cfStartTime:String?
    var VisitTypeName:String?
    var ParamedicName:String?
    var SpecialtyName:String?
    var GCAppointmentStatus:String?
    var LastUpdatedDate:DateTime?
}

public class vVaccinationType : BaseClass{
    var VaccinationTypeID:NSNumber?
    var VaccinationTypeName:String?
}

