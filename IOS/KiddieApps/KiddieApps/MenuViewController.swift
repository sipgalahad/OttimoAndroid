//
//  MenuViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class MenuViewController: UITableViewController {
    @IBOutlet weak var imgProfile: UIImageView!
    @IBOutlet weak var lblPatientName: UILabel!
    @IBOutlet weak var lblMedicalNo: UILabel!
    @IBOutlet weak var cellHeader: UITableViewCell!
    @IBOutlet weak var lblAppointmentCount: UILabel!
    @IBOutlet weak var lblMessageCenterCount: UILabel!
    let MRN:Int = (UserDefaults.standard.object(forKey: "MRN") as? Int)!;
    override func viewDidLoad() {
        super.viewDidLoad()
        
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

        
        let lstAppointment = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: MRN))");
        if(lstAppointment.count == 0){
            lblAppointmentCount.isHidden = true;
        }
        lblAppointmentCount.text = String(lstAppointment.count);
        
        let gradient: CAGradientLayer = CAGradientLayer()
        
        gradient.colors = [UIColorFromRGB(rgbValue: 0x2E7D32).cgColor, UIColorFromRGB(rgbValue: 0x96F299).cgColor];
        gradient.locations = [0.0 , 1.0]
        gradient.startPoint = CGPoint(x: 0.0, y: 0.0)
        gradient.endPoint = CGPoint(x: 1.0, y: 1.0)
        gradient.frame = CGRect(x: 0.0, y: 0.0, width: self.view.frame.size.width, height: self.view.frame.size.height)
        
        self.cellHeader.layer.insertSublayer(gradient, at: 0)
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
        return 11;
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if(indexPath.row == 5){
            let lstOldAppointment:[Appointment] = BusinessLayer.getAppointmentList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldAppointment {
                BusinessLayer.deleteAppointment(AppointmentID: app.AppointmentID as! Int);
            }
            let lstOldVaccination:[VaccinationShotDt] = BusinessLayer.getVaccinationShotDtList(filterExpression: "MRN = \(String(describing: self.MRN))");
            for app in lstOldVaccination {
                BusinessLayer.deleteVaccinationShotDt(Type: app.Type as! Int, ID: app.ID as! Int);
            }

            BusinessLayer.deletePatient(MRN: self.MRN);

            self.performSegue(withIdentifier: "initViewLogout", sender: self)
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
