//
//  MyAppointmentViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class MyAppointmentViewController: UITableViewController {

    @IBOutlet weak var btnMenu: UIBarButtonItem!
    var lstAppointment:[Appointment] = [];
    override func viewDidLoad() {
        super.viewDidLoad()
        if revealViewController() != nil {
            btnMenu.target = revealViewController()
            btnMenu.action = #selector(SWRevealViewController.revealToggle(_:))
            view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
        }
        
        lstAppointment = BusinessLayer.getAppointmentList(filterExpression: "");
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
}
