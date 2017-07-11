//
//  ProfileViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class ProfileViewController: BasePatientPageViewController {
    @IBOutlet weak var imgProfile: UIImageView!
    @IBOutlet weak var lblMedicalNo: UILabel!
    @IBOutlet weak var lblPatientName: UILabel!
    @IBOutlet weak var lblPreferredName: UILabel!
    @IBOutlet weak var lblCityOfBirth: UILabel!
    @IBOutlet weak var lblDateOfBirth: UILabel!
    @IBOutlet weak var lblMobilePhoneNo: UILabel!
    @IBOutlet weak var lblEmailAddress: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        let MRN = UserDefaults.standard.object(forKey: "MRN") as? Int;
        
        let entity:Patient = BusinessLayer.getPatient(MRN: MRN!)!;
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
        lblPreferredName.text = entity.PreferredName;
        lblCityOfBirth.text = entity.CityOfBirth;
        lblDateOfBirth.text = entity.DateOfBirth!.toString(format: Constant.FormatString.DATE_FORMAT);
        lblMobilePhoneNo.text = entity.getMobilePhoneNoDisplay();
        lblEmailAddress.text = entity.getEmailAddressDisplay();
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
