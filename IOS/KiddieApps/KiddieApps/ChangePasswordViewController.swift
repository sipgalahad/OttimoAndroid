//
//  ChangePasswordViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 6/28/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class ChangePasswordViewController: UIViewController {
    @IBOutlet weak var txtOldPassword: UITextField!
    @IBOutlet weak var txtNewPassword: UITextField!
    @IBOutlet weak var txtConfirmNewPassword: UITextField!
    @IBOutlet weak var btnMenu: UIBarButtonItem!
    override func viewDidLoad() {
        super.viewDidLoad()
        if revealViewController() != nil {
            btnMenu.target = revealViewController()
            btnMenu.action = #selector(SWRevealViewController.revealToggle(_:))
            view.addGestureRecognizer(self.revealViewController().panGestureRecognizer())
        }
        
        // Do any additional setup after loading the view.
    }

    @IBAction func onBtnSaveClick(_ sender: Any) {
        let MRN = UserDefaults.standard.object(forKey: "MRN") as? Int;
        let oldPassword:String = txtOldPassword.text!;
        let newPassword:String = txtNewPassword.text!;
        let confirmNewPassword:String = txtConfirmNewPassword.text!;
        
        if(newPassword != confirmNewPassword){
            displayMyAlertMessage(ctrl: self, userMessage: "Konfirmasi Password Tidak Cocok.");
        }
        changePassword(MRN: MRN!, oldPassword: oldPassword, newPassword: newPassword,  completionHandler: { (result) -> Void in
            if(result == "1"){
                DispatchQueue.main.async() {
                    displayMyAlertMessage(ctrl: self, userMessage: "Ubah Password Berhasil Dilakukan.");
                }
            }
            else{
                DispatchQueue.main.async() {
                    displayMyAlertMessage(ctrl: self, userMessage: "Ubah Password Gagal. Password Lama Tidak Cocok.");
                }
            }
        });
    }
    
    public func changePassword(MRN:Int, oldPassword: String, newPassword: String, completionHandler: @escaping (_ result:String) -> Void){
        WebServiceHelper().ChangePassword(MRN: MRN, oldPassword: oldPassword, newPassword: newPassword,  completionHandler: { (result) -> Void in
            let dict = WebServiceHelper.convertToDictionary(text: result)
            let retval:String = dict?["Result"] as! String;
            completionHandler(retval);
        });
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
