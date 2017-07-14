//
//  ErrorFeedbackViewController.swift
//  KiddieApps
//
//  Created by Aloysius Ari Wicaksono on 7/8/17.
//  Copyright © 2017 Samanasoft. All rights reserved.
//

import UIKit

class ErrorFeedbackViewController: BasePatientPageViewController {

    @IBOutlet weak var txtMessage: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()        // Do any additional setup after loading the view.
    }
    
    @IBAction func onBtnSaveClick(_ sender: Any) {        
        let deviceID = UIDevice.current.identifierForVendor!.uuidString;
        let message:String = txtMessage.text!;
        self.showLoadingPanel()
        insertErrorFeedback(deviceID: deviceID, errorMessage: message, completionHandler: { (result) -> Void in
            if(result == "1"){
                self.hideLoadingPanel()
                displayMyAlertMessage(ctrl: self, userMessage: "Kirim Error Feedback Berhasil Dilakukan.");
            }
            else{
                self.hideLoadingPanel()
                displayMyAlertMessage(ctrl: self, userMessage: "Kirim Error Feedback Gagal. Password Lama Tidak Cocok.");
                
            }
        });
    }
    
    public func insertErrorFeedback(deviceID: String, errorMessage: String, completionHandler: @escaping (_ result:String) -> Void){
        WebServiceHelper().InsertErrorFeedback(deviceID: deviceID, errorMessage: errorMessage,  completionHandler: { (result) -> Void in
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
