//
//  VaccinationViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/11/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class VaccinationViewController: BasePatientPageViewController, UITableViewDelegate, UITableViewDataSource, UIPickerViewDelegate, UIPickerViewDataSource {
    
    @IBOutlet weak var spnVaccinationType: UIPickerView!
    @IBOutlet weak var tblView: UITableView!
    let MRN:Int = (UserDefaults.standard.object(forKey: "MRN") as? Int)!;
    var lstVaccination:[VaccinationShotDt] = [];
    var lstVaccinationType:[vVaccinationType] = [];
    var selectedVaccinationTpeID:Int = 0;
    
    
    @IBAction func onBtnRefreshClick(_ sender: Any) {
        BusinessLayerWebService.getVaccinationShotDtList(filterExpression: "MRN = \(String(describing: MRN))", completionHandler: { (result) -> Void in
            let lstOldVaccination:[VaccinationShotDt] = BusinessLayer.getVaccinationShotDtList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldVaccination {
                let _ = BusinessLayer.deleteVaccinationShotDt(Type: app.Type as! Int, ID: app.ID as! Int);
            }
            for app in result.returnObj {
                let _ = BusinessLayer.insertVaccinationShotDt(record: app as! VaccinationShotDt);
            }
            let patient:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
            patient.LastSyncVaccinationDateTime = DateTime.now();
            let _ = BusinessLayer.updatePatient(record: patient);
            
            self.lstVaccinationType = BusinessLayer.getvVaccinationTypeList(filterExpression: "1 = 1 ORDER BY VaccinationTypeName");
            if self.lstVaccinationType.count > 0{
                self.selectedVaccinationTpeID = self.lstVaccinationType[0].VaccinationTypeID as! Int;
            }
            self.bindGridView();
            DispatchQueue.main.async() {
                self.spnVaccinationType.reloadAllComponents();
                self.spnVaccinationType.selectRow(0, inComponent: 0, animated: true)
                self.tblView.reloadData();
            }
        });
        
    }


    override func viewDidLoad() {
        super.viewDidLoad()

        lstVaccinationType = BusinessLayer.getvVaccinationTypeList(filterExpression: "1 = 1 ORDER BY VaccinationTypeName");
        
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
        
        // Register the table view cell class and its reuse id
        //self.tblView.register(VaccinationViewCell.self, forCellReuseIdentifier: "Cell")
        
        // This view controller itself will provide the delegate methods and row data for the table view.
        
        
        spnVaccinationType.delegate = self
        spnVaccinationType.dataSource = self;
        
        if lstVaccinationType.count > 0{
            selectedVaccinationTpeID = lstVaccinationType[0].VaccinationTypeID as! Int;
        }
        
        bindGridView()
        tblView.delegate = self
        tblView.dataSource = self
        tblView.isScrollEnabled = true
    }
    
    func bindGridView(){
        if selectedVaccinationTpeID == 0{
            lstVaccination = [];
        }
        else{
            lstVaccination = BusinessLayer.getVaccinationShotDtList(filterExpression: "MRN = \(String(describing: MRN)) AND VaccinationTypeID = \(selectedVaccinationTpeID)");
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1;
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return lstVaccinationType.count;
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return lstVaccinationType[row].VaccinationTypeName;
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if(lstVaccinationType.count > 0){
            selectedVaccinationTpeID = lstVaccinationType[row].VaccinationTypeID as! Int;
            bindGridView();
            tblView.reloadData();
        }
    }
    

    // MARK: - Table view data source

    func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return lstVaccination.count;
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! VaccinationViewCell
        
        let vaccination:VaccinationShotDt = lstVaccination[indexPath.row];
        cell.lblVaccinationDate.text = "\(vaccination.VaccinationDate!.toString(format: "dd-MMM-yyyy")) (\(vaccination.VaccinationNo!))";
        cell.lblParamedicName.text = vaccination.ParamedicName;
        cell.lblDose.text = "\(String(describing: vaccination.VaccineName!)) \(String(describing: vaccination.Dose!)) \(String(describing: vaccination.DoseUnit!))";
        return cell
    }
}
