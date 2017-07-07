//
//  LoginViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/21/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    
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
        sharedInstance.database!.open()
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
        sharedInstance.database!.close()
        let en = BusinessLayer.getSetting(settingCode: "A");
        txtMedicalNo.text = en?.SettingValue;
        
        var lstSetting = BusinessLayer.getSettingList(filterExpression: "");
        txtMedicalNo.text = lstSetting[0].SettingCode;
        //var query = DBHelper().getRecord(tableName: "Setting", lstPrimaryKey: Setting().getPrimaryKey());
        //txtMedicalNo.text = query;
        //txtMedicalNo.text = update(record: setting);
        
        BusinessLayerWebService.getAppointmentList(filterExpression: "AppointmentID < 3000", completionHandler: { (result) -> Void in
            //self.txtMedicalNo.text = result;
            self.txtMedicalNo.text = (result.returnObj[0] as! Appointment).ServiceUnitName;
            self.performSegue(withIdentifier: "mainView", sender: self)
            
            //let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
            
            //let nextViewController = storyBoard.instantiateViewController(withIdentifier: "SWRevealViewController")
            //self.present(nextViewController, animated:true, completion:nil)
        });
        
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

    }
    
    func displayMyAlertMessage(userMessage:String){
        let myAlert = UIAlertController(title: "Alert", message: userMessage, preferredStyle: UIAlertControllerStyle.alert);
        
        let okAction = UIAlertAction(title: "OK", style: UIAlertActionStyle.default, handler: nil);
        
        myAlert.addAction(okAction);
        
        self.present(myAlert, animated: true, completion: nil);
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

