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
        if (row != nil) {
            while (row.next()) {
                result.append(helper.dataRowToObject(row: row, obj: Appointment()) as! Appointment);
            }
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
        if (row != nil) {
            while (row.next()) {
                result.append(helper.dataRowToObject(row: row, obj: Patient()) as! Patient);
            }
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
            if (row != nil) {
                while (row.next()) {
                    result.append(helper.dataRowToObject(row: row, obj: Setting()) as! Setting);
                }
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
