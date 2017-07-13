//
//  BusinessLayer.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/21/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import Foundation

public class BusinessLayer{
    // MARK: Appointment
    public static func getAppointment(AppointmentID:Int) -> Appointment?
    {
        return AppointmentDao().get(AppointmentID: AppointmentID);
    }
    public static func insertAppointment(record:Appointment) -> Bool
    {
        return AppointmentDao().insert(record: record);
    }
    public static func updateAppointment(record:Appointment) -> Bool
    {
        return AppointmentDao().update(record: record);
    }
    public static func deleteAppointment(AppointmentID:Int) -> Bool
    {
        return AppointmentDao().delete(AppointmentID: AppointmentID);
    }
    public static func getAppointmentList(filterExpression:String) -> [Appointment]{
        var result:Array<Appointment> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.select(tableName: "Appointment", filterExpression: filterExpression);
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(helper.dataRowToObject(row: row, obj: Appointment()) as! Appointment);
        }
        sharedInstance.database!.close()
        
        //}
        //catch (Exception)
        // {
        //e.printStackTrace();
        //}
        return result;
    }
    
    // MARK: LaboratoryResultDt
    public static func getLaboratoryResultDt(LaboratoryResultDtID:Int) -> LaboratoryResultDt?
    {
        return LaboratoryResultDtDao().get(LaboratoryResultDtID: LaboratoryResultDtID);
    }
    public static func insertLaboratoryResultDt(record:LaboratoryResultDt) -> Bool
    {
        return LaboratoryResultDtDao().insert(record: record);
    }
    public static func updateLaboratoryResultDt(record:LaboratoryResultDt) -> Bool
    {
        return LaboratoryResultDtDao().update(record: record);
    }
    public static func deleteLaboratoryResultDt(LaboratoryResultDtID:Int) -> Bool
    {
        return LaboratoryResultDtDao().delete(LaboratoryResultDtID: LaboratoryResultDtID);
    }
    public static func getLaboratoryResultDtList(filterExpression:String) -> [LaboratoryResultDt]{
        var result:Array<LaboratoryResultDt> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.select(tableName: "LaboratoryResultDt", filterExpression: filterExpression);
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(helper.dataRowToObject(row: row, obj: LaboratoryResultDt()) as! LaboratoryResultDt);
        }
        sharedInstance.database!.close()
        
        //}
        //catch (Exception)
        // {
        //e.printStackTrace();
        //}
        return result;
    }

    public static func getLaboratoryResultHd(ID:Int) -> LaboratoryResultHd?
    {
        return LaboratoryResultHdDao().get(ID: ID);
    }
    public static func insertLaboratoryResultHd(record:LaboratoryResultHd) -> Bool
    {
        return LaboratoryResultHdDao().insert(record: record);
    }
    public static func updateLaboratoryResultHd(record:LaboratoryResultHd) -> Bool
    {
        return LaboratoryResultHdDao().update(record: record);
    }
    public static func deleteLaboratoryResultHd(ID:Int) -> Bool
    {
        return LaboratoryResultHdDao().delete(ID: ID);
    }
    public static func getLaboratoryResultHdList(filterExpression:String) -> [LaboratoryResultHd]{
        var result:Array<LaboratoryResultHd> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.select(tableName: "LaboratoryResultHd", filterExpression: filterExpression);
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(helper.dataRowToObject(row: row, obj: LaboratoryResultHd()) as! LaboratoryResultHd);
        }
        sharedInstance.database!.close()
        
        //}
        //catch (Exception)
        // {
        //e.printStackTrace();
        //}
        return result;
    }

    
    // MARK: Patient
    public static func getPatient(MRN:Int) -> Patient?
    {
        return PatientDao().get(MRN: MRN);
    }
    public static func insertPatient(record:Patient) -> Bool
    {
        return PatientDao().insert(record: record);
    }
    public static func updatePatient(record:Patient) -> Bool
    {
        return PatientDao().update(record: record);
    }
    public static func deletePatient(MRN:Int) -> Bool
    {
        return PatientDao().delete(MRN: MRN);
    }
    public static func getPatientList(filterExpression:String) -> [Patient]{
        var result:Array<Patient> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.select(tableName: "Patient", filterExpression: filterExpression);
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(helper.dataRowToObject(row: row, obj: Patient()) as! Patient);
        }
        sharedInstance.database!.close()
        
        //}
        //catch (Exception)
        // {
        //e.printStackTrace();
        //}
        return result;
    }
    public static func getPatientMRNList(filterExpression:String) -> [Int]{
        var result:Array<Int> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.selectListColumn(tableName: "Patient", filterExpression: filterExpression, columnName: "MRN");
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(Int(row.string(forColumn: "MRN")!)!);
        }
        sharedInstance.database!.close()
        
        //}
        //catch (Exception)
        // {
        //e.printStackTrace();
        //}
        return result;
    }


    // MARK: Setting
    public static func getSetting(settingCode:String) -> Setting?
    {
        return SettingDao().get(SettingCode: settingCode);
    }
    public static func insertSetting(record:Setting) -> Bool
    {
        return SettingDao().insert(record: record);
    }
    public static func updateSetting(record:Setting) -> Bool
    {
        return SettingDao().update(record: record);
    }
    public static func deleteSetting(settingCode:String) -> Bool
    {
        return SettingDao().delete(SettingCode: settingCode);
    }
    public static func getSettingList(filterExpression:String) -> [Setting]{
        var result:Array<Setting> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.select(tableName: "Setting", filterExpression: filterExpression);
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(helper.dataRowToObject(row: row, obj: Setting()) as! Setting);
        }
        sharedInstance.database!.close()

        //}
        //catch (Exception)
       // {
            //e.printStackTrace();
        //}
        return result;
    }
    
    // MARK: VaccinationShotDt
    public static func getVaccinationShotDt(Type:Int, ID:Int) -> VaccinationShotDt?
    {
        return VaccinationShotDtDao().get(Type: Type, ID: ID);
    }
    public static func insertVaccinationShotDt(record:VaccinationShotDt) -> Bool
    {
        return VaccinationShotDtDao().insert(record: record);
    }
    public static func updateVaccinationShotDt(record:VaccinationShotDt) -> Bool
    {
        return VaccinationShotDtDao().update(record: record);
    }
    public static func deleteVaccinationShotDt(Type:Int, ID:Int) -> Bool
    {
        return VaccinationShotDtDao().delete(Type: Type, ID: ID);
    }
    public static func getVaccinationShotDtList(filterExpression:String) -> [VaccinationShotDt]{
        var result:Array<VaccinationShotDt> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.select(tableName: "VaccinationShotDt", filterExpression: filterExpression);
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(helper.dataRowToObject(row: row, obj: VaccinationShotDt()) as! VaccinationShotDt);
        }
        sharedInstance.database!.close()
        
        //}
        //catch (Exception)
        // {
        //e.printStackTrace();
        //}
        return result;
    }

    
    
    // MARK: vAppointment
    public static func getvAppointmentList(filterExpression:String) -> [vAppointment]{
        var result:Array<vAppointment> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.select(tableName: "vAppointment", filterExpression: filterExpression);
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(helper.dataRowToObject(row: row, obj: vAppointment()) as! vAppointment);
        }

        sharedInstance.database!.close()
        
        //}
        //catch (Exception)
        // {
        //e.printStackTrace();
        //}
        return result;
    }
    
    
    // MARK: vVaccinationType
    public static func getvVaccinationTypeList(filterExpression:String) -> [vVaccinationType]{
        var result:Array<vVaccinationType> = Array();
        //do
        //{
        let helper:DBHelper = DBHelper();
        sharedInstance.database!.open()
        let query = helper.select(tableName: "vVaccinationType", filterExpression: filterExpression);
        let row = DaoBase.getInstance().getDataRow(query: query);
        while (row.next()) {
            result.append(helper.dataRowToObject(row: row, obj: vVaccinationType()) as! vVaccinationType);
        }
        sharedInstance.database!.close()
        
        //}
        //catch (Exception)
        // {
        //e.printStackTrace();
        //}
        return result;
    }

}
