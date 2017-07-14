//
//  AppDelegate.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/15/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit
import Firebase
import FirebaseMessaging
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {

    var window: UIWindow?


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        UIApplication.shared.setMinimumBackgroundFetchInterval(60 * 60 * 12);
        if #available(iOS 10.0, *){
            UNUserNotificationCenter.current().delegate = self;
            let authOptions : UNAuthorizationOptions = [.alert, .badge, .sound];
            UNUserNotificationCenter.current().requestAuthorization(options: authOptions, completionHandler: {_, _ in });
            Messaging.messaging().delegate = self;
        }
        else{
            let setting:UIUserNotificationSettings = UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil);
            application.registerUserNotificationSettings(setting);
        }
        
        application.registerForRemoteNotifications();
        FirebaseApp.configure();
        return true
    }
    
    func application(_ application: UIApplication, performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        
        let lstPatient:[Patient] = BusinessLayer.getPatientList(filterExpression: "");
        for entity in lstPatient{
            let deviceID = UIDevice.current.identifierForVendor!.uuidString;
            
            syncPatient(MRN: entity.MRN as! Int, deviceID: deviceID, patientLastUpdatedDate: (entity.LastSyncDateTime?.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB))!, photoLastUpdatedDate: (entity.LastSyncDateTime?.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB))!, appointmentLastUpdatedDate: (entity.LastSyncAppointmentDateTime?.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB))!, vaccinationLastUpdatedDate: (entity.LastSyncVaccinationDateTime?.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB))!, labResultLastUpdatedDate: (entity.LastSyncLabResultDateTime?.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB))!, completionHandler: { (result) -> Void in
                
                if(result.returnObjPatient.count > 0){
                    let entityPatient = result.returnObjPatient[0];
                    for patient in result.returnObjPatient{
                        patient.LastSyncDateTime = DateTime.now();
                        patient.LastSyncAppointmentDateTime = DateTime.now();
                        patient.LastSyncVaccinationDateTime = DateTime.now();
                        patient.LastSyncLabResultDateTime = DateTime.now();
                        let _ = BusinessLayer.updatePatient(record: patient);
                    }
                    
                    var lstID = "";
                    for entity in result.returnObjAppointment{
                        if lstID != ""{
                            lstID += ",";
                        }
                        lstID += String(describing: entity.AppointmentID!);
                    }
                    if (lstID != ""){
                        let lstOldEntity:[Appointment] = BusinessLayer.getAppointmentList(filterExpression: "AppointmentID IN (\(lstID))");
                        for oldEntity in lstOldEntity{
                            let _ = BusinessLayer.deleteAppointment(AppointmentID: oldEntity.AppointmentID as! Int);
                        }
                    }
                    
                    lstID = "";
                    for entity in result.returnObjVaccination{
                        if lstID != ""{
                            lstID += ",";
                        }
                        lstID += String(describing: entity.ID!);
                    }
                    if (lstID != ""){
                        let lstOldEntity:[VaccinationShotDt] = BusinessLayer.getVaccinationShotDtList(filterExpression: "Type = 1 AND ID IN (\(lstID))");
                        for oldEntity in lstOldEntity{
                            let _ = BusinessLayer.deleteVaccinationShotDt(Type: 1, ID: oldEntity.ID as! Int);
                        }
                    }
                    
                    lstID = "";
                    for entity in result.returnObjLabResultHd{
                        if lstID != ""{
                            lstID += ",";
                        }
                        lstID += String(describing: entity.ID!);
                    }
                    if (lstID != ""){
                        let lstOldEntity:[LaboratoryResultHd] = BusinessLayer.getLaboratoryResultHdList(filterExpression: "ID IN (\(lstID))");
                        for oldEntity in lstOldEntity{
                            let _ = BusinessLayer.deleteLaboratoryResultHd(ID: oldEntity.ID as! Int);
                        }
                    }
                    
                    lstID = "";
                    for entity in result.returnObjLabResultDt{
                        if lstID != ""{
                            lstID += ",";
                        }
                        lstID += String(describing: entity.LaboratoryResultDtID!);
                    }
                    if (lstID != ""){
                        let lstOldEntity:[LaboratoryResultDt] = BusinessLayer.getLaboratoryResultDtList(filterExpression: "LaboratoryResultDtID IN (\(lstID))");
                        for oldEntity in lstOldEntity{
                            let _ = BusinessLayer.deleteLaboratoryResultDt(LaboratoryResultDtID: oldEntity.LaboratoryResultDtID as! Int);
                        }
                    }
                    
                    for app in result.returnObjAppointment{
                        let _ = BusinessLayer.insertAppointment(record: app);
                    }
                    for vaccination in result.returnObjVaccination{
                        let _ = BusinessLayer.insertVaccinationShotDt(record: vaccination);
                    }
                    for labResultHd in result.returnObjLabResultHd{
                        let _ = BusinessLayer.insertLaboratoryResultHd(record: labResultHd);
                    }
                    for labResultDt in result.returnObjLabResultDt{
                        let _ = BusinessLayer.insertLaboratoryResultDt(record: labResultDt);
                    }
                    
                    
                    if(result.returnObjImg != ""){
                        let imageData = NSData(base64Encoded: result.returnObjImg);
                        let image = UIImage(data: imageData! as Data);
                        let _ = saveImageToDocumentDirectory(medicalNo: entityPatient.MedicalNo!, image!);
                    }
                }
            });
        }
    }
    
    public func syncPatient(MRN:Int, deviceID:String, patientLastUpdatedDate:String, photoLastUpdatedDate:String, appointmentLastUpdatedDate:String, vaccinationLastUpdatedDate:String, labResultLastUpdatedDate:String, completionHandler: @escaping (_ result:WebServiceResponsePatient) -> Void){
        WebServiceHelper().SyncPatient(MRN: MRN, deviceID: deviceID, patientLastUpdatedDate: patientLastUpdatedDate, photoLastUpdatedDate: photoLastUpdatedDate, appointmentLastUpdatedDate: appointmentLastUpdatedDate, vaccinationLastUpdatedDate: vaccinationLastUpdatedDate, labResultLastUpdatedDate: labResultLastUpdatedDate, completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            let retval:WebServiceResponsePatient = WebServiceResponsePatient();
            
            let dict = WebServiceHelper.convertToDictionary(text: result)
            retval.timeStamp = WebServiceHelper.JSONDateToDateTime(jsonDate: dict?["Timestamp"] as! String);
            retval.returnObjImg = dict?["ReturnObjImage"] as! String;
            if(dict?["ReturnObjAppointment"] != nil){
                let objAppointment = dict?["ReturnObjAppointment"] as! NSArray
                for tmp in objAppointment{
                    let entity:Appointment = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: Appointment()) as! Appointment
                    retval.returnObjAppointment.append(entity);
                }
            }
            if(dict?["ReturnObjVaccination"] != nil){
                let objVaccination = dict?["ReturnObjVaccination"] as! NSArray
                for tmp in objVaccination{
                    let entity:VaccinationShotDt = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: VaccinationShotDt()) as! VaccinationShotDt
                    retval.returnObjVaccination.append(entity);
                }
            }
            if(dict?["ReturnObjLabResultHd"] != nil){
                let objLabResultHd = dict?["ReturnObjLabResultHd"] as! NSArray
                for tmp in objLabResultHd{
                    let entity:LaboratoryResultHd = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: LaboratoryResultHd()) as! LaboratoryResultHd
                    retval.returnObjLabResultHd.append(entity);
                }
            }
            if(dict?["ReturnObjLabResultDt"] != nil){
                let objLabResultDt = dict?["ReturnObjLabResultDt"] as! NSArray
                for tmp in objLabResultDt{
                    let entity:LaboratoryResultDt = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: LaboratoryResultDt()) as! LaboratoryResultDt
                    retval.returnObjLabResultDt.append(entity);
                }
            }
            if(dict?["ReturnObjPatient"] != nil){
                let objPatient = dict?["ReturnObjPatient"] as! NSArray
                for tmp in objPatient{
                    let entity:Patient = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: Patient()) as! Patient
                    retval.returnObjPatient.append(entity);
                }
            }
            completionHandler(retval);
        });
    }
    
    func application(received remoteMessage: MessagingRemoteMessage) {
        print(remoteMessage.appData);
    }
    
    func messaging(_ messaging: Messaging, didRefreshRegistrationToken fcmToken: String) {
        
    }

    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }


}

