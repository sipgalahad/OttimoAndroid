//
//  InitViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/20/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class InitViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        var isGoToNextPage = true;
        if UserDefaults.standard.object(forKey: Constant.Session.DB_VERSION) == nil {
            Util.copyFile(fileName: "OttimoPatient.db", isReplaceDB: true);
            DaoBase.getInstance();
        }
        else{
            let DBVersion = UserDefaults.standard.object(forKey: Constant.Session.DB_VERSION) as? String;
            if DBVersion != Constant.DB_VERSION{
                let lstMRN:[Int] = BusinessLayer.getPatientMRNList(filterExpression: "");
                
                Util.copyFile(fileName: "OttimoPatient.db", isReplaceDB: true);
                DaoBase.getInstance();
                isGoToNextPage = false;
                
                var listMRN = "";
                for mrn in lstMRN{
                    if(listMRN != ""){
                        listMRN += ",";
                    }
                    listMRN += String(mrn);
                }
                
                goToNextPage();
            }
            else{
                DaoBase.getInstance();
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
    
    public func reloadDataAfterUpdateApps(listMRN:String, deviceID: String, completionHandler: @escaping (_ result:WebServiceResponsePatient) -> Void){
        WebServiceHelper().ReloadDataAfterUpdateApps(listMRN: listMRN, deviceID: deviceID, completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            let retval:WebServiceResponsePatient = WebServiceResponsePatient();
            
            let dict = WebServiceHelper.convertToDictionary(text: result)
            retval.timeStamp = WebServiceHelper.JSONDateToDateTime(jsonDate: dict?["Timestamp"] as! String);
            retval.returnObjImg = dict?["ReturnObjImage"] as! String;
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
        if(lstPatient.count == 0){
            self.performSegue(withIdentifier: "loginView", sender: self)
        }
        else if(lstPatient.count == 1){
            let patient:Patient = lstPatient[0];
            let MRN:Int = Int(patient.MRN!);
            UserDefaults.standard.set(MRN, forKey:"MRN");
            UserDefaults.standard.synchronize();
            self.performSegue(withIdentifier: "mainViewInit", sender: self);
        }
        else{
            self.performSegue(withIdentifier: "manageAccountView", sender: self)
        }

        
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
