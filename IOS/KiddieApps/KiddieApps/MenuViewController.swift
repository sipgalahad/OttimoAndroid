                //
//  MenuViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit
import Firebase
import FirebaseMessaging

class MenuViewController: UITableViewController {
    @IBOutlet weak var imgProfile: UIImageView!
    @IBOutlet weak var lblPatientName: UILabel!
    @IBOutlet weak var lblMedicalNo: UILabel!
    @IBOutlet weak var cellHeader: UITableViewCell!
    @IBOutlet weak var lblAppointmentCount: UILabel!
    @IBOutlet weak var lblAnnouncementCount: UILabel!
    @IBOutlet weak var lblNewsCount: UILabel!
    @IBOutlet weak var lblAdvertisementCount: UILabel!
    @IBOutlet weak var lblMessageCenterCount: UILabel!
    let MRN:Int = (UserDefaults.standard.object(forKey: "MRN") as? Int)!;
    var pageType:NSString = "";
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if(UserDefaults.standard.object(forKey: "pageType") != nil){
            self.pageType = (UserDefaults.standard.object(forKey: "pageType") as? NSString)!;
        }
        let entity:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
        self.imgProfile.layer.cornerRadius = self.imgProfile.frame.size.width / 2;
        self.imgProfile.clipsToBounds = true;
        
        let directoryPath = NSHomeDirectory().appending("/KiddieApps/");
        let filename = "\(entity.MedicalNo!).jpg";
        let filepath = directoryPath.appending(filename);
        if(FileManager.default.fileExists(atPath: filepath)){
            let url = NSURL.fileURL(withPath: filepath);
            let data = NSData(contentsOf: url)
            self.imgProfile.image = UIImage(data: data! as Data)
        }
        else if(entity.GCSex == Constant.Sex.MALE){
            self.imgProfile.image = UIImage(named: "patient_male")!
        }
        else {
            self.imgProfile.image = UIImage(named: "patient_female")!
        }
        lblPatientName.text = entity.FullName;
        lblMedicalNo.text = entity.MedicalNo;
        
        let dtNow = "\(DateTime.now().toString(format: Constant.FormatString.DATE_FORMAT_DB)) 00:00:00"
        
