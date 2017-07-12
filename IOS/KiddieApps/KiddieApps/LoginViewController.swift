//
//  LoginViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/21/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class LoginViewController: BaseViewController {
    
    @IBOutlet weak var txtMedicalNo: UITextField!
    @IBOutlet weak var txtPassword: UITextField!
    
    var mutableData:NSMutableData = NSMutableData()
    var currentElementName:NSString = ""
    
    @IBAction func onBtnSignInTapped(_ sender: Any) {
        
        //if(txtMedicalNo.text == "001" && txtPassword.text == "123"){
        //    self.dismiss(animated: true, completion: nil);
        //
        //}
        //else{
        //    displayMyAlertMessage(userMessage: "Login Gagal");
        //}
        //var setting:Setting = Setting();
        //setting.SettingCode = "Temp";
        
        //txtMedicalNo.text = setting.value(forKey: "SettingCode") as! String;
        //let patient:Patient = Patient();
        //patient.MedicalNo = "1";
        /*sharedInstance.database!.open()
        let resultSet: FMResultSet! = sharedInstance.database!.executeQuery("SELECT * FROM Setting", withArgumentsIn: [])
        //let marrStudentInfo : NSMutableArray = NSMutableArray()
        if (resultSet != nil) {
            while resultSet.next() {
                //let studentInfo : StudentInfo = StudentInfo()
                //studentInfo.RollNo = resultSet.stringForColumn("SettingCode")
                //studentInfo.Name = resultSet.stringForColumn("SettingName")
                //studentInfo.Marks =
                txtMedicalNo.text = resultSet.string(forColumn: "SettingValue");
                //marrStudentInfo.addObject(studentInfo)
                //setting.SettingCode = resultSet.string(forColumn: "SettingCode");
                //setting.SettingName = resultSet.string(forColumn: "SettingName");
                //setting.SettingValue = resultSet.string(forColumn: "SettingValue");
            }
        }
        sharedInstance.database!.close()*/
        //let en = BusinessLayer.getSetting(settingCode: "A");
        //txtMedicalNo.text = en?.SettingValue;
        
        //var lstSetting = BusinessLayer.getSettingList(filterExpression: "");
        //txtMedicalNo.text = lstSetting[0].SettingCode;
        //var query = DBHelper().getRecord(tableName: "Setting", lstPrimaryKey: Setting().getPrimaryKey());
        //txtMedicalNo.text = query;
        //txtMedicalNo.text = update(record: setting);
        
        /*BusinessLayerWebService.getAppointmentList(filterExpression: "AppointmentID < 3000", completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            self.txtMedicalNo.text = (result.returnObj[0] as! Appointment).ServiceUnitName;
            //self.performSegue(withIdentifier: "mainView", sender: self)
            
            //let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
            
            //let nextViewController = storyBoard.instantiateViewController(withIdentifier: "SWRevealViewController")
            //self.present(nextViewController, animated:true, completion:nil)
        });*/
        
        /*WebServiceHelper().getListObject(methodName: "GetvMobileAppointmentPerPatientList", filterExpression: "AppointmentID < 1000", completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            
            
            let dict = WebServiceHelper.convertToDictionary(text: result)
            let obj = dict?["ReturnObj"] as! NSArray
            
            let tmp = obj[0] as! [String:AnyObject]

            let entity:Appointment = WebServiceHelper.JSONObjectToObject(row: tmp, obj: Appointment()) as! Appointment
            //let AppointmentID:Int32! = tmp["AppointmentID"] as! Int32!;
            //let GCVisitType:String! = tmp["GCVisitType"] as! String!;
            self.txtMedicalNo.text = entity.ServiceUnitName;
        });*/
        
        
        /*let str = "{\"name\":\"James\"}"
        
        let dict = convertToDictionary(text: str)
        self.txtMedicalNo.text = dict?["name"] as? String;*/
        /*let jsonObject: [String: Any] = [
            "firstname": "aaa",
            "lastname": "sss",
            "email": "my_email",
            "nickname": "ddd",
            "password": "123",
            "username": "qqq"
        ]
        
        do {
            let jsonData = try JSONSerialization.data(withJSONObject: jsonObject, options: .prettyPrinted)
            // here "jsonData" is the dictionary encoded in JSON data
            
            let decoded = try JSONSerialization.jsonObject(with: jsonData, options: [])
            // here "decoded" is of type `Any`, decoded from JSON data
            
            // you can now cast it with the right type
            if let dictFromJSON = decoded as? [String:Array<Any>] {
                txtMedicalNo.text = dictFromJSON["firstname"] as! String;
            }
        } catch {
            print(error.localizedDescription)
         }*/
        //self.performSegue(withIdentifier: "mainView", sender: self)
        
        /*let query = "INSERT INTO Patient (MRN,MedicalNo,FullName,PreferredName,CityOfBirth,DateOfBirth,GCSex,Sex,Gender,BloodType,BloodRhesus,EmailAddress,EmailAddress2,MobilePhone1,MobilePhone2,LastSyncDateTime,LastSyncAppointmentDateTime) VALUES (22008,'01-00022181','Aloysius Lie','Aloysius','Jakarta','2011-10-09 00:00:00','0003^M','Laki-Laki','Laki-Laki','B','','lai.miria@yahoo.com','','','','1900-01-01 00:00:00','1900-01-01 00:00:00')";
        var entity:Patient = Patient();
        entity.MRN = 22008;
        entity.MedicalNo = "01-00022181";
        entity.FullName = "Aloysius Lie";
        entity.PreferredName = "Aloysius";
        entity.CityOfBirth = "Jakarta";
        entity.DateOfBirth = DateTime(year: 2011, month: 10, day: 9, hour: 0, minute: 0, second: 0);
        entity.GCSex = "0003^M";
        entity.Sex = "Laki-Laki";
        entity.Gender = "Laki-Laki";
        entity.BloodType = "B";
        entity.BloodRhesus = "";
        entity.EmailAddress = "";
        entity.EmailAddress2 = "";*/
        
        let deviceID = UIDevice.current.identifierForVendor!.uuidString;
        let OSVersion = UIDevice.current.systemVersion;
        let deviceName = UIDevice.current.modelName;
        
        self.indicator.startAnimating();
        login(medicalNo: txtMedicalNo.text!, password: txtPassword.text!, deviceID: deviceID, deviceName: deviceName, OSVersion: OSVersion, appVersion: Constant.APP_VERSION, FCMToken: "1", completionHandler: { (result) -> Void in
            
            DispatchQueue.main.async() {
                self.indicator.stopAnimating();
            }
            if(result.returnObjPatient.count > 0){
                var entityPatient = result.returnObjPatient[0];
                for patient in result.returnObjPatient{
                    patient.LastSyncDateTime = DateTime.now();
                    patient.LastSyncAppointmentDateTime = DateTime.now();
                    patient.LastSyncVaccinationDateTime = DateTime.now();
                    BusinessLayer.insertPatient(record: patient);
                }
                for app in result.returnObjAppointment{
                    BusinessLayer.insertAppointment(record: app);
                }
                for vaccination in result.returnObjVaccination{
                    BusinessLayer.insertVaccinationShotDt(record: vaccination);
                }

                
                if(result.returnObjImg != ""){
                    let imageData = NSData(base64Encoded: result.returnObjImg);
                    let image = UIImage(data: imageData! as Data);
                    self.saveImageToDocumentDirectory(medicalNo: entityPatient.MedicalNo!, image!);
                }
                UserDefaults.standard.set(entityPatient.MRN, forKey:"MRN");
                UserDefaults.standard.synchronize();
                DispatchQueue.main.async() {
                    self.performSegue(withIdentifier: "mainView", sender: self);
                }
            }
            else{
                DispatchQueue.main.async() {
                    displayMyAlertMessage(ctrl: self, userMessage: "Login Gagal. No RM tidak ditemukan / Password tidak  cocok.");
                }
            }
        });
    }
    
    func saveImageToDocumentDirectory(medicalNo:String, _ chosenImage: UIImage) -> String{
        let directoryPath = NSHomeDirectory().appending("/KiddieApps/");
        if(!FileManager.default.fileExists(atPath: directoryPath)){
            do {
                try FileManager.default.createDirectory(at: NSURL.fileURL(withPath: directoryPath), withIntermediateDirectories: true, attributes: nil)
            }
            catch {
                print(error);
            }
        }
        let filename = "\(medicalNo).jpg";
        let filepath = directoryPath.appending(filename);
        let url = NSURL.fileURL(withPath: filepath);
        do{
            try UIImageJPEGRepresentation(chosenImage, 1.0)?.write(to: url, options: .atomic);
            return String.init("/KiddieApps/\(filename)")
        }
        catch{
            print(error)
            print("file cannot be save at path \(filepath), with error : \(error)");
            return filepath;
        }
    }
    
    public func login(medicalNo:String, password: String, deviceID: String, deviceName: String, OSVersion: String, appVersion: String, FCMToken: String, completionHandler: @escaping (_ result:WebServiceResponsePatient) -> Void){
        WebServiceHelper().Login(medicalNo: medicalNo, password: password, deviceID: deviceID, deviceName: deviceName, OSVersion: OSVersion, appVersion: appVersion, FCMToken: FCMToken, completionHandler: { (result) -> Void in
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

}

