//
//  DataLayer.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/20/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

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
        let result = helper.dataListRowToObject(row: row, obj: Patient()) as! Patient?;
        sharedInstance.database!.close()
        return result;
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
        var query = helper.getRecord(tableName: "Patient", lstPrimaryKey: Patient().getPrimaryKey());
        query = query.replacingOccurrences(of: p_MRN, with: String(MRN));
        return DaoBase.getInstance().executeNonQuery(query: query);
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
        let result = helper.dataListRowToObject(row: row, obj: Appointment()) as! Appointment?;
        sharedInstance.database!.close()
        return result;
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
        var query = helper.getRecord(tableName: "Appointment", lstPrimaryKey: Appointment().getPrimaryKey());
        query = query.replacingOccurrences(of: p_AppointmentID, with: String(AppointmentID));
        return DaoBase.getInstance().executeNonQuery(query: query);
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
        let result = helper.dataListRowToObject(row: row, obj: Setting()) as! Setting?;
        sharedInstance.database!.close()
        return result;
    }
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


