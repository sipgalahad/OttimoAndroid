//
//  InitViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/20/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class InitViewController: BaseViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    public var pageType:NSString = "";
    public var appointmentID:Int = 0;
    public var labResultID:Int = 0;
    public var announcementID:Int = 0;
    public var MRN:Int = 0;
    override func viewDidAppear(_ animated: Bool) {
        var isGoToNextPage = true;
        if UserDefaults.standard.object(forKey: Constant.Session.DB_VERSION) == nil {
            Util.copyFile(fileName: "OttimoPatient.db", isReplaceDB: true);
            let _ = DaoBase.getInstance();
        }
        else{
            let DBVersion = UserDefaults.standard.object(forKey: Constant.Session.DB_VERSION) as? String;
            if DBVersion != Constant.DB_VERSION{
                var isDifferentDBVersion = true;
                let _ = DaoBase.getInstance();
                let lstMRN:[Int] = BusinessLayer.getPatientMRNList(filterExpression: "");
                
                Util.copyFile(fileName: "OttimoPatient.db", isReplaceDB: true);
                let _ = DaoBase.getInstance();
                isGoToNextPage = false;
                
                var listMRN = "";
                for mrn in lstMRN{
                    if(listMRN != ""){
                        listMRN += ",";
                    }
                    listMRN += String(mrn);
                }
                if listMRN == "" {
                    isDifferentDBVersion = false;
                }
                
                self.indicator.startAnimating();
                
                if isDifferentDBVersion {
                    UserDefaults.standard.set(Constant.LIST_MRN, forKey:listMRN);
                    reloadDateTask(listMRN: listMRN);

                }
            }
            else{
                var listMRN = "";
                if UserDefaults.standard.object(forKey: Constant.LIST_MRN) != nil {
                    listMRN = (UserDefaults.standard.object(forKey: Constant.LIST_MRN) as? String)!;
                }
                if listMRN != "" {
                    reloadDateTask(listMRN: listMRN);
                }                
                
                let _ = DaoBase.getInstance();
            }
        }
        //ModelManager.getInstance().insertSetting();
        //let entity:Setting = Setting();
        //entity.SettingCode = "A";
        //entity.SettingName = "B";
        //entity.SettingValue = "C";
        //BusinessLayer.insertSetting(record: entity);
        UserDefaults.standard.set(Constant.DB_VERSION, forKey:Constant.Session.DB_VERSION);
        UserDefaults.standard.set(Constant.APP_VERSION, forKey:Constant.Session.APP_VERSION);
        UserDefaults.standard.synchronize();
        
        if isGoToNextPage {
            goToNextPage();
        }
    }
    
    private func reloadDateTask(listMRN:String){
        let deviceID = UIDevice.current.identifierForVendor!.uuidString;
        reloadDataAfterUpdateApps(listMRN: listMRN, deviceID: deviceID, completionHandler: { (result) -> Void in
            DispatchQueue.main.async() {
                self.indicator.stopAnimating();
            }
            var ctr:Int = 0;
            for patient in result.returnObjPatient{
                patient.LastSyncDateTime = DateTime.now();
                patient.LastSyncAppointmentDateTime = DateTime.now();
                patient.LastSyncVaccinationDateTime = DateTime.now();
                patient.LastSyncLabResultDateTime = DateTime.now();
                let _ = BusinessLayer.insertPatient(record: patient);
                
                let returnObjImg = result.returnObjImg[ctr];
                if(returnObjImg != ""){
                    let imageData = NSData(base64Encoded: returnObjImg);
                    let image = UIImage(data: imageData! as Data);
                    let _ = saveImageToDocumentDirectory(medicalNo: patient.MedicalNo!, image!);
                }
                ctr += 1;
                
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
            for announcement in result.returnObjAnnouncement{
                let _ = BusinessLayer.insertAnnouncement(record: announcement);
            }

            UserDefaults.standard.set(Constant.LIST_MRN, forKey:"");
            
            DispatchQueue.main.async() {
                self.goToNextPage();
            }
        });

    }
    
    public func reloadDataAfterUpdateApps(listMRN:String, deviceID: String, completionHandler: @escaping (_ result:WebServiceResponsePatient2) -> Void){
        WebServiceHelper().ReloadDataAfterUpdateApps(listMRN: listMRN, deviceID: deviceID, completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            let retval:WebServiceResponsePatient2 = WebServiceResponsePatient2();
            
            let dict = WebServiceHelper.convertToDictionary(text: result)
            retval.timeStamp = WebServiceHelper.JSONDateToDateTime(jsonDate: dict?["Timestamp"] as! String);
            let objImage = dict?["ReturnObjImage"] as! NSArray
            for tmp in objImage{
                retval.returnObjImg.append(tmp as! String);
            }
            let objAppointment = dict?["ReturnObjAppointment"] as! NSArray
            for tmp in objAppointment{
                let entity:Appointment = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: Appointment()) as! Appointment
                retval.returnObjAppointment.append(entity);
            }
            let objVaccination = dict?["ReturnObjVaccination"] as! NSArray
            for tmp in objVaccination{
                let entity:VaccinationShotDt = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: VaccinationShotDt()) as! VaccinationShotDt
                retval.returnObjVaccination.append(entity);
            }
            let objLabResultHd = dict?["ReturnObjLabResultHd"] as! NSArray
            for tmp in objLabResultHd{
                let entity:LaboratoryResultHd = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: LaboratoryResultHd()) as! LaboratoryResultHd
                retval.returnObjLabResultHd.append(entity);
            }
            let objLabResultDt = dict?["ReturnObjLabResultDt"] as! NSArray
            for tmp in objLabResultDt{
                let entity:LaboratoryResultDt = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: LaboratoryResultDt()) as! LaboratoryResultDt
                retval.returnObjLabResultDt.append(entity);
            }
            let objAnnouncement = dict?["ReturnObjAnnouncement"] as! NSArray
            for tmp in objAnnouncement{
                let entity:Announcement = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: Announcement()) as! Announcement
                retval.returnObjAnnouncement.append(entity);
            }
            
            let objPatient = dict?["ReturnObjPatient"] as! NSArray
            for tmp in objPatient{
                let entity:Patient = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: Patient()) as! Patient
                retval.returnObjPatient.append(entity);
            }
            
            completionHandler(retval);
        });
    }
    
    func goToNextPage(){
        let lstPatient:[Patient] = BusinessLayer.getPatientList(filterExpression: "");
        
        if(pageType.isEqual(to: "app")){
            let appointment = BusinessLayer.getAppointment(AppointmentID: appointmentID);
            if(appointment != nil){
                appointment?.GCAppointmentStatus = Constant.AppointmentStatus.CHECK_IN;
                _ = BusinessLayer.updateAppointment(record: appointment!);
                UserDefaults.standard.set(MRN, forKey:"MRN");
                UserDefaults.standard.set(pageType, forKey:"pageType");
                UserDefaults.standard.synchronize();
                self.performSegue(withIdentifier: "mainViewInit", sender: self);
            }
            else{
                self.showLoadingPanel();
                let patient:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
                syncAppointment(MRN: patient.MRN as! Int, appointmentLastUpdatedDate: (patient.LastSyncAppointmentDateTime?.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB))!, completionHandler: { (result) -> Void in                    self.hideLoadingPanel();
                    let lstOldAppointment:[Appointment] = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: self.MRN))");
                    for app in lstOldAppointment {
                        let _ = BusinessLayer.deleteAppointment(AppointmentID: app.AppointmentID as! Int);
                    }
                    for app in result.returnObjAppointment {
                        let _ = BusinessLayer.insertAppointment(record: app );
                    }
                    let patient:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
                    patient.LastSyncAppointmentDateTime = DateTime.now();
                    let _ = BusinessLayer.updatePatient(record: patient);
                    
                    UserDefaults.standard.set(self.MRN, forKey:"MRN");
                    UserDefaults.standard.set(self.pageType, forKey:"pageType");
                    UserDefaults.standard.synchronize();
                    self.performSegue(withIdentifier: "mainViewInit", sender: self);
                });
            }
        }
        if(pageType.isEqual(to: "ann")){
            let announcement = BusinessLayer.getAnnouncement(AnnouncementID: announcementID)
            if(announcement != nil){
                UserDefaults.standard.set(MRN, forKey:"MRN");
                UserDefaults.standard.set(pageType, forKey:"pageType");
                UserDefaults.standard.set(self.announcementID, forKey:"announcementID");
                UserDefaults.standard.synchronize();
                self.performSegue(withIdentifier: "mainViewInit", sender: self);
            }
            else{
                self.showLoadingPanel();
                
                let dtNow = DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB)
                
                BusinessLayerWebService.getAnnouncementList(filterExpression: "'\(dtNow)' BETWEEN StartDate AND EndDate", completionHandler: { (result) -> Void in
                    self.hideLoadingPanel();
                    let lstOldAnnouncement = BusinessLayer.getAnnouncementList(filterExpression: "");
                    for announcement in lstOldAnnouncement{
                        let _ = BusinessLayer.deleteAnnouncement(AnnouncementID: announcement.AnnouncementID as! Int);
                    }
                    for announcement in result.returnObj{
                        let _ = BusinessLayer.insertAnnouncement(record: announcement as! Announcement);
                    }
                    DispatchQueue.main.async() {
                        UserDefaults.standard.set(self.MRN, forKey:"MRN");
                        UserDefaults.standard.set(self.pageType, forKey:"pageType");
                        UserDefaults.standard.set(self.announcementID, forKey:"announcementID");
                        UserDefaults.standard.synchronize();
                        self.performSegue(withIdentifier: "mainViewInit", sender: self);
                    }
                });
            }
        }

        else if(pageType.isEqual(to: "lab")){
            self.showLoadingPanel()
            syncLabResultPerID(ID: labResultID , completionHandler: { (result) -> Void in
                self.hideLoadingPanel();
                var lstID = "";
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
                for labResultHd in result.returnObjLabResultHd{
                    let _ = BusinessLayer.insertLaboratoryResultHd(record: labResultHd);
                }
                for labResultDt in result.returnObjLabResultDt{
                    let _ = BusinessLayer.insertLaboratoryResultDt(record: labResultDt);
                }
                
                
                DispatchQueue.main.async() {
                    UserDefaults.standard.set(self.MRN, forKey:"MRN");
                    UserDefaults.standard.set(self.pageType, forKey:"pageType");
                    UserDefaults.standard.set(self.labResultID, forKey:"labResultID");
                    UserDefaults.standard.synchronize();
                    self.performSegue(withIdentifier: "mainViewInit", sender: self);
                }

            });
        }
        else if(lstPatient.count == 0){
            self.performSegue(withIdentifier: "loginView", sender: self)
        }
        else if(lstPatient.count == 1){
            let patient:Patient = lstPatient[0];
            let MRN:Int = Int(patient.MRN!);
            UserDefaults.standard.set(MRN, forKey:"MRN");
            UserDefaults.standard.synchronize();
            UserDefaults.standard.set("", forKey:"pageType");
            self.performSegue(withIdentifier: "mainViewInit", sender: self);
        }
        else{
            self.performSegue(withIdentifier: "manageAccountView", sender: self)
        }

        
    }
    
    public func syncAppointment(MRN:Int, appointmentLastUpdatedDate:String, completionHandler: @escaping (_ result:WebServiceResponsePatient) -> Void){
        let deviceID = UIDevice.current.identifierForVendor!.uuidString;
        WebServiceHelper().SyncAppointment(MRN: MRN, deviceID: deviceID, appointmentLastUpdatedDate: appointmentLastUpdatedDate, completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            let retval:WebServiceResponsePatient = WebServiceResponsePatient();
            
            let dict = WebServiceHelper.convertToDictionary(text: result)
            retval.timeStamp = WebServiceHelper.JSONDateToDateTime(jsonDate: dict?["Timestamp"] as! String);
            if(dict?["ReturnObjAppointment"] != nil){
                let objAppointment = dict?["ReturnObjAppointment"] as! NSArray
                for tmp in objAppointment{
                    let entity:Appointment = WebServiceHelper.JSONObjectToObject(row: tmp as! [String : AnyObject], obj: Appointment()) as! Appointment
                    retval.returnObjAppointment.append(entity);
                }
            }
            completionHandler(retval);
        });
    }

    
    public func syncLabResultPerID(ID:Int, completionHandler: @escaping (_ result:WebServiceResponsePatient) -> Void){
        WebServiceHelper().SyncLabResultPerID(ID: ID, completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            let retval:WebServiceResponsePatient = WebServiceResponsePatient();
            
            let dict = WebServiceHelper.convertToDictionary(text: result)
            retval.timeStamp = WebServiceHelper.JSONDateToDateTime(jsonDate: dict?["Timestamp"] as! String);
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
            completionHandler(retval);
        });
    }


    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