        let lstAppointment = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: MRN)) AND StartDate >= '\(dtNow)'");
        if(lstAppointment.count == 0){
            lblAppointmentCount.isHidden = true;
        }
        else{
            lblAppointmentCount.isHidden = false;
        }
        lblAppointmentCount.text = String(lstAppointment.count);
        
        let lstMessageCenter = BusinessLayer.getAppointmentList(filterExpression: "(GCAppointmentStatus != '\(Constant.AppointmentStatus.VOID)' AND ('\(dtNow)' BETWEEN ReminderDate AND StartDate)) OR (GCAppointmentStatus = '\(Constant.AppointmentStatus.CHECK_IN)' AND StartDate >= '\(dtNow)')");
        if(lstMessageCenter.count == 0){
            lblMessageCenterCount.isHidden = true;
        }
        else{
            lblMessageCenterCount.isHidden = false;
        }
        lblMessageCenterCount.text = String(lstMessageCenter.count);
        
        let lstAnnouncement = BusinessLayer.getAnnouncementList(filterExpression: "'\(dtNow)' BETWEEN StartDate AND EndDate AND GCAnnouncementType = '\(String(describing: Constant.AnnouncementType.ANNOUNCEMENT))'");
        if(lstAnnouncement.count == 0){
            lblAnnouncementCount.isHidden = true;
        }
        else{
            lblAnnouncementCount.isHidden = false;
        }
        lblAnnouncementCount.text = String(lstAnnouncement.count);
        
        let lstNews = BusinessLayer.getAnnouncementList(filterExpression: "'\(dtNow)' BETWEEN StartDate AND EndDate AND GCAnnouncementType = '\(String(describing: Constant.AnnouncementType.NEWS))'");
        if(lstNews.count == 0){
            lblNewsCount.isHidden = true;
        }
        else{
            lblNewsCount.isHidden = false;
        }
        lblNewsCount.text = String(lstNews.count);
        
        let lstAdvertisement = BusinessLayer.getAnnouncementList(filterExpression: "'\(dtNow)' BETWEEN StartDate AND EndDate AND GCAnnouncementType = '\(String(describing: Constant.AnnouncementType.ADVERTISEMENT))'");
        if(lstAdvertisement.count == 0){
            lblAdvertisementCount.isHidden = true;
        }
        else{
            lblAdvertisementCount.isHidden = false;
        }
        lblAdvertisementCount.text = String(lstAdvertisement.count);
        
        let gradient: CAGradientLayer = CAGradientLayer()
        
        gradient.colors = [UIColorFromRGB(rgbValue: 0x2E7D32).cgColor, UIColorFromRGB(rgbValue: 0x96F299).cgColor];
        gradient.locations = [0.0 , 1.0]
        gradient.startPoint = CGPoint(x: 0.0, y: 0.0)
        gradient.endPoint = CGPoint(x: 1.0, y: 1.0)
        gradient.frame = CGRect(x: 0.0, y: 0.0, width: self.view.frame.size.width, height: self.view.frame.size.height)
        
        self.cellHeader.layer.insertSublayer(gradient, at: 0)
        
        
        if(pageType.isEqual(to: "app")){
            UserDefaults.standard.set(MRN, forKey:"MRN");
            UserDefaults.standard.synchronize();
            self.performSegue(withIdentifier: "messageCenterView", sender: self);
        }
        else if(pageType.isEqual(to: "lab")){
            let labResultID:Int = (UserDefaults.standard.object(forKey: "labResultID") as? Int)!;
            UserDefaults.standard.set(MRN, forKey:"MRN");
            UserDefaults.standard.set(pageType, forKey:"pageType");
            UserDefaults.standard.set(labResultID, forKey:"labResultID");
            UserDefaults.standard.synchronize();
            self.performSegue(withIdentifier: "labResultView", sender: self);
        }
        else if(pageType.isEqual(to: "ann")){
            let announcementID:Int = (UserDefaults.standard.object(forKey: "announcementID") as? Int)!;
            UserDefaults.standard.set(MRN, forKey:"MRN");
            UserDefaults.standard.set(pageType, forKey:"pageType");
            UserDefaults.standard.set(announcementID, forKey:"announcementID");
            UserDefaults.standard.synchronize();
            self.performSegue(withIdentifier: "announcementView", sender: self);
        }
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
        // Return the number of rows in the section.
        return 17;
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if(indexPath.row == 7){
            let lstOldAppointment:[Appointment] = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldAppointment {
                let _ = BusinessLayer.deleteAppointment(AppointmentID: app.AppointmentID as! Int);
            }
            let lstOldVaccination:[VaccinationShotDt] = BusinessLayer.getVaccinationShotDtList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldVaccination {
                let _ = BusinessLayer.deleteVaccinationShotDt(Type: app.Type as! Int, ID: app.ID as! Int);
            }
            let lstOldLabResultHd:[LaboratoryResultHd] = BusinessLayer.getLaboratoryResultHdList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldLabResultHd {
                let _ = BusinessLayer.deleteLaboratoryResultHd(ID: app.ID as! Int);
            }
            let lstOldLabResultDt:[LaboratoryResultDt] = BusinessLayer.getLaboratoryResultDtList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldLabResultDt {
                let _ = BusinessLayer.deleteLaboratoryResultDt(LaboratoryResultDtID: app.LaboratoryResultDtID as! Int);
            }

            let entity:Patient = BusinessLayer.getPatient(MRN: self.MRN)!;
            let _ = BusinessLayer.deletePatient(MRN: self.MRN);
            
            let medicalNo = entity.MedicalNo!
            Messaging.messaging().unsubscribe(fromTopic: medicalNo)
            
            
            let directoryPath = NSHomeDirectory().appending("/KiddieApps/");
            let filename = "\(entity.MedicalNo!).jpg";
            let filepath = directoryPath.appending(filename);
            if(FileManager.default.fileExists(atPath: filepath)){
                do {
                    try FileManager.default.removeItem(atPath: filepath);
                } catch let error as NSError {
                    print(error.debugDescription)
                }
            }
            
            self.performSegue(withIdentifier: "initViewLogout", sender: self)
        }
        else if(indexPath.row == 9){
            UserDefaults.standard.set(Constant.AnnouncementType.ANNOUNCEMENT, forKey:"GCAnnouncementType");
            self.performSegue(withIdentifier: "announcementView", sender: self)
        }
        else if(indexPath.row == 10){
            UserDefaults.standard.set(Constant.AnnouncementType.NEWS, forKey:"GCAnnouncementType");
            self.performSegue(withIdentifier: "announcementView", sender: self)
        }
        else if(indexPath.row == 11){
            UserDefaults.standard.set(Constant.AnnouncementType.ADVERTISEMENT, forKey:"GCAnnouncementType");
            self.performSegue(withIdentifier: "announcementView", sender: self)
        }
    }

    /*
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentifier", for: indexPath)

        // Configure the cell...

        return cell
    }
    */

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
