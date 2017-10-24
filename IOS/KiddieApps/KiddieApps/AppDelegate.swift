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
        
        UIApplication.shared.setMinimumBackgroundFetchInterval(2);
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }
        
        application.registerForRemoteNotifications()
        FirebaseApp.configure()
        
        /*// iOS 10 support
        if #available(iOS 10, *) {
            UNUserNotificationCenter.current().requestAuthorization(options:[.badge, .alert, .sound]){ (granted, error) in }
            application.registerForRemoteNotifications()
        }
            // iOS 9 support
        else if #available(iOS 9, *) {
            UIApplication.shared.registerUserNotificationSettings(UIUserNotificationSettings(types: [.badge, .sound, .alert], categories: nil))
            UIApplication.shared.registerForRemoteNotifications()
        }
            // iOS 8 support
        else if #available(iOS 8, *) {
            UIApplication.shared.registerUserNotificationSettings(UIUserNotificationSettings(types: [.badge, .sound, .alert], categories: nil))
            UIApplication.shared.registerForRemoteNotifications()
        }
            // iOS 7 support
        else {
            application.registerForRemoteNotifications(matching: [.badge, .sound, .alert])
        }*/
        
        Messaging.messaging().shouldEstablishDirectChannel = true
        NotificationCenter.default.addObserver(self, selector: #selector(self.tokenRefreshNotification), name: NSNotification.Name.InstanceIDTokenRefresh, object: nil)
        
        
        return true
    }
    
    var refreshedToken:NSString = "";
    func tokenRefreshNotification(notification: NSNotification) {
        //    NOTE: It can be nil here
        if let token = InstanceID.instanceID().token() {
            refreshedToken = token as NSString
            print("InstanceID token: \(refreshedToken)")
            
        }
    }
    
    func application(_ application: UIApplication, performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        
        let lstPatient:[Patient] = BusinessLayer.getPatientList(filterExpression: "");
        for entity in lstPatient{
            let deviceID = UIDevice.current.identifierForVendor!.uuidString;
            syncPatientPerMRN(entity: entity, deviceID: deviceID)
        }
    }
    
    private func syncPatientPerMRN(entity:Patient, deviceID:String){
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
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any]) {
        // If you are receiving a notification message while your app is in the background,
        // this callback will not be fired till the user taps on the notification launching the application.
        // TODO: Handle data of notification
        
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)
        
        // Print message ID.
        //if let messageID = userInfo[gcmMessageIDKey] {
        //    print("Message ID: \(messageID)")
        //}
        
        // Print full message.
        print(userInfo)
    }

    
    func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
        print("Received data message: \(remoteMessage.appData)")
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        // If you are receiving a notification message while your app is in the background,
        // this callback will not be fired till the user taps on the notification launching the application.
        // TODO: Handle data of notification
        
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)
        
        // Print message ID.
        //if let messageID = userInfo[gcmMessageIDKey] {
        //    print("Message ID: \(messageID)")
        //}
        
        // Print full message.
        print(userInfo)
        
        let state:UIApplicationState = application.applicationState
        
        if(state == .background || state == .active || state == .inactive){            let type = userInfo["type"] as! NSString;
            if(type.isEqual(to: "AppReminder")){
                let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                
                let appointmentID = userInfo["appointmentID"] as! NSString;
                let MRN = userInfo["MRN"] as! NSString;
                let vc = storyBoard.instantiateViewController(withIdentifier: "InitViewController") as! InitViewController;
                
                vc.appointmentID = Int(appointmentID as String)!;
                vc.MRN = Int(MRN as String)!;
                vc.pageType = "app"
                self.window?.rootViewController = vc;
            }
            else if(type.isEqual(to: "LabResult")){
                let storyBoard = UIStoryboard(name: "Main", bundle: nil)
                
                let labResultID = userInfo["labResultID"] as! NSString;
                let MRN = userInfo["MRN"] as! NSString;
                let vc = storyBoard.instantiateViewController(withIdentifier: "InitViewController") as! InitViewController;
                
                vc.labResultID = Int(labResultID as String)!;
                vc.MRN = Int(MRN as String)!;
                vc.pageType = "lab"
                self.window?.rootViewController = vc;
            }
            else if(type.isEqual(to: "SyncApp")){
                let MRN = userInfo["MRN"] as! NSString;
                
                let deviceID = UIDevice.current.identifierForVendor!.uuidString;
                let entity:Patient = BusinessLayer.getPatient(MRN: Int(MRN.intValue))!
                syncPatientPerMRN(entity: entity, deviceID: deviceID)
            }

        }
        completionHandler(UIBackgroundFetchResult.newData)
    }
    func messaging(_ messaging: Messaging, didRefreshRegistrationToken fcmToken: String) {
        if let refreshedToken = InstanceID.instanceID().token() {
            print("InstanceID token: \(refreshedToken)")
        }
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        var token = ""
        for i in 0..<deviceToken.count {
            token = token + String(format: "%02.2hhx", arguments: [deviceToken[i]])
        }
        print("Registration succeeded! Token: ", token)
        Messaging.messaging().apnsToken = deviceToken
        
        InstanceID.instanceID().setAPNSToken(deviceToken, type:InstanceIDAPNSTokenType.sandbox)
        InstanceID.instanceID().setAPNSToken(deviceToken, type:InstanceIDAPNSTokenType.prod)
        InstanceID.instanceID().setAPNSToken(
            deviceToken as Data,
            type:.unknown)
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Registration failed!")
    }
    
}
/*
//
//  AppDelegate.swift
//  PushNotification
//
//  Created by Cambridge on 21/4/2017.
//  Copyright © 2017 Inventlinks. All rights reserved.
//

import UIKit
import Firebase
import FirebaseMessaging
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate,UNUserNotificationCenterDelegate{
    
    var window: UIWindow?
    
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        //create the notificationCenter
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
            
            // For iOS 10 data message (sent via FCM)
            //FIRMessaging.messaging().remoteMessageDelegate = self
            
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }
        
        application.registerForRemoteNotifications()
        
        FirebaseApp.configure()
        
        return true
    }
    
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        var token = ""
        for i in 0..<deviceToken.count {
            token = token + String(format: "%02.2hhx", arguments: [deviceToken[i]])
        }
        print("Registration succeeded! Token: ", token)
    }
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Registration failed!")
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
    
    // Firebase notification received
    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter,  willPresent notification: UNNotification, withCompletionHandler   completionHandler: @escaping (_ options:   UNNotificationPresentationOptions) -> Void) {
        
        // custom code to handle push while app is in the foreground
        print("Handle push from foreground\(notification.request.content.userInfo)")
        
        let dict = notification.request.content.userInfo["aps"] as! NSDictionary
        let d : [String : Any] = dict["alert"] as! [String : Any]
        let body : String = d["body"] as! String
        let title : String = d["title"] as! String
        print("Title:\(title) + body:\(body)")
        self.showAlertAppDelegate(title: title,message:body,buttonTitle:"ok",window:self.window!)
        
    }
    
    @available(iOS 10.0, *)
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        // if you set a member variable in didReceiveRemoteNotification, you  will know if this is from closed or background
        print("Handle push from background or closed\(response.notification.request.content.userInfo)")
    }
    
    func showAlertAppDelegate(title: String,message : String,buttonTitle: String,window: UIWindow){
        let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertControllerStyle.alert)
        alert.addAction(UIAlertAction(title: buttonTitle, style: UIAlertActionStyle.default, handler: nil))
        window.rootViewController?.present(alert, animated: false, completion: nil)
    }
    // Firebase ended here
    
}
*/
