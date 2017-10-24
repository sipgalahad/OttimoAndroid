//
//  MyAppointmentViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class MyAppointmentViewController: BasePatientTableViewController {
    
    @IBOutlet weak var btnRefresh: UIBarButtonItem!
    let MRN:Int = (UserDefaults.standard.object(forKey: "MRN") as? Int)!;
    var lstAppointment:[Appointment] = [];
    override func viewDidLoad() {
        super.viewDidLoad()
        lstAppointment = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: MRN)) AND StartDate >= '\(DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB))'");
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
        
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        // Return the number of sections.
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // Return the number of rows in the section.
        return lstAppointment.count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! MyAppointmentViewCell
        
        let appointment:Appointment = lstAppointment[indexPath.row];
        cell.lblStartDate.text = appointment.StartDate!.toString(format: "dd-MMM-yyyy");
        //cell.lblStartDate.text = "A";
        cell.lblParamedicName.text = appointment.ParamedicName;
        cell.lblVisitTypeName.text = appointment.VisitTypeName;
        return cell
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

    @IBAction func onBtnRefreshClick(_ sender: Any) {
        self.showLoadingPanel();
        self.btnRefresh.isEnabled = false;
        let patient:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
        
        syncAppointment(MRN: patient.MRN as! Int, appointmentLastUpdatedDate: (patient.LastSyncAppointmentDateTime?.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB))!, completionHandler: { (result) -> Void in
            self.btnRefresh.isEnabled = true;
            self.hideLoadingPanel();
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
            for appointment in result.returnObjAppointment{
                let _ = BusinessLayer.insertAppointment(record: appointment);
            }
            self.lstAppointment = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: self.MRN)) AND StartDate >= '\(DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB))'");
            
            DispatchQueue.main.async() {
                self.tableView.reloadData();
            }
        });

        
    }

}
