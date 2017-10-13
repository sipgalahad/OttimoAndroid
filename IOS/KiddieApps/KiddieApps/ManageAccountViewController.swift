//
//  ManageAccountViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/9/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class ManageAccountViewController: UITableViewController {

    @IBAction func onBtnAddClick(_ sender: Any) {
        self.performSegue(withIdentifier: "loginViewManageAccount", sender: self)
    }
    var lstPatient:[Patient] = [];

    override func viewDidLoad() {
        super.viewDidLoad()
        lstPatient = BusinessLayer.getPatientList(filterExpression: "");

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return lstPatient.count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! ManageAccountViewCell
        
        let patient:Patient = lstPatient[indexPath.row];
        cell.lblPatientName.text = "\(patient.FullName!) (\(patient.MedicalNo!))";
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let patient:Patient = lstPatient[indexPath.row];
        let MRN:Int = Int(patient.MRN!);
        UserDefaults.standard.set(MRN, forKey:"MRN");
        UserDefaults.standard.set("", forKey:"pageType");
        UserDefaults.standard.synchronize();
        self.performSegue(withIdentifier: "mainViewManageAccount", sender: self);
    }
}
