//
//  InitViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/20/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class InitViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        Util.copyFile(fileName: "OttimoPatient.db");
        //ModelManager.getInstance().insertSetting();
        //let entity:Setting = Setting();
        //entity.SettingCode = "A";
        //entity.SettingName = "B";
        //entity.SettingValue = "C";
        //BusinessLayer.insertSetting(record: entity);
        DaoBase.getInstance();
        let lstPatient:[Patient] = BusinessLayer.getPatientList(filterExpression: "");
        if(lstPatient.count == 0){
            self.performSegue(withIdentifier: "loginView", sender: self)
        }
        else if(lstPatient.count == 1){
            let patient:Patient = lstPatient[0];
            let MRN:Int = Int(patient.MRN!);
            UserDefaults.standard.set(MRN, forKey:"MRN");
            UserDefaults.standard.synchronize();
            self.performSegue(withIdentifier: "mainViewInit", sender: self);
        }
        else{
            self.performSegue(withIdentifier: "manageAccountView", sender: self)
        }
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
