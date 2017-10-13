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
    @IBAction func onBtnRefreshClick(_ sender: Any) {
        self.showLoadingPanel();
        self.btnRefresh.isEnabled = false;
        BusinessLayerWebService.getAppointmentList(filterExpression: "MRN = \(String(describing: MRN)) AND StartDate >= '\(DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB))'", completionHandler: { (result) -> Void in
            self.btnRefresh.isEnabled = true;
            self.hideLoadingPanel();
            let lstOldAppointment:[Appointment] = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldAppointment {
                let _ = BusinessLayer.deleteAppointment(AppointmentID: app.AppointmentID as! Int);
            }
            for app in result.returnObj {
                let _ = BusinessLayer.insertAppointment(record: app as! Appointment);
            }
            let patient:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
            patient.LastSyncAppointmentDateTime = DateTime.now();
            let _ = BusinessLayer.updatePatient(record: patient);
            
            self.lstAppointment = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: self.MRN)) AND StartDate >= '\(DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB))'");
            DispatchQueue.main.async() {
                self.tableView.reloadData();
            }
        });
        
    }

}
