//
//  RequestPasswordViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/8/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class RequestPasswordViewController: UIViewController {

    @IBOutlet weak var txtMedicalNo: UITextField!
    @IBOutlet weak var txtEmail: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        //navigationItem.title = "One"
    }

    @IBAction func onBtnRequestPasswordClick(_ sender: Any) {
        let medicalNo:String = txtMedicalNo.text!;
        let emailAddress:String = txtEmail.text!;
        
        requestPassword(medicalNo: medicalNo, emailAddress: emailAddress,  completionHandler: { (result) -> Void in
            if(result == "1"){
                DispatchQueue.main.async() {
                    displayMyAlertMessage(ctrl: self, userMessage: "Permintaan Password Berhasil. Silakan cek Email anda untuk mengetahui password Anda.");
                }
            }
            else{
                DispatchQueue.main.async() {
                    displayMyAlertMessage(ctrl: self, userMessage: "Ubah Password Gagal. No RM dan Email Tidak Cocok.");
                }
            }
        });
    }
    
    public func requestPassword(medicalNo:String, emailAddress: String, completionHandler: @escaping (_ result:String) -> Void){
        WebServiceHelper().RequestPassword(medicalNo: medicalNo, emailAddress: emailAddress, completionHandler: { (result) -> Void in
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
