//
//  LabResultViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/19/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class LabResultViewController: BasePatientTableViewController {
    
    @IBOutlet weak var btnRefresh: UIBarButtonItem!
    let MRN:Int = (UserDefaults.standard.object(forKey: "MRN") as? Int)!;
    var lstLabResultHd:[LaboratoryResultHd] = [];
    var selectedLabResultID:Int = 0;

    override func viewDidLoad() {
        super.viewDidLoad()
        lstLabResultHd = BusinessLayer.getLaboratoryResultHdList(filterExpression: "MRN = \(String(describing: MRN))");
        
        if(UserDefaults.standard.object(forKey: "pageType") != nil){
            let pageType = (UserDefaults.standard.object(forKey: "pageType") as? NSString)!;
            if(pageType.isEqual(to: "lab")){
                UserDefaults.standard.set("", forKey:"pageType");
                selectedLabResultID = (UserDefaults.standard.object(forKey: "labResultID") as? Int)!;
                self.performSegue(withIdentifier: "labResultDtView", sender: self);
            }
        }
        
        // Do any additional setup after loading the view.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "labResultDtView" {
            let destinationController:LabResultDtViewController = segue.destination as! LabResultDtViewController;
            destinationController.labResultID = selectedLabResultID;
            
            let backItem = UIBarButtonItem()
            backItem.title = "Back"
            navigationItem.backBarButtonItem = backItem // This will show in the next view controller being pushed
        }
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        selectedLabResultID = lstLabResultHd[indexPath.row].ID as! Int;
        self.performSegue(withIdentifier: "labResultDtView", sender: self);
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
        return lstLabResultHd.count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! LabResultViewCell
        
        let entity:LaboratoryResultHd = lstLabResultHd[indexPath.row];
        cell.lblResultDate.text = entity.ResultDate!.toString(format: "dd-MMM-yyyy");
        //cell.lblStartDate.text = "A";
        cell.lblResultNo.text = entity.ReferenceNo;
        cell.lblProvider.text = entity.ProviderName;
        return cell
    }
    @IBAction func onBtnRefreshClick(_ sender: Any) {
        self.showLoadingPanel();
        self.btnRefresh.isEnabled = false;
        
        let patient:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
        
        syncLabResult(MRN: patient.MRN as! Int, labResultLastUpdatedDate: (patient.LastSyncLabResultDateTime?.toString(format: Constant.FormatString.DATE_TIME_FORMAT_DB))!, completionHandler: { (result) -> Void in
            self.btnRefresh.isEnabled = true;
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
            
            
            self.lstLabResultHd = BusinessLayer.getLaboratoryResultHdList(filterExpression: "MRN = \(String(describing: self.MRN))");
            
            DispatchQueue.main.async() {
                self.tableView.reloadData();
            }
        });
    }
    
    public func syncLabResult(MRN:Int, labResultLastUpdatedDate:String, completionHandler: @escaping (_ result:WebServiceResponsePatient) -> Void){
        let deviceID = UIDevice.current.identifierForVendor!.uuidString;
        WebServiceHelper().SyncLabResult(MRN: MRN, deviceID: deviceID, labResultLastUpdatedDate: labResultLastUpdatedDate, completionHandler: { (result) -> Void in
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
}
