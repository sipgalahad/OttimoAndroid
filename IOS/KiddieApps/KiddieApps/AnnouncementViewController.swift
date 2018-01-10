//
//  MyAppointmentViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class AnnouncementViewController: BasePatientTableViewController {
    
    @IBOutlet weak var btnRefresh: UIBarButtonItem!
    let MRN:Int = (UserDefaults.standard.object(forKey: "MRN") as? Int)!;
    let GCAnnouncementType:String = (UserDefaults.standard.object(forKey: "GCAnnouncementType") as? String)!;
    var lstAnnouncement:[Announcement] = [];
    var selectedAnnouncementID:Int = 0;

    override func viewDidLoad() {
        super.viewDidLoad()
        let dtNow = "\(DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB)) 00:00:00"
        lstAnnouncement = BusinessLayer.getAnnouncementList(filterExpression: "'\(dtNow)' BETWEEN StartDate AND EndDate AND GCAnnouncementType = '\(String(describing: GCAnnouncementType))'");
        
        if (GCAnnouncementType == Constant.AnnouncementType.ANNOUNCEMENT){
            navigationItem.title = "Announcement";
        }
        else if (GCAnnouncementType == Constant.AnnouncementType.NEWS){
            navigationItem.title = "News";
        }
        else {
            navigationItem.title = "Advertisement";
        }
        
        if(UserDefaults.standard.object(forKey: "pageType") != nil){
            let pageType = (UserDefaults.standard.object(forKey: "pageType") as? NSString)!;
            if(pageType.isEqual(to: "ann")){
                UserDefaults.standard.set("", forKey:"pageType");
                selectedAnnouncementID = (UserDefaults.standard.object(forKey: "announcementID") as? Int)!;
                self.performSegue(withIdentifier: "announcementDtView", sender: self);
            }
        }

        // Do any additional setup after loading the view.
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "announcementDtView" {
            let destinationController:AnnouncementDtViewController = segue.destination as! AnnouncementDtViewController;
            destinationController.announcementID = selectedAnnouncementID;
            
            let backItem = UIBarButtonItem()
            backItem.title = "Back"
            navigationItem.backBarButtonItem = backItem // This will show in the next view controller being pushed
        }
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        selectedAnnouncementID = lstAnnouncement[indexPath.row].AnnouncementID as! Int;
        self.performSegue(withIdentifier: "announcementDtView", sender: self);
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
        return lstAnnouncement.count;
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! AnnouncementViewCell
        
        let entity:Announcement = lstAnnouncement[indexPath.row];
        cell.lblDate.text = entity.StartDate!.toString(format: "dd-MMM-yyyy") + " - " + entity.EndDate!.toString(format: "dd-MMM-yyyy");
        cell.lblTitle.text = entity.Title;
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
        
        var dtNow = DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB)

        BusinessLayerWebService.getAnnouncementList(filterExpression: "'\(dtNow)' BETWEEN StartDate AND EndDate", completionHandler: { (result) -> Void in
            self.btnRefresh.isEnabled = true;
            self.hideLoadingPanel();
            let lstOldAnnouncement = BusinessLayer.getAnnouncementList(filterExpression: "");
            for announcement in lstOldAnnouncement{
                let _ = BusinessLayer.deleteAnnouncement(AnnouncementID: announcement.AnnouncementID as! Int);
            }
            for announcement in result.returnObj{
                let _ = BusinessLayer.insertAnnouncement(record: announcement as! Announcement);
            }
            dtNow = "\(DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB)) 00:00:00"
            self.lstAnnouncement = BusinessLayer.getAnnouncementList(filterExpression: "'\(dtNow)' BETWEEN StartDate AND EndDate AND GCAnnouncementType = '\(String(describing: self.GCAnnouncementType))'");
            
            DispatchQueue.main.async() {
                self.tableView.reloadData();
            }
        });
        
        
    }
    
}
