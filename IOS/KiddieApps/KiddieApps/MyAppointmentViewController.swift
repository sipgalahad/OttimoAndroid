//
//  MyAppointmentViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class MyAppointmentViewController: UITableViewController {
    
    let MRN:Int = (UserDefaults.standard.object(forKey: "MRN") as? Int)!;
    @IBOutlet weak var btnMenu: UIBarButtonItem!
    var lstAppointment:[Appointment] = [];
    override func viewDidLoad() {
        super.viewDidLoad()
        if revealViewController() != nil {
            btnMenu.target = revealViewController()
            btnMenu.action = #selector(SWRevealViewController.revealToggle(_:))
            view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
        }
        
        lstAppointment = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: MRN))");
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
        BusinessLayerWebService.getAppointmentList(filterExpression: "MRN = \(String(describing: MRN)) AND StartDate >= '\(DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB))'", completionHandler: { (result) -> Void in
            let lstOldAppointment:[Appointment] = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldAppointment {
                BusinessLayer.deleteAppointment(AppointmentID: app.AppointmentID as! Int);
            }
            for app in result.returnObj {
                BusinessLayer.insertAppointment(record: app as! Appointment);
            }
            let patient:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
            patient.LastSyncAppointmentDateTime = DateTime.now();
            BusinessLayer.updatePatient(record: patient);
            
            self.lstAppointment = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: self.MRN))");
            DispatchQueue.main.async() {
                self.tableView.reloadData();
            }
        });
        
    }

}
